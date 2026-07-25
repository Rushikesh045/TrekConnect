package com.trekconnect.core.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing Organizer Business Details stored in main_db.
 * 
 * WHY THIS ENTITY WAS CREATED:
 * When a user requests to become a verified Trek Organizer, this entity stores their
 * business organization name, verification documents, and current status ('PENDING', 'VERIFIED', 'REJECTED').
 * Admins review this table to grant or deny event creation permissions.
 */
@Entity
@Table(name = "organizer_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizerDetails {

    @Id
    @Column(length = 36)
    private String id;

    /**
     * Foreign Key referencing user_profile(user_id).
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserProfile user;

    /**
     * Name of the trekking business / organization.
     */
    @Column(name = "organization_name", nullable = false, length = 200)
    private String organizationName;

    /**
     * Verification status: 'PENDING', 'VERIFIED', or 'REJECTED'.
     */
    @Column(name = "verification_status", nullable = false, length = 20)
    @Builder.Default
    private String verificationStatus = "PENDING";

    /**
     * URL pointing to uploaded government registration / verification documents.
     */
    @Column(name = "verification_docs_url", length = 500)
    private String verificationDocsUrl;

    /**
     * Admin user ID who reviewed and verified/rejected this organizer request.
     */
    @Column(name = "verified_by_admin_id", length = 36)
    private String verifiedByAdminId;

    /**
     * Verification decision timestamp.
     */
    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    /**
     * Explanation provided by admin if verification request was rejected.
     */
    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.verificationStatus == null) {
            this.verificationStatus = "PENDING";
        }
    }
}
