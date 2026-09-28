package com.trekconnect.core.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO Response containing Razorpay order credentials and status.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentOrderResponse {

    private String paymentOrderId;
    private String bookingId;
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String keyId;
    private BigDecimal amount;
    private String currency;
    private String status;
    private String refundId;
    private LocalDateTime createdAt;
}
