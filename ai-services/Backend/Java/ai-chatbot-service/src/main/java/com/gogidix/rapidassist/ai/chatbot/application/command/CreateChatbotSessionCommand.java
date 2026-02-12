package com.gogidix.rapidassist.ai.chatbot.application.command;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Command to create a new chatbot session.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateChatbotSessionCommand {

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Channel is required")
    private String channel;

    private Map<String, Object> metadata;
}
