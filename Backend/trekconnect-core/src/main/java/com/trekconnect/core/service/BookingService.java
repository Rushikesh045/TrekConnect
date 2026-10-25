package com.trekconnect.core.service;

import com.trekconnect.core.dto.request.ReserveSeatRequest;
import com.trekconnect.core.dto.response.BookingResponse;
import com.trekconnect.core.entity.Booking;
import com.trekconnect.core.entity.BookingStatus;
import com.trekconnect.core.entity.Event;
import com.trekconnect.core.entity.SeatLock;
import com.trekconnect.core.entity.UserProfile;
import com.trekconnect.core.repository.BookingRepository;
import com.trekconnect.core.repository.EventRepository;
import com.trekconnect.core.repository.SeatLockRepository;
import com.trekconnect.core.repository.UserProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.OptimisticLockException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service handling seat reservations, idempotency protection, and booking cancellations.
 */
@Service
public class BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;
    private final SeatLockRepository seatLockRepository;
    private final UserProfileRepository userProfileRepository;
    private final EmailNotificationService emailNotificationService;
    private final NotificationService notificationService;

    @Autowired
    public BookingService(BookingRepository bookingRepository, 
                          EventRepository eventRepository, 
                          SeatLockRepository seatLockRepository, 
                          UserProfileRepository userProfileRepository,
                          EmailNotificationService emailNotificationService,
                          NotificationService notificationService) {
        this.bookingRepository = bookingRepository;
        this.eventRepository = eventRepository;
        this.seatLockRepository = seatLockRepository;
        this.userProfileRepository = userProfileRepository;
        this.emailNotificationService = emailNotificationService;
        this.notificationService = notificationService;
    }

    /**
     * Reserves seats for a trekker with optimistic locking and idempotency protection.
     */
    @Transactional
    @Retryable(
        retryFor = { ObjectOptimisticLockingFailureException.class, OptimisticLockException.class },
        maxAttempts = 3,
        backoff = @Backoff(delay = 100, multiplier = 2)
    )
    public BookingResponse reserveSeats(String userId, ReserveSeatRequest request) {
        logger.info("Reserving seats for user: {}, event: {}, seats: {}, idempotencyKey: {}", 
                    userId, request.getEventId(), request.getNumSeats(), request.getIdempotencyKey());

        Optional<Booking> existingBooking = bookingRepository.findByIdempotencyKey(request.getIdempotencyKey());
        if (existingBooking.isPresent()) {
            logger.info("Duplicate request with IdempotencyKey: {}. Returning booking ID: {}", 
                        request.getIdempotencyKey(), existingBooking.get().getId());
            return mapToBookingResponse(existingBooking.get());
        }

        Event event = eventRepository.findByIdWithOptimisticLock(request.getEventId())
                .orElseThrow(() -> {
                    logger.error("Event batch not found with ID: {}", request.getEventId());
                    return new IllegalArgumentException("Event batch not found with ID: " + request.getEventId());
                });

        int totalCapacity = event.getCapacityTotal() != null ? event.getCapacityTotal() : 25;
        int currentlyBooked = event.getCapacityBooked() != null ? event.getCapacityBooked() : 0;
        int seatsRequested = request.getNumSeats() != null ? request.getNumSeats() : 1;

        if (currentlyBooked + seatsRequested > totalCapacity) {
            logger.warn("Insufficient seats for event: {}. Requested: {}, Available: {}", 
                        event.getId(), seatsRequested, (totalCapacity - currentlyBooked));
            throw new IllegalStateException("Insufficient seats available. Requested: " + seatsRequested + ", Available: " + (totalCapacity - currentlyBooked));
        }

        event.setCapacityBooked(currentlyBooked + seatsRequested);
        eventRepository.save(event);

        UserProfile userProfile = userProfileRepository.findById(userId)
                .orElseGet(() -> userProfileRepository.save(UserProfile.builder().userId(userId).name("Trekker").email("trekker@trekconnect.com").role("USER").build()));

        if (userProfile.getEmail() == null || userProfile.getEmail().isBlank()) {
            userProfile.setEmail("trekker@trekconnect.com");
            userProfileRepository.save(userProfile);
        }

        BigDecimal price = event.getPrice() != null ? event.getPrice() : BigDecimal.valueOf(1850);
        BigDecimal totalAmount = price.multiply(BigDecimal.valueOf(seatsRequested));
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(10);

        SeatLock seatLock = SeatLock.builder()
                .eventId(event.getId())
                .userId(userId)
                .lockedSeats(seatsRequested)
                .lockToken(request.getIdempotencyKey())
                .expiresAt(expiresAt)
                .build();
        seatLockRepository.save(seatLock);

        Booking booking = Booking.builder()
                .event(event)
                .user(userProfile)
                .numSeats(seatsRequested)
                .totalAmount(totalAmount)
                .status(BookingStatus.PENDING_PAYMENT)
                .idempotencyKey(request.getIdempotencyKey())
                .expiresAt(expiresAt)
                .build();

        Booking savedBooking = bookingRepository.save(booking);
        logger.info("Reserved {} seat(s) for booking ID: {}, expires at: {}", seatsRequested, savedBooking.getId(), expiresAt);
        return mapToBookingResponse(savedBooking);
    }

    /**
     * Retrieves booking history for a user.
     */
    @Transactional(readOnly = true)
    public List<BookingResponse> getMyBookings(String userId) {
        logger.debug("Fetching booking history for user: {}", userId);
        List<Booking> bookings = bookingRepository.findByUserUserIdOrderByCreatedAtDesc(userId);
        return bookings.stream().map(this::mapToBookingResponse).toList();
    }

    /**
     * Cancels a booking and releases reserved seats back to the event capacity.
     */
    @Transactional
    public BookingResponse cancelBooking(String userId, String bookingId) {
        logger.info("User {} requested cancellation for booking: {}", userId, bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + bookingId));

        if (booking.getBookingStatus().canTransitionTo(BookingStatus.CANCELLED)) {
            booking.setStatus(BookingStatus.CANCELLED);
            
            Event event = booking.getEvent();
            if (event != null && event.getCapacityBooked() != null) {
                int newBooked = Math.max(0, event.getCapacityBooked() - (booking.getNumSeats() != null ? booking.getNumSeats() : 1));
                event.setCapacityBooked(newBooked);
                eventRepository.save(event);
                logger.info("Released {} seats for event: {}. New booked total: {}", booking.getNumSeats(), event.getId(), newBooked);
            }

            bookingRepository.save(booking);

            // Send in-app notification & cancellation email
            if (booking.getUser() != null) {
                String trekName = (event != null && event.getTrek() != null) ? event.getTrek().getName() : "Expedition";
                if (booking.getUser().getUserId() != null) {
                    notificationService.createNotification(
                            booking.getUser().getUserId(),
                            "Booking Cancelled",
                            "Your booking (" + booking.getId() + ") for " + trekName + " has been cancelled.",
                            "BOOKING_CANCELLED"
                    );
                }
                if (booking.getUser().getEmail() != null) {
                    emailNotificationService.sendBookingCancellation(
                            booking.getUser().getEmail(),
                            booking.getUser().getName(),
                            trekName,
                            booking.getId()
                    );
                }
            }
        }

        return mapToBookingResponse(booking);
    }

    private BookingResponse mapToBookingResponse(Booking booking) {
        Event event = booking.getEvent();
        String trekName = (event != null && event.getTrek() != null) ? event.getTrek().getName() : "Sahyadri Trek";
        String region = (event != null && event.getTrek() != null) ? event.getTrek().getRegion() : "Maharashtra";

        return BookingResponse.builder()
                .id(booking.getId())
                .eventId(event != null ? event.getId() : "N/A")
                .eventTitle(event != null ? event.getTitle() : "Trek Expedition")
                .trekName(trekName)
                .region(region)
                .eventDate(event != null ? event.getEventDate() : null)
                .userId(booking.getUser() != null ? booking.getUser().getUserId() : "N/A")
                .userName(booking.getUser() != null ? booking.getUser().getName() : "Trekker")
                .numSeats(booking.getNumSeats())
                .totalAmount(booking.getTotalAmount())
                .status(booking.getStatus())
                .idempotencyKey(booking.getIdempotencyKey())
                .expiresAt(booking.getExpiresAt())
                .createdAt(booking.getCreatedAt())
                .build();
    }
}

