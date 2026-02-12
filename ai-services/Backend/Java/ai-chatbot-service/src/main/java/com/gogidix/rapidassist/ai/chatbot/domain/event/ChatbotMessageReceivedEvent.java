package com.gogidix.rapidassist.ai.chatbot.domain.event;

import com.gogidix.rapidassist.ai.chatbot.domain.model.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain event published when a message is received in a chatbot session.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatbotMessageReceivedEvent {

    private UUID eventId;
    private UUID sessionId;
    private UUID messageId;
    private String tenantId;
    private String userId;
    private String direction;
    private String content;
    private LocalDateTime occurredAt;
    private java.util.Map<String, Object> metadata;

    public static ChatbotMessageReceivedEvent from(UUID sessionId, String tenantId, String userId, ChatMessage message) {
        return ChatbotMessageReceivedEvent.builder()
                .eventId(UUID.randomUUID())
                .sessionId(sessionId)
                .messageId(message.getId())
                .tenantId(tenantId)
                .userId(userId)
                .direction(message.getDirection().name())
                .content(message.getContent())
                .occurredAt(LocalDateTime.now())
                .metadata(message.getMetadata())
                .build();
    }
}
