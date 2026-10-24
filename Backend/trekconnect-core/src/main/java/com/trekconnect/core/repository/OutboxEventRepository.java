package com.trekconnect.core.repository;

import com.trekconnect.core.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for querying and persisting OutboxEvent records.
 */
@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    List<OutboxEvent> findTop50ByStatusOrderByCreatedAtAsc(String status);

    List<OutboxEvent> findTop50ByOrderByCreatedAtDesc();
}
