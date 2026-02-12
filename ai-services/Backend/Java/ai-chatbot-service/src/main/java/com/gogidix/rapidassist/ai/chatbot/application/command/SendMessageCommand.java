package com.gogidix.rapidassist.ai.chatbot.application.command;

import com.gogidix.rapidassist.ai.chatbot.domain.model.MessageDirection;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

/**
 * Command to send a message in a chatbot session.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageCommand {

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    @NotNull(message = "Session ID is required")
    private UUID sessionId;

    @NotBlank(message = "Content is required")
    private String content;

    @NotNull(message = "Direction is required")
    private MessageDirection direction;

    private String messageType;

    private Map<String, Object> metadata;
}
