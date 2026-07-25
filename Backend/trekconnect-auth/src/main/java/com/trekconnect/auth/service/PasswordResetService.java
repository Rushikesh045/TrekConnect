package com.trekconnect.auth.service;

import com.trekconnect.auth.dto.response.MessageResponse;
import com.trekconnect.auth.entity.PasswordResetToken;
import com.trekconnect.auth.entity.UserCredentials;
import com.trekconnect.auth.exception.ResourceNotFoundException;
import com.trekconnect.auth.exception.TokenExpiredException;
import com.trekconnect.auth.repository.PasswordResetTokenRepository;
import com.trekconnect.auth.repository.RefreshTokenRepository;
import com.trekconnect.auth.repository.UserCredentialsRepository;
import com.trekconnect.auth.util.TokenHashUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service managing user password reset requests and link tokens.
 * 
 * WHY THIS SERVICE WAS CREATED:
 * Handles forgot password requests, issues single-use hashed reset tokens, and updates user password hashes
 * while invalidating active refresh tokens.
 */
@Service
public class PasswordResetService {

    private static final Logger log = LoggerFactory.getLogger(PasswordResetService.class);

    private final PasswordResetTokenRepository resetTokenRepository;
    private final UserCredentialsRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${password.reset-token-expiration-minutes:30}")
    private long resetTokenExpirationMinutes;

    @Autowired
    public PasswordResetService(
            PasswordResetTokenRepository resetTokenRepository,
            UserCredentialsRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder) {
        this.resetTokenRepository = resetTokenRepository;
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public MessageResponse forgotPassword(String email) {
        log.info("Processing forgot password request for email: {}", email);

        userRepository.findByEmail(email).ifPresent(user -> {
            resetTokenRepository.deleteByUserId(user.getId());

            String rawToken = UUID.randomUUID().toString();
            String tokenHash = TokenHashUtil.hashToken(rawToken);

            PasswordResetToken resetToken = PasswordResetToken.builder()
                    .userId(user.getId())
                    .tokenHash(tokenHash)
                    .expiresAt(LocalDateTime.now().plusMinutes(resetTokenExpirationMinutes))
                    .used(false)
                    .build();

            resetTokenRepository.save(resetToken);
            log.info("Generated password reset token for userId: {}", user.getId());
        });

        return MessageResponse.builder()
                .message("If an account exists with that email, a password reset link has been generated")
                .success(true)
                .build();
    }

    @Transactional
    public MessageResponse resetPassword(String rawToken, String newPassword) {
        log.info("Processing reset password request");

        String tokenHash = TokenHashUtil.hashToken(rawToken);

        PasswordResetToken resetToken = resetTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid or non-existent password reset token"));

        if (!resetToken.isValid()) {
            throw new TokenExpiredException("Password reset token is expired or has already been used");
        }

        UserCredentials user = userRepository.findById(resetToken.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found for token"));

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        userRepository.save(user);

        resetToken.setUsed(true);
        resetTokenRepository.save(resetToken);

        refreshTokenRepository.revokeAllUserTokens(user.getId());

        log.info("Successfully reset password for userId: {}", user.getId());
        return MessageResponse.builder()
                .message("Password has been reset successfully. Please log in with your new password.")
                .success(true)
                .build();
    }
}
