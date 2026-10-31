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

    @Column(name = "contact_phone", length = 30)
    private String contactPhone;

    @Column(name = "city_location", length = 100)
    private String cityLocation;

    @Column(name = "license_number", length = 100)
    private String licenseNumber;

    @Column(name = "verification_status", nullable = false, length = 20)
    private String verificationStatus = "PENDING";

    @Column(name = "verification_docs_url", columnDefinition = "TEXT")
    private String verificationDocsUrl;

    @Column(name = "verified_by_admin_id", length = 36)
    private String verifiedByAdminId;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    @Column(name = "ocr_document_type", length = 50)
    private String ocrDocumentType;

    @Column(name = "ocr_extracted_id", length = 100)
    private String ocrExtractedId;

    @Column(name = "ocr_confidence_score")
    private Double ocrConfidenceScore;

    @Column(name = "ocr_status", length = 30)
    private String ocrStatus;

    @Column(name = "ocr_summary", columnDefinition = "TEXT")
    private String ocrSummary;

    public OrganizerDetails() {
    }

    public OrganizerDetails(String id, UserProfile user, String organizationName, String contactPhone, String cityLocation, String licenseNumber, String verificationStatus, String verificationDocsUrl, String verifiedByAdminId, LocalDateTime verifiedAt, String rejectionReason) {
        this.id = id;
        this.user = user;
        this.organizationName = organizationName;
        this.contactPhone = contactPhone;
        this.cityLocation = cityLocation;
        this.licenseNumber = licenseNumber;
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

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getCityLocation() {
        return cityLocation;
    }

    public void setCityLocation(String cityLocation) {
        this.cityLocation = cityLocation;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
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

    public String getOcrDocumentType() {
        return ocrDocumentType;
    }

    public void setOcrDocumentType(String ocrDocumentType) {
        this.ocrDocumentType = ocrDocumentType;
    }

    public String getOcrExtractedId() {
        return ocrExtractedId;
    }

    public void setOcrExtractedId(String ocrExtractedId) {
        this.ocrExtractedId = ocrExtractedId;
    }

    public Double getOcrConfidenceScore() {
        return ocrConfidenceScore;
    }

    public void setOcrConfidenceScore(Double ocrConfidenceScore) {
        this.ocrConfidenceScore = ocrConfidenceScore;
    }

    public String getOcrStatus() {
        return ocrStatus;
    }

    public void setOcrStatus(String ocrStatus) {
        this.ocrStatus = ocrStatus;
    }

    public String getOcrSummary() {
        return ocrSummary;
    }

    public void setOcrSummary(String ocrSummary) {
        this.ocrSummary = ocrSummary;
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
        private String contactPhone;
        private String cityLocation;
        private String licenseNumber;
        private String verificationStatus = "PENDING";
        private String verificationDocsUrl;
        private String verifiedByAdminId;
        private LocalDateTime verifiedAt;
        private String rejectionReason;
        private String ocrDocumentType;
        private String ocrExtractedId;
        private Double ocrConfidenceScore;
        private String ocrStatus;
        private String ocrSummary;

        public OrganizerDetailsBuilder ocrDocumentType(String ocrDocumentType) {
            this.ocrDocumentType = ocrDocumentType;
            return this;
        }

        public OrganizerDetailsBuilder ocrExtractedId(String ocrExtractedId) {
            this.ocrExtractedId = ocrExtractedId;
            return this;
        }

        public OrganizerDetailsBuilder ocrConfidenceScore(Double ocrConfidenceScore) {
            this.ocrConfidenceScore = ocrConfidenceScore;
            return this;
        }

        public OrganizerDetailsBuilder ocrStatus(String ocrStatus) {
            this.ocrStatus = ocrStatus;
            return this;
        }

        public OrganizerDetailsBuilder ocrSummary(String ocrSummary) {
            this.ocrSummary = ocrSummary;
            return this;
        }

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

        public OrganizerDetailsBuilder contactPhone(String contactPhone) {
            this.contactPhone = contactPhone;
            return this;
        }

        public OrganizerDetailsBuilder cityLocation(String cityLocation) {
            this.cityLocation = cityLocation;
            return this;
        }

        public OrganizerDetailsBuilder licenseNumber(String licenseNumber) {
            this.licenseNumber = licenseNumber;
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
            OrganizerDetails details = new OrganizerDetails(id, user, organizationName, contactPhone, cityLocation, licenseNumber, verificationStatus, verificationDocsUrl, verifiedByAdminId, verifiedAt, rejectionReason);
            details.setOcrDocumentType(this.ocrDocumentType);
            details.setOcrExtractedId(this.ocrExtractedId);
            details.setOcrConfidenceScore(this.ocrConfidenceScore);
            details.setOcrStatus(this.ocrStatus);
            details.setOcrSummary(this.ocrSummary);
            return details;
        }
    }
}
