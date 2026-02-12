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
 * REST Request to send a message.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageRequest {

    @NotBlank(message = "Content is required")
    private String content;

    @NotNull(message = "Direction is required")
    private MessageDirection direction;

    private String messageType;

    private Map<String, Object> metadata;
}
