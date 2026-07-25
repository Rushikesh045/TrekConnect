package com.trekconnect.core.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entity representing User Wishlist / Saved Events stored in main_db.
 * 
 * WHY THIS ENTITY WAS CREATED:
 * Allows trekkers to bookmark upcoming trek events to their personal wishlist for quick access later.
 */
@Entity
@Table(name = "saved_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavedEvent {

    @EmbeddedId
    private SavedEventId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private UserProfile user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("eventId")
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "saved_at", nullable = false, updatable = false)
    private LocalDateTime savedAt;

    @PrePersist
    public void prePersist() {
        if (this.savedAt == null) {
            this.savedAt = LocalDateTime.now();
        }
    }
}
