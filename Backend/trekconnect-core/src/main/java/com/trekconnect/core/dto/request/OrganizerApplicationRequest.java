package com.trekconnect.core.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for submitting an Organizer verification application.
 */
public class OrganizerApplicationRequest {

    @NotBlank(message = "Organization name is required")
    @Size(min = 3, max = 200, message = "Organization name must be between 3 and 200 characters")
    private String organizationName;

    @Size(max = 500, message = "Verification documents URL must be less than 500 characters")
    private String verificationDocsUrl;

    public OrganizerApplicationRequest() {
    }

    public OrganizerApplicationRequest(String organizationName, String verificationDocsUrl) {
        this.organizationName = organizationName;
        this.verificationDocsUrl = verificationDocsUrl;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getVerificationDocsUrl() {
        return verificationDocsUrl;
    }

    public void setVerificationDocsUrl(String verificationDocsUrl) {
        this.verificationDocsUrl = verificationDocsUrl;
    }
}
