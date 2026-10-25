package com.trekconnect.core.service;

import com.trekconnect.core.dto.response.EventResponse;
import com.trekconnect.core.dto.response.WishlistFeedResponse;
import com.trekconnect.core.entity.Event;
import com.trekconnect.core.entity.Trek;
import com.trekconnect.core.entity.Wishlist;
import com.trekconnect.core.repository.EventRepository;
import com.trekconnect.core.repository.TrekRepository;
import com.trekconnect.core.repository.WishlistRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service managing user saved wishlist bookmarks and enriched wishlist feed.
 */
@Service
public class WishlistService {

    private static final Logger logger = LoggerFactory.getLogger(WishlistService.class);

    private final WishlistRepository wishlistRepository;
    private final TrekRepository trekRepository;
    private final EventRepository eventRepository;

    @Autowired
    public WishlistService(WishlistRepository wishlistRepository,
                           TrekRepository trekRepository,
                           EventRepository eventRepository) {
        this.wishlistRepository = wishlistRepository;
        this.trekRepository = trekRepository;
        this.eventRepository = eventRepository;
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
     * Retrieves raw saved wishlist items for a user.
     */
    @Transactional(readOnly = true)
    public List<Wishlist> getMyWishlist(String userId) {
        logger.debug("Fetching wishlist for User: {}", userId);
        return wishlistRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Retrieves enriched wishlist feed with full trek details and active event batches.
     */
    @Transactional(readOnly = true)
    public List<WishlistFeedResponse> getWishlistFeed(String userId) {
        logger.info("Building enriched wishlist feed for User: {}", userId);
        List<Wishlist> wishlistItems = wishlistRepository.findByUserIdOrderByCreatedAtDesc(userId);

        List<WishlistFeedResponse> feed = new ArrayList<>();
        for (Wishlist item : wishlistItems) {
            Optional<Trek> trekOpt = trekRepository.findById(item.getTrekId());
            if (trekOpt.isPresent()) {
                Trek trek = trekOpt.get();
                List<Event> events = eventRepository.findByTrekId(trek.getId());

                List<EventResponse> availableEvents = events.stream()
                        .filter(e -> "APPROVED".equalsIgnoreCase(e.getStatus()))
                        .map(this::mapToEventResponse)
                        .collect(Collectors.toList());

                feed.add(WishlistFeedResponse.builder()
                        .wishlistId(item.getId())
                        .trekId(trek.getId())
                        .savedAt(item.getCreatedAt())
                        .trekName(trek.getName())
                        .region(trek.getRegion())
                        .category(trek.getCategory())
                        .difficulty(trek.getDifficulty())
                        .imageUrl(trek.getImageUrl())
                        .description(trek.getDescription())
                        .altitudeMeters(trek.getAltitudeMeters())
                        .durationDays(trek.getDurationDays())
                        .distanceKm(trek.getDistanceKm())
                        .bestSeason(trek.getBestSeason())
                        .availableEvents(availableEvents)
                        .build());
            }
        }
        return feed;
    }

    private EventResponse mapToEventResponse(Event event) {
        int totalCap = event.getCapacityTotal() != null ? event.getCapacityTotal() : 25;
        int bookedCap = event.getCapacityBooked() != null ? event.getCapacityBooked() : 0;
        String organizerName = (event.getOrganizer() != null && event.getOrganizer().getOrganizationName() != null)
                ? event.getOrganizer().getOrganizationName() : "Sahyadri Wanderers Expeditions";

        return EventResponse.builder()
                .id(event.getId())
                .trekId(event.getTrek() != null ? event.getTrek().getId() : "N/A")
                .trekName(event.getTrek() != null ? event.getTrek().getName() : event.getTitle())
                .title(event.getTitle())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .price(event.getPrice())
                .capacityTotal(totalCap)
                .capacityBooked(bookedCap)
                .availableSlots(Math.max(0, totalCap - bookedCap))
                .status(event.getStatus())
                .organizerName(organizerName)
                .build();
    }
}
