package com.trekconnect.core.dto.event;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO matching the 'user.registered' RabbitMQ event published by auth_db service.
 * 
 * WHY THIS CLASS WAS CREATED:
 * Allows trekconnect-core to deserialize user registration messages and create matching UserProfile records.
 */
public class UserRegisteredEvent implements Serializable {

    private String userId;
    private String email;
    private String role;
    private LocalDateTime createdAt;

    public UserRegisteredEvent() {
    }

    public UserRegisteredEvent(String userId, String email, String role, LocalDateTime createdAt) {
        this.userId = userId;
        this.email = email;
        this.role = role;
        this.createdAt = createdAt;
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
}
