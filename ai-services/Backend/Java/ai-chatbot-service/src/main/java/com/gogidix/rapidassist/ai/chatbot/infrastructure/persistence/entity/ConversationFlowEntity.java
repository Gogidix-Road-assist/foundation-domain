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
 * JPA Entity for ConversationFlow.
 * Maps to conversation_flows table with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "conversation_flow")
public class ConversationFlowEntity {

    @Id
    
    private Long id;

    @Indexed(unique = true)
    private UUID uuid;

    private String tenantId;

    private UUID sessionId;

    private String currentState;

    private String previousState;

    private String nextState;

    private String flowType;

    private String flowParameters;

    private Integer stepNumber;

    private LocalDateTime stateEnteredAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long version;
}
