package com.gogidix.rapidassist.ai.chatbot.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gogidix.rapidassist.ai.chatbot.domain.model.MessageDirection;
import com.gogidix.rapidassist.ai.chatbot.domain.model.SessionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO representing a chatbot session.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatbotSessionDto {

    private UUID id;
    private String tenantId;
    private String userId;
    private String sessionId;
    private SessionStatus status;
    private String channel;
    private java.util.Map<String, Object> metadata;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastActivityAt;

    private String createdBy;
    private String updatedBy;

    // Child entities
    private List<ChatMessageDto> messages;
    private List<ChatbotContextDto> contexts;
    private List<ConversationFlowDto> flows;

    // Computed fields
    private Integer messageCount;
    private Long durationSeconds;
    private String currentFlowState;
    private Integer healthScore;
    private String recommendedAction;
}
