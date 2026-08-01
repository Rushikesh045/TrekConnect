package com.trekconnect.core.repository;

import com.trekconnect.core.entity.OrganizerDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA Repository for OrganizerDetails entity stored in main_db.
 */
@Repository
public interface OrganizerDetailsRepository extends JpaRepository<OrganizerDetails, String> {
    
    Optional<OrganizerDetails> findByUserUserId(String userId);
    
    boolean existsByUserUserId(String userId);
}
