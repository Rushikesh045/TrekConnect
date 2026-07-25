package com.trekconnect.core.entity;

import jakarta.persistence.*;
import lombok.*;
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
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @Column(name = "event_date", nullable = false)
    private LocalDate eventDate;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "capacity_total", nullable = false)
    private Integer capacityTotal;

    @Column(name = "capacity_booked", nullable = false)
    @Builder.Default
    private Integer capacityBooked = 0;

    /**
     * JPA Optimistic Locking Version column to prevent concurrent overbooking.
     */
    @Version
    @Column(nullable = false)
    @Builder.Default
    private Integer version = 0;

    /**
     * Event Status: 'PENDING_APPROVAL', 'APPROVED', 'REJECTED', 'CANCELLED', 'COMPLETED'.
     */
    @Column(nullable = false, length = 30)
    @Builder.Default
    private String status = "PENDING_APPROVAL";

    @Column(name = "approved_by_admin_id", length = 36)
    private String approvedByAdminId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

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
            this.status = "PENDING_APPROVAL";
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
