package com.gogidix.rapidassist.ai.contentanalysis.application.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * Command to bulk analyze multiple content items
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkAnalyzeContentCommand {

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    @NotEmpty(message = "Content items list cannot be empty")
    @Valid
    private List<ContentItem> contentItems;

    private String requestedBy;
    private String analysisType;
    private List<String> analysisOptions;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContentItem {
        @NotBlank(message = "Content ID is required")
        private String contentId;

        @NotBlank(message = "Content type is required")
        private String contentType;

        private String contentTitle;

        @NotBlank(message = "Content body is required")
        private String contentBody;

        private String contentLanguage;
        private String callbackUrl;
    }
}
