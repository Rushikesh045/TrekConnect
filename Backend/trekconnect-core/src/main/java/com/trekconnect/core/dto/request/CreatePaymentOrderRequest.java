package com.trekconnect.core.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * DTO for creating a Razorpay order.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePaymentOrderRequest {

    @NotBlank(message = "Booking ID is required")
    private String bookingId;
}
