package com.trekconnect.core.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing a Refund Request stored in main_db.
 * 
 * WHY THIS ENTITY WAS CREATED:
 * When a user cancels a booking or requests a dispute refund, a RefundRequest is queued
 * for platform Admin review ('PENDING', 'APPROVED', 'REJECTED', 'PROCESSED').
 */
@Entity
@Table(name = "refund_requests")
public class RefundRequest {

    @Id
    @Column(length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by_user_id", nullable = false)
    private UserProfile requestedByUser;

    @Column(length = 500)
    private String reason;

    @Column(nullable = false, length = 20)
    private String status = "PENDING";

    @Column(name = "handled_by_admin_id", length = 36)
    private String handledByAdminId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public RefundRequest() {
    }

    public RefundRequest(String id, Payment payment, UserProfile requestedByUser, String reason, String status, String handledByAdminId, LocalDateTime createdAt) {
        this.id = id;
        this.payment = payment;
        this.requestedByUser = requestedByUser;
        this.reason = reason;
        this.status = status != null ? status : "PENDING";
        this.handledByAdminId = handledByAdminId;
        this.createdAt = createdAt;
    }

    public static RefundRequestBuilder builder() {
        return new RefundRequestBuilder();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public UserProfile getRequestedByUser() {
        return requestedByUser;
    }

    public void setRequestedByUser(UserProfile requestedByUser) {
        this.requestedByUser = requestedByUser;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHandledByAdminId() {
        return handledByAdminId;
    }

    public void setHandledByAdminId(String handledByAdminId) {
        this.handledByAdminId = handledByAdminId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.status == null) {
            this.status = "PENDING";
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public static class RefundRequestBuilder {
        private String id;
        private Payment payment;
        private UserProfile requestedByUser;
        private String reason;
        private String status = "PENDING";
        private String handledByAdminId;
        private LocalDateTime createdAt;

        public RefundRequestBuilder id(String id) {
            this.id = id;
            return this;
        }

        public RefundRequestBuilder payment(Payment payment) {
            this.payment = payment;
            return this;
        }

        public RefundRequestBuilder requestedByUser(UserProfile requestedByUser) {
            this.requestedByUser = requestedByUser;
            return this;
        }

        public RefundRequestBuilder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public RefundRequestBuilder status(String status) {
            this.status = status;
            return this;
        }

        public RefundRequestBuilder handledByAdminId(String handledByAdminId) {
            this.handledByAdminId = handledByAdminId;
            return this;
        }

        public RefundRequestBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public RefundRequest build() {
            return new RefundRequest(id, payment, requestedByUser, reason, status, handledByAdminId, createdAt);
        }
    }
}
