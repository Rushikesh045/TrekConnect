package com.trekconnect.auth.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing core user security credentials stored in auth_db.
 * 
 * WHY THIS ENTITY WAS CREATED:
 * Encapsulates identity metadata for registration, login authentication, role-based authorization,
 * email verification flags, and brute-force account lockout tracking.
 */
@Entity
@Table(name = "users_credentials")
public class UserCredentials {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false, length = 36)
    private String id;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role role = Role.USER;

    @Column(name = "is_email_verified", nullable = false)
    private Boolean isEmailVerified = false;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "failed_login_attempts", nullable = false)
    private Integer failedLoginAttempts = 0;

    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public UserCredentials() {}

    public UserCredentials(
            String id,
            String email,
            String passwordHash,
            Role role,
            Boolean isEmailVerified,
            Boolean isActive,
            Integer failedLoginAttempts,
            LocalDateTime lockedUntil,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role != null ? role : Role.USER;
        this.isEmailVerified = isEmailVerified != null ? isEmailVerified : false;
        this.isActive = isActive != null ? isActive : true;
        this.failedLoginAttempts = failedLoginAttempts != null ? failedLoginAttempts : 0;
        this.lockedUntil = lockedUntil;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.role == null) this.role = Role.USER;
        if (this.isEmailVerified == null) this.isEmailVerified = false;
        if (this.isActive == null) this.isActive = true;
        if (this.failedLoginAttempts == null) this.failedLoginAttempts = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isAccountNonLocked() {
        if (lockedUntil == null) return true;
        return LocalDateTime.now().isAfter(lockedUntil);
    }

    public static UserCredentialsBuilder builder() {
        return new UserCredentialsBuilder();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Boolean getIsEmailVerified() {
        return isEmailVerified;
    }

    public void setIsEmailVerified(Boolean isEmailVerified) {
        this.isEmailVerified = isEmailVerified;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Integer getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(Integer failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public LocalDateTime getLockedUntil() {
        return lockedUntil;
    }

    public void setLockedUntil(LocalDateTime lockedUntil) {
        this.lockedUntil = lockedUntil;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static class UserCredentialsBuilder {
        private String id;
        private String email;
        private String passwordHash;
        private Role role = Role.USER;
        private Boolean isEmailVerified = false;
        private Boolean isActive = true;
        private Integer failedLoginAttempts = 0;
        private LocalDateTime lockedUntil;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public UserCredentialsBuilder id(String id) {
            this.id = id;
            return this;
        }

        public UserCredentialsBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserCredentialsBuilder passwordHash(String passwordHash) {
            this.passwordHash = passwordHash;
            return this;
        }

        public UserCredentialsBuilder role(Role role) {
            this.role = role;
            return this;
        }

        public UserCredentialsBuilder isEmailVerified(Boolean isEmailVerified) {
            this.isEmailVerified = isEmailVerified;
            return this;
        }

        public UserCredentialsBuilder isActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public UserCredentialsBuilder failedLoginAttempts(Integer failedLoginAttempts) {
            this.failedLoginAttempts = failedLoginAttempts;
            return this;
        }

        public UserCredentialsBuilder lockedUntil(LocalDateTime lockedUntil) {
            this.lockedUntil = lockedUntil;
            return this;
        }

        public UserCredentialsBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public UserCredentialsBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public UserCredentials build() {
            return new UserCredentials(
                    id, email, passwordHash, role, isEmailVerified, isActive,
                    failedLoginAttempts, lockedUntil, createdAt, updatedAt
            );
        }
    }
}
