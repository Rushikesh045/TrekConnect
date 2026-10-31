package com.trekconnect.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Native Ollama LLM Client Integration Service.
 * 
 * CORE RESPONSIBILITIES:
 * 1. Interfaces with local Ollama daemon (default: http://localhost:11434).
 * 2. Executes zero-cost, private inference using open-source models (Llama 3.2, Mistral, Gemma 2).
 * 3. Provides graceful fallback to the in-JVM dynamic multi-region engine when Ollama is offline.
 */
@Service
public class OllamaAiClientService {

    private static final Logger logger = LoggerFactory.getLogger(OllamaAiClientService.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${ollama.api.url:http://localhost:11434}")
    private String ollamaApiUrl;

    @Value("${ollama.model.name:llama3.2:3b}")
    private String modelName;

    @Value("${ollama.enabled:true}")
    private boolean enabled;

    public OllamaAiClientService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Checks if Ollama service is reachable and running locally.
     */
    public boolean isAvailable() {
        if (!enabled) return false;
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(ollamaApiUrl + "/api/tags", String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            logger.debug("Ollama local service not reachable at {}: {}", ollamaApiUrl, e.getMessage());
            return false;
        }
    }

    /**
     * Generates a context-grounded response via Ollama using the configured model.
     * 
     * @param systemPrompt System instructions specifying role and constraints
     * @param userPrompt   User query combined with retrieved RAG context
     * @return Optional containing the generated text, or empty if Ollama is unavailable
     */
    public Optional<String> generateResponse(String systemPrompt, String userPrompt) {
        if (!enabled) return Optional.empty();

        try {
            Map<String, Object> requestPayload = new LinkedHashMap<>();
            requestPayload.put("model", modelName);
            requestPayload.put("system", systemPrompt);
            requestPayload.put("prompt", userPrompt);
            requestPayload.put("stream", false);

            // CPU optimization parameters to ensure fast inference without timeouts
            Map<String, Object> options = new LinkedHashMap<>();
            options.put("num_predict", 160);
            options.put("temperature", 0.5);
            requestPayload.put("options", options);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestPayload, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    ollamaApiUrl + "/api/generate",
                    requestEntity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                if (rootNode.has("response")) {
                    String generatedText = rootNode.get("response").asText();
                    if (generatedText != null && !generatedText.isBlank()) {
                        logger.info("Successfully generated AI response via Ollama model '{}'", modelName);
                        return Optional.of(generatedText.trim());
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Ollama inference bypassed, falling back to in-JVM RAG engine: {}", e.getMessage());
        }

        return Optional.empty();
    }

    public String getModelName() {
        return modelName;
    }

    public String getOllamaApiUrl() {
        return ollamaApiUrl;
    }
}
