package com.trekconnect.core.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing a Temporary Seat Reservation Lock stored in main_db.
 * 
 * WHY THIS ENTITY WAS CREATED:
 * Temporarily locks event seats for 10 minutes while the user completes payment checkout.
 * If payment is not completed within 10 minutes, seats are automatically unlocked and returned to capacity.
 */
@Entity
@Table(name = "seat_locks")
public class SeatLock {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "event_id", nullable = false, length = 36)
    private String eventId;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(name = "locked_seats", nullable = false)
    private Integer lockedSeats;

    @Column(name = "lock_token", nullable = false, length = 100)
    private String lockToken;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public SeatLock() {
    }

    public SeatLock(String id, String eventId, String userId, Integer lockedSeats, String lockToken, LocalDateTime expiresAt, LocalDateTime createdAt) {
        this.id = id;
        this.eventId = eventId;
        this.userId = userId;
        this.lockedSeats = lockedSeats;
        this.lockToken = lockToken;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
    }

    public static SeatLockBuilder builder() {
        return new SeatLockBuilder();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getLockedSeats() {
        return lockedSeats;
    }

    public void setLockedSeats(Integer lockedSeats) {
        this.lockedSeats = lockedSeats;
    }

    public String getLockToken() {
        return lockToken;
    }

    public void setLockToken(String lockToken) {
        this.lockToken = lockToken;
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
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.expiresAt == null) {
            this.expiresAt = this.createdAt.plusMinutes(10);
        }
    }

    public static class SeatLockBuilder {
        private String id;
        private String eventId;
        private String userId;
        private Integer lockedSeats;
        private String lockToken;
        private LocalDateTime expiresAt;
        private LocalDateTime createdAt;

        public SeatLockBuilder id(String id) {
            this.id = id;
            return this;
        }

        public SeatLockBuilder eventId(String eventId) {
            this.eventId = eventId;
            return this;
        }

        public SeatLockBuilder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public SeatLockBuilder lockedSeats(Integer lockedSeats) {
            this.lockedSeats = lockedSeats;
            return this;
        }

        public SeatLockBuilder lockToken(String lockToken) {
            this.lockToken = lockToken;
            return this;
        }

        public SeatLockBuilder expiresAt(LocalDateTime expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }

        public SeatLockBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public SeatLock build() {
            return new SeatLock(id, eventId, userId, lockedSeats, lockToken, expiresAt, createdAt);
        }
    }
}
