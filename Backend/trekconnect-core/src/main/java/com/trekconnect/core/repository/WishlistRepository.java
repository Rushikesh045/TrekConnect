package com.trekconnect.core.repository;

import com.trekconnect.core.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA Repository for Wishlist entities.
 */
@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, String> {
    List<Wishlist> findByUserIdOrderByCreatedAtDesc(String userId);
    Optional<Wishlist> findByUserIdAndTrekId(String userId, String trekId);
    boolean existsByUserIdAndTrekId(String userId, String trekId);
}
