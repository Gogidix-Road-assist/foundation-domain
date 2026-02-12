package com.gogidix.rapidassist.ai.chatbot.domain.event;

import com.gogidix.rapidassist.ai.chatbot.domain.model.IntentType;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event published when a chatbot request is completed.
 */
public class ChatbotRequestCompletedEvent {

    private UUID requestId;
    private String tenantId;
    private String sessionId;
    private IntentType intentType;
    private Double confidenceScore;
    private Long processingTimeMs;
    private Instant completedAt;
    private String eventType = "ChatbotRequestCompleted";

    public ChatbotRequestCompletedEvent(UUID requestId, String tenantId, String sessionId,
                                        IntentType intentType, Double confidenceScore,
                                        Long processingTimeMs, Instant completedAt) {
        this.requestId = requestId;
        this.tenantId = tenantId;
        this.sessionId = sessionId;
        this.intentType = intentType;
        this.confidenceScore = confidenceScore;
        this.processingTimeMs = processingTimeMs;
        this.completedAt = completedAt;
    }
}
