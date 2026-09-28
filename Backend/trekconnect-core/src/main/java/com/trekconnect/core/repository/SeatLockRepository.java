package com.trekconnect.core.repository;

import com.trekconnect.core.entity.SeatLock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for SeatLock entity.
 */
@Repository
public interface SeatLockRepository extends JpaRepository<SeatLock, String> {

    Optional<SeatLock> findByEventIdAndUserIdAndExpiresAtAfter(String eventId, String userId, LocalDateTime now);

    List<SeatLock> findByEventId(String eventId);

    List<SeatLock> findByExpiresAtBefore(LocalDateTime now);
}
