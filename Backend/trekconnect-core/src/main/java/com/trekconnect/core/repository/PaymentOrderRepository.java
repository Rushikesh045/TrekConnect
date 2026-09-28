package com.trekconnect.core.repository;

import com.trekconnect.core.entity.PaymentOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA Repository for PaymentOrder entities.
 */
@Repository
public interface PaymentOrderRepository extends JpaRepository<PaymentOrder, String> {
    
    /**
     * Find payment order by Razorpay Order ID.
     */
    Optional<PaymentOrder> findByRazorpayOrderId(String razorpayOrderId);

    /**
     * Find payment order by Booking ID.
     */
    Optional<PaymentOrder> findByBookingId(String bookingId);
}
