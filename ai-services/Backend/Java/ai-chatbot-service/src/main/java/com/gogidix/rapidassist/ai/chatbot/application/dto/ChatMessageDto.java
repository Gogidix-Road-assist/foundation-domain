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
 * DTO representing a chat message.
 */
public class ChatMessageDto {

    private UUID id;
    private String tenantId;
    private UUID chatbotSessionId;
    private String direction;
    private String content;
    private String messageType;
    private Map<String, Object> metadata;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    private Integer sequenceNumber;
}
