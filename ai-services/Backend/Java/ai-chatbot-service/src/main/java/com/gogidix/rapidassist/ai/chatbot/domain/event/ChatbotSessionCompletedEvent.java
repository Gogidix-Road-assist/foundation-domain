package com.gogidix.rapidassist.ai.chatbot.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain event published when a chatbot session is completed.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatbotSessionCompletedEvent {

    private UUID eventId;
    private UUID sessionId;
    private String tenantId;
    private String userId;
    private String completionReason;
    private Integer messageCount;
    private Long durationSeconds;
    private LocalDateTime occurredAt;
    private java.util.Map<String, Object> metadata;

    public static ChatbotSessionCompletedEvent create(UUID sessionId, String tenantId, String userId, 
                                                       String reason, int messageCount, long durationSeconds) {
        return ChatbotSessionCompletedEvent.builder()
                .eventId(UUID.randomUUID())
                .sessionId(sessionId)
                .tenantId(tenantId)
                .userId(userId)
                .completionReason(reason)
                .messageCount(messageCount)
                .durationSeconds(durationSeconds)
                .occurredAt(LocalDateTime.now())
                .metadata(new java.util.HashMap<>())
                .build();
    }
}
