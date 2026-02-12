package com.gogidix.rapidassist.ai.chatbot.infrastructure.persistence.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA Entity for ChatMessage.
 * Maps to chat_messages table with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "chat_message")
public class ChatMessageEntity {

    @Id
    
    private Long id;

    @Indexed(unique = true)
    private UUID uuid;

    private String tenantId;

    private UUID sessionId;

    private com.gogidix.rapidassist.ai.chatbot.domain.model.MessageDirection direction;

    private String content;

    private String messageType;

    private String metadata;

    private LocalDateTime timestamp;

    private Integer sequenceNumber;

    private LocalDateTime createdAt;

    private Long version;
}
