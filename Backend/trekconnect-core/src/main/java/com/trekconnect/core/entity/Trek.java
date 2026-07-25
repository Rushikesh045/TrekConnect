package com.trekconnect.core.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing Trek Reference Information stored in main_db.
 * 
 * WHY THIS ENTITY WAS CREATED:
 * Forts and trekking locations (e.g., Rajmachi, Harishchandragad, Triund) contain static reference data
 * such as historical background, trail distance, difficulty level, and altitude.
 * Multiple event batches hosted by different organizers link back to a single Trek entry.
 */
@Entity
@Table(name = "treks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trek {

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 100)
    private String region;

    @Column(columnDefinition = "TEXT")
    private String history;

    @Column(name = "distance_km", precision = 6, scale = 2)
    private BigDecimal distanceKm;

    /**
     * Difficulty level: 'EASY', 'MODERATE', 'HARD', or 'EXTREME'.
     */
    @Column(nullable = false, length = 20)
    private String difficulty;

    @Column(name = "altitude_m")
    private Integer altitudeMeters;

    @Column(name = "best_season", length = 100)
    private String bestSeason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
