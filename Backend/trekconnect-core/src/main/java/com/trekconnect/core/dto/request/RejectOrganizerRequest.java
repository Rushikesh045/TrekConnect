package com.trekconnect.core.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO containing admin rejection reason for organizer verification.
 */
public class RejectOrganizerRequest {

    @NotBlank(message = "Rejection reason is required")
    @Size(min = 5, max = 500, message = "Rejection reason must be between 5 and 500 characters")
    private String rejectionReason;

    public RejectOrganizerRequest() {
    }

    public RejectOrganizerRequest(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}
