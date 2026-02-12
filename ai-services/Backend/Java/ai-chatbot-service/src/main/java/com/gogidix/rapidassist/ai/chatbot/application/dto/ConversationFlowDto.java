package com.gogidix.rapidassist.ai.chatbot.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * DTO representing conversation flow.
 */
public class ConversationFlowDto {

    private UUID id;
    private String tenantId;
    private UUID chatbotSessionId;
    private String currentState;
    private String previousState;
    private String nextState;
    private String flowType;
    private Map<String, Object> flowParameters;
    private Integer stepNumber;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime stateEnteredAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    private Long timeInCurrentStateSeconds;
}
