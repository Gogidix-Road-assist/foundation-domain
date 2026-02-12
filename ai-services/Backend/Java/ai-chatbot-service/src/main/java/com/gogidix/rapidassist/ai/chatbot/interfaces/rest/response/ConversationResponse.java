package com.gogidix.rapidassist.ai.chatbot.interfaces.rest.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class ConversationResponse {
    private UUID id;
    private String tenantId;
    private String name;
    private String description;
    private String status;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
