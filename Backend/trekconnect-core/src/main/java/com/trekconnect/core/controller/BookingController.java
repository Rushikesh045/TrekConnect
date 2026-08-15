package com.trekconnect.core.controller;

import com.trekconnect.core.dto.request.ReserveSeatRequest;
import com.trekconnect.core.dto.response.BookingResponse;
import com.trekconnect.core.service.BookingService;
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

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
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

        BookingResponse response = bookingService.reserveSeats(userId, request);
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
}
