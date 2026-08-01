package com.trekconnect.core.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * Request DTO for creating a new Trek catalog reference location.
 */
public class CreateTrekRequest {

    @NotBlank(message = "Trek name is required")
    @Size(min = 2, max = 200, message = "Trek name must be between 2 and 200 characters")
    private String name;

    @Size(max = 100, message = "Region must be less than 100 characters")
    private String region;

    private String history;

    private BigDecimal distanceKm;

    @NotBlank(message = "Difficulty is required")
    private String difficulty;

    private Integer altitudeMeters;

    private String bestSeason;

    public CreateTrekRequest() {
    }

    public CreateTrekRequest(String name, String region, String history, BigDecimal distanceKm, String difficulty, Integer altitudeMeters, String bestSeason) {
        this.name = name;
        this.region = region;
        this.history = history;
        this.distanceKm = distanceKm;
        this.difficulty = difficulty;
        this.altitudeMeters = altitudeMeters;
        this.bestSeason = bestSeason;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public BigDecimal getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(BigDecimal distanceKm) {
        this.distanceKm = distanceKm;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public Integer getAltitudeMeters() {
        return altitudeMeters;
    }

    public void setAltitudeMeters(Integer altitudeMeters) {
        this.altitudeMeters = altitudeMeters;
    }

    public String getBestSeason() {
        return bestSeason;
    }

    public void setBestSeason(String bestSeason) {
        this.bestSeason = bestSeason;
    }
}
