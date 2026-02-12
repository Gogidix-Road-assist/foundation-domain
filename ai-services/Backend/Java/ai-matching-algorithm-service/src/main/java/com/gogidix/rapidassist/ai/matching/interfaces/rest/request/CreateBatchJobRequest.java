package com.gogidix.rapidassist.ai.matching.interfaces.rest.request;

import com.gogidix.rapidassist.ai.matching.domain.model.MatchingAlgorithmType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * REST Request to create a batch matching job.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBatchJobRequest {

    @NotBlank(message = "Job name is required")
    private String jobName;

    @NotBlank(message = "Source entity type is required")
    private String sourceEntityType;

    @NotBlank(message = "Target entity type is required")
    private String targetEntityType;

    @NotNull(message = "Algorithm type is required")
    private MatchingAlgorithmType algorithmType;

    @NotNull(message = "Source entity IDs are required")
    @Size(min = 1, message = "At least one source entity ID is required")
    private List<String> sourceEntityIds;

    private List<String> targetEntityIds;

    private Double minSimilarityThreshold;

    private Integer batchSize;

    private Map<String, Object> configuration;

    private Map<String, Object> metadata;
}
