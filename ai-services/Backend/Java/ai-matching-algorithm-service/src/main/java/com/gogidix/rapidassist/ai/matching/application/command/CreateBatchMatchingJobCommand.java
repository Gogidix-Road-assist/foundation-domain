package com.gogidix.rapidassist.ai.matching.application.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * Command to create a batch matching job.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBatchMatchingJobCommand {

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    @NotBlank(message = "Job name is required")
    private String jobName;

    @NotBlank(message = "Source entity type is required")
    private String sourceEntityType;

    @NotBlank(message = "Target entity type is required")
    private String targetEntityType;

    @NotNull(message = "Algorithm type is required")
    private com.gogidix.rapidassist.ai.matching.domain.model.MatchingAlgorithmType algorithmType;

    @NotNull(message = "Source entity IDs are required")
    private List<String> sourceEntityIds;

    private List<String> targetEntityIds;

    private Double minSimilarityThreshold;

    private Integer batchSize;

    private Map<String, Object> configuration;

    private Map<String, Object> metadata;
}
