package com.gogidix.rapidassist.ai.chatbot.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

public class ChatbotCreatedEvent {
    private UUID eventId;
    private String tenantId;
    private UUID aggregateId;
    private LocalDateTime occurredAt;
    private String eventType;
    private String version;
}
