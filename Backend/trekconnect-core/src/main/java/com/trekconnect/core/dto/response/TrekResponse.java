package com.trekconnect.core.dto.response;

import java.math.BigDecimal;

/**
 * Response DTO returning Trek catalog details.
 */
public class TrekResponse {

    private String id;
    private String name;
    private String region;
    private String history;
    private BigDecimal distanceKm;
    private String difficulty;
    private Integer altitudeMeters;
    private String bestSeason;

    public TrekResponse() {
    }

    public TrekResponse(String id, String name, String region, String history, BigDecimal distanceKm, String difficulty, Integer altitudeMeters, String bestSeason) {
        this.id = id;
        this.name = name;
        this.region = region;
        this.history = history;
        this.distanceKm = distanceKm;
        this.difficulty = difficulty;
        this.altitudeMeters = altitudeMeters;
        this.bestSeason = bestSeason;
    }

    public static TrekResponseBuilder builder() {
        return new TrekResponseBuilder();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public static class TrekResponseBuilder {
        private String id;
        private String name;
        private String region;
        private String history;
        private BigDecimal distanceKm;
        private String difficulty;
        private Integer altitudeMeters;
        private String bestSeason;

        public TrekResponseBuilder id(String id) {
            this.id = id;
            return this;
        }

        public TrekResponseBuilder name(String name) {
            this.name = name;
            return this;
        }

        public TrekResponseBuilder region(String region) {
            this.region = region;
            return this;
        }

        public TrekResponseBuilder history(String history) {
            this.history = history;
            return this;
        }

        public TrekResponseBuilder distanceKm(BigDecimal distanceKm) {
            this.distanceKm = distanceKm;
            return this;
        }

        public TrekResponseBuilder difficulty(String difficulty) {
            this.difficulty = difficulty;
            return this;
        }

        public TrekResponseBuilder altitudeMeters(Integer altitudeMeters) {
            this.altitudeMeters = altitudeMeters;
            return this;
        }

        public TrekResponseBuilder bestSeason(String bestSeason) {
            this.bestSeason = bestSeason;
            return this;
        }

        public TrekResponse build() {
            return new TrekResponse(id, name, region, history, distanceKm, difficulty, altitudeMeters, bestSeason);
        }
    }
}
