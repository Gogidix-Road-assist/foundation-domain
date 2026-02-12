package com.gogidix.rapidassist.ai.chatbot.infrastructure.messaging.kafka.event;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Kafka event for chatbot session operations.
 */
public class ChatbotSessionEvent {

    private UUID sessionId;
    private String tenantId;
    private String eventType;
    private LocalDateTime timestamp;

    public ChatbotSessionEvent() {
    }

    public ChatbotSessionEvent(UUID sessionId, String tenantId, String eventType, LocalDateTime timestamp) {
        this.sessionId = sessionId;
        this.tenantId = tenantId;
        this.eventType = eventType;
        this.timestamp = timestamp;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public void setSessionId(UUID sessionId) {
        this.sessionId = sessionId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
