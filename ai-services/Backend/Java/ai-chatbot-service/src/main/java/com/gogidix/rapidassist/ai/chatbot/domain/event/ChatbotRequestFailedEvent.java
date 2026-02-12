package com.gogidix.rapidassist.ai.chatbot.domain.event;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event published when a chatbot request fails.
 */
public class ChatbotRequestFailedEvent {

    private UUID requestId;
    private String tenantId;
    private String sessionId;
    private String errorMessage;
    private Instant failedAt;
    private String eventType = "ChatbotRequestFailed";

    public ChatbotRequestFailedEvent(UUID requestId, String tenantId, String sessionId,
                                     String errorMessage, Instant failedAt) {
        this.requestId = requestId;
        this.tenantId = tenantId;
        this.sessionId = sessionId;
        this.errorMessage = errorMessage;
        this.failedAt = failedAt;
    }
}
