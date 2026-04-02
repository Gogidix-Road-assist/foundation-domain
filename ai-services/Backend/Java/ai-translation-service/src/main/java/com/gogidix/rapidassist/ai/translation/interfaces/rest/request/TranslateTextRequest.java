package com.gogidix.rapidassist.ai.translation.interfaces.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Request to translate text.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TranslateTextRequest {

    @NotBlank(message = "Source text is required")
    private String sourceText;

    private String sourceLanguage;

    @NotBlank(message = "Target language is required")
    private String targetLanguage;

    private Map<String, Object> metadata;
}
