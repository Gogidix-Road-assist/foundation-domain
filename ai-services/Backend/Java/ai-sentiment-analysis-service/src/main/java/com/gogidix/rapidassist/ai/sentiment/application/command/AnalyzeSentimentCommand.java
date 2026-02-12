package com.gogidix.rapidassist.ai.sentiment.application.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Map;

/**
 * Command to analyze sentiment
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyzeSentimentCommand {

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Text is required")
    @Size(min = 1, max = 10000, message = "Text must be between 1 and 10000 characters")
    private String text;

    private String language;
    private String sourceType;
    private String sourceId;
    private Boolean includeEmotions;
    private Boolean includeAspects;
    private Map<String, Object> metadata;
}
