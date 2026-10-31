package com.trekconnect.ai.service;

import com.trekconnect.ai.dto.request.KycDocumentVerifyRequest;
import com.trekconnect.ai.dto.response.KycVerificationResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Automated KYC Document Cross-Validation & Decision Engine.
 * 
 * CORE RESPONSIBILITIES:
 * 1. Fuzzy Name Matching: Computes Jaro-Winkler & Token-Sort similarity between user profile and document.
 * 2. Algorithmic Checksum & Format Validation: Validates PAN card 4th character and Aadhaar Verhoeff checksum.
 * 3. Confidence Scoring: Aggregates detection weights into an audited 0.0 to 100.0 confidence score.
 * 4. Anti-Fraud Classification: Emits AUTO_VERIFIED, REQUIRES_MANUAL_REVIEW, or NAME_MISMATCH statuses.
 */
@Service
public class KycValidationService {

    private final DocumentOcrService ocrService;

    @Value("${kyc.ocr.min-confidence-threshold:75.0}")
    private double minConfidenceThreshold = 75.0;

    @Value("${kyc.ocr.name-match-threshold:75.0}")
    private double nameMatchThreshold = 75.0;

    public KycValidationService(DocumentOcrService ocrService) {
        this.ocrService = ocrService;
    }

    public KycVerificationResult verifyDocument(KycDocumentVerifyRequest request) {
        String applicantName = request.getApplicantFullName() != null ? request.getApplicantFullName().trim() : "";
        String base64Payload = request.getDocumentBase64Data() != null ? request.getDocumentBase64Data() : "";
        String fileName = request.getDocumentFileName();

        // 1. Run OCR Extraction
        DocumentOcrService.ExtractedDocumentData ocrData = ocrService.extractDocument(base64Payload, fileName, request.getDocumentTypeHint());

        List<String> flags = new ArrayList<>(ocrData.flags);
        String docType = ocrData.documentType;
        String idNumber = ocrData.idNumber;
        String docName = ocrData.extractedName;

        // If hint was provided and detection is UNKNOWN, inspect text with hint
        if ("UNKNOWN".equals(docType) && request.getDocumentTypeHint() != null && !request.getDocumentTypeHint().isBlank()) {
            docType = request.getDocumentTypeHint().toUpperCase();
            flags.add("DOCUMENT_TYPE_INFERRED_FROM_HINT");
        }

        // 2. Fuzzy Name Matching
        double nameMatchScore = computeFuzzyNameMatch(applicantName, docName, ocrData.rawText);
        if (nameMatchScore >= nameMatchThreshold) {
            flags.add(String.format("NAME_MATCH_VERIFIED_%.1f%%", nameMatchScore));
        } else if (nameMatchScore < 50.0 && !docName.isBlank()) {
            flags.add(String.format("NAME_DISCREPANCY_FLAGGED_%.1f%%", nameMatchScore));
        }

        // 3. Organization Matching (if organization name provided)
        double orgMatchScore = 0.0;
        if (request.getOrganizationName() != null && !request.getOrganizationName().isBlank()) {
            orgMatchScore = computeFuzzyStringMatch(request.getOrganizationName(), ocrData.rawText);
            if (orgMatchScore >= 70.0) {
                flags.add("ORGANIZATION_AFFILIATION_DETECTED");
            }
        }

        // 4. ID Format Checksum Validation
        boolean isIdValid = validateIdFormat(docType, idNumber);
        if (isIdValid && !idNumber.isBlank()) {
            flags.add("ID_FORMAT_CHECKSUM_VALIDATED");
        } else if (!idNumber.isBlank()) {
            flags.add("ID_FORMAT_ANOMALY_DETECTED");
        }

        // 5. Calculate Composite Confidence Score
        double confidence = 0.0;
        if (!"UNKNOWN".equals(docType)) confidence += 25.0;
        if (isIdValid) confidence += 30.0;
        confidence += (nameMatchScore * 0.35); // Max 35 points
        if (ocrData.rawText != null && ocrData.rawText.length() > 20) confidence += 10.0;

        confidence = Math.min(100.0, Math.max(0.0, confidence));

        // 6. Decision Status Engine
        String status;
        StringBuilder summary = new StringBuilder();

        if (confidence >= minConfidenceThreshold && nameMatchScore >= nameMatchThreshold && isIdValid) {
            status = "AUTO_VERIFIED";
            summary.append(String.format("Document successfully verified by AI OCR. Valid %s (ID: %s) matching applicant '%s' with %.1f%% confidence.",
                    docType.replace("_", " "), idNumber, applicantName, confidence));
        } else if (nameMatchScore < 50.0 && !docName.isBlank()) {
            status = "NAME_MISMATCH";
            summary.append(String.format("Name on document ('%s') does not match registered applicant name ('%s'). Match score: %.1f%%.",
                    docName, applicantName, nameMatchScore));
        } else if (!isIdValid && !idNumber.isBlank()) {
            status = "INVALID_FORMAT";
            summary.append(String.format("Extracted identifier '%s' failed checksum and pattern validation for %s.",
                    idNumber, docType));
        } else if (flags.contains("RASTER_IMAGE_DETECTED") && idNumber.isBlank()) {
            status = "FLAGGED_FOR_REVIEW";
            confidence = Math.max(confidence, 55.0);
            summary.append("Uploaded image file received. For instant sub-second AI auto-verification, upload an official digital PDF (e-Aadhaar, e-PAN, or mountaineering certification). Your image has been queued for expedited administrator review.");
        } else if (confidence < 40.0) {
            status = "UNREADABLE_DOCUMENT";
            summary.append("Document image resolution is too low or text is obscured. Please upload a clear, high-resolution photo or PDF.");
        } else {
            status = "FLAGGED_FOR_REVIEW";
            summary.append(String.format("AI extracted %s with %.1f%% confidence. Manual administrator review recommended before approval.",
                    docType.replace("_", " "), confidence));
        }

        String preview = ocrData.rawText.length() > 300 
                ? ocrData.rawText.substring(0, 300) + "..." 
                : ocrData.rawText;

        return KycVerificationResult.builder()
                .status(status)
                .confidenceScore(confidence)
                .documentTypeDetected(docType)
                .extractedIdNumber(idNumber.isBlank() ? "N/A" : idNumber)
                .extractedName(docName.isBlank() ? applicantName : docName)
                .nameMatchScore(nameMatchScore)
                .organizationMatchScore(orgMatchScore)
                .summary(summary.toString())
                .verificationFlags(flags)
                .extractedTextPreview(preview)
                .build();
    }

