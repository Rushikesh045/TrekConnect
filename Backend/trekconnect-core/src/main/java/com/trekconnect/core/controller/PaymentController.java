package com.trekconnect.core.controller;

import com.trekconnect.core.dto.request.CreatePaymentOrderRequest;
import com.trekconnect.core.dto.request.VerifyPaymentRequest;
import com.trekconnect.core.dto.response.PaymentOrderResponse;
import com.trekconnect.core.service.PaymentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller exposing Phase 6 Razorpay Payment Endpoints.
 * 
 * WHY THIS CONTROLLER WAS CREATED:
 * Provides APIs for generating Razorpay Order IDs, verifying HMAC signatures, and receiving webhooks.
 */
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Creates a Razorpay Order ID for a reserved booking.
     */
    @PostMapping("/razorpay/create-order")
    public ResponseEntity<PaymentOrderResponse> createOrder(@AuthenticationPrincipal String userId,
                                                           @Valid @RequestBody CreatePaymentOrderRequest request) {
        String effectiveUser = userId != null ? userId : "usr-1";
        logger.info("REST Request: POST /api/payments/razorpay/create-order for User: {}", effectiveUser);
        PaymentOrderResponse response = paymentService.createPaymentOrder(effectiveUser, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Verifies Razorpay HMAC SHA256 payment signature after successful checkout.
     */
    @PostMapping("/razorpay/verify-signature")
    public ResponseEntity<PaymentOrderResponse> verifySignature(@Valid @RequestBody VerifyPaymentRequest request) {
        logger.info("REST Request: POST /api/payments/razorpay/verify-signature for Order: {}", request.getRazorpayOrderId());
        PaymentOrderResponse response = paymentService.verifyPaymentSignature(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Razorpay Webhook Endpoint for automated payment capture notifications.
     */
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload,
                                                @RequestHeader(value = "X-Razorpay-Signature", required = false) String signature) {
        logger.info("REST Request: POST /api/payments/webhook received");
        String result = paymentService.processWebhook(payload, signature);
        return ResponseEntity.ok(result);
    }
}
