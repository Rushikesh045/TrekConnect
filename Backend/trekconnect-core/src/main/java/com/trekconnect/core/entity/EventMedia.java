package com.trekconnect.core.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing Event Media Gallery Assets stored in main_db.
 */
@Entity
@Table(name = "event_media")
public class EventMedia {

    @Id
    @Column(length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "media_url", nullable = false, length = 500)
    private String mediaUrl;

    @Column(name = "media_type", nullable = false, length = 10)
    private String mediaType;

    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private LocalDateTime uploadedAt;

    public EventMedia() {
    }

    public EventMedia(String id, Event event, String mediaUrl, String mediaType, LocalDateTime uploadedAt) {
        this.id = id;
        this.event = event;
        this.mediaUrl = mediaUrl;
        this.mediaType = mediaType != null ? mediaType : "IMAGE";
        this.uploadedAt = uploadedAt;
    }

    public static EventMediaBuilder builder() {
        return new EventMediaBuilder();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.mediaType == null) {
            this.mediaType = "IMAGE";
        }
        if (this.uploadedAt == null) {
            this.uploadedAt = LocalDateTime.now();
        }
    }

    public static class EventMediaBuilder {
        private String id;
        private Event event;
        private String mediaUrl;
        private String mediaType = "IMAGE";
        private LocalDateTime uploadedAt;

        public EventMediaBuilder id(String id) {
            this.id = id;
            return this;
        }

        public EventMediaBuilder event(Event event) {
            this.event = event;
            return this;
        }

        public EventMediaBuilder mediaUrl(String mediaUrl) {
            this.mediaUrl = mediaUrl;
            return this;
        }

        public EventMediaBuilder mediaType(String mediaType) {
            this.mediaType = mediaType;
            return this;
        }

        public EventMediaBuilder uploadedAt(LocalDateTime uploadedAt) {
            this.uploadedAt = uploadedAt;
            return this;
        }

        public EventMedia build() {
            return new EventMedia(id, event, mediaUrl, mediaType, uploadedAt);
        }
    }
}
