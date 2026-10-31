package com.trekconnect.ai.dto.request;

/**
 * Individual chat message entry in the conversation history.
 */
public class ChatMessage {

    private String role; // "user" or "assistant" or "system"
    private String content;

    public ChatMessage() {
    }

    public ChatMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
