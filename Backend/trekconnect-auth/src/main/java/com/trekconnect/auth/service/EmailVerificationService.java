package com.trekconnect.auth.service;

import com.trekconnect.auth.dto.response.MessageResponse;
import com.trekconnect.auth.entity.EmailVerificationToken;
import com.trekconnect.auth.entity.UserCredentials;
import com.trekconnect.auth.exception.ResourceNotFoundException;
import com.trekconnect.auth.exception.TokenExpiredException;
import com.trekconnect.auth.repository.EmailVerificationTokenRepository;
import com.trekconnect.auth.repository.UserCredentialsRepository;
import com.trekconnect.auth.util.TokenHashUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service managing user email verification flows.
 * 
 * WHY THIS SERVICE WAS CREATED:
 * Generates cryptographically secure single-use email verification tokens, stores SHA-256 hashes
 * in auth_db, and verifies tokens when clicked by users.
 */
@Service
public class EmailVerificationService {

    private static final Logger log = LoggerFactory.getLogger(EmailVerificationService.class);

    private final EmailVerificationTokenRepository tokenRepository;
    private final UserCredentialsRepository userRepository;

    @Value("${email.verification-token-expiration-hours:24}")
    private long tokenExpirationHours;

    @Autowired
    public EmailVerificationService(
            EmailVerificationTokenRepository tokenRepository,
            UserCredentialsRepository userRepository) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public String createVerificationToken(String userId) {
        tokenRepository.deleteByUserId(userId);

        String rawToken = UUID.randomUUID().toString();
        String tokenHash = TokenHashUtil.hashToken(rawToken);

        EmailVerificationToken verificationToken = EmailVerificationToken.builder()
                .userId(userId)
                .tokenHash(tokenHash)
                .expiresAt(LocalDateTime.now().plusHours(tokenExpirationHours))
                .used(false)
                .build();

        tokenRepository.save(verificationToken);
        log.info("Created email verification token for userId: {}", userId);
        return rawToken;
    }

    @Transactional
    public MessageResponse verifyEmail(String rawToken) {
        String tokenHash = TokenHashUtil.hashToken(rawToken);

        EmailVerificationToken verificationToken = tokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid or non-existent email verification token"));

        if (!verificationToken.isValid()) {
            throw new TokenExpiredException("Email verification token is expired or has already been used");
        }

        UserCredentials user = userRepository.findById(verificationToken.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found for token"));

        user.setIsEmailVerified(true);
        userRepository.save(user);

        verificationToken.setUsed(true);
        tokenRepository.save(verificationToken);

        log.info("Successfully verified email for userId: {}", user.getId());
        return MessageResponse.builder()
                .message("Email verified successfully")
                .success(true)
                .build();
    }

    @Transactional
    public MessageResponse resendVerificationToken(String email) {
        UserCredentials user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        if (Boolean.TRUE.equals(user.getIsEmailVerified())) {
            return MessageResponse.builder()
                    .message("Email is already verified")
                    .success(true)
                    .build();
        }

        createVerificationToken(user.getId());
        return MessageResponse.builder()
                .message("Verification email has been resent")
                .success(true)
                .build();
    }
}
