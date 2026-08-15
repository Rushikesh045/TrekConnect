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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service managing high-concurrency seat reservation locks, idempotency verification, and booking cancellations.
 */
@Service
public class BookingService {

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

    @Transactional
    public BookingResponse reserveSeats(String userId, ReserveSeatRequest request) {
        // Step 1: Idempotency Key Check
        Optional<Booking> existingBooking = bookingRepository.findByIdempotencyKey(request.getIdempotencyKey());
        if (existingBooking.isPresent()) {
            return mapToBookingResponse(existingBooking.get());
        }

        // Step 2: Pessimistic Write Lock on Event to prevent concurrent overbooking
        Event event = eventRepository.findByIdWithPessimisticLock(request.getEventId())
                .orElseThrow(() -> new IllegalArgumentException("Event batch not found with ID: " + request.getEventId()));

        int totalCapacity = event.getCapacityTotal() != null ? event.getCapacityTotal() : 25;
        int currentlyBooked = event.getCapacityBooked() != null ? event.getCapacityBooked() : 0;
        int seatsRequested = request.getNumSeats() != null ? request.getNumSeats() : 1;

        if (currentlyBooked + seatsRequested > totalCapacity) {
            throw new IllegalStateException("Insufficient seats available. Requested: " + seatsRequested + ", Available: " + (totalCapacity - currentlyBooked));
        }

        // Step 3: Increment booked seats and save Event
        event.setCapacityBooked(currentlyBooked + seatsRequested);
        eventRepository.save(event);

        // Step 4: Create UserProfile fallback if missing
        UserProfile userProfile = userProfileRepository.findById(userId)
                .orElseGet(() -> userProfileRepository.save(UserProfile.builder().userId(userId).name("Trekker").role("USER").build()));

        BigDecimal price = event.getPrice() != null ? event.getPrice() : BigDecimal.valueOf(1850);
        BigDecimal totalAmount = price.multiply(BigDecimal.valueOf(seatsRequested));
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(10);

        // Step 5: Save 10-Minute Temporary SeatLock
        SeatLock seatLock = SeatLock.builder()
                .eventId(event.getId())
                .userId(userId)
                .lockedSeats(seatsRequested)
                .lockToken(request.getIdempotencyKey())
                .expiresAt(expiresAt)
                .build();
        seatLockRepository.save(seatLock);

        // Step 6: Create Booking record in PENDING_PAYMENT status
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
        return mapToBookingResponse(savedBooking);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getMyBookings(String userId) {
        List<Booking> bookings = bookingRepository.findByUserUserIdOrderByCreatedAtDesc(userId);
        return bookings.stream().map(this::mapToBookingResponse).toList();
    }

    @Transactional
    public BookingResponse cancelBooking(String userId, String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + bookingId));

        if (!"CANCELLED".equals(booking.getStatus())) {
            booking.setStatus("CANCELLED");
            
            // Release seats back to Event
            Event event = booking.getEvent();
            if (event != null && event.getCapacityBooked() != null) {
                int newBooked = Math.max(0, event.getCapacityBooked() - (booking.getNumSeats() != null ? booking.getNumSeats() : 1));
                event.setCapacityBooked(newBooked);
                eventRepository.save(event);
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
