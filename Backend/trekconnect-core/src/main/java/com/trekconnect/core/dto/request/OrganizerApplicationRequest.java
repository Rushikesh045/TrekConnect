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

    private String contactPhone;

    private String cityLocation;

    private String licenseNumber;

    private String verificationDocsUrl;

    public OrganizerApplicationRequest() {
    }

    public OrganizerApplicationRequest(String organizationName, String contactPhone, String cityLocation, String licenseNumber, String verificationDocsUrl) {
        this.organizationName = organizationName;
        this.contactPhone = contactPhone;
        this.cityLocation = cityLocation;
        this.licenseNumber = licenseNumber;
        this.verificationDocsUrl = verificationDocsUrl;
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

    public String getVerificationDocsUrl() {
        return verificationDocsUrl;
    }

    public void setVerificationDocsUrl(String verificationDocsUrl) {
        this.verificationDocsUrl = verificationDocsUrl;
    }
}
