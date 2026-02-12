package com.gogidix.rapidassist.ai.chatbot.application.query;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Query to get a chatbot session by ID.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetChatbotSessionQuery {

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    @NotNull(message = "Session ID is required")
    private UUID sessionId;

    private Boolean includeMessages;

    private Boolean includeContexts;

    private Boolean includeFlows;
}