    private boolean validateIdFormat(String docType, String idNumber) {
        if (idNumber == null || idNumber.isBlank()) return false;
        String clean = idNumber.replaceAll("\\s+", "").toUpperCase();

        switch (docType) {
            case "PAN_CARD":
                // 5 letters, 4 digits, 1 letter. 4th letter is entity status: P (Person), C (Company), F (Firm), H (HUF), A, T, B, L, J, G
                return clean.matches("^[A-Z]{3}[PCHFATBLJG][A-Z][0-9]{4}[A-Z]$");
            case "AADHAAR_CARD":
                if (!clean.matches("^[2-9][0-9]{11}$")) {
                    return false;
                }
                // Mathematical Verhoeff dihedral group D5 check-digit validation
                return VerhoeffAlgorithm.validateVerhoeff(clean);
            case "PASSPORT":
                return clean.matches("^[A-PR-WYa-pr-wy][1-9][0-9]{6}$");
            case "MOUNTAINEERING_CERTIFICATE":
                return clean.length() >= 5;
            default:
                return clean.length() >= 4;
        }
    }

    /**
     * Computes fuzzy name matching using Jaro-Winkler and Token-Sort logic.
     * Handles reversed names (e.g. "Ghuge Rushikesh" vs "Rushikesh Ghuge").
     */
    public double computeFuzzyNameMatch(String expectedName, String extractedName, String rawText) {
        if (expectedName == null || expectedName.isBlank()) return 0.0;
        String normExpected = normalize(expectedName);

        // Check 1: If extracted name is available, compare directly
        if (extractedName != null && !extractedName.isBlank()) {
            String normExtracted = normalize(extractedName);
            double jaroWinkler = jaroWinklerDistance(normExpected, normExtracted);
            double tokenSort = tokenSortSimilarity(normExpected, normExtracted);
            double score = Math.max(jaroWinkler, tokenSort) * 100.0;
            if (score >= 70.0) return score;
        }

        // Check 2: Check if expected name tokens appear in the raw extracted text
        if (rawText != null && !rawText.isBlank()) {
            String normText = normalize(rawText);
            String[] tokens = normExpected.split("\\s+");
            int matches = 0;
            for (String t : tokens) {
                if (t.length() >= 3 && normText.contains(t)) {
                    matches++;
                }
            }
            if (tokens.length > 0) {
                double tokenMatch = ((double) matches / tokens.length) * 100.0;
                if (tokenMatch >= 75.0) {
                    return Math.min(98.0, tokenMatch + 10.0);
                }
            }
        }

        return extractedName != null && !extractedName.isBlank() 
                ? jaroWinklerDistance(normExpected, normalize(extractedName)) * 100.0 
                : 60.0; // Baseline fallback for valid documents
    }

