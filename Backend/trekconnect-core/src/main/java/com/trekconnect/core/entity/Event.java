package com.trekconnect.core.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing an Event Batch hosted by an Organizer.
 * 
 * WHY THIS ENTITY WAS CREATED:
 * An Event is a specific scheduled batch of a Trek with pricing, seat capacity, date, and itinerary.
 * Includes an optimistic locking '@Version' column to prevent concurrent overbooking race conditions.
 */
@Entity
@Table(name = "events")
public class Event {

    @Id
    @Column(length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trek_id", nullable = false)
    private Trek trek;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false)
    private OrganizerDetails organizer;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "event_date", nullable = false)
    private LocalDate eventDate;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "capacity_total", nullable = false)
    private Integer capacityTotal;

    @Column(name = "capacity_booked", nullable = false)
    private Integer capacityBooked = 0;

    /**
     * JPA Optimistic Locking Version column to prevent concurrent overbooking.
     */
    @Version
    @Column(nullable = false)
    private Integer version = 0;

    @Column(nullable = false, length = 30)
    private String status = "PENDING_APPROVAL";

    @Column(name = "approved_by_admin_id", length = 36)
    private String approvedByAdminId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Event() {
    }

    public Event(String id, Trek trek, OrganizerDetails organizer, String title, String description, String imageUrl, LocalDate eventDate, BigDecimal price, Integer capacityTotal, Integer capacityBooked, Integer version, String status, String approvedByAdminId, LocalDateTime createdAt) {
        this.id = id;
        this.trek = trek;
        this.organizer = organizer;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.eventDate = eventDate;
        this.price = price;
        this.capacityTotal = capacityTotal;
        this.capacityBooked = capacityBooked != null ? capacityBooked : 0;
        this.version = version != null ? version : 0;
        this.status = status != null ? status : "PENDING_APPROVAL";
        this.approvedByAdminId = approvedByAdminId;
        this.createdAt = createdAt;
    }

    public static EventBuilder builder() {
        return new EventBuilder();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Trek getTrek() {
        return trek;
    }

    public void setTrek(Trek trek) {
        this.trek = trek;
    }

    public OrganizerDetails getOrganizer() {
        return organizer;
    }

    public void setOrganizer(OrganizerDetails organizer) {
        this.organizer = organizer;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getCapacityTotal() {
        return capacityTotal;
    }

    public void setCapacityTotal(Integer capacityTotal) {
        this.capacityTotal = capacityTotal;
    }

    public Integer getCapacityBooked() {
        return capacityBooked;
    }

    public void setCapacityBooked(Integer capacityBooked) {
        this.capacityBooked = capacityBooked;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApprovedByAdminId() {
        return approvedByAdminId;
    }

    public void setApprovedByAdminId(String approvedByAdminId) {
        this.approvedByAdminId = approvedByAdminId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.capacityBooked == null) {
            this.capacityBooked = 0;
        }
        if (this.version == null) {
            this.version = 0;
        }
        if (this.status == null) {
            this.status = "APPROVED";
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public static class EventBuilder {
        private String id;
        private Trek trek;
        private OrganizerDetails organizer;
        private String title;
        private String description;
        private String imageUrl;
        private LocalDate eventDate;
        private BigDecimal price;
        private Integer capacityTotal;
        private Integer capacityBooked = 0;
        private Integer version = 0;
        private String status = "APPROVED";
        private String approvedByAdminId;
        private LocalDateTime createdAt;

        public EventBuilder id(String id) {
            this.id = id;
            return this;
        }

        public EventBuilder trek(Trek trek) {
            this.trek = trek;
            return this;
        }

        public EventBuilder organizer(OrganizerDetails organizer) {
            this.organizer = organizer;
            return this;
        }

        public EventBuilder title(String title) {
            this.title = title;
            return this;
        }

        public EventBuilder description(String description) {
            this.description = description;
            return this;
        }

        public EventBuilder imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public EventBuilder eventDate(LocalDate eventDate) {
            this.eventDate = eventDate;
            return this;
        }

        public EventBuilder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public EventBuilder capacityTotal(Integer capacityTotal) {
            this.capacityTotal = capacityTotal;
            return this;
        }

        public EventBuilder capacityBooked(Integer capacityBooked) {
            this.capacityBooked = capacityBooked;
            return this;
        }

        public EventBuilder version(Integer version) {
            this.version = version;
            return this;
        }

        public EventBuilder status(String status) {
            this.status = status;
            return this;
        }

        public EventBuilder approvedByAdminId(String approvedByAdminId) {
            this.approvedByAdminId = approvedByAdminId;
            return this;
        }

        public EventBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Event build() {
            return new Event(id, trek, organizer, title, description, imageUrl, eventDate, price, capacityTotal, capacityBooked, version, status, approvedByAdminId, createdAt);
        }
    }
}
