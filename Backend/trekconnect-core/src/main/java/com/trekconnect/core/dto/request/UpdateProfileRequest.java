package com.trekconnect.core.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for updating user profile info.
 */
public class UpdateProfileRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 150, message = "Name must be between 2 and 150 characters")
    private String name;

    @Pattern(regexp = "^$|^[0-9+\\-\\s]{8,15}$", message = "Please enter a valid phone number")
    private String phone;

    private String profilePicUrl;

    @Size(max = 1000, message = "Bio must be less than 1000 characters")
    private String bio;

    public UpdateProfileRequest() {
    }

    public UpdateProfileRequest(String name, String phone, String profilePicUrl, String bio) {
        this.name = name;
        this.phone = phone;
        this.profilePicUrl = profilePicUrl;
        this.bio = bio;
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
}
