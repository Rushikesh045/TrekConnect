package com.trekconnect.core.dto.response;

import java.time.LocalDateTime;

/**
 * Response DTO returning media gallery details for an Event.
 */
public class EventMediaResponse {

    private String id;
    private String eventId;
    private String mediaUrl;
    private String mediaType;
    private LocalDateTime uploadedAt;

    public EventMediaResponse() {
    }

    public EventMediaResponse(String id, String eventId, String mediaUrl, String mediaType, LocalDateTime uploadedAt) {
        this.id = id;
        this.eventId = eventId;
        this.mediaUrl = mediaUrl;
        this.mediaType = mediaType;
        this.uploadedAt = uploadedAt;
    }

    public static EventMediaResponseBuilder builder() {
        return new EventMediaResponseBuilder();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
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

    public static class EventMediaResponseBuilder {
        private String id;
        private String eventId;
        private String mediaUrl;
        private String mediaType;
        private LocalDateTime uploadedAt;

        public EventMediaResponseBuilder id(String id) {
            this.id = id;
            return this;
        }

        public EventMediaResponseBuilder eventId(String eventId) {
            this.eventId = eventId;
            return this;
        }

        public EventMediaResponseBuilder mediaUrl(String mediaUrl) {
            this.mediaUrl = mediaUrl;
            return this;
        }

        public EventMediaResponseBuilder mediaType(String mediaType) {
            this.mediaType = mediaType;
            return this;
        }

        public EventMediaResponseBuilder uploadedAt(LocalDateTime uploadedAt) {
            this.uploadedAt = uploadedAt;
            return this;
        }

        public EventMediaResponse build() {
            return new EventMediaResponse(id, eventId, mediaUrl, mediaType, uploadedAt);
        }
    }
}
