package com.gogidix.rapidassist.ai.chatbot.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain model representing a chat message.
 * Pure domain model without JPA annotations.
 * Part of the ChatbotSession aggregate.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private UUID chatbotSessionId;
    private MessageDirection direction;
    private String content;
    private String messageType;
    private java.util.Map<String, Object> metadata;
    private LocalDateTime timestamp;
    private Integer sequenceNumber;

    /**
     * Business logic: Check if this is an inbound message
     */
    public boolean isInbound() {
        return MessageDirection.INBOUND.equals(this.direction);
    }

    /**
     * Business logic: Check if this is an outbound message
     */
    public boolean isOutbound() {
        return MessageDirection.OUTBOUND.equals(this.direction);
    }

    /**
     * Business logic: Validate message content
     */
    public boolean hasContent() {
        return content != null && !content.trim().isEmpty();
    }

    /**
     * Business logic: Get content length
     */
    public int getContentLength() {
        return content != null ? content.length() : 0;
    }
}
