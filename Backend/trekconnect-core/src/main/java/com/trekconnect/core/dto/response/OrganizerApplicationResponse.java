package com.trekconnect.core.dto.response;

import java.time.LocalDateTime;

/**
 * Response DTO returning organizer verification application details and status.
 */
public class OrganizerApplicationResponse {

    private String id;
    private String userId;
    private String organizationName;
    private String verificationStatus;
    private String verificationDocsUrl;
    private String rejectionReason;
    private LocalDateTime verifiedAt;

    public OrganizerApplicationResponse() {
    }

    public OrganizerApplicationResponse(String id, String userId, String organizationName, String verificationStatus, String verificationDocsUrl, String rejectionReason, LocalDateTime verifiedAt) {
        this.id = id;
        this.userId = userId;
        this.organizationName = organizationName;
        this.verificationStatus = verificationStatus;
        this.verificationDocsUrl = verificationDocsUrl;
        this.rejectionReason = rejectionReason;
        this.verifiedAt = verifiedAt;
    }

    public static OrganizerApplicationResponseBuilder builder() {
        return new OrganizerApplicationResponseBuilder();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public static class OrganizerApplicationResponseBuilder {
        private String id;
        private String userId;
        private String organizationName;
        private String verificationStatus;
        private String verificationDocsUrl;
        private String rejectionReason;
        private LocalDateTime verifiedAt;

        public OrganizerApplicationResponseBuilder id(String id) {
            this.id = id;
            return this;
        }

        public OrganizerApplicationResponseBuilder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public OrganizerApplicationResponseBuilder organizationName(String organizationName) {
            this.organizationName = organizationName;
            return this;
        }

        public OrganizerApplicationResponseBuilder verificationStatus(String verificationStatus) {
            this.verificationStatus = verificationStatus;
            return this;
        }

        public OrganizerApplicationResponseBuilder verificationDocsUrl(String verificationDocsUrl) {
            this.verificationDocsUrl = verificationDocsUrl;
            return this;
        }

        public OrganizerApplicationResponseBuilder rejectionReason(String rejectionReason) {
            this.rejectionReason = rejectionReason;
            return this;
        }

        public OrganizerApplicationResponseBuilder verifiedAt(LocalDateTime verifiedAt) {
            this.verifiedAt = verifiedAt;
            return this;
        }

        public OrganizerApplicationResponse build() {
            return new OrganizerApplicationResponse(id, userId, organizationName, verificationStatus, verificationDocsUrl, rejectionReason, verifiedAt);
        }
    }
}
