package com.trekconnect.core.repository;

import com.trekconnect.core.entity.EventMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA Repository for EventMedia gallery assets.
 */
@Repository
public interface EventMediaRepository extends JpaRepository<EventMedia, String> {
    List<EventMedia> findByEventId(String eventId);
}
