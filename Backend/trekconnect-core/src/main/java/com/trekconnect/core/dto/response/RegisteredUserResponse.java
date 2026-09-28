package com.trekconnect.core.dto.response;

import java.time.LocalDateTime;

/**
 * Response DTO returning registered user details for Admin management oversight.
 */
public class RegisteredUserResponse {

    private String userId;
    private String name;
    private String email;
    private String phone;
    private String role;
    private String profilePicUrl;
    private String bio;
    private LocalDateTime createdAt;

    public RegisteredUserResponse() {
    }

    public RegisteredUserResponse(String userId, String name, String email, String phone, String role, String profilePicUrl, String bio, LocalDateTime createdAt) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.profilePicUrl = profilePicUrl;
        this.bio = bio;
        this.createdAt = createdAt;
    }

    public static RegisteredUserResponseBuilder builder() {
        return new RegisteredUserResponseBuilder();
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static class RegisteredUserResponseBuilder {
        private String userId;
        private String name;
        private String email;
        private String phone;
        private String role;
        private String profilePicUrl;
        private String bio;
        private LocalDateTime createdAt;

        public RegisteredUserResponseBuilder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public RegisteredUserResponseBuilder name(String name) {
            this.name = name;
            return this;
        }

        public RegisteredUserResponseBuilder email(String email) {
            this.email = email;
            return this;
        }

        public RegisteredUserResponseBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public RegisteredUserResponseBuilder role(String role) {
            this.role = role;
            return this;
        }

        public RegisteredUserResponseBuilder profilePicUrl(String profilePicUrl) {
            this.profilePicUrl = profilePicUrl;
            return this;
        }

        public RegisteredUserResponseBuilder bio(String bio) {
            this.bio = bio;
            return this;
        }

        public RegisteredUserResponseBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public RegisteredUserResponse build() {
            return new RegisteredUserResponse(userId, name, email, phone, role, profilePicUrl, bio, createdAt);
        }
    }
}
