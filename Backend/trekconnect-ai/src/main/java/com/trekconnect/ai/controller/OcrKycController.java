package com.trekconnect.ai.controller;

import com.trekconnect.ai.dto.request.KycDocumentVerifyRequest;
import com.trekconnect.ai.dto.response.KycVerificationResult;
import com.trekconnect.ai.service.KycValidationService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * REST Controller for Open-Source OCR & Automated KYC Verification.
 * Ingests identity documents, runs OCR extraction, validates patterns, and scores applicant authenticity.
 */
@RestController
@RequestMapping("/api/ai/kyc")
public class OcrKycController {

    private final KycValidationService kycValidationService;

    public OcrKycController(KycValidationService kycValidationService) {
        this.kycValidationService = kycValidationService;
    }

    /**
     * Verifies an organizer KYC document sent as JSON (Base64 data or data URL).
     */
    @PostMapping("/verify-document")
    public ResponseEntity<KycVerificationResult> verifyDocumentJson(@Valid @RequestBody KycDocumentVerifyRequest request) {
        KycVerificationResult result = kycValidationService.verifyDocument(request);
        return ResponseEntity.ok(result);
    }

    /**
     * Verifies an organizer KYC document uploaded directly as a multipart file.
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<KycVerificationResult> verifyDocumentMultipart(
            @RequestParam("file") MultipartFile file,
            @RequestParam("applicantFullName") String applicantFullName,
            @RequestParam(value = "applicantUserId", required = false) String applicantUserId,
            @RequestParam(value = "organizationName", required = false) String organizationName,
            @RequestParam(value = "documentTypeHint", required = false) String documentTypeHint
    ) {
        try {
            byte[] fileBytes = file.getBytes();
            String base64Data = Base64.getEncoder().encodeToString(fileBytes);

            KycDocumentVerifyRequest req = new KycDocumentVerifyRequest();
            req.setApplicantFullName(applicantFullName);
            req.setApplicantUserId(applicantUserId);
            req.setOrganizationName(organizationName);
            req.setDocumentTypeHint(documentTypeHint);
            req.setDocumentFileName(file.getOriginalFilename());
            req.setDocumentBase64Data(base64Data);

            KycVerificationResult result = kycValidationService.verifyDocument(req);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            KycVerificationResult errResult = KycVerificationResult.builder()
                    .status("UNREADABLE_DOCUMENT")
                    .confidenceScore(0.0)
                    .summary("Failed to read uploaded file: " + e.getMessage())
                    .verificationFlags(Collections.singletonList("FILE_READ_ERROR"))
                    .build();
            return ResponseEntity.badRequest().body(errResult);
        }
    }

    /**
     * Returns metadata on supported document types and security criteria.
     */
    @GetMapping("/supported-docs")
    public ResponseEntity<Map<String, Object>> getSupportedDocs() {
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("supportedFormats", Arrays.asList("PDF", "JPEG", "PNG", "Base64 Data URI"));
        resp.put("maxFileSize", "10 MB");

        List<Map<String, String>> docs = new ArrayList<>();
        docs.add(Map.of("type", "PAN_CARD", "description", "Indian Income Tax Permanent Account Number (Individual or Firm)", "pattern", "5 Letters + 4 Digits + 1 Letter"));
        docs.add(Map.of("type", "AADHAAR_CARD", "description", "Unique Identification Authority of India (UIDAI) 12-Digit Card", "pattern", "12 Digits with Verhoeff Checksum"));
        docs.add(Map.of("type", "PASSPORT", "description", "Republic of India Official Travel Passport", "pattern", "1 Letter + 7 Digits"));
        docs.add(Map.of("type", "MOUNTAINEERING_CERTIFICATE", "description", "Certified Basic/Advanced Mountaineering License (NIM, HMI, ABVIMAS, JIM&WS)", "pattern", "Institute Prefix + Course + Registration ID"));

        resp.put("documentTypes", docs);
        return ResponseEntity.ok(resp);
    }
}
