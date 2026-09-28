package com.trekconnect.auth.dto.request;

import com.trekconnect.auth.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    private String deviceInfo;

    private Role role;

    public LoginRequest() {}

    public LoginRequest(String email, String password, String deviceInfo, Role role) {
        this.email = email;
        this.password = password;
        this.deviceInfo = deviceInfo;
        this.role = role;
    }

    public static LoginRequestBuilder builder() {
        return new LoginRequestBuilder();
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
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
        private Role role;

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

        public LoginRequestBuilder role(Role role) {
            this.role = role;
            return this;
        }

        public LoginRequest build() {
            return new LoginRequest(email, password, deviceInfo, role);
        }
    }
}
