package com.trekconnect.auth.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing Refresh Tokens stored in auth_db.
 * 
 * WHY THIS ENTITY WAS CREATED:
 * Implements token rotation and family tracking to detect token reuse attacks. Stores SHA-256
 * token hashes along with revocation state and device info.
 */
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false, length = 36)
    private String id;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(name = "token_hash", nullable = false, unique = true, length = 255)
    private String tokenHash;

    @Column(name = "device_info", length = 255)
    private String deviceInfo;

    @Column(name = "issued_at", nullable = false, updatable = false)
    private LocalDateTime issuedAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "revoked", nullable = false)
    private Boolean revoked = false;

    @Column(name = "replaced_by_token_id", length = 36)
    private String replacedByTokenId;

    public RefreshToken() {}

    public RefreshToken(
            String id,
            String userId,
            String tokenHash,
            String deviceInfo,
            LocalDateTime issuedAt,
            LocalDateTime expiresAt,
            Boolean revoked,
            String replacedByTokenId) {
        this.id = id;
        this.userId = userId;
        this.tokenHash = tokenHash;
        this.deviceInfo = deviceInfo;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.revoked = revoked != null ? revoked : false;
        this.replacedByTokenId = replacedByTokenId;
    }

    @PrePersist
    protected void onCreate() {
        if (this.issuedAt == null) this.issuedAt = LocalDateTime.now();
        if (this.revoked == null) this.revoked = false;
    }

    public boolean isValid() {
        return !Boolean.TRUE.equals(revoked) && LocalDateTime.now().isBefore(expiresAt);
    }

    public static RefreshTokenBuilder builder() {
        return new RefreshTokenBuilder();
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

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Boolean getRevoked() {
        return revoked;
    }

    public void setRevoked(Boolean revoked) {
        this.revoked = revoked;
    }

    public String getReplacedByTokenId() {
        return replacedByTokenId;
    }

    public void setReplacedByTokenId(String replacedByTokenId) {
        this.replacedByTokenId = replacedByTokenId;
    }

    public static class RefreshTokenBuilder {
        private String id;
        private String userId;
        private String tokenHash;
        private String deviceInfo;
        private LocalDateTime issuedAt;
        private LocalDateTime expiresAt;
        private Boolean revoked = false;
        private String replacedByTokenId;

        public RefreshTokenBuilder id(String id) {
            this.id = id;
            return this;
        }

        public RefreshTokenBuilder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public RefreshTokenBuilder tokenHash(String tokenHash) {
            this.tokenHash = tokenHash;
            return this;
        }

        public RefreshTokenBuilder deviceInfo(String deviceInfo) {
            this.deviceInfo = deviceInfo;
            return this;
        }

        public RefreshTokenBuilder issuedAt(LocalDateTime issuedAt) {
            this.issuedAt = issuedAt;
            return this;
        }

        public RefreshTokenBuilder expiresAt(LocalDateTime expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }

        public RefreshTokenBuilder revoked(Boolean revoked) {
            this.revoked = revoked;
            return this;
        }

        public RefreshTokenBuilder replacedByTokenId(String replacedByTokenId) {
            this.replacedByTokenId = replacedByTokenId;
            return this;
        }

        public RefreshToken build() {
            return new RefreshToken(id, userId, tokenHash, deviceInfo, issuedAt, expiresAt, revoked, replacedByTokenId);
        }
    }
}
