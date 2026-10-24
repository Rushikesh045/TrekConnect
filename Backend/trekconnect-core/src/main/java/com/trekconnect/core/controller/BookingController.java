package com.trekconnect.core.controller;

import com.trekconnect.core.dto.request.ReserveSeatRequest;
import com.trekconnect.core.dto.response.BookingResponse;
import com.trekconnect.core.entity.OutboxEvent;
import com.trekconnect.core.service.BookingService;
import com.trekconnect.core.service.BookingSagaOrchestrator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller exposing high-concurrency seat reservation and booking management endpoints.
 */
@RestController
@RequestMapping("/api/bookings")
@PreAuthorize("hasAnyRole('USER', 'ORGANIZER', 'ADMIN')")
public class BookingController {

    private final BookingService bookingService;
    private final BookingSagaOrchestrator sagaOrchestrator;

    @Autowired
    public BookingController(BookingService bookingService, BookingSagaOrchestrator sagaOrchestrator) {
        this.bookingService = bookingService;
        this.sagaOrchestrator = sagaOrchestrator;
    }

    @PostMapping("/reserve")
    public ResponseEntity<BookingResponse> reserveSeats(
            Authentication authentication,
            @RequestHeader(value = "X-Idempotency-Key", required = false) String idempotencyHeader,
            @Valid @RequestBody ReserveSeatRequest request) {
        String userId = (String) authentication.getPrincipal();

        // Use header idempotency key if provided
        if (idempotencyHeader != null && !idempotencyHeader.isBlank()) {
            request.setIdempotencyKey(idempotencyHeader);
        }

        BookingResponse response = sagaOrchestrator.executeBookingSaga(userId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-bookings")
    public ResponseEntity<List<BookingResponse>> getMyBookings(Authentication authentication) {
        String userId = (String) authentication.getPrincipal();
        List<BookingResponse> response = bookingService.getMyBookings(userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(
            Authentication authentication,
            @PathVariable("id") String id) {
        String userId = (String) authentication.getPrincipal();
        BookingResponse response = bookingService.cancelBooking(userId, id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/saga/outbox")
    public ResponseEntity<List<OutboxEvent>> getOutboxEvents() {
        return ResponseEntity.ok(sagaOrchestrator.getRecentOutboxEvents());
    }

    @PostMapping("/saga/simulate-failure/{id}")
    public ResponseEntity<String> simulatePaymentFailure(@PathVariable("id") String bookingId) {
        sagaOrchestrator.handlePaymentFailure(bookingId, "Simulated Payment Failure Test");
        return ResponseEntity.ok("Compensating transaction executed for booking ID: " + bookingId);
    }

    @PostMapping("/saga/simulate-success/{id}")
    public ResponseEntity<String> simulatePaymentSuccess(@PathVariable("id") String bookingId) {
        sagaOrchestrator.handlePaymentSuccess(bookingId, "sim_pay_" + System.currentTimeMillis());
        return ResponseEntity.ok("Saga transition to CONFIRMED executed for booking ID: " + bookingId);
    }
}
