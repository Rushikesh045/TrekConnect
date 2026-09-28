package com.trekconnect.core.repository;

import com.trekconnect.core.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA Repository for Review entities.
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, String> {
    List<Review> findByTrekIdOrderByCreatedAtDesc(String trekId);
    List<Review> findByUserIdOrderByCreatedAtDesc(String userId);
}
