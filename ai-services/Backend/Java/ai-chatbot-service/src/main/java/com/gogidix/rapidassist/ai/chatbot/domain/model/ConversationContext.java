package com.gogidix.rapidassist.ai.chatbot.domain.model;

import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Domain model representing conversation context for maintaining chat history.
 */
public class ConversationContext {

    private UUID contextId;
    private String sessionId;
    private String tenantId;
    private String userId;
    private List<ContextMessage> messageHistory = new ArrayList<>();
    private IntentType currentIntent;
    private String currentTopic;
    private Integer messageCount = 0;
    private Instant lastActivity;
    private String languageCode;
    private ChannelType channelType;
    private String metadata;

    /**
     * Inner class representing a message in context
     */
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContextMessage {
        private MessageType type;
        private String content;
        private Instant timestamp;
        private IntentType detectedIntent;
        private Double confidenceScore;
    }

    /**
     * Adds a message to the conversation history
     */
    public void addMessage(MessageType type, String content, IntentType intent, Double confidence) {
        ContextMessage message = ContextMessage.builder()
                .type(type)
                .content(content)
                .timestamp(Instant.now())
                .detectedIntent(intent)
                .confidenceScore(confidence)
                .build();

        this.messageHistory.add(message);
        this.messageCount++;
        this.lastActivity = Instant.now();
    }

    /**
     * Gets recent messages from history
     */
    public List<ContextMessage> getRecentMessages(int count) {
        int fromIndex = Math.max(0, messageHistory.size() - count);
        return messageHistory.subList(fromIndex, messageHistory.size());
    }

    /**
     * Gets the last N messages of a specific type
     */
    public List<ContextMessage> getRecentMessagesByType(MessageType type, int count) {
        return messageHistory.stream()
                .filter(m -> m.getType() == type)
                .skip(Math.max(0, messageHistory.size() - count))
                .toList();
    }

    /**
     * Checks if conversation is stale (no activity for specified minutes)
     */
    public boolean isStale(int staleMinutes) {
        if (lastActivity == null) {
            return true;
        }
        Instant staleThreshold = Instant.now().minusSeconds(staleMinutes * 60L);
        return lastActivity.isBefore(staleThreshold);
    }

    /**
     * Clears message history while preserving session info
     */
    public void clearHistory() {
        this.messageHistory.clear();
        this.messageCount = 0;
        this.lastActivity = Instant.now();
    }

    /**
     * Updates the current intent
     */
    public void updateIntent(IntentType intent) {
        this.currentIntent = intent;
        this.lastActivity = Instant.now();
    }

    /**
     * Gets the conversation summary
     */
    public String getSummary() {
        return String.format("Session: %s, Messages: %d, Intent: %s, Last Activity: %s",
                sessionId, messageCount, currentIntent, lastActivity);
    }
}
