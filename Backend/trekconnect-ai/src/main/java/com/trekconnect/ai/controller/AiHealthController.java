package com.trekconnect.ai.controller;

import com.trekconnect.ai.dto.response.AiHealthResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

/**
 * Health & Capabilities Controller for TrekConnect AI Microservice.
 */
@RestController
@RequestMapping("/api/ai/health")
public class AiHealthController {

    @Value("${server.port:8082}")
    private int port;

    @GetMapping
    public ResponseEntity<AiHealthResponse> checkHealth() {
        AiHealthResponse response = new AiHealthResponse(
                "UP",
                "trekconnect-ai",
                "1.0.0",
                port,
                Arrays.asList(
                        "TREKBOT_RAG_ASSISTANT",
                        "CONTEXT_GROUNDING_ENGINE",
                        "OUTDOOR_DOMAIN_GUARDRAILS",
                        "KYC_DOCUMENT_OCR",
                        "INDIAN_ID_PATTERN_VALIDATION",
                        "JARO_WINKLER_FUZZY_NAME_MATCHING",
                        "CONFIDENCE_SCORING_ENGINE"
                )
        );
        return ResponseEntity.ok(response);
    }
}
