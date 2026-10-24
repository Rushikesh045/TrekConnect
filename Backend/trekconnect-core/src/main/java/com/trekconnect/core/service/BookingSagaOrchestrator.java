package com.trekconnect.core.service;

import com.trekconnect.core.dto.request.ReserveSeatRequest;
import com.trekconnect.core.dto.response.BookingResponse;
import com.trekconnect.core.entity.Booking;
import com.trekconnect.core.entity.BookingStatus;
import com.trekconnect.core.entity.Event;
import com.trekconnect.core.entity.OutboxEvent;
import com.trekconnect.core.repository.BookingRepository;
import com.trekconnect.core.repository.EventRepository;
import com.trekconnect.core.repository.OutboxEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Orchestrates booking transactions, status changes, outbox events, and compensating seat releases.
 */
@Service
public class BookingSagaOrchestrator {

    private static final Logger logger = LoggerFactory.getLogger(BookingSagaOrchestrator.class);

    private final BookingService bookingService;
    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;
    private final RedisLockService redisLockService;
    private final OutboxEventRepository outboxEventRepository;

    @Autowired
    public BookingSagaOrchestrator(BookingService bookingService,
                                  BookingRepository bookingRepository,
                                  EventRepository eventRepository,
                                  RedisLockService redisLockService,
                                  OutboxEventRepository outboxEventRepository) {
        this.bookingService = bookingService;
        this.bookingRepository = bookingRepository;
        this.eventRepository = eventRepository;
        this.redisLockService = redisLockService;
        this.outboxEventRepository = outboxEventRepository;
    }

    /**
     * Executes initial seat reservation under a Redis distributed lock.
     */
    @Transactional
    @CacheEvict(value = "public_events", allEntries = true)
    public BookingResponse executeBookingSaga(String userId, ReserveSeatRequest request) {
        String lockKey = "lock:event:" + request.getEventId();
        String lockToken = redisLockService.acquireLock(lockKey, 10);

        try {
            logger.info("Executing booking reservation for user: {}, event: {}", userId, request.getEventId());
            BookingResponse response = bookingService.reserveSeats(userId, request);

            createOutboxEvent("Booking", response.getId(), "BOOKING_RESERVED",
                    String.format("{\"bookingId\":\"%s\",\"eventId\":\"%s\",\"userId\":\"%s\",\"numSeats\":%d,\"status\":\"PENDING_PAYMENT\"}",
                            response.getId(), response.getEventId(), userId, response.getNumSeats()));

            return response;
        } finally {
            if (lockToken != null) {
                redisLockService.releaseLock(lockKey, lockToken);
            }
        }
    }

    /**
     * Handles payment success callback and confirms booking.
     */
    @Transactional
    public void handlePaymentSuccess(String bookingId, String paymentId) {
        logger.info("Payment successful for booking: {}, paymentId: {}", bookingId, paymentId);

        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null) {
            logger.error("Booking not found for ID: {}", bookingId);
            return;
        }

        BookingStatus currentStatus = booking.getBookingStatus();
        if (currentStatus.canTransitionTo(BookingStatus.CONFIRMED)) {
            booking.setStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);

            createOutboxEvent("Booking", booking.getId(), "BOOKING_CONFIRMED",
                    String.format("{\"bookingId\":\"%s\",\"paymentId\":\"%s\",\"status\":\"CONFIRMED\"}",
                            booking.getId(), paymentId));

            logger.info("Booking confirmed: {}", booking.getId());
        } else {
            logger.warn("Invalid transition: {} -> CONFIRMED for booking: {}", currentStatus, bookingId);
        }
    }

    /**
     * Handles payment failure callback and releases reserved seats.
     */
    @Transactional
    public void handlePaymentFailure(String bookingId, String reason) {
        logger.info("Payment failed for booking: {}. Reason: {}", bookingId, reason);

        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null) {
            return;
        }

        BookingStatus currentStatus = booking.getBookingStatus();
        if (currentStatus.canTransitionTo(BookingStatus.PAYMENT_FAILED)) {
            booking.setStatus(BookingStatus.PAYMENT_FAILED);

            releaseSeatsCompensatingAction(booking);

            bookingRepository.save(booking);

            createOutboxEvent("Booking", booking.getId(), "BOOKING_FAILED",
                    String.format("{\"bookingId\":\"%s\",\"reason\":\"%s\",\"status\":\"PAYMENT_FAILED\"}",
                            booking.getId(), reason));

            logger.info("Released seats for failed booking: {}", booking.getId());
        }
    }

    /**
     * Background worker that releases expired pending bookings.
     */
    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void processExpiredBookingsSaga() {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> expiredBookings = bookingRepository.findByStatusAndExpiresAtBefore(BookingStatus.PENDING_PAYMENT, now);

        if (expiredBookings.isEmpty()) {
            return;
        }

        logger.info("Found {} expired pending booking(s)", expiredBookings.size());

        for (Booking booking : expiredBookings) {
            if (booking.getBookingStatus().canTransitionTo(BookingStatus.EXPIRED)) {
                booking.setStatus(BookingStatus.EXPIRED);

                releaseSeatsCompensatingAction(booking);

                bookingRepository.save(booking);

                createOutboxEvent("Booking", booking.getId(), "BOOKING_EXPIRED",
                        String.format("{\"bookingId\":\"%s\",\"status\":\"EXPIRED\"}", booking.getId()));

                logger.info("Released {} seats for expired booking: {}", booking.getNumSeats(), booking.getId());
            }
        }
    }

    /**
     * Fetches recent outbox events for audit or debugging.
     */
    @Transactional(readOnly = true)
    public List<OutboxEvent> getRecentOutboxEvents() {
        return outboxEventRepository.findTop50ByOrderByCreatedAtDesc();
    }

    private void releaseSeatsCompensatingAction(Booking booking) {
        Event event = booking.getEvent();
        if (event != null && event.getCapacityBooked() != null) {
            int seatsToRelease = booking.getNumSeats() != null ? booking.getNumSeats() : 1;
            int newBooked = Math.max(0, event.getCapacityBooked() - seatsToRelease);
            event.setCapacityBooked(newBooked);
            eventRepository.save(event);
            logger.info("Released {} seat(s) for event: {}. New booked total: {}", seatsToRelease, event.getId(), newBooked);
        }
    }

    private void createOutboxEvent(String aggregateType, String aggregateId, String eventType, String payload) {
        OutboxEvent outboxEvent = OutboxEvent.builder()
                .aggregateType(aggregateType)
                .aggregateId(aggregateId)
                .eventType(eventType)
                .payload(payload)
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();
        outboxEventRepository.save(outboxEvent);
    }
}

