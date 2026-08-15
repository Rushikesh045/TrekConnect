package com.trekconnect.core.dto.request;

import jakarta.validation.constraints.Size;

/**
 * Request DTO for requesting booking cancellation.
 */
public class CancelBookingRequest {

    @Size(max = 500, message = "Reason must be less than 500 characters")
    private String reason;

    public CancelBookingRequest() {
    }

    public CancelBookingRequest(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
