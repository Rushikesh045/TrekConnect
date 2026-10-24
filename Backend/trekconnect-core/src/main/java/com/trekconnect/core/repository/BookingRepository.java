package com.trekconnect.core.repository;

import com.trekconnect.core.entity.Booking;
import com.trekconnect.core.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Booking entities.
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {

    Optional<Booking> findByIdempotencyKey(String idempotencyKey);

    List<Booking> findByUserUserIdOrderByCreatedAtDesc(String userId);

    List<Booking> findByEventId(String eventId);

    List<Booking> findByStatusAndExpiresAtBefore(BookingStatus status, LocalDateTime now);
}

