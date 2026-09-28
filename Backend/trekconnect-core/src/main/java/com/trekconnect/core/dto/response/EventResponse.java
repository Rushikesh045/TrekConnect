package com.trekconnect.core.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO returning Event batch details with optimistic version.
 */
public class EventResponse {

    private String id;
    private String trekId;
    private String trekName;
    private String region;
    private String difficulty;
    private String category;
    private String imageUrl;
    private Integer altitudeFt;
    private Integer durationDays;
    private String inclusions;
    private String organizerId;
    private String organizerName;
    private String title;
    private String description;
    private LocalDate eventDate;
    private BigDecimal price;
    private Integer capacityTotal;
    private Integer capacityBooked;
    private Integer availableSlots;
    private Integer version;
    private String status;
    private LocalDateTime createdAt;
    private List<EventMediaResponse> mediaGallery;

    public EventResponse() {
    }

    public EventResponse(String id, String trekId, String trekName, String region, String difficulty, String category, String imageUrl, Integer altitudeFt, Integer durationDays, String inclusions, String organizerId, String organizerName, String title, String description, LocalDate eventDate, BigDecimal price, Integer capacityTotal, Integer capacityBooked, Integer availableSlots, Integer version, String status, LocalDateTime createdAt, List<EventMediaResponse> mediaGallery) {
        this.id = id;
        this.trekId = trekId;
        this.trekName = trekName;
        this.region = region;
        this.difficulty = difficulty;
        this.category = category;
        this.imageUrl = imageUrl;
        this.altitudeFt = altitudeFt;
        this.durationDays = durationDays;
        this.inclusions = inclusions;
        this.organizerId = organizerId;
        this.organizerName = organizerName;
        this.title = title;
        this.description = description;
        this.eventDate = eventDate;
        this.price = price;
        this.capacityTotal = capacityTotal;
        this.capacityBooked = capacityBooked;
        this.availableSlots = availableSlots;
        this.version = version;
        this.status = status;
        this.createdAt = createdAt;
        this.mediaGallery = mediaGallery;
    }

    public static EventResponseBuilder builder() {
        return new EventResponseBuilder();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTrekId() { return trekId; }
    public void setTrekId(String trekId) { this.trekId = trekId; }

    public String getTrekName() { return trekName; }
    public void setTrekName(String trekName) { this.trekName = trekName; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Integer getAltitudeFt() { return altitudeFt; }
    public void setAltitudeFt(Integer altitudeFt) { this.altitudeFt = altitudeFt; }

    public Integer getDurationDays() { return durationDays; }
    public void setDurationDays(Integer durationDays) { this.durationDays = durationDays; }

    public String getInclusions() { return inclusions; }
    public void setInclusions(String inclusions) { this.inclusions = inclusions; }

    public String getOrganizerId() { return organizerId; }
    public void setOrganizerId(String organizerId) { this.organizerId = organizerId; }

    public String getOrganizerName() { return organizerName; }
    public void setOrganizerName(String organizerName) { this.organizerName = organizerName; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getEventDate() { return eventDate; }
    public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getCapacityTotal() { return capacityTotal; }
    public void setCapacityTotal(Integer capacityTotal) { this.capacityTotal = capacityTotal; }

    public Integer getCapacityBooked() { return capacityBooked; }
    public void setCapacityBooked(Integer capacityBooked) { this.capacityBooked = capacityBooked; }

    public Integer getAvailableSlots() { return availableSlots; }
    public void setAvailableSlots(Integer availableSlots) { this.availableSlots = availableSlots; }

    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<EventMediaResponse> getMediaGallery() { return mediaGallery; }
    public void setMediaGallery(List<EventMediaResponse> mediaGallery) { this.mediaGallery = mediaGallery; }

    public static class EventResponseBuilder {
        private String id;
        private String trekId;
        private String trekName;
        private String region;
        private String difficulty;
        private String category;
        private String imageUrl;
        private Integer altitudeFt;
        private Integer durationDays;
        private String inclusions;
        private String organizerId;
        private String organizerName;
        private String title;
        private String description;
        private LocalDate eventDate;
        private BigDecimal price;
        private Integer capacityTotal;
        private Integer capacityBooked;
        private Integer availableSlots;
        private Integer version;
        private String status;
        private LocalDateTime createdAt;
        private List<EventMediaResponse> mediaGallery;

        public EventResponseBuilder id(String id) { this.id = id; return this; }
        public EventResponseBuilder trekId(String trekId) { this.trekId = trekId; return this; }
        public EventResponseBuilder trekName(String trekName) { this.trekName = trekName; return this; }
        public EventResponseBuilder region(String region) { this.region = region; return this; }
        public EventResponseBuilder difficulty(String difficulty) { this.difficulty = difficulty; return this; }
        public EventResponseBuilder category(String category) { this.category = category; return this; }
        public EventResponseBuilder imageUrl(String imageUrl) { this.imageUrl = imageUrl; return this; }
        public EventResponseBuilder altitudeFt(Integer altitudeFt) { this.altitudeFt = altitudeFt; return this; }
        public EventResponseBuilder durationDays(Integer durationDays) { this.durationDays = durationDays; return this; }
        public EventResponseBuilder inclusions(String inclusions) { this.inclusions = inclusions; return this; }
        public EventResponseBuilder organizerId(String organizerId) { this.organizerId = organizerId; return this; }
        public EventResponseBuilder organizerName(String organizerName) { this.organizerName = organizerName; return this; }
        public EventResponseBuilder title(String title) { this.title = title; return this; }
        public EventResponseBuilder description(String description) { this.description = description; return this; }
        public EventResponseBuilder eventDate(LocalDate eventDate) { this.eventDate = eventDate; return this; }
        public EventResponseBuilder price(BigDecimal price) { this.price = price; return this; }
        public EventResponseBuilder capacityTotal(Integer capacityTotal) { this.capacityTotal = capacityTotal; return this; }
        public EventResponseBuilder capacityBooked(Integer capacityBooked) { this.capacityBooked = capacityBooked; return this; }
        public EventResponseBuilder availableSlots(Integer availableSlots) { this.availableSlots = availableSlots; return this; }
        public EventResponseBuilder version(Integer version) { this.version = version; return this; }
        public EventResponseBuilder status(String status) { this.status = status; return this; }
        public EventResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public EventResponseBuilder mediaGallery(List<EventMediaResponse> mediaGallery) { this.mediaGallery = mediaGallery; return this; }

        public EventResponse build() {
            return new EventResponse(id, trekId, trekName, region, difficulty, category, imageUrl, altitudeFt, durationDays, inclusions, organizerId, organizerName, title, description, eventDate, price, capacityTotal, capacityBooked, availableSlots, version, status, createdAt, mediaGallery);
        }
    }
}
