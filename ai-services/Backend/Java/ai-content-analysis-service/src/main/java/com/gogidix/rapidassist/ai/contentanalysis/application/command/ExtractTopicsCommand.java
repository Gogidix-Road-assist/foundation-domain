package com.gogidix.rapidassist.ai.contentanalysis.application.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

/**
 * Command to extract topics from content
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtractTopicsCommand {

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    @NotBlank(message = "Content ID is required")
    private String contentId;

    @NotBlank(message = "Content body is required")
    private String contentBody;

    private String contentLanguage;
    private Integer maxTopics;
    private Double minRelevanceScore;
    private String requestedBy;
}
