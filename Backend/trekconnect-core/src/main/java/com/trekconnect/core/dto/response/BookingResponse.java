package com.trekconnect.core.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO returning Booking details and lock expiration countdown timer.
 */
public class BookingResponse {

    private String id;
    private String eventId;
    private String eventTitle;
    private String trekName;
    private String region;
    private LocalDate eventDate;
    private String userId;
    private String userName;
    private Integer numSeats;
    private BigDecimal totalAmount;
    private String status;
    private String idempotencyKey;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;

    public BookingResponse() {
    }

    public BookingResponse(String id, String eventId, String eventTitle, String trekName, String region, LocalDate eventDate, String userId, String userName, Integer numSeats, BigDecimal totalAmount, String status, String idempotencyKey, LocalDateTime expiresAt, LocalDateTime createdAt) {
        this.id = id;
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.trekName = trekName;
        this.region = region;
        this.eventDate = eventDate;
        this.userId = userId;
        this.userName = userName;
        this.numSeats = numSeats;
        this.totalAmount = totalAmount;
        this.status = status;
        this.idempotencyKey = idempotencyKey;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
    }

    public static BookingResponseBuilder builder() {
        return new BookingResponseBuilder();
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

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getTrekName() {
        return trekName;
    }

    public void setTrekName(String trekName) {
        this.trekName = trekName;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public static class BookingResponseBuilder {
        private String id;
        private String eventId;
        private String eventTitle;
        private String trekName;
        private String region;
        private LocalDate eventDate;
        private String userId;
        private String userName;
        private Integer numSeats;
        private BigDecimal totalAmount;
        private String status;
        private String idempotencyKey;
        private LocalDateTime expiresAt;
        private LocalDateTime createdAt;

        public BookingResponseBuilder id(String id) {
            this.id = id;
            return this;
        }

        public BookingResponseBuilder eventId(String eventId) {
            this.eventId = eventId;
            return this;
        }

        public BookingResponseBuilder eventTitle(String eventTitle) {
            this.eventTitle = eventTitle;
            return this;
        }

        public BookingResponseBuilder trekName(String trekName) {
            this.trekName = trekName;
            return this;
        }

        public BookingResponseBuilder region(String region) {
            this.region = region;
            return this;
        }

        public BookingResponseBuilder eventDate(LocalDate eventDate) {
            this.eventDate = eventDate;
            return this;
        }

        public BookingResponseBuilder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public BookingResponseBuilder userName(String userName) {
            this.userName = userName;
            return this;
        }

        public BookingResponseBuilder numSeats(Integer numSeats) {
            this.numSeats = numSeats;
            return this;
        }

        public BookingResponseBuilder totalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }

        public BookingResponseBuilder status(String status) {
            this.status = status;
            return this;
        }

        public BookingResponseBuilder idempotencyKey(String idempotencyKey) {
            this.idempotencyKey = idempotencyKey;
            return this;
        }

        public BookingResponseBuilder expiresAt(LocalDateTime expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }

        public BookingResponseBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public BookingResponse build() {
            return new BookingResponse(id, eventId, eventTitle, trekName, region, eventDate, userId, userName, numSeats, totalAmount, status, idempotencyKey, expiresAt, createdAt);
        }
    }
}
