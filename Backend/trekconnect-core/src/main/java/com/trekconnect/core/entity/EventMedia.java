package com.trekconnect.core.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing Event Media Gallery Assets stored in main_db.
 * 
 * WHY THIS ENTITY WAS CREATED:
 * Allows organizers to upload photos and videos of past trek batches to showcase on event detail pages.
 */
@Entity
@Table(name = "event_media")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventMedia {

    @Id
    @Column(length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "media_url", nullable = false, length = 500)
    private String mediaUrl;

    /**
     * Media type: 'IMAGE' or 'VIDEO'.
     */
    @Column(name = "media_type", nullable = false, length = 10)
    private String mediaType;

    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private LocalDateTime uploadedAt;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.uploadedAt == null) {
            this.uploadedAt = LocalDateTime.now();
        }
    }
}
