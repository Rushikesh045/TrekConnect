package com.trekconnect.core.service;

import com.trekconnect.core.entity.Wishlist;
import com.trekconnect.core.repository.WishlistRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service managing user saved wishlist bookmarks.
 * 
 * WHY THIS SERVICE WAS CREATED:
 * Toggles and persists bookmarked treks for logged in users.
 */
@Service
public class WishlistService {

    private static final Logger logger = LoggerFactory.getLogger(WishlistService.class);

    private final WishlistRepository wishlistRepository;

    @Autowired
    public WishlistService(WishlistRepository wishlistRepository) {
        this.wishlistRepository = wishlistRepository;
    }

    /**
     * Toggles saved bookmark status for a trek. Returns true if saved, false if removed.
     */
    @Transactional
    public boolean toggleWishlist(String userId, String trekId) {
        logger.info("Toggling wishlist item for User: {}, Trek: {}", userId, trekId);

        Optional<Wishlist> existing = wishlistRepository.findByUserIdAndTrekId(userId, trekId);
        if (existing.isPresent()) {
            wishlistRepository.delete(existing.get());
            logger.info("Removed Trek: {} from User: {} wishlist", trekId, userId);
            return false;
        } else {
            Wishlist item = Wishlist.builder().userId(userId).trekId(trekId).build();
            wishlistRepository.save(item);
            logger.info("Added Trek: {} to User: {} wishlist", trekId, userId);
            return true;
        }
    }

    /**
     * Retrieves all saved wishlist items for a user.
     */
    @Transactional(readOnly = true)
    public List<Wishlist> getMyWishlist(String userId) {
        logger.debug("Fetching wishlist for User: {}", userId);
        return wishlistRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}
