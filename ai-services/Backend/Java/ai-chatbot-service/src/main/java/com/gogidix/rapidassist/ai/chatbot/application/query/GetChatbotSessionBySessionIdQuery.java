package com.gogidix.rapidassist.ai.chatbot.application.query;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Query to get a chatbot session by session ID string.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetChatbotSessionBySessionIdQuery {

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    @NotBlank(message = "Session ID string is required")
    private String sessionId;
}
