package com.trekconnect.auth.service;

import com.trekconnect.auth.dto.event.UserRegisteredEvent;
import com.trekconnect.auth.dto.request.*;
import com.trekconnect.auth.dto.response.AdminUserCreateResponse;
import com.trekconnect.auth.dto.response.AuthResponse;
import com.trekconnect.auth.dto.response.MessageResponse;
import com.trekconnect.auth.dto.response.UserInfoResponse;
import com.trekconnect.auth.entity.RefreshToken;
import com.trekconnect.auth.entity.Role;
import com.trekconnect.auth.entity.UserCredentials;
import com.trekconnect.auth.event.UserRegisteredEventPublisher;
import com.trekconnect.auth.exception.*;
import com.trekconnect.auth.repository.RefreshTokenRepository;
import com.trekconnect.auth.repository.UserCredentialsRepository;
import com.trekconnect.auth.security.JwtProvider;
import com.trekconnect.auth.util.TokenHashUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service class handling all core Authentication & Identity operations.
 * 
 * WHY THIS SERVICE WAS CREATED:
 * Centralizes security identity business logic: registering new user credentials,
 * authenticating user logins with account lockout protection, rotating refresh tokens,
 * blacklisting access tokens in Redis upon logout, and serving identity profile info.
 */
@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserCredentialsRepository userCredentialsRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final StringRedisTemplate redisTemplate;
    private final UserRegisteredEventPublisher userRegisteredEventPublisher;
    private final EmailVerificationService emailVerificationService;
    private final WelcomeEmailService welcomeEmailService;

    @Value("${jwt.refresh-token-expiration-ms:604800000}")
    private long refreshTokenExpirationMs;

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCK_TIME_DURATION_MINUTES = 15;

    @Autowired
    public AuthService(
            UserCredentialsRepository userCredentialsRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtProvider jwtProvider,
            StringRedisTemplate redisTemplate,
            UserRegisteredEventPublisher userRegisteredEventPublisher,
            EmailVerificationService emailVerificationService,
            WelcomeEmailService welcomeEmailService) {
        this.userCredentialsRepository = userCredentialsRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.redisTemplate = redisTemplate;
        this.userRegisteredEventPublisher = userRegisteredEventPublisher;
        this.emailVerificationService = emailVerificationService;
        this.welcomeEmailService = welcomeEmailService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Processing registration request for email: {}", request.getEmail());

        if (userCredentialsRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("User with email " + request.getEmail() + " already exists");
        }

        Role role = request.getRole() != null ? request.getRole() : Role.USER;

        UserCredentials user = UserCredentials.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .isEmailVerified(false)
                .isActive(true)
                .failedLoginAttempts(0)
                .build();

        user = userCredentialsRepository.save(user);
        log.info("UserCredentials created successfully with userId: {}", user.getId());

        emailVerificationService.createVerificationToken(user.getId());

        UserRegisteredEvent event = UserRegisteredEvent.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .name(request.getName())
                .role(user.getRole())
                .build();
        userRegisteredEventPublisher.publishUserRegistered(event);

        String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getEmail(), user.getRole());
        String refreshToken = createAndSaveRefreshToken(user.getId(), null);

        return buildAuthResponse(accessToken, refreshToken, user);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.info("Processing login request for email: {}", request.getEmail());

        UserCredentials user = userCredentialsRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new InvalidCredentialsException("Account is disabled. Please contact support.");
        }

        if (!user.isAccountNonLocked()) {
            log.warn("Login attempt for locked account: {}", user.getEmail());
            throw new AccountLockedException("Account is temporarily locked due to multiple failed attempts. Try again later.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            handleFailedLoginAttempt(user);
            throw new InvalidCredentialsException("Invalid email or password");
        }

        if (request.getRole() != null && !request.getRole().equals(user.getRole())) {
            log.warn("Role mismatch during login for email: {}. Requested: {}, Account role: {}", request.getEmail(), request.getRole(), user.getRole());
            throw new InvalidCredentialsException("Invalid email, password, or role mismatch for selected role.");
        }

        if (user.getFailedLoginAttempts() > 0 || user.getLockedUntil() != null) {
            user.setFailedLoginAttempts(0);
            user.setLockedUntil(null);
            userCredentialsRepository.save(user);
        }

        String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getEmail(), user.getRole());
        String refreshToken = createAndSaveRefreshToken(user.getId(), request.getDeviceInfo());

        log.info("User successfully logged in: {}", user.getId());
        return buildAuthResponse(accessToken, refreshToken, user);
    }

    @Transactional
    public AuthResponse refresh(RefreshRequest request) {
        log.info("Processing refresh token request");

        String rawRefreshToken = request.getRefreshToken();
        String tokenHash = TokenHashUtil.hashToken(rawRefreshToken);

        RefreshToken storedToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid refresh token"));

        if (!storedToken.isValid()) {
            if (storedToken.getRevoked()) {
                log.warn("Revoked refresh token reuse detected for userId: {}. Revoking all user tokens for security.", storedToken.getUserId());
                refreshTokenRepository.revokeAllUserTokens(storedToken.getUserId());
            }
            throw new TokenExpiredException("Refresh token is expired or revoked");
        }

        UserCredentials user = userCredentialsRepository.findByEmail(storedToken.getUserId())
                .orElseGet(() -> userCredentialsRepository.findById(storedToken.getUserId())
                        .orElseThrow(() -> new ResourceNotFoundException("User not found")));

        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new InvalidCredentialsException("Account is disabled");
        }

        String newRawRefreshToken = UUID.randomUUID().toString();
        String newRefreshTokenHash = TokenHashUtil.hashToken(newRawRefreshToken);

        RefreshToken newToken = RefreshToken.builder()
                .userId(user.getId())
                .tokenHash(newRefreshTokenHash)
                .deviceInfo(request.getDeviceInfo() != null ? request.getDeviceInfo() : storedToken.getDeviceInfo())
                .issuedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusSeconds(refreshTokenExpirationMs / 1000))
                .revoked(false)
                .build();

        newToken = refreshTokenRepository.save(newToken);

        storedToken.setRevoked(true);
        storedToken.setReplacedByTokenId(newToken.getId());
        refreshTokenRepository.save(storedToken);

        String newAccessToken = jwtProvider.generateAccessToken(user.getId(), user.getEmail(), user.getRole());

        log.info("Successfully refreshed tokens for userId: {}", user.getId());
        return buildAuthResponse(newAccessToken, newRawRefreshToken, user);
    }

    @Transactional
    public MessageResponse logout(LogoutRequest request, String authorizationHeader) {
        log.info("Processing logout request");

        if (request.getRefreshToken() != null && !request.getRefreshToken().isBlank()) {
            String tokenHash = TokenHashUtil.hashToken(request.getRefreshToken());
            refreshTokenRepository.findByTokenHash(tokenHash).ifPresent(token -> {
                token.setRevoked(true);
                refreshTokenRepository.save(token);
            });
        }

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String accessToken = authorizationHeader.substring(7);
            if (jwtProvider.validateToken(accessToken)) {
                String jti = jwtProvider.getJtiFromToken(accessToken);
                long ttlSeconds = (jwtProvider.getExpirationFromToken(accessToken).getTime() - System.currentTimeMillis()) / 1000;
                if (ttlSeconds > 0) {
                    try {
                        redisTemplate.opsForValue().set("blacklist:" + jti, "true", Duration.ofSeconds(ttlSeconds));
                        log.info("Access token JTI [{}] blacklisted in Redis for {}s", jti, ttlSeconds);
                    } catch (Exception e) {
                        log.error("Failed to blacklist JTI in Redis", e);
                    }
                }
            }
        }

        return MessageResponse.builder()
                .message("Logged out successfully")
                .success(true)
                .build();
    }

    public UserInfoResponse getMe(String userId) {
        UserCredentials user = userCredentialsRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        return UserInfoResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .isEmailVerified(user.getIsEmailVerified())
                .isActive(user.getIsActive())
                .build();
    }

    @Transactional
    public AdminUserCreateResponse createAdminUser(AdminUserCreateRequest request) {
        log.info("Processing admin user creation request for email: {}, role: {}", request.getEmail(), request.getRole());

        if (userCredentialsRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("User with email " + request.getEmail() + " already exists");
        }

        Role role = request.getRole() != null ? request.getRole() : Role.ADMIN;
        
        String rawPassword = request.getPassword();
        if (rawPassword == null || rawPassword.isBlank()) {
            rawPassword = "Tk#" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        }

        UserCredentials user = UserCredentials.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(rawPassword))
                .role(role)
                .isEmailVerified(true) // Created by admin, auto-verified
                .isActive(true)
                .failedLoginAttempts(0)
                .build();

        user = userCredentialsRepository.save(user);
        log.info("Admin-provisioned UserCredentials created successfully with userId: {}", user.getId());

        // Publish event for profile creation in main_db
        UserRegisteredEvent event = UserRegisteredEvent.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .name(request.getName())
                .role(user.getRole())
                .build();
        userRegisteredEventPublisher.publishUserRegistered(event);

        // Send welcome email
        boolean emailSent = welcomeEmailService.sendWelcomeEmail(request.getName(), user.getEmail(), user.getRole(), rawPassword);

        return AdminUserCreateResponse.builder()
                .userId(user.getId())
                .name(request.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .generatedPassword(rawPassword)
                .emailSent(emailSent)
                .message("User account created successfully! " + (emailSent ? "Welcome email sent to user." : "Account created. Password details available in admin console."))
                .build();
    }

    @Transactional
    public MessageResponse changePassword(String userId, ChangePasswordRequest request) {
        log.info("Processing change password request for userId: {}", userId);

        UserCredentials user = userCredentialsRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        userCredentialsRepository.save(user);

        refreshTokenRepository.revokeAllUserTokens(user.getId());

        log.info("Successfully changed password for userId: {}", user.getId());
        return MessageResponse.builder()
                .message("Password changed successfully! Please log in with your new password.")
                .success(true)
                .build();
    }

    private void handleFailedLoginAttempt(UserCredentials user) {
        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);

        if (attempts >= MAX_FAILED_ATTEMPTS) {
            user.setLockedUntil(LocalDateTime.now().plusMinutes(LOCK_TIME_DURATION_MINUTES));
            log.warn("User account locked due to {} failed attempts: {}", attempts, user.getEmail());
        }

        userCredentialsRepository.save(user);
    }

    private String createAndSaveRefreshToken(String userId, String deviceInfo) {
        String rawToken = UUID.randomUUID().toString();
        String tokenHash = TokenHashUtil.hashToken(rawToken);

        RefreshToken refreshToken = RefreshToken.builder()
                .userId(userId)
                .tokenHash(tokenHash)
                .deviceInfo(deviceInfo)
                .issuedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusSeconds(refreshTokenExpirationMs / 1000))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);
        return rawToken;
    }

    private AuthResponse buildAuthResponse(String accessToken, String refreshToken, UserCredentials user) {
        UserInfoResponse userInfo = UserInfoResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .isEmailVerified(user.getIsEmailVerified())
                .isActive(user.getIsActive())
                .build();

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresInSeconds(jwtProvider.getAccessTokenExpirationMs() / 1000)
                .user(userInfo)
                .build();
    }
}
