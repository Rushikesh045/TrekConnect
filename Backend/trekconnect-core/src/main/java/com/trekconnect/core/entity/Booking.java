package com.trekconnect.core.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing a Trek Event Booking stored in main_db.
 * 
 * WHY THIS ENTITY WAS CREATED:
 * When a trekker reserves seats for an event, a Booking record is created in 'PENDING_PAYMENT' status.
 * It contains an 'idempotencyKey' header value to guarantee duplicate booking prevention on network retry
 * and an 'expiresAt' timestamp for 10-minute temporary seat reservation locks.
 */
@Entity
@Table(name = "bookings")
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
    private Integer numSeats = 1;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    /**
     * Booking status: 'PENDING_PAYMENT', 'CONFIRMED', 'CANCELLED', 'EXPIRED', or 'REFUNDED'.
     */
    @Column(nullable = false, length = 20)
    private String status = "PENDING_PAYMENT";

    /**
     * Idempotency Key header value ensuring idempotent booking creations on retries.
     */
    @Column(name = "idempotency_key", nullable = false, unique = true, length = 100)
    private String idempotencyKey;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Booking() {
    }

    public Booking(String id, Event event, UserProfile user, Integer numSeats, BigDecimal totalAmount, String status, String idempotencyKey, LocalDateTime expiresAt, LocalDateTime createdAt) {
        this.id = id;
        this.event = event;
        this.user = user;
        this.numSeats = numSeats != null ? numSeats : 1;
        this.totalAmount = totalAmount;
        this.status = status != null ? status : "PENDING_PAYMENT";
        this.idempotencyKey = idempotencyKey;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
    }

    public static BookingBuilder builder() {
        return new BookingBuilder();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public UserProfile getUser() {
        return user;
    }

    public void setUser(UserProfile user) {
        this.user = user;
    }

    public Integer getNumSeats() {
        return numSeats;
    }

    public void setNumSeats(Integer numSeats) {
        this.numSeats = numSeats;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
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
        if (this.numSeats == null) {
            this.numSeats = 1;
        }
        if (this.status == null) {
            this.status = "PENDING_PAYMENT";
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.expiresAt == null && "PENDING_PAYMENT".equals(this.status)) {
            this.expiresAt = this.createdAt.plusMinutes(10);
        }
    }

    public static class BookingBuilder {
        private String id;
        private Event event;
        private UserProfile user;
        private Integer numSeats = 1;
        private BigDecimal totalAmount;
        private String status = "PENDING_PAYMENT";
        private String idempotencyKey;
        private LocalDateTime expiresAt;
        private LocalDateTime createdAt;

        public BookingBuilder id(String id) {
            this.id = id;
            return this;
        }

        public BookingBuilder event(Event event) {
            this.event = event;
            return this;
        }

        public BookingBuilder user(UserProfile user) {
            this.user = user;
            return this;
        }

        public BookingBuilder numSeats(Integer numSeats) {
            this.numSeats = numSeats;
            return this;
        }

        public BookingBuilder totalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }

        public BookingBuilder status(String status) {
            this.status = status;
            return this;
        }

        public BookingBuilder idempotencyKey(String idempotencyKey) {
            this.idempotencyKey = idempotencyKey;
            return this;
        }

        public BookingBuilder expiresAt(LocalDateTime expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }

        public BookingBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Booking build() {
            return new Booking(id, event, user, numSeats, totalAmount, status, idempotencyKey, expiresAt, createdAt);
        }
    }
}
