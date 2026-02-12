package com.gogidix.rapidassist.ai.chatbot.interfaces.rest.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * REST Request to add context to a session.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddContextRequest {

    @NotBlank(message = "Context key is required")
    private String contextKey;

    @NotBlank(message = "Context value is required")
    private String contextValue;

    private String contextType;

    private Map<String, Object> additionalContext;

    private Integer ttl;
}
