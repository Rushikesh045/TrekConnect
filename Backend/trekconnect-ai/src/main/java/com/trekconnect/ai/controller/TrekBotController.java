package com.trekconnect.ai.controller;

import com.trekconnect.ai.dto.request.TrekBotChatRequest;
import com.trekconnect.ai.dto.response.TrekBotChatResponse;
import com.trekconnect.ai.service.TrekBotRagService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * REST Controller for TrekBot Context-Aware AI Expedition Assistant.
 * Exposes conversational RAG endpoints for adventurers browsing treks.
 */
@RestController
@RequestMapping("/api/ai/chat")
public class TrekBotController {

    private final TrekBotRagService ragService;

    public TrekBotController(TrekBotRagService ragService) {
        this.ragService = ragService;
    }

    @PostMapping
    public ResponseEntity<TrekBotChatResponse> chat(@Valid @RequestBody TrekBotChatRequest request) {
        TrekBotChatResponse response = ragService.generateResponse(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/quick-prompts")
    public ResponseEntity<Map<String, List<String>>> getQuickPrompts() {
        Map<String, List<String>> prompts = new LinkedHashMap<>();
        prompts.put("Gear & Packing", Arrays.asList(
                "What shoes should I wear for this trek?",
                "What rain gear is mandatory for monsoon treks?",
                "How much water should I carry in my backpack?"
        ));
        prompts.put("Difficulty & Fitness", Arrays.asList(
                "Is this trek beginner friendly?",
                "What fitness training is needed before departure?",
                "Can children or elder family members join?"
        ));
        prompts.put("Pricing & Meals", Arrays.asList(
                "What food and meals are included?",
                "Is local transport included from the city?",
                "What is the cancellation and refund policy?"
        ));
        prompts.put("Safety & Itinerary", Arrays.asList(
                "What is the reporting time and schedule?",
                "What safety protocols are followed in heavy rain?",
                "What is the peak summit altitude?"
        ));
        return ResponseEntity.ok(prompts);
    }
}
