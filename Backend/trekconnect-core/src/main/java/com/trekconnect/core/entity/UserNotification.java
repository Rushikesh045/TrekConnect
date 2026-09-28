package com.trekconnect.core.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * UserNotification entity storing in-app alert notifications for trekkers.
 * 
 * WHY THIS ENTITY WAS CREATED:
 * Notifies trekkers of booking confirmations, payment status updates, and organizer announcements.
 */
@Entity
@Table(name = "user_notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "message", columnDefinition = "TEXT", nullable = false)
    private String message;

    @Column(name = "type", nullable = false, length = 30)
    private String type; // BOOKING_CONFIRMED, PAYMENT_SUCCESS, ORGANIZER_UPDATE, GENERAL

    @Column(name = "is_read", nullable = false)
    private Boolean isRead;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.isRead == null) {
            this.isRead = false;
        }
        if (this.type == null) {
            this.type = "GENERAL";
        }
    }
}
