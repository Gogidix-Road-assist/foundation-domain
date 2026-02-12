package com.gogidix.rapidassist.ai.chatbot.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain event published when a chatbot session is activated.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatbotSessionActivatedEvent {

    private UUID eventId;
    private UUID sessionId;
    private String tenantId;
    private String userId;
    private LocalDateTime occurredAt;
    private java.util.Map<String, Object> metadata;

    public static ChatbotSessionActivatedEvent create(UUID sessionId, String tenantId, String userId) {
        return ChatbotSessionActivatedEvent.builder()
                .eventId(UUID.randomUUID())
                .sessionId(sessionId)
                .tenantId(tenantId)
                .userId(userId)
                .occurredAt(LocalDateTime.now())
                .metadata(new java.util.HashMap<>())
                .build();
    }
}
