package com.trekconnect.core.repository;

import com.trekconnect.core.entity.RefundRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA Repository for RefundRequest entity stored in main_db.
 */
@Repository
public interface RefundRequestRepository extends JpaRepository<RefundRequest, String> {
    List<RefundRequest> findByStatus(String status);
}
