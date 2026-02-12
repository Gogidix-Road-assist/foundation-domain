package com.gogidix.rapidassist.ai.matching.application.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

/**
 * Command to execute a matching operation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteMatchingCommand {

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    @NotBlank(message = "Source entity type is required")
    private String sourceEntityType;

    @NotBlank(message = "Source entity ID is required")
    private String sourceEntityId;

    @NotBlank(message = "Target entity type is required")
    private String targetEntityType;

    @NotBlank(message = "Target entity ID is required")
    private String targetEntityId;

    @NotNull(message = "Algorithm type is required")
    private com.gogidix.rapidassist.ai.matching.domain.model.MatchingAlgorithmType algorithmType;

    private Map<String, Object> sourceAttributes;

    private Map<String, Object> targetAttributes;

    private Double minSimilarityThreshold;

    private Integer maxResults;

    private Map<String, Object> metadata;
}
