package com.gogidix.rapidassist.ai.tagging.application.command;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Command to generate AI tag suggestions for content
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GenerateTagSuggestionsCommand {

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    @NotBlank(message = "Content ID is required")
    private String contentId;

    @NotBlank(message = "Content type is required")
    private String contentType;

    @NotBlank(message = "Content text/data is required")
    private String contentText;

    private Integer maxSuggestions;

    private Double minConfidenceThreshold;

    private String aiModel;
}
