package com.trekconnect.ai;

import com.trekconnect.ai.dto.request.TrekBotChatRequest;
import com.trekconnect.ai.dto.request.TrekContextPayload;
import com.trekconnect.ai.dto.response.TrekBotChatResponse;
import com.trekconnect.ai.service.OllamaAiClientService;
import com.trekconnect.ai.service.TrekBotRagService;
import com.trekconnect.ai.service.TrekKnowledgeBaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class TrekBotRagServiceTest {

    private TrekBotRagService ragService;
    private OllamaAiClientService ollamaClient;

    @BeforeEach
    void setUp() {
        TrekKnowledgeBaseService kbService = new TrekKnowledgeBaseService();
        ollamaClient = Mockito.mock(OllamaAiClientService.class);
        ragService = new TrekBotRagService(kbService, ollamaClient);
    }

    private TrekContextPayload createSahyadriContext() {
        TrekContextPayload ctx = new TrekContextPayload();
        ctx.setTrekId("101");
        ctx.setTitle("Harishchandragad Monsoon Trek");
        ctx.setLocation("Ahmednagar, Maharashtra");
        ctx.setDifficulty("MODERATE");
        ctx.setAltitudeFt(4671);
        ctx.setPricePerSlot(1499.0);
        ctx.setDurationDays(2);
        ctx.setOrganizerName("Western Ghats Explorers");
        ctx.setInclusions(Arrays.asList("Breakfast", "Authentic Village Lunch", "Guide Fees", "First Aid"));
        ctx.setExclusions(Arrays.asList("Personal Snacks", "Mineral Water"));
        ctx.setUpcomingDate("October 30, 2026");
        return ctx;
    }

    @Test
    void testGuardrailsBlockOffTopicQueries() {
        TrekBotChatRequest req = new TrekBotChatRequest("101", "Harishchandragad Trek", "Write a python script to parse crypto prices");
        req.setTrekContext(createSahyadriContext());

        TrekBotChatResponse res = ragService.generateResponse(req);

        assertNotNull(res);
        assertTrue(res.getIsOffTopic());
        assertTrue(res.getReply().contains("dedicated mountain expedition assistant"));
    }

    @Test
    void testOllamaOfflineReturnsOfflineMessage() {
        // When Ollama service is down / unreachable
        when(ollamaClient.isAvailable()).thenReturn(false);

        TrekBotChatRequest req = new TrekBotChatRequest("101", "Harishchandragad Trek", "What shoes should I pack?");
        TrekBotChatResponse res = ragService.generateResponse(req);

        assertNotNull(res);
        assertFalse(res.getIsOffTopic());
        assertTrue(res.getReply().contains("currently unavailable") || res.getReply().contains("offline"));
        assertEquals("Ollama Service Offline", res.getGroundedIn());
        assertEquals(0.0, res.getConfidenceScore());
    }

    @Test
    void testOllamaInferenceFailureReturnsOfflineMessage() {
        // When Ollama is reachable but generation fails/times out
        when(ollamaClient.isAvailable()).thenReturn(true);
        when(ollamaClient.generateResponse(anyString(), anyString())).thenReturn(Optional.empty());

        TrekBotChatRequest req = new TrekBotChatRequest("101", "Harishchandragad Trek", "What is the trail difficulty?");
        TrekBotChatResponse res = ragService.generateResponse(req);

        assertNotNull(res);
        assertTrue(res.getReply().contains("currently unavailable") || res.getReply().contains("offline"));
        assertEquals("Ollama Service Offline", res.getGroundedIn());
        assertEquals(0.0, res.getConfidenceScore());
    }

    @Test
    void testOllamaTrailRecommendationResponse() {
        // When Ollama is active and successfully generates recommendation
        when(ollamaClient.isAvailable()).thenReturn(true);
        when(ollamaClient.getModelName()).thenReturn("llama3.2:3b");
        when(ollamaClient.generateResponse(anyString(), anyString())).thenReturn(
                Optional.of("Here are top treks near Nashik: 1. Harihar Fort (80-degree cliff staircase), 2. Kalsubai Peak (highest point in Maharashtra).")
        );

        TrekBotChatRequest req = new TrekBotChatRequest(null, null, "will you provide me treks near by nashik");
        TrekBotChatResponse res = ragService.generateResponse(req);

        assertNotNull(res);
        assertFalse(res.getIsOffTopic());
        assertTrue(res.getReply().contains("Harihar Fort"));
        assertTrue(res.getReply().contains("Kalsubai Peak"));
        assertEquals("Ollama (llama3.2:3b) & Regional Trek Directory", res.getGroundedIn());
        assertEquals(0.99, res.getConfidenceScore());
    }

    @Test
    void testOllamaTrekSpecificResponse() {
        // When Ollama generates trek-specific gear/itinerary response
        when(ollamaClient.isAvailable()).thenReturn(true);
        when(ollamaClient.getModelName()).thenReturn("llama3.2:3b");
        when(ollamaClient.generateResponse(anyString(), anyString())).thenReturn(
                Optional.of("For Harishchandragad Monsoon Trek, bring sturdy water-resistant trekking shoes, poncho, and 2L hydration.")
        );

        TrekBotChatRequest req = new TrekBotChatRequest("101", "Harishchandragad Trek", "What shoes and gear should I pack?");
        req.setTrekContext(createSahyadriContext());

        TrekBotChatResponse res = ragService.generateResponse(req);

        assertNotNull(res);
        assertFalse(res.getIsOffTopic());
        assertTrue(res.getReply().contains("Harishchandragad"));
        assertTrue(res.getReply().contains("trekking shoes"));
        assertEquals("Ollama (llama3.2:3b) & Harishchandragad Monsoon Trek", res.getGroundedIn());
    }
}
