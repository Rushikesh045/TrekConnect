package com.trekconnect.core.repository;

import com.trekconnect.core.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for Booking entity.
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {

    Optional<Booking> findByIdempotencyKey(String idempotencyKey);

    List<Booking> findByUserUserIdOrderByCreatedAtDesc(String userId);

    List<Booking> findByEventId(String eventId);

    List<Booking> findByStatusAndExpiresAtBefore(String status, LocalDateTime now);
}