    private double computeFuzzyStringMatch(String s1, String s2) {
        if (s1 == null || s2 == null) return 0.0;
        String norm1 = normalize(s1);
        String norm2 = normalize(s2);
        if (norm2.contains(norm1)) return 100.0;
        return jaroWinklerDistance(norm1, norm2) * 100.0;
    }

    private String normalize(String str) {
        return str.toUpperCase(Locale.ENGLISH).replaceAll("[^A-Z0-9\\s]", " ").replaceAll("\\s+", " ").trim();
    }

    private double tokenSortSimilarity(String s1, String s2) {
        String[] arr1 = s1.split("\\s+");
        String[] arr2 = s2.split("\\s+");
        Arrays.sort(arr1);
        Arrays.sort(arr2);
        String sorted1 = String.join(" ", arr1);
        String sorted2 = String.join(" ", arr2);
        return jaroWinklerDistance(sorted1, sorted2);
    }

    /**
     * Standard Jaro-Winkler string distance algorithm implementation.
     */
    public double jaroWinklerDistance(String s1, String s2) {
        if (s1.equals(s2)) return 1.0;
        if (s1.isEmpty() || s2.isEmpty()) return 0.0;

        int matchDistance = Math.max(s1.length(), s2.length()) / 2 - 1;
        boolean[] s1Matches = new boolean[s1.length()];
        boolean[] s2Matches = new boolean[s2.length()];

        int matches = 0;
        for (int i = 0; i < s1.length(); i++) {
            int start = Math.max(0, i - matchDistance);
            int end = Math.min(i + matchDistance + 1, s2.length());
            for (int j = start; j < end; j++) {
                if (!s2Matches[j] && s1.charAt(i) == s2.charAt(j)) {
                    s1Matches[i] = true;
                    s2Matches[j] = true;
                    matches++;
                    break;
                }
            }
        }

        if (matches == 0) return 0.0;

        double transpositions = 0;
        int k = 0;
        for (int i = 0; i < s1.length(); i++) {
            if (s1Matches[i]) {
                while (!s2Matches[k]) k++;
                if (s1.charAt(i) != s2.charAt(k)) transpositions++;
                k++;
            }
        }

        double jaro = ((matches / (double) s1.length()) +
                       (matches / (double) s2.length()) +
                       ((matches - transpositions / 2.0) / matches)) / 3.0;

        // Winkler prefix bonus (up to 4 matching initial characters)
        int prefixLength = 0;
        for (int i = 0; i < Math.min(4, Math.min(s1.length(), s2.length())); i++) {
            if (s1.charAt(i) == s2.charAt(i)) {
                prefixLength++;
            } else {
                break;
            }
        }

        return jaro + (prefixLength * 0.1 * (1.0 - jaro));
    }
}
