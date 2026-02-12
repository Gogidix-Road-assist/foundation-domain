package com.gogidix.rapidassist.ai.chatbot.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain event published when a chatbot session is terminated.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatbotSessionTerminatedEvent {

    private UUID eventId;
    private UUID sessionId;
    private String tenantId;
    private String userId;
    private String terminationReason;
    private LocalDateTime occurredAt;
    private java.util.Map<String, Object> metadata;

    public static ChatbotSessionTerminatedEvent create(UUID sessionId, String tenantId, String userId, String reason) {
        return ChatbotSessionTerminatedEvent.builder()
                .eventId(UUID.randomUUID())
                .sessionId(sessionId)
                .tenantId(tenantId)
                .userId(userId)
                .terminationReason(reason)
                .occurredAt(LocalDateTime.now())
                .metadata(new java.util.HashMap<>())
                .build();
    }
}
