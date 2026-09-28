package com.trekconnect.core.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing Trek Reference Information stored in main_db.
 */
@Entity
@Table(name = "treks")
public class Trek {

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 100)
    private String region;

    @Column(length = 50)
    private String category; // FORT, SAHYADRI, WATERFALL, CAMPING, HIMALAYA

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String inclusions;

    @Column(columnDefinition = "TEXT")
    private String history;

    @Column(name = "distance_km", precision = 6, scale = 2)
    private BigDecimal distanceKm;

    @Column(nullable = false, length = 20)
    private String difficulty;

    @Column(name = "altitude_m")
    private Integer altitudeMeters;

    @Column(name = "duration_days")
    private Integer durationDays;

    @Column(name = "best_season", length = 100)
    private String bestSeason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Trek() {
    }

    public Trek(String id, String name, String region, String category, String imageUrl, String description, String inclusions, String history, BigDecimal distanceKm, String difficulty, Integer altitudeMeters, Integer durationDays, String bestSeason, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.region = region;
        this.category = category;
        this.imageUrl = imageUrl;
        this.description = description;
        this.inclusions = inclusions;
        this.history = history;
        this.distanceKm = distanceKm;
        this.difficulty = difficulty;
        this.altitudeMeters = altitudeMeters;
        this.durationDays = durationDays;
        this.bestSeason = bestSeason;
        this.createdAt = createdAt;
    }

    public static TrekBuilder builder() {
        return new TrekBuilder();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getInclusions() { return inclusions; }
    public void setInclusions(String inclusions) { this.inclusions = inclusions; }

    public String getHistory() { return history; }
    public void setHistory(String history) { this.history = history; }

    public BigDecimal getDistanceKm() { return distanceKm; }
    public void setDistanceKm(BigDecimal distanceKm) { this.distanceKm = distanceKm; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public Integer getAltitudeMeters() { return altitudeMeters; }
    public void setAltitudeMeters(Integer altitudeMeters) { this.altitudeMeters = altitudeMeters; }

    public Integer getDurationDays() { return durationDays; }
    public void setDurationDays(Integer durationDays) { this.durationDays = durationDays; }

    public String getBestSeason() { return bestSeason; }
    public void setBestSeason(String bestSeason) { this.bestSeason = bestSeason; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public static class TrekBuilder {
        private String id;
        private String name;
        private String region;
        private String category;
        private String imageUrl;
        private String description;
        private String inclusions;
        private String history;
        private BigDecimal distanceKm;
        private String difficulty;
        private Integer altitudeMeters;
        private Integer durationDays;
        private String bestSeason;
        private LocalDateTime createdAt;

        public TrekBuilder id(String id) { this.id = id; return this; }
        public TrekBuilder name(String name) { this.name = name; return this; }
        public TrekBuilder region(String region) { this.region = region; return this; }
        public TrekBuilder category(String category) { this.category = category; return this; }
        public TrekBuilder imageUrl(String imageUrl) { this.imageUrl = imageUrl; return this; }
        public TrekBuilder description(String description) { this.description = description; return this; }
        public TrekBuilder inclusions(String inclusions) { this.inclusions = inclusions; return this; }
        public TrekBuilder history(String history) { this.history = history; return this; }
        public TrekBuilder distanceKm(BigDecimal distanceKm) { this.distanceKm = distanceKm; return this; }
        public TrekBuilder difficulty(String difficulty) { this.difficulty = difficulty; return this; }
        public TrekBuilder altitudeMeters(Integer altitudeMeters) { this.altitudeMeters = altitudeMeters; return this; }
        public TrekBuilder durationDays(Integer durationDays) { this.durationDays = durationDays; return this; }
        public TrekBuilder bestSeason(String bestSeason) { this.bestSeason = bestSeason; return this; }
        public TrekBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Trek build() {
            return new Trek(id, name, region, category, imageUrl, description, inclusions, history, distanceKm, difficulty, altitudeMeters, durationDays, bestSeason, createdAt);
        }
    }
}
