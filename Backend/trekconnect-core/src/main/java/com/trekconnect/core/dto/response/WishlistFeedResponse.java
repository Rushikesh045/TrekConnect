package com.trekconnect.core.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO returning enriched wishlist feed items including trek details and active events.
 */
public class WishlistFeedResponse {
    private String wishlistId;
    private String trekId;
    private LocalDateTime savedAt;
    private String trekName;
    private String region;
    private String category;
    private String difficulty;
    private String imageUrl;
    private String description;
    private Integer altitudeMeters;
    private Integer durationDays;
    private BigDecimal distanceKm;
    private String bestSeason;
    private List<EventResponse> availableEvents;

    public WishlistFeedResponse() {
    }

    public WishlistFeedResponse(String wishlistId, String trekId, LocalDateTime savedAt, String trekName,
                                String region, String category, String difficulty, String imageUrl,
                                String description, Integer altitudeMeters, Integer durationDays,
                                BigDecimal distanceKm, String bestSeason, List<EventResponse> availableEvents) {
        this.wishlistId = wishlistId;
        this.trekId = trekId;
        this.savedAt = savedAt;
        this.trekName = trekName;
        this.region = region;
        this.category = category;
        this.difficulty = difficulty;
        this.imageUrl = imageUrl;
        this.description = description;
        this.altitudeMeters = altitudeMeters;
        this.durationDays = durationDays;
        this.distanceKm = distanceKm;
        this.bestSeason = bestSeason;
        this.availableEvents = availableEvents;
    }

    public String getWishlistId() { return wishlistId; }
    public void setWishlistId(String wishlistId) { this.wishlistId = wishlistId; }

    public String getTrekId() { return trekId; }
    public void setTrekId(String trekId) { this.trekId = trekId; }

    public LocalDateTime getSavedAt() { return savedAt; }
    public void setSavedAt(LocalDateTime savedAt) { this.savedAt = savedAt; }

    public String getTrekName() { return trekName; }
    public void setTrekName(String trekName) { this.trekName = trekName; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getAltitudeMeters() { return altitudeMeters; }
    public void setAltitudeMeters(Integer altitudeMeters) { this.altitudeMeters = altitudeMeters; }

    public Integer getDurationDays() { return durationDays; }
    public void setDurationDays(Integer durationDays) { this.durationDays = durationDays; }

    public BigDecimal getDistanceKm() { return distanceKm; }
    public void setDistanceKm(BigDecimal distanceKm) { this.distanceKm = distanceKm; }

    public String getBestSeason() { return bestSeason; }
    public void setBestSeason(String bestSeason) { this.bestSeason = bestSeason; }

    public List<EventResponse> getAvailableEvents() { return availableEvents; }
    public void setAvailableEvents(List<EventResponse> availableEvents) { this.availableEvents = availableEvents; }

    public static WishlistFeedResponseBuilder builder() {
        return new WishlistFeedResponseBuilder();
    }

    public static class WishlistFeedResponseBuilder {
        private String wishlistId;
        private String trekId;
        private LocalDateTime savedAt;
        private String trekName;
        private String region;
        private String category;
        private String difficulty;
        private String imageUrl;
        private String description;
        private Integer altitudeMeters;
        private Integer durationDays;
        private BigDecimal distanceKm;
        private String bestSeason;
        private List<EventResponse> availableEvents;

        public WishlistFeedResponseBuilder wishlistId(String wishlistId) { this.wishlistId = wishlistId; return this; }
        public WishlistFeedResponseBuilder trekId(String trekId) { this.trekId = trekId; return this; }
        public WishlistFeedResponseBuilder savedAt(LocalDateTime savedAt) { this.savedAt = savedAt; return this; }
        public WishlistFeedResponseBuilder trekName(String trekName) { this.trekName = trekName; return this; }
        public WishlistFeedResponseBuilder region(String region) { this.region = region; return this; }
        public WishlistFeedResponseBuilder category(String category) { this.category = category; return this; }
        public WishlistFeedResponseBuilder difficulty(String difficulty) { this.difficulty = difficulty; return this; }
        public WishlistFeedResponseBuilder imageUrl(String imageUrl) { this.imageUrl = imageUrl; return this; }
        public WishlistFeedResponseBuilder description(String description) { this.description = description; return this; }
        public WishlistFeedResponseBuilder altitudeMeters(Integer altitudeMeters) { this.altitudeMeters = altitudeMeters; return this; }
        public WishlistFeedResponseBuilder durationDays(Integer durationDays) { this.durationDays = durationDays; return this; }
        public WishlistFeedResponseBuilder distanceKm(BigDecimal distanceKm) { this.distanceKm = distanceKm; return this; }
        public WishlistFeedResponseBuilder bestSeason(String bestSeason) { this.bestSeason = bestSeason; return this; }
        public WishlistFeedResponseBuilder availableEvents(List<EventResponse> availableEvents) { this.availableEvents = availableEvents; return this; }

        public WishlistFeedResponse build() {
            return new WishlistFeedResponse(wishlistId, trekId, savedAt, trekName, region, category, difficulty,
                    imageUrl, description, altitudeMeters, durationDays, distanceKm, bestSeason, availableEvents);
        }
    }
}
