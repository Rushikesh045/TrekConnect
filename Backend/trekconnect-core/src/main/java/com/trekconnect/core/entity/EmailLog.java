package com.trekconnect.core.entity;

import jakarta.persistence.*;
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
    private String status = "QUEUED";

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "retry_count", nullable = false)
    private Integer retryCount = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public EmailLog() {
    }

    public EmailLog(String id, String recipientEmail, String type, String status,
                    LocalDateTime sentAt, Integer retryCount, LocalDateTime createdAt) {
        this.id = id;
        this.recipientEmail = recipientEmail;
        this.type = type;
        this.status = status != null ? status : "QUEUED";
        this.sentAt = sentAt;
        this.retryCount = retryCount != null ? retryCount : 0;
        this.createdAt = createdAt;
    }

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

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRecipientEmail() { return recipientEmail; }
    public void setRecipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }

    public Integer getRetryCount() { return retryCount; }
    public void setRetryCount(Integer retryCount) { this.retryCount = retryCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static EmailLogBuilder builder() {
        return new EmailLogBuilder();
    }

    public static class EmailLogBuilder {
        private String id;
        private String recipientEmail;
        private String type;
        private String status = "QUEUED";
        private LocalDateTime sentAt;
        private Integer retryCount = 0;
        private LocalDateTime createdAt;

        public EmailLogBuilder id(String id) { this.id = id; return this; }
        public EmailLogBuilder recipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; return this; }
        public EmailLogBuilder type(String type) { this.type = type; return this; }
        public EmailLogBuilder status(String status) { this.status = status; return this; }
        public EmailLogBuilder sentAt(LocalDateTime sentAt) { this.sentAt = sentAt; return this; }
        public EmailLogBuilder retryCount(Integer retryCount) { this.retryCount = retryCount; return this; }
        public EmailLogBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public EmailLog build() {
            return new EmailLog(id, recipientEmail, type, status, sentAt, retryCount, createdAt);
        }
    }
}
