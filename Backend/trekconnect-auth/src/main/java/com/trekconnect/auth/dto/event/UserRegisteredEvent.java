package com.trekconnect.auth.dto.event;

import com.trekconnect.auth.entity.Role;

import java.io.Serializable;

public class UserRegisteredEvent implements Serializable {

    private String userId;
    private String email;
    private String name;
    private Role role;

    public UserRegisteredEvent() {}

    public UserRegisteredEvent(String userId, String email, String name, Role role) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.role = role;
    }

    public static UserRegisteredEventBuilder builder() {
        return new UserRegisteredEventBuilder();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public static class UserRegisteredEventBuilder {
        private String userId;
        private String email;
        private String name;
        private Role role;

        public UserRegisteredEventBuilder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public UserRegisteredEventBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserRegisteredEventBuilder name(String name) {
            this.name = name;
            return this;
        }

        public UserRegisteredEventBuilder role(Role role) {
            this.role = role;
            return this;
        }

        public UserRegisteredEvent build() {
            return new UserRegisteredEvent(userId, email, name, role);
        }
    }
}
