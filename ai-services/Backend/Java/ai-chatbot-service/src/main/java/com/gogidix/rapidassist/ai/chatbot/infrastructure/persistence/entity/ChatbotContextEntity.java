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
 * JPA Entity for ChatbotContext.
 * Maps to chatbot_contexts table with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "chatbot_context")
public class ChatbotContextEntity {

    @Id
    
    private Long id;

    @Indexed(unique = true)
    private UUID uuid;

    private String tenantId;

    private UUID sessionId;

    private String contextKey;

    private String contextValue;

    private String contextType;

    private String additionalContext;

    private Integer ttl;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long version;
}
