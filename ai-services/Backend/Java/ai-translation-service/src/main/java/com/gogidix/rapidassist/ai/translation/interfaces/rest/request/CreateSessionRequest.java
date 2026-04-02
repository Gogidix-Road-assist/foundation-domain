package com.gogidix.rapidassist.ai.translation.interfaces.rest.request;

import com.gogidix.rapidassist.ai.translation.domain.aggregate.TranslationSession;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Request to create a translation session.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSessionRequest {

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotNull(message = "Session type is required")
    private TranslationSession.SessionType sessionType;

    private String sourceLanguage;

    private String targetLanguage;

    @NotBlank(message = "Channel is required")
    private String channel;

    private Map<String, Object> metadata;
}
