package com.trekconnect.core.entity;

/**
 * Lifecycle status enum for trek bookings.
 */
public enum BookingStatus {

    PENDING_PAYMENT,
    CONFIRMED,
    PAYMENT_FAILED,
    CANCELLED,
    EXPIRED,
    REFUNDED;

    /**
     * Checks if transitioning from the current status to targetStatus is allowed.
     */
    public boolean canTransitionTo(BookingStatus targetStatus) {
        if (targetStatus == null) {
            return false;
        }
        return switch (this) {
            case PENDING_PAYMENT -> targetStatus == CONFIRMED 
                                 || targetStatus == PAYMENT_FAILED 
                                 || targetStatus == CANCELLED 
                                 || targetStatus == EXPIRED;
            case CONFIRMED -> targetStatus == CANCELLED || targetStatus == REFUNDED;
            case PAYMENT_FAILED, CANCELLED, EXPIRED, REFUNDED -> false;
        };
    }
}

