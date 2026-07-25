-- ==============================================================================
-- TREKCONNECT AUTHENTICATION DATABASE SCHEMA (auth_db)
-- Database Engine: PostgreSQL
-- Owned Exclusively By: Auth Microservice (trekconnect-auth)
-- Purpose: Holds identity credentials, security tokens, and lockout details.
-- ==============================================================================

-- 1. Table: users_credentials
-- Purpose: Primary table storing user login credentials, hashed passwords, roles, and lockout counters.
CREATE TABLE IF NOT EXISTS users_credentials (
    id VARCHAR(36) PRIMARY KEY, -- UNIVERSAL UUID: Shared across both auth_db and main_db as the global user identifier.
    email VARCHAR(255) NOT NULL UNIQUE, -- User's unique login email address.
    password_hash VARCHAR(255) NOT NULL, -- BCrypt hashed password string (never stored in plain text).
    role VARCHAR(20) NOT NULL DEFAULT 'USER', -- Enum role: 'USER', 'ORGANIZER', or 'ADMIN'.
    is_email_verified BOOLEAN NOT NULL DEFAULT FALSE, -- Flag indicating if email verification link was completed.
    is_active BOOLEAN NOT NULL DEFAULT TRUE, -- Flag indicating active vs admin-disabled accounts.
    failed_login_attempts INT NOT NULL DEFAULT 0, -- Counter tracking consecutive failed logins for brute-force protection.
    locked_until TIMESTAMP NULL, -- Temporary lockout expiration timestamp after 5 failed login attempts.
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- Account creation timestamp.
    updated_at TIMESTAMP NULL -- Last account update timestamp.
);

-- Index for fast lookup during login requests
CREATE INDEX IF NOT EXISTS idx_users_credentials_email ON users_credentials(email);

-- 2. Table: refresh_tokens
-- Purpose: Stores SHA-256 hashed refresh tokens for long-lived session handling and token rotation chains.
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id VARCHAR(36) PRIMARY KEY, -- Unique refresh token record identifier.
    user_id VARCHAR(36) NOT NULL, -- Foreign key referencing users_credentials(id).
    token_hash VARCHAR(255) NOT NULL, -- SHA-256 hash of the issued refresh token string.
    device_info VARCHAR(255) NULL, -- Client User-Agent or device details for active session tracking.
    issued_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- Token issuance timestamp.
    expires_at TIMESTAMP NOT NULL, -- Token expiration timestamp (e.g. 7 days from issuance).
    revoked BOOLEAN NOT NULL DEFAULT FALSE, -- Flag indicating if token was invalidated/logged out.
    replaced_by_token_id VARCHAR(36) NULL, -- Tracks token rotation chain to detect token reuse theft.
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users_credentials(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_refresh_tokens_hash ON refresh_tokens(token_hash);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user_id ON refresh_tokens(user_id);

-- 3. Table: email_verification_tokens
-- Purpose: Stores SHA-256 hashed single-use email verification tokens.
CREATE TABLE IF NOT EXISTS email_verification_tokens (
    id VARCHAR(36) PRIMARY KEY, -- Unique verification token record identifier.
    user_id VARCHAR(36) NOT NULL, -- Foreign key referencing users_credentials(id).
    token_hash VARCHAR(255) NOT NULL, -- SHA-256 hash of the raw verification token link.
    expires_at TIMESTAMP NOT NULL, -- Expiration timestamp (e.g. 24 hours from issuance).
    used BOOLEAN NOT NULL DEFAULT FALSE, -- Flag indicating if token was already consumed.
    CONSTRAINT fk_email_verification_user FOREIGN KEY (user_id) REFERENCES users_credentials(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_email_verification_hash ON email_verification_tokens(token_hash);

-- 4. Table: password_reset_tokens
-- Purpose: Stores SHA-256 hashed password reset tokens issued during forgot-password flow.
CREATE TABLE IF NOT EXISTS password_reset_tokens (
    id VARCHAR(36) PRIMARY KEY, -- Unique reset token record identifier.
    user_id VARCHAR(36) NOT NULL, -- Foreign key referencing users_credentials(id).
    token_hash VARCHAR(255) NOT NULL, -- SHA-256 hash of the password reset link token.
    expires_at TIMESTAMP NOT NULL, -- Expiration timestamp (e.g. 1 hour from issuance).
    used BOOLEAN NOT NULL DEFAULT FALSE, -- Flag indicating if reset link was consumed.
    CONSTRAINT fk_password_reset_user FOREIGN KEY (user_id) REFERENCES users_credentials(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_password_reset_hash ON password_reset_tokens(token_hash);
