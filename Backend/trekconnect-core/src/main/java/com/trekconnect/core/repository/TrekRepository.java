package com.trekconnect.core.repository;

import com.trekconnect.core.entity.Trek;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA Repository for Trek catalog reference entries.
 */
@Repository
public interface TrekRepository extends JpaRepository<Trek, String> {
    Optional<Trek> findByName(String name);
}
