package com.trekconnect.ai.dto.response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Response DTO returned by the OCR & KYC engine containing extracted document credentials,
 * fuzzy name match scores, and automated verification recommendations.
 */
public class KycVerificationResult {

    private String status; // "AUTO_VERIFIED", "FLAGGED_FOR_REVIEW", "NAME_MISMATCH", "INVALID_FORMAT", "UNREADABLE_DOCUMENT"
    private Double confidenceScore; // 0.0 to 100.0
    private String documentTypeDetected; // "PAN_CARD", "AADHAAR_CARD", "PASSPORT", "MOUNTAINEERING_CERTIFICATE", "UNKNOWN"
    private String extractedIdNumber;
    private String extractedName;
    private Double nameMatchScore; // 0.0 to 100.0
    private Double organizationMatchScore;
    private String summary;
    private List<String> verificationFlags = new ArrayList<>();
    private String extractedTextPreview;
    private LocalDateTime timestamp = LocalDateTime.now();

    public KycVerificationResult() {
    }

    public static KycVerificationResultBuilder builder() {
        return new KycVerificationResultBuilder();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(Double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public String getDocumentTypeDetected() {
        return documentTypeDetected;
    }

    public void setDocumentTypeDetected(String documentTypeDetected) {
        this.documentTypeDetected = documentTypeDetected;
    }

    public String getExtractedIdNumber() {
        return extractedIdNumber;
    }

    public void setExtractedIdNumber(String extractedIdNumber) {
        this.extractedIdNumber = extractedIdNumber;
    }

    public String getExtractedName() {
        return extractedName;
    }

    public void setExtractedName(String extractedName) {
        this.extractedName = extractedName;
    }

    public Double getNameMatchScore() {
        return nameMatchScore;
    }

    public void setNameMatchScore(Double nameMatchScore) {
        this.nameMatchScore = nameMatchScore;
    }

    public Double getOrganizationMatchScore() {
        return organizationMatchScore;
    }

    public void setOrganizationMatchScore(Double organizationMatchScore) {
        this.organizationMatchScore = organizationMatchScore;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<String> getVerificationFlags() {
        return verificationFlags;
    }

    public void setVerificationFlags(List<String> verificationFlags) {
        this.verificationFlags = verificationFlags;
    }

    public String getExtractedTextPreview() {
        return extractedTextPreview;
    }

    public void setExtractedTextPreview(String extractedTextPreview) {
        this.extractedTextPreview = extractedTextPreview;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public static class KycVerificationResultBuilder {
        private String status;
        private Double confidenceScore = 0.0;
        private String documentTypeDetected = "UNKNOWN";
        private String extractedIdNumber;
        private String extractedName;
        private Double nameMatchScore = 0.0;
        private Double organizationMatchScore = 0.0;
        private String summary;
        private List<String> verificationFlags = new ArrayList<>();
        private String extractedTextPreview;

        public KycVerificationResultBuilder status(String status) {
            this.status = status;
            return this;
        }

        public KycVerificationResultBuilder confidenceScore(Double confidenceScore) {
            this.confidenceScore = confidenceScore;
            return this;
        }

        public KycVerificationResultBuilder documentTypeDetected(String documentTypeDetected) {
            this.documentTypeDetected = documentTypeDetected;
            return this;
        }

        public KycVerificationResultBuilder extractedIdNumber(String extractedIdNumber) {
            this.extractedIdNumber = extractedIdNumber;
            return this;
        }

        public KycVerificationResultBuilder extractedName(String extractedName) {
            this.extractedName = extractedName;
            return this;
        }

        public KycVerificationResultBuilder nameMatchScore(Double nameMatchScore) {
            this.nameMatchScore = nameMatchScore;
            return this;
        }

        public KycVerificationResultBuilder organizationMatchScore(Double organizationMatchScore) {
            this.organizationMatchScore = organizationMatchScore;
            return this;
        }

        public KycVerificationResultBuilder summary(String summary) {
            this.summary = summary;
            return this;
        }

        public KycVerificationResultBuilder verificationFlags(List<String> verificationFlags) {
            this.verificationFlags = verificationFlags;
            return this;
        }

        public KycVerificationResultBuilder extractedTextPreview(String extractedTextPreview) {
            this.extractedTextPreview = extractedTextPreview;
            return this;
        }

        public KycVerificationResult build() {
            KycVerificationResult res = new KycVerificationResult();
            res.setStatus(this.status);
            res.setConfidenceScore(this.confidenceScore);
            res.setDocumentTypeDetected(this.documentTypeDetected);
            res.setExtractedIdNumber(this.extractedIdNumber);
            res.setExtractedName(this.extractedName);
            res.setNameMatchScore(this.nameMatchScore);
            res.setOrganizationMatchScore(this.organizationMatchScore);
            res.setSummary(this.summary);
            res.setVerificationFlags(this.verificationFlags != null ? this.verificationFlags : new ArrayList<>());
            res.setExtractedTextPreview(this.extractedTextPreview);
            res.setTimestamp(LocalDateTime.now());
            return res;
        }
    }
}
