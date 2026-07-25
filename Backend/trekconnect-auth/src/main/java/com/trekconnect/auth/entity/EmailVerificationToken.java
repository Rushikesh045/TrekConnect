package com.trekconnect.auth.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing single-use Email Verification Tokens stored in auth_db.
 * 
 * WHY THIS ENTITY WAS CREATED:
 * Manages email account verification links by storing SHA-256 hashed single-use tokens
 * with explicit expiration timestamps.
 */
@Entity
@Table(name = "email_verification_tokens")
public class EmailVerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false, length = 36)
    private String id;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(name = "token_hash", nullable = false, unique = true, length = 255)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "used", nullable = false)
    private Boolean used = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public EmailVerificationToken() {}

    public EmailVerificationToken(String id, String userId, String tokenHash, LocalDateTime expiresAt, Boolean used, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
        this.used = used != null ? used : false;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.used == null) this.used = false;
    }

    public boolean isValid() {
        return !Boolean.TRUE.equals(used) && LocalDateTime.now().isBefore(expiresAt);
    }

    public static EmailVerificationTokenBuilder builder() {
        return new EmailVerificationTokenBuilder();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Boolean getUsed() {
        return used;
    }

    public void setUsed(Boolean used) {
        this.used = used;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static class EmailVerificationTokenBuilder {
        private String id;
        private String userId;
        private String tokenHash;
        private LocalDateTime expiresAt;
        private Boolean used = false;
        private LocalDateTime createdAt;

        public EmailVerificationTokenBuilder id(String id) {
            this.id = id;
            return this;
        }

        public EmailVerificationTokenBuilder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public EmailVerificationTokenBuilder tokenHash(String tokenHash) {
            this.tokenHash = tokenHash;
            return this;
        }

        public EmailVerificationTokenBuilder expiresAt(LocalDateTime expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }

        public EmailVerificationTokenBuilder used(Boolean used) {
            this.used = used;
            return this;
        }

        public EmailVerificationTokenBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public EmailVerificationToken build() {
            return new EmailVerificationToken(id, userId, tokenHash, expiresAt, used, createdAt);
        }
    }
}
