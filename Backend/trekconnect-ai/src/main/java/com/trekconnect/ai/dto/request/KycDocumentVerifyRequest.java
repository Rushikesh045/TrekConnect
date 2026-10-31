package com.trekconnect.ai.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for submitting an organizer KYC identity document for automated OCR parsing and validation.
 */
public class KycDocumentVerifyRequest {

    private String applicantUserId;

    @NotBlank(message = "Applicant full name is required for KYC identity matching")
    private String applicantFullName;

    private String organizationName;

    private String documentTypeHint; // "PAN", "AADHAAR", "PASSPORT", "MOUNTAINEERING_CERTIFICATE", or "AUTO_DETECT"

    private String documentBase64Data; // Base64 data URI or raw base64 string

    private String documentFileName;

    private String documentUrl;

    public KycDocumentVerifyRequest() {
    }

    public KycDocumentVerifyRequest(String applicantFullName, String documentBase64Data) {
        this.applicantFullName = applicantFullName;
        this.documentBase64Data = documentBase64Data;
    }

    public String getApplicantUserId() {
        return applicantUserId;
    }

    public void setApplicantUserId(String applicantUserId) {
        this.applicantUserId = applicantUserId;
    }

    public String getApplicantFullName() {
        return applicantFullName;
    }

    public void setApplicantFullName(String applicantFullName) {
        this.applicantFullName = applicantFullName;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getDocumentTypeHint() {
        return documentTypeHint;
    }

    public void setDocumentTypeHint(String documentTypeHint) {
        this.documentTypeHint = documentTypeHint;
    }

    public String getDocumentBase64Data() {
        return documentBase64Data;
    }

    public void setDocumentBase64Data(String documentBase64Data) {
        this.documentBase64Data = documentBase64Data;
    }

    public String getDocumentFileName() {
        return documentFileName;
    }

    public void setDocumentFileName(String documentFileName) {
        this.documentFileName = documentFileName;
    }

    public String getDocumentUrl() {
        return documentUrl;
    }

    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }
}
