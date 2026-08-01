package com.trekconnect.core.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing Organizer Business Details stored in main_db.
 * 
 * WHY THIS ENTITY WAS CREATED:
 * When a user requests to become a verified Trek Organizer, this entity stores their
 * organization name, verification documents, and current status ('PENDING', 'VERIFIED', 'REJECTED').
 */
@Entity
@Table(name = "organizer_details")
public class OrganizerDetails {

    @Id
    @Column(length = 36)
    private String id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserProfile user;

    @Column(name = "organization_name", nullable = false, length = 200)
    private String organizationName;

    @Column(name = "verification_status", nullable = false, length = 20)
    private String verificationStatus = "PENDING";

    @Column(name = "verification_docs_url", length = 500)
    private String verificationDocsUrl;

    @Column(name = "verified_by_admin_id", length = 36)
    private String verifiedByAdminId;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    public OrganizerDetails() {
    }

    public OrganizerDetails(String id, UserProfile user, String organizationName, String verificationStatus, String verificationDocsUrl, String verifiedByAdminId, LocalDateTime verifiedAt, String rejectionReason) {
        this.id = id;
        this.user = user;
        this.organizationName = organizationName;
        this.verificationStatus = verificationStatus != null ? verificationStatus : "PENDING";
        this.verificationDocsUrl = verificationDocsUrl;
        this.verifiedByAdminId = verifiedByAdminId;
        this.verifiedAt = verifiedAt;
        this.rejectionReason = rejectionReason;
    }

    public static OrganizerDetailsBuilder builder() {
        return new OrganizerDetailsBuilder();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserProfile getUser() {
        return user;
    }

    public void setUser(UserProfile user) {
        this.user = user;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public String getVerificationDocsUrl() {
        return verificationDocsUrl;
    }

    public void setVerificationDocsUrl(String verificationDocsUrl) {
        this.verificationDocsUrl = verificationDocsUrl;
    }

    public String getVerifiedByAdminId() {
        return verifiedByAdminId;
    }

    public void setVerifiedByAdminId(String verifiedByAdminId) {
        this.verifiedByAdminId = verifiedByAdminId;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.verificationStatus == null) {
            this.verificationStatus = "PENDING";
        }
    }

    public static class OrganizerDetailsBuilder {
        private String id;
        private UserProfile user;
        private String organizationName;
        private String verificationStatus = "PENDING";
        private String verificationDocsUrl;
        private String verifiedByAdminId;
        private LocalDateTime verifiedAt;
        private String rejectionReason;

        public OrganizerDetailsBuilder id(String id) {
            this.id = id;
            return this;
        }

        public OrganizerDetailsBuilder user(UserProfile user) {
            this.user = user;
            return this;
        }

        public OrganizerDetailsBuilder organizationName(String organizationName) {
            this.organizationName = organizationName;
            return this;
        }

        public OrganizerDetailsBuilder verificationStatus(String verificationStatus) {
            this.verificationStatus = verificationStatus;
            return this;
        }

        public OrganizerDetailsBuilder verificationDocsUrl(String verificationDocsUrl) {
            this.verificationDocsUrl = verificationDocsUrl;
            return this;
        }

        public OrganizerDetailsBuilder verifiedByAdminId(String verifiedByAdminId) {
            this.verifiedByAdminId = verifiedByAdminId;
            return this;
        }

        public OrganizerDetailsBuilder verifiedAt(LocalDateTime verifiedAt) {
            this.verifiedAt = verifiedAt;
            return this;
        }

        public OrganizerDetailsBuilder rejectionReason(String rejectionReason) {
            this.rejectionReason = rejectionReason;
            return this;
        }

        public OrganizerDetails build() {
            return new OrganizerDetails(id, user, organizationName, verificationStatus, verificationDocsUrl, verifiedByAdminId, verifiedAt, rejectionReason);
        }
    }
}
