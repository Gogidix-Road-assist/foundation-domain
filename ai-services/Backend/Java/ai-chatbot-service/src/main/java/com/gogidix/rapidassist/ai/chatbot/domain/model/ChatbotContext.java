package com.gogidix.rapidassist.ai.chatbot.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain model representing the context of a chatbot session.
 * Maintains conversation state, user preferences, and session metadata.
 * Pure domain model without JPA annotations.
 * Part of the ChatbotSession aggregate.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatbotContext {

    private UUID id;
    private String tenantId;
    private UUID chatbotSessionId;
    private String contextKey;
    private String contextValue;
    private String contextType;
    private java.util.Map<String, Object> additionalContext;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer ttl;

    /**
     * Business logic: Check if context has expired
     */
    public boolean isExpired() {
        if (ttl == null || ttl == 0) {
            return false;
        }
        LocalDateTime expiryTime = updatedAt.plusSeconds(ttl);
        return LocalDateTime.now().isAfter(expiryTime);
    }

    /**
     * Business logic: Get context age in seconds
     */
    public long getAgeInSeconds() {
        return updatedAt != null ? java.time.Duration.between(updatedAt, LocalDateTime.now()).getSeconds() : 0;
    }

    /**
     * Business logic: Update context value
     */
    public void updateValue(String newValue) {
        this.contextValue = newValue;
        this.updatedAt = LocalDateTime.now();
    }
}
