package com.trekconnect.auth.dto.response;

public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private long expiresInSeconds;
    private UserInfoResponse user;

    public AuthResponse() {}

    public AuthResponse(String accessToken, String refreshToken, String tokenType, long expiresInSeconds, UserInfoResponse user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType != null ? tokenType : "Bearer";
        this.expiresInSeconds = expiresInSeconds;
        this.user = user;
    }

    public static AuthResponseBuilder builder() {
        return new AuthResponseBuilder();
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public long getExpiresInSeconds() {
        return expiresInSeconds;
    }

    public void setExpiresInSeconds(long expiresInSeconds) {
        this.expiresInSeconds = expiresInSeconds;
    }

    public UserInfoResponse getUser() {
        return user;
    }

    public void setUser(UserInfoResponse user) {
        this.user = user;
    }

    public static class AuthResponseBuilder {
        private String accessToken;
        private String refreshToken;
        private String tokenType = "Bearer";
        private long expiresInSeconds;
        private UserInfoResponse user;

        public AuthResponseBuilder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public AuthResponseBuilder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }

        public AuthResponseBuilder tokenType(String tokenType) {
            this.tokenType = tokenType;
            return this;
        }

        public AuthResponseBuilder expiresInSeconds(long expiresInSeconds) {
            this.expiresInSeconds = expiresInSeconds;
            return this;
        }

        public AuthResponseBuilder user(UserInfoResponse user) {
            this.user = user;
            return this;
        }

        public AuthResponse build() {
            return new AuthResponse(accessToken, refreshToken, tokenType, expiresInSeconds, user);
        }
    }
}
