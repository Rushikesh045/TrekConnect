package com.trekconnect.auth.dto.response;

import com.trekconnect.auth.entity.Role;

public class UserInfoResponse {

    private String id;
    private String email;
    private Role role;
    private Boolean isEmailVerified;
    private Boolean isActive;

    public UserInfoResponse() {}

    public UserInfoResponse(String id, String email, Role role, Boolean isEmailVerified, Boolean isActive) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.isEmailVerified = isEmailVerified;
        this.isActive = isActive;
    }

    public static UserInfoResponseBuilder builder() {
        return new UserInfoResponseBuilder();
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

    public static class UserInfoResponseBuilder {
        private String id;
        private String email;
        private Role role;
        private Boolean isEmailVerified;
        private Boolean isActive;

        public UserInfoResponseBuilder id(String id) {
            this.id = id;
            return this;
        }

        public UserInfoResponseBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserInfoResponseBuilder role(Role role) {
            this.role = role;
            return this;
        }

        public UserInfoResponseBuilder isEmailVerified(Boolean isEmailVerified) {
            this.isEmailVerified = isEmailVerified;
            return this;
        }

        public UserInfoResponseBuilder isActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public UserInfoResponse build() {
            return new UserInfoResponse(id, email, role, isEmailVerified, isActive);
        }
    }
}
