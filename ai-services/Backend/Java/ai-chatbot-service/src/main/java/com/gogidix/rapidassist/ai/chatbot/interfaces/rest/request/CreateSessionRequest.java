package com.gogidix.rapidassist.ai.chatbot.interfaces.rest.request;

import com.gogidix.rapidassist.ai.chatbot.domain.model.MessageDirection;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * REST Request to create a chatbot session.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSessionRequest {

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Channel is required")
    private String channel;

    private Map<String, Object> metadata;
}
