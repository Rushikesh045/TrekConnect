package com.trekconnect.core.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing Email Dispatch Logs stored in main_db.
 * 
 * WHY THIS ENTITY WAS CREATED:
 * Replaces a separate notification microservice/database. Monolith's EmailService logs outgoing
 * welcome emails, booking confirmations, password resets, and refund updates directly to this table.
 */
@Entity
@Table(name = "email_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailLog {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "recipient_email", nullable = false, length = 255)
    private String recipientEmail;

    /**
     * Email Type: 'WELCOME', 'BOOKING_CONFIRMATION', 'PASSWORD_RESET', 'EVENT_APPROVED', 'REFUND_UPDATE'.
     */
    @Column(nullable = false, length = 50)
    private String type;

    /**
     * Delivery Status: 'QUEUED', 'SENT', or 'FAILED'.
     */
    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "QUEUED";

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "retry_count", nullable = false)
    @Builder.Default
    private Integer retryCount = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.status == null) {
            this.status = "QUEUED";
        }
        if (this.retryCount == null) {
            this.retryCount = 0;
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
