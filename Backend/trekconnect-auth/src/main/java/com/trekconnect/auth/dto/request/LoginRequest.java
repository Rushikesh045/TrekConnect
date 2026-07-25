package com.trekconnect.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    private String deviceInfo;

    public LoginRequest() {}

    public LoginRequest(String email, String password, String deviceInfo) {
        this.email = email;
        this.password = password;
        this.deviceInfo = deviceInfo;
    }

    public static LoginRequestBuilder builder() {
        return new LoginRequestBuilder();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public static class LoginRequestBuilder {
        private String email;
        private String password;
        private String deviceInfo;

        public LoginRequestBuilder email(String email) {
            this.email = email;
            return this;
        }

        public LoginRequestBuilder password(String password) {
            this.password = password;
            return this;
        }

        public LoginRequestBuilder deviceInfo(String deviceInfo) {
            this.deviceInfo = deviceInfo;
            return this;
        }

        public LoginRequest build() {
            return new LoginRequest(email, password, deviceInfo);
        }
    }
}
