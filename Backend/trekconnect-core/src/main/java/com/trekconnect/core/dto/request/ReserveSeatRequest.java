package com.trekconnect.core.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for requesting a temporary seat reservation lock.
 */
public class ReserveSeatRequest {

    @NotBlank(message = "Event ID is required")
    private String eventId;

    @NotNull(message = "Number of seats is required")
    @Min(value = 1, message = "Must reserve at least 1 seat")
    private Integer numSeats;

    @NotBlank(message = "Idempotency key is required")
    private String idempotencyKey;

    public ReserveSeatRequest() {
    }

    public ReserveSeatRequest(String eventId, Integer numSeats, String idempotencyKey) {
        this.eventId = eventId;
        this.numSeats = numSeats;
        this.idempotencyKey = idempotencyKey;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Integer getNumSeats() {
        return numSeats;
    }

    public void setNumSeats(Integer numSeats) {
        this.numSeats = numSeats;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }
}
