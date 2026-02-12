package com.gogidix.rapidassist.ai.chatbot.domain.event;

import com.gogidix.rapidassist.ai.chatbot.domain.model.ChannelType;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event published when a chatbot request is created.
 */
public class ChatbotRequestRequestedEvent {

    private UUID requestId;
    private String tenantId;
    private String sessionId;
    private ChannelType channelType;
    private Instant occurredAt;
    private String eventType = "ChatbotRequestRequested";

    public ChatbotRequestRequestedEvent(UUID requestId, String tenantId, String sessionId,
                                        ChannelType channelType, Instant occurredAt) {
        this.requestId = requestId;
        this.tenantId = tenantId;
        this.sessionId = sessionId;
        this.channelType = channelType;
        this.occurredAt = occurredAt;
    }
}
