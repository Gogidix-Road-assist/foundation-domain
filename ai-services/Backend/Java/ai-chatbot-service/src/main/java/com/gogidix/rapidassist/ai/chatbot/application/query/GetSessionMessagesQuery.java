package com.gogidix.rapidassist.ai.chatbot.application.query;

import com.gogidix.rapidassist.ai.chatbot.domain.model.MessageDirection;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Query to get messages for a session.
 */
public class GetSessionMessagesQuery {

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    @NotNull(message = "Session ID is required")
    private UUID sessionId;

    private MessageDirection direction;

    private Integer page;

    private Integer size;
}
