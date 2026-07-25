package com.trekconnect.core.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing a Refund Request stored in main_db.
 * 
 * WHY THIS ENTITY WAS CREATED:
 * When a user cancels a confirmed booking or requests a dispute refund, a RefundRequest is queued
 * for platform Admin review ('PENDING', 'APPROVED', 'REJECTED', 'PROCESSED').
 */
@Entity
@Table(name = "refund_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    /**
     * Refund status: 'PENDING', 'APPROVED', 'REJECTED', or 'PROCESSED'.
     */
    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "PENDING";

    @Column(name = "handled_by_admin_id", length = 36)
    private String handledByAdminId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

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
}
