package com.trekconnect.core.repository;

import com.trekconnect.core.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA Repository for Event batches.
 */
@Repository
public interface EventRepository extends JpaRepository<Event, String> {
    List<Event> findByOrganizerUserUserId(String userId);
    List<Event> findByStatus(String status);
}
