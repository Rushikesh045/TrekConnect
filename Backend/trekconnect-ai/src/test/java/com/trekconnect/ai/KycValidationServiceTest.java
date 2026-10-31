package com.trekconnect.ai;

import com.trekconnect.ai.dto.request.KycDocumentVerifyRequest;
import com.trekconnect.ai.dto.response.KycVerificationResult;
import com.trekconnect.ai.service.DocumentOcrService;
import com.trekconnect.ai.service.KycValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class KycValidationServiceTest {

    private KycValidationService kycValidationService;

    @BeforeEach
    void setUp() {
        DocumentOcrService ocrService = new DocumentOcrService();
        kycValidationService = new KycValidationService(ocrService);
    }

    @Test
    void testPanCardExtractionAndVerification() {
        String panDocText = "INCOME TAX DEPARTMENT GOVT OF INDIA PERMANENT ACCOUNT NUMBER CARD ABCPE1234F NAME RUSHIKESH GHUGE FATHER SUBHASH GHUGE DOB 15/08/1998";
        String base64Data = Base64.getEncoder().encodeToString(panDocText.getBytes(StandardCharsets.UTF_8));

        KycDocumentVerifyRequest req = new KycDocumentVerifyRequest("Rushikesh Ghuge", base64Data);
        req.setDocumentFileName("pan_card.txt");

        KycVerificationResult result = kycValidationService.verifyDocument(req);

        assertNotNull(result);
        assertEquals("PAN_CARD", result.getDocumentTypeDetected());
        assertEquals("ABCPE1234F", result.getExtractedIdNumber());
        assertTrue(result.getNameMatchScore() >= 80.0, "Name match should be >= 80%");
        assertEquals("AUTO_VERIFIED", result.getStatus());
        assertTrue(result.getConfidenceScore() >= 75.0);
    }

    @Test
    void testAadhaarCardPatternAndNameMatching() {
        // "2345 6789 0124" is mathematically valid under Verhoeff algorithm
        String aadhaarText = "GOVERNMENT OF INDIA UNIQUE IDENTIFICATION AUTHORITY OF INDIA 2345 6789 0124 VIKRAMADITYA SINGH DOB 22/03/1995 MALE";
        String base64Data = Base64.getEncoder().encodeToString(aadhaarText.getBytes(StandardCharsets.UTF_8));

        KycDocumentVerifyRequest req = new KycDocumentVerifyRequest("Vikramaditya Singh", base64Data);

        KycVerificationResult result = kycValidationService.verifyDocument(req);

        assertNotNull(result);
        assertEquals("AADHAAR_CARD", result.getDocumentTypeDetected());
        assertEquals("234567890124", result.getExtractedIdNumber());
        assertTrue(result.getNameMatchScore() >= 80.0);
        assertEquals("AUTO_VERIFIED", result.getStatus());
    }

    @Test
    void testAadhaarCardVerhoeffChecksumFailure() {
        // "2345 6789 0123" has a wrong check-digit (3 instead of 4)
        String aadhaarText = "GOVERNMENT OF INDIA UNIQUE IDENTIFICATION AUTHORITY OF INDIA 2345 6789 0123 VIKRAMADITYA SINGH DOB 22/03/1995 MALE";
        String base64Data = Base64.getEncoder().encodeToString(aadhaarText.getBytes(StandardCharsets.UTF_8));

        KycDocumentVerifyRequest req = new KycDocumentVerifyRequest("Vikramaditya Singh", base64Data);

        KycVerificationResult result = kycValidationService.verifyDocument(req);

        assertNotNull(result);
        assertEquals("AADHAAR_CARD", result.getDocumentTypeDetected());
        assertEquals("INVALID_FORMAT", result.getStatus());
        assertTrue(result.getVerificationFlags().contains("ID_FORMAT_ANOMALY_DETECTED"));
    }

    @Test
    void testNameMismatchDetection() {
        String docText = "INCOME TAX DEPARTMENT PERMANENT ACCOUNT NUMBER CARD XYZPQ9876R NAME AMIT SHARMA";
        String base64Data = Base64.getEncoder().encodeToString(docText.getBytes(StandardCharsets.UTF_8));

        KycDocumentVerifyRequest req = new KycDocumentVerifyRequest("Rushikesh Ghuge", base64Data);

        KycVerificationResult result = kycValidationService.verifyDocument(req);

        assertNotNull(result);
        assertEquals("NAME_MISMATCH", result.getStatus());
        assertTrue(result.getNameMatchScore() < 60.0);
    }

    @Test
    void testJaroWinklerWithReversedNames() {
        double score = kycValidationService.computeFuzzyNameMatch("Rushikesh Ghuge", "GHUGE RUSHIKESH", "");
        assertTrue(score >= 85.0, "Token sort or Jaro-Winkler should recognize reversed names");
    }
}
