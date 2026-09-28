package com.trekconnect.auth.dto.response;

import com.trekconnect.auth.entity.Role;

public class AdminUserCreateResponse {

    private String userId;
    private String name;
    private String email;
    private Role role;
    private String generatedPassword;
    private boolean emailSent;
    private String message;

    public AdminUserCreateResponse() {}

    public AdminUserCreateResponse(String userId, String name, String email, Role role, String generatedPassword, boolean emailSent, String message) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.generatedPassword = generatedPassword;
        this.emailSent = emailSent;
        this.message = message;
    }

    public static AdminUserCreateResponseBuilder builder() {
        return new AdminUserCreateResponseBuilder();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getGeneratedPassword() {
        return generatedPassword;
    }

    public void setGeneratedPassword(String generatedPassword) {
        this.generatedPassword = generatedPassword;
    }

    public boolean isEmailSent() {
        return emailSent;
    }

    public void setEmailSent(boolean emailSent) {
        this.emailSent = emailSent;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class AdminUserCreateResponseBuilder {
        private String userId;
        private String name;
        private String email;
        private Role role;
        private String generatedPassword;
        private boolean emailSent;
        private String message;

        public AdminUserCreateResponseBuilder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public AdminUserCreateResponseBuilder name(String name) {
            this.name = name;
            return this;
        }

        public AdminUserCreateResponseBuilder email(String email) {
            this.email = email;
            return this;
        }

        public AdminUserCreateResponseBuilder role(Role role) {
            this.role = role;
            return this;
        }

        public AdminUserCreateResponseBuilder generatedPassword(String generatedPassword) {
            this.generatedPassword = generatedPassword;
            return this;
        }

        public AdminUserCreateResponseBuilder emailSent(boolean emailSent) {
            this.emailSent = emailSent;
            return this;
        }

        public AdminUserCreateResponseBuilder message(String message) {
            this.message = message;
            return this;
        }

        public AdminUserCreateResponse build() {
            return new AdminUserCreateResponse(userId, name, email, role, generatedPassword, emailSent, message);
        }
    }
}
