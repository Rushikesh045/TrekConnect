package com.trekconnect.ai.dto.response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Response DTO returned by TrekBot containing the grounded answer, source attribution, and suggestions.
 */
public class TrekBotChatResponse {

    private String reply;
    private String groundedIn;
    private Double confidenceScore;
    private List<String> suggestedFollowUps = new ArrayList<>();
    private Boolean isOffTopic = false;
    private LocalDateTime timestamp = LocalDateTime.now();

    public TrekBotChatResponse() {
    }

    public TrekBotChatResponse(String reply, String groundedIn, Double confidenceScore, List<String> suggestedFollowUps) {
        this.reply = reply;
        this.groundedIn = groundedIn;
        this.confidenceScore = confidenceScore;
        this.suggestedFollowUps = suggestedFollowUps;
        this.isOffTopic = false;
        this.timestamp = LocalDateTime.now();
    }

    public static TrekBotChatResponseBuilder builder() {
        return new TrekBotChatResponseBuilder();
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getGroundedIn() {
        return groundedIn;
    }

    public void setGroundedIn(String groundedIn) {
        this.groundedIn = groundedIn;
    }

    public Double getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(Double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public List<String> getSuggestedFollowUps() {
        return suggestedFollowUps;
    }

    public void setSuggestedFollowUps(List<String> suggestedFollowUps) {
        this.suggestedFollowUps = suggestedFollowUps;
    }

    public Boolean getIsOffTopic() {
        return isOffTopic;
    }

    public void setIsOffTopic(Boolean isOffTopic) {
        this.isOffTopic = isOffTopic;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public static class TrekBotChatResponseBuilder {
        private String reply;
        private String groundedIn;
        private Double confidenceScore = 1.0;
        private List<String> suggestedFollowUps = new ArrayList<>();
        private Boolean isOffTopic = false;

        public TrekBotChatResponseBuilder reply(String reply) {
            this.reply = reply;
            return this;
        }

        public TrekBotChatResponseBuilder groundedIn(String groundedIn) {
            this.groundedIn = groundedIn;
            return this;
        }

        public TrekBotChatResponseBuilder confidenceScore(Double confidenceScore) {
            this.confidenceScore = confidenceScore;
            return this;
        }

        public TrekBotChatResponseBuilder suggestedFollowUps(List<String> suggestedFollowUps) {
            this.suggestedFollowUps = suggestedFollowUps;
            return this;
        }

        public TrekBotChatResponseBuilder isOffTopic(Boolean isOffTopic) {
            this.isOffTopic = isOffTopic;
            return this;
        }

        public TrekBotChatResponse build() {
            TrekBotChatResponse res = new TrekBotChatResponse();
            res.setReply(this.reply);
            res.setGroundedIn(this.groundedIn);
            res.setConfidenceScore(this.confidenceScore);
            res.setSuggestedFollowUps(this.suggestedFollowUps != null ? this.suggestedFollowUps : new ArrayList<>());
            res.setIsOffTopic(this.isOffTopic);
            res.setTimestamp(LocalDateTime.now());
            return res;
        }
    }
}
