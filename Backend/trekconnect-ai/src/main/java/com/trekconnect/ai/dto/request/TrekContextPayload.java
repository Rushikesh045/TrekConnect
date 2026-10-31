package com.trekconnect.ai.dto.request;

import java.util.List;

/**
 * Context payload containing metadata about the specific trek currently being browsed.
 * Used by RAG retriever to ground AI answers in factual expedition parameters.
 */
public class TrekContextPayload {

    private String trekId;
    private String title;
    private String location;
    private String difficulty;
    private Integer altitudeFt;
    private Double pricePerSlot;
    private Integer durationDays;
    private String description;
    private List<String> inclusions;
    private List<String> exclusions;
    private String upcomingDate;
    private String organizerName;
    private String fitnessRequirement;
    private String trailType;
    private String cancellationPolicy;

    public TrekContextPayload() {
    }

    public String getTrekId() {
        return trekId;
    }

    public void setTrekId(String trekId) {
        this.trekId = trekId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public Integer getAltitudeFt() {
        return altitudeFt;
    }

    public void setAltitudeFt(Integer altitudeFt) {
        this.altitudeFt = altitudeFt;
    }

    public Double getPricePerSlot() {
        return pricePerSlot;
    }

    public void setPricePerSlot(Double pricePerSlot) {
        this.pricePerSlot = pricePerSlot;
    }

    public Integer getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(Integer durationDays) {
        this.durationDays = durationDays;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getInclusions() {
        return inclusions;
    }

    public void setInclusions(List<String> inclusions) {
        this.inclusions = inclusions;
    }

    public List<String> getExclusions() {
        return exclusions;
    }

    public void setExclusions(List<String> exclusions) {
        this.exclusions = exclusions;
    }

    public String getUpcomingDate() {
        return upcomingDate;
    }

    public void setUpcomingDate(String upcomingDate) {
        this.upcomingDate = upcomingDate;
    }

    public String getOrganizerName() {
        return organizerName;
    }

    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
    }

    public String getFitnessRequirement() {
        return fitnessRequirement;
    }

    public void setFitnessRequirement(String fitnessRequirement) {
        this.fitnessRequirement = fitnessRequirement;
    }

    public String getTrailType() {
        return trailType;
    }

    public void setTrailType(String trailType) {
        this.trailType = trailType;
    }

    public String getCancellationPolicy() {
        return cancellationPolicy;
    }

    public void setCancellationPolicy(String cancellationPolicy) {
        this.cancellationPolicy = cancellationPolicy;
    }
}
