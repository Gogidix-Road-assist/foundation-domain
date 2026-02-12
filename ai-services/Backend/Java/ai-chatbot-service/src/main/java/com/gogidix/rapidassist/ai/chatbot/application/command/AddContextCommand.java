package com.gogidix.rapidassist.ai.chatbot.application.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

/**
 * Command to add context to a session.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddContextCommand {

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    @NotNull(message = "Session ID is required")
    private UUID sessionId;

    @NotBlank(message = "Context key is required")
    private String contextKey;

    @NotBlank(message = "Context value is required")
    private String contextValue;

    private String contextType;

    private Map<String, Object> additionalContext;

    private Integer ttl;
}
