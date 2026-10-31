package com.trekconnect.ai.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

/**
 * Incoming request DTO for TrekBot RAG conversational inquiries.
 */
public class TrekBotChatRequest {

    private String trekId;
    private String trekTitle;

    @NotBlank(message = "User message cannot be empty")
    private String userMessage;

    private List<ChatMessage> conversationHistory = new ArrayList<>();
    private TrekContextPayload trekContext;

    public TrekBotChatRequest() {
    }

    public TrekBotChatRequest(String trekId, String trekTitle, String userMessage) {
        this.trekId = trekId;
        this.trekTitle = trekTitle;
        this.userMessage = userMessage;
    }

    public String getTrekId() {
        return trekId;
    }

    public void setTrekId(String trekId) {
        this.trekId = trekId;
    }

    public String getTrekTitle() {
        return trekTitle;
    }

    public void setTrekTitle(String trekTitle) {
        this.trekTitle = trekTitle;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public List<ChatMessage> getConversationHistory() {
        return conversationHistory;
    }

    public void setConversationHistory(List<ChatMessage> conversationHistory) {
        this.conversationHistory = conversationHistory;
    }

    public TrekContextPayload getTrekContext() {
        return trekContext;
    }

    public void setTrekContext(TrekContextPayload trekContext) {
        this.trekContext = trekContext;
    }
}
