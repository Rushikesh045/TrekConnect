package com.trekconnect.core.service;

import com.trekconnect.core.dto.request.ReserveSeatRequest;
import com.trekconnect.core.dto.response.BookingResponse;
import com.trekconnect.core.entity.Booking;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service handling high-concurrency seat reservations, idempotency protection, and booking cancellations.
 * 
 * WHY THIS SERVICE WAS CREATED:
 * This class contains the core booking engine logic. When multiple trekkers try to book seats simultaneously,
 * it uses database-level pessimistic locking (@Lock PESSIMISTIC_WRITE) to ensure zero overbooking race conditions.
 * It also checks idempotency keys so network retries never double-charge or double-book seats.
 */
@Service
public class BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;
    private final SeatLockRepository seatLockRepository;
    private final UserProfileRepository userProfileRepository;

    @Autowired
    public BookingService(BookingRepository bookingRepository, 
                          EventRepository eventRepository, 
                          SeatLockRepository seatLockRepository, 
                          UserProfileRepository userProfileRepository) {
        this.bookingRepository = bookingRepository;
        this.eventRepository = eventRepository;
        this.seatLockRepository = seatLockRepository;
        this.userProfileRepository = userProfileRepository;
    }

    /**
     * Reserves seats for a trekker with pessimistic locking and idempotency protection.
     * 
     * @param userId Logged in trekker's ID.
     * @param request Booking reservation details (eventId, numSeats, idempotencyKey).
     * @return BookingResponse containing booking ID, total price, and 10-minute lock expiration timer.
     */
    @Transactional
    public BookingResponse reserveSeats(String userId, ReserveSeatRequest request) {
        logger.info("Processing seat reservation for User: {}, Event: {}, Seats: {}, IdempotencyKey: {}", 
                    userId, request.getEventId(), request.getNumSeats(), request.getIdempotencyKey());

        // Step 1: Idempotency Key Verification
        // If the user already clicked "Reserve" or retried a request with the same idempotency key,
        // return the existing booking without re-deducting seats or charging money.
        Optional<Booking> existingBooking = bookingRepository.findByIdempotencyKey(request.getIdempotencyKey());
        if (existingBooking.isPresent()) {
            logger.info("Duplicate request detected with IdempotencyKey: {}. Returning existing booking ID: {}", 
                        request.getIdempotencyKey(), existingBooking.get().getId());
            return mapToBookingResponse(existingBooking.get());
        }

        // Step 2: Acquire Database-level Pessimistic Write Lock on the Event entity
        // SELECT FOR UPDATE locks the database row so no other user can alter capacity at the exact same millisecond.
        Event event = eventRepository.findByIdWithPessimisticLock(request.getEventId())
                .orElseThrow(() -> {
                    logger.error("Event batch not found with ID: {}", request.getEventId());
                    return new IllegalArgumentException("Event batch not found with ID: " + request.getEventId());
                });

        int totalCapacity = event.getCapacityTotal() != null ? event.getCapacityTotal() : 25;
        int currentlyBooked = event.getCapacityBooked() != null ? event.getCapacityBooked() : 0;
        int seatsRequested = request.getNumSeats() != null ? request.getNumSeats() : 1;

        // Step 3: Check if enough seats are left in this trek batch
        if (currentlyBooked + seatsRequested > totalCapacity) {
            logger.warn("Booking failed due to insufficient seats for Event: {}. Requested: {}, Available: {}", 
                        event.getId(), seatsRequested, (totalCapacity - currentlyBooked));
            throw new IllegalStateException("Insufficient seats available. Requested: " + seatsRequested + ", Available: " + (totalCapacity - currentlyBooked));
        }

        // Step 4: Safely update capacityBooked count
        event.setCapacityBooked(currentlyBooked + seatsRequested);
        eventRepository.save(event);

        // Step 5: Get or create UserProfile fallback
        UserProfile userProfile = userProfileRepository.findById(userId)
                .orElseGet(() -> userProfileRepository.save(UserProfile.builder().userId(userId).name("Trekker").role("USER").build()));

        BigDecimal price = event.getPrice() != null ? event.getPrice() : BigDecimal.valueOf(1850);
        BigDecimal totalAmount = price.multiply(BigDecimal.valueOf(seatsRequested));
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(10);

        // Step 6: Create 10-Minute Temporary Seat Lock
        SeatLock seatLock = SeatLock.builder()
                .eventId(event.getId())
                .userId(userId)
                .lockedSeats(seatsRequested)
                .lockToken(request.getIdempotencyKey())
                .expiresAt(expiresAt)
                .build();
        seatLockRepository.save(seatLock);

        // Step 7: Create Booking record in PENDING_PAYMENT status
        Booking booking = Booking.builder()
                .event(event)
                .user(userProfile)
                .numSeats(seatsRequested)
                .totalAmount(totalAmount)
                .status("PENDING_PAYMENT")
                .idempotencyKey(request.getIdempotencyKey())
                .expiresAt(expiresAt)
                .build();

        Booking savedBooking = bookingRepository.save(booking);
        logger.info("Successfully reserved {} seat(s) for Booking ID: {}, expires at: {}", seatsRequested, savedBooking.getId(), expiresAt);
        return mapToBookingResponse(savedBooking);
    }

    /**
     * Retrieves all bookings for the logged-in trekker sorted by creation date.
     */
    @Transactional(readOnly = true)
    public List<BookingResponse> getMyBookings(String userId) {
        logger.debug("Fetching booking history for User: {}", userId);
        List<Booking> bookings = bookingRepository.findByUserUserIdOrderByCreatedAtDesc(userId);
        return bookings.stream().map(this::mapToBookingResponse).toList();
    }

    /**
     * Cancels an existing booking and releases the reserved seats back to the event capacity.
     */
    @Transactional
    public BookingResponse cancelBooking(String userId, String bookingId) {
        logger.info("User: {} requested cancellation for Booking ID: {}", userId, bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + bookingId));

        if (!"CANCELLED".equals(booking.getStatus())) {
            booking.setStatus("CANCELLED");
            
            // Release reserved seats back to the Event
            Event event = booking.getEvent();
            if (event != null && event.getCapacityBooked() != null) {
                int newBooked = Math.max(0, event.getCapacityBooked() - (booking.getNumSeats() != null ? booking.getNumSeats() : 1));
                event.setCapacityBooked(newBooked);
                eventRepository.save(event);
                logger.info("Released {} seats back to Event ID: {}. New booked total: {}", booking.getNumSeats(), event.getId(), newBooked);
            }

            bookingRepository.save(booking);
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
