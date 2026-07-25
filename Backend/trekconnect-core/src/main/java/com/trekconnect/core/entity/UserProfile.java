package com.trekconnect.core.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entity representing the User Profile stored in main_db.
 * 
 * WHY THIS ENTITY WAS CREATED:
 * This entity holds trekker profile metadata (display name, phone, bio, avatar picture URL).
 * It uses the universal user_id (UUID) matching auth_db.users_credentials.id.
 * It is populated asynchronously when a user registers, via the RabbitMQ 'user.registered' event.
 */
@Entity
@Table(name = "user_profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {

    /**
     * Primary Key matching auth_db.users_credentials.id.
     * No foreign key constraint across databases (enforced at application level).
     */
    @Id
    @Column(name = "user_id", length = 36)
    private String userId;

    /**
     * User's full display name.
     */
    @Column(nullable = false, length = 150)
    private String name;

    /**
     * Contact phone number.
     */
    @Column(length = 15)
    private String phone;

    /**
     * Profile picture URL stored in cloud storage / S3.
     */
    @Column(name = "profile_pic_url", length = 500)
    private String profilePicUrl;

    /**
     * User's biography / trekker tagline.
     */
    @Column(columnDefinition = "TEXT")
    private String bio;

    /**
     * Denormalized user role copy ('USER', 'ORGANIZER', 'ADMIN') for local query performance.
     */
    @Column(nullable = false, length = 20)
    private String role;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
