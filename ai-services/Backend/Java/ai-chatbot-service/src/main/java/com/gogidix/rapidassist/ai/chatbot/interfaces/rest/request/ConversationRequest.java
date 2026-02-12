package com.gogidix.rapidassist.ai.chatbot.interfaces.rest.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;

public class ConversationRequest {
    @NotBlank(message = "Name is required")
    private String name;
    private String description;
    private String status;
    private Map<String, Object> metadata;
}
