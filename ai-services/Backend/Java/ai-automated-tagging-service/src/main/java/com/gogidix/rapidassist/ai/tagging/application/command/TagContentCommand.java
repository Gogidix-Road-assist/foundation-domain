package com.gogidix.rapidassist.ai.tagging.application.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Command to tag content with specified tags
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TagContentCommand {

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    @NotBlank(message = "Content ID is required")
    private String contentId;

    @NotBlank(message = "Content type is required")
    private String contentType;

    @NotEmpty(message = "At least one tag ID is required")
    private List<UUID> tagIds;

    private com.gogidix.rapidassist.ai.tagging.domain.model.ContentTag.TaggingSource taggingSource;

    private Double confidenceThreshold;

    private String taggedBy;
}
