package com.trekconnect.core.dto.response;

import java.time.LocalDateTime;

/**
 * Response DTO returning user profile data.
 */
public class UserProfileResponse {

    private String userId;
    private String name;
    private String phone;
    private String profilePicUrl;
    private String bio;
    private String role;
    private LocalDateTime createdAt;

    public UserProfileResponse() {
    }

    public UserProfileResponse(String userId, String name, String phone, String profilePicUrl, String bio, String role, LocalDateTime createdAt) {
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.profilePicUrl = profilePicUrl;
        this.bio = bio;
        this.role = role;
        this.createdAt = createdAt;
    }

    public static UserProfileResponseBuilder builder() {
        return new UserProfileResponseBuilder();
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static class UserProfileResponseBuilder {
        private String userId;
        private String name;
        private String phone;
        private String profilePicUrl;
        private String bio;
        private String role;
        private LocalDateTime createdAt;

        public UserProfileResponseBuilder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public UserProfileResponseBuilder name(String name) {
            this.name = name;
            return this;
        }

        public UserProfileResponseBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public UserProfileResponseBuilder profilePicUrl(String profilePicUrl) {
            this.profilePicUrl = profilePicUrl;
            return this;
        }

        public UserProfileResponseBuilder bio(String bio) {
            this.bio = bio;
            return this;
        }

        public UserProfileResponseBuilder role(String role) {
            this.role = role;
            return this;
        }

        public UserProfileResponseBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public UserProfileResponse build() {
            return new UserProfileResponse(userId, name, phone, profilePicUrl, bio, role, createdAt);
        }
    }
}
