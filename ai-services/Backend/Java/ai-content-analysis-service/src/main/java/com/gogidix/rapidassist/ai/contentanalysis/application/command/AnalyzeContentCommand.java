package com.gogidix.rapidassist.ai.contentanalysis.application.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

/**
 * Command to analyze content
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyzeContentCommand {

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    @NotBlank(message = "Content ID is required")
    private String contentId;

    @NotBlank(message = "Content type is required")
    private String contentType;

    private String contentTitle;

    @NotBlank(message = "Content body is required")
    private String contentBody;

    private String contentLanguage;
    private String requestedBy;
    private String analysisType;
    private List<String> analysisOptions;
    private Map<String, Object> metadata;
    private String callbackUrl;
    private Integer priority;
}
