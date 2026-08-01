package com.trekconnect.core.dto.response;

import java.time.LocalDateTime;

/**
 * Response DTO returning booking dispute details for Admin oversight.
 */
public class DisputeResponse {

    private String id;
    private String paymentId;
    private String requestedByUserId;
    private String requestedByUserName;
    private String reason;
    private String status;
    private LocalDateTime createdAt;

    public DisputeResponse() {
    }

    public DisputeResponse(String id, String paymentId, String requestedByUserId, String requestedByUserName, String reason, String status, LocalDateTime createdAt) {
        this.id = id;
        this.paymentId = paymentId;
        this.requestedByUserId = requestedByUserId;
        this.requestedByUserName = requestedByUserName;
        this.reason = reason;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static DisputeResponseBuilder builder() {
        return new DisputeResponseBuilder();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getRequestedByUserId() {
        return requestedByUserId;
    }

    public void setRequestedByUserId(String requestedByUserId) {
        this.requestedByUserId = requestedByUserId;
    }

    public String getRequestedByUserName() {
        return requestedByUserName;
    }

    public void setRequestedByUserName(String requestedByUserName) {
        this.requestedByUserName = requestedByUserName;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static class DisputeResponseBuilder {
        private String id;
        private String paymentId;
        private String requestedByUserId;
        private String requestedByUserName;
        private String reason;
        private String status;
        private LocalDateTime createdAt;

        public DisputeResponseBuilder id(String id) {
            this.id = id;
            return this;
        }

        public DisputeResponseBuilder paymentId(String paymentId) {
            this.paymentId = paymentId;
            return this;
        }

        public DisputeResponseBuilder requestedByUserId(String requestedByUserId) {
            this.requestedByUserId = requestedByUserId;
            return this;
        }

        public DisputeResponseBuilder requestedByUserName(String requestedByUserName) {
            this.requestedByUserName = requestedByUserName;
            return this;
        }

        public DisputeResponseBuilder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public DisputeResponseBuilder status(String status) {
            this.status = status;
            return this;
        }

        public DisputeResponseBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public DisputeResponse build() {
            return new DisputeResponse(id, paymentId, requestedByUserId, requestedByUserName, reason, status, createdAt);
        }
    }
}
