package com.trekconnect.core.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing a Razorpay Payment Transaction stored in main_db.
 * 
 * WHY THIS ENTITY WAS CREATED:
 * Links 1-to-1 with a Booking. Stores Razorpay order_id, payment_id, signature, and status.
 * Server-side Razorpay Webhooks update 'webhook_verified' to TRUE and mark status = 'SUCCESS'.
 */
@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @Column(length = 36)
    private String id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    private Booking booking;

    @Column(name = "razorpay_order_id", nullable = false, length = 100)
    private String razorpayOrderId;

    @Column(name = "razorpay_payment_id", length = 100)
    private String razorpayPaymentId;

    @Column(name = "razorpay_signature", length = 255)
    private String razorpaySignature;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    /**
     * Payment status: 'CREATED', 'SUCCESS', 'FAILED', 'REFUNDED'.
     */
    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "CREATED";

    @Column(name = "webhook_verified", nullable = false)
    @Builder.Default
    private Boolean webhookVerified = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.status == null) {
            this.status = "CREATED";
        }
        if (this.webhookVerified == null) {
            this.webhookVerified = false;
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
