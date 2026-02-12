package com.gogidix.rapidassist.ai.chatbot.domain.model;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain model representing a chatbot response.
 */
@Builder
public class ChatbotResponse {

    private UUID requestId;
    private String sessionId;
    private String tenantId;
    private String responseMessage;
    private IntentType intentType;
    private Double confidenceScore;
    private MessageType messageType;
    private Instant timestamp;
    private String suggestedActions;
    private Boolean requiresHumanIntervention;
    private String metadata;

    /**
     * Creates a successful response
     */
    public static ChatbotResponse success(UUID requestId, String sessionId, String tenantId,
                                          String message, IntentType intent, Double confidence) {
        return ChatbotResponse.builder()
                .requestId(requestId)
                .sessionId(sessionId)
                .tenantId(tenantId)
                .responseMessage(message)
                .intentType(intent)
                .confidenceScore(confidence)
                .messageType(MessageType.BOT)
                .timestamp(Instant.now())
                .requiresHumanIntervention(confidence < 0.5)
                .build();
    }

    /**
     * Creates an error response
     */
    public static ChatbotResponse error(UUID requestId, String sessionId, String tenantId,
                                        String errorMessage) {
        return ChatbotResponse.builder()
                .requestId(requestId)
                .sessionId(sessionId)
                .tenantId(tenantId)
                .responseMessage(errorMessage)
                .messageType(MessageType.ERROR)
                .timestamp(Instant.now())
                .build();
    }

    /**
     * Checks if response has high confidence
     */
    public boolean hasHighConfidence() {
        return confidenceScore != null && confidenceScore >= 0.7;
    }

    /**
     * Checks if response indicates need for human intervention
     */
    public boolean needsHumanHelp() {
        return Boolean.TRUE.equals(requiresHumanIntervention);
    }
}
