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
 * MongoDB Document for ChatbotSession.
 * Maps to chatbot_session collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "chatbot_session")
public class ChatbotSessionEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private String userId;

    @Indexed(unique = true)
    private String sessionId;

    private com.gogidix.rapidassist.ai.chatbot.domain.model.SessionStatus status;

    private String channel;

    private String metadata;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime lastActivityAt;

    private String createdBy;

    private String updatedBy;

    private Long version;
}
