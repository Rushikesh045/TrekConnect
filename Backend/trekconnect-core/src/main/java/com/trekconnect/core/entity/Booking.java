package com.trekconnect.core.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing a Trek Event Booking stored in main_db.
 * 
 * WHY THIS ENTITY WAS CREATED:
 * When a trekker reserves seats for an event, a Booking record is created in 'PENDING' status.
 * It contains an 'idempotencyKey' header value to guarantee duplicate booking prevention on network retry.
 */
@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @Column(length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserProfile user;

    @Column(name = "num_seats", nullable = false)
    @Builder.Default
    private Integer numSeats = 1;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    /**
     * Booking status: 'PENDING', 'CONFIRMED', 'CANCELLED', or 'REFUNDED'.
     */
    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "PENDING";

    /**
     * Idempotency Key header value ensuring idempotent booking creations on retries.
     */
    @Column(name = "idempotency_key", nullable = false, unique = true, length = 100)
    private String idempotencyKey;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.numSeats == null) {
            this.numSeats = 1;
        }
        if (this.status == null) {
            this.status = "PENDING";
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
