package com.trekconnect.core.repository;

import com.trekconnect.core.entity.Event;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for Event batches with Pessimistic Concurrency Locking.
 */
@Repository
public interface EventRepository extends JpaRepository<Event, String> {

    List<Event> findByOrganizerUserUserId(String userId);

    List<Event> findByStatus(String status);

    /**
     * Executes SELECT FOR UPDATE query to obtain a DB-level Pessimistic Write Lock on Event.
     * Prevents overbooking race conditions when multiple users book remaining seats concurrently.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Event e WHERE e.id = :id")
    Optional<Event> findByIdWithPessimisticLock(@Param("id") String id);
}
