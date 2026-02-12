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
 * DTO representing chatbot context.
 */
public class ChatbotContextDto {

    private UUID id;
    private String tenantId;
    private UUID chatbotSessionId;
    private String contextKey;
    private String contextValue;
    private String contextType;
    private Map<String, Object> additionalContext;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    private Integer ttl;
    private Boolean expired;
    private Long ageInSeconds;
}
