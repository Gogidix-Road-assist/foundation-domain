package com.gogidix.rapidassist.ai.chatbot.domain.event;

import com.gogidix.rapidassist.ai.chatbot.domain.aggregate.ChatbotSession;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain event published when a chatbot session is created.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatbotSessionCreatedEvent {

    private UUID eventId;
    private UUID sessionId;
    private String tenantId;
    private String userId;
    private String channel;
    private String status;
    private LocalDateTime occurredAt;
    private java.util.Map<String, Object> metadata;

    public static ChatbotSessionCreatedEvent from(ChatbotSession session) {
        return ChatbotSessionCreatedEvent.builder()
                .eventId(UUID.randomUUID())
                .sessionId(session.getId())
                .tenantId(session.getTenantId())
                .userId(session.getUserId())
                .channel(session.getChannel())
                .status(session.getStatus().name())
                .occurredAt(LocalDateTime.now())
                .metadata(session.getMetadata())
                .build();
    }
}
