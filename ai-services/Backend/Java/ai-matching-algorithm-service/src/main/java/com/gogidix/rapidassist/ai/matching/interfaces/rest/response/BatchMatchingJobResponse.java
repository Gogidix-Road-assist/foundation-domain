package com.gogidix.rapidassist.ai.matching.interfaces.rest.response;

import com.gogidix.rapidassist.ai.matching.domain.model.BatchJobStatus;
import com.gogidix.rapidassist.ai.matching.domain.model.MatchingAlgorithmType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST Response for BatchMatchingJob.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchMatchingJobResponse {

    private UUID id;
    private String tenantId;
    private String jobId;
    private String jobName;
    private String sourceEntityType;
    private String targetEntityType;
    private MatchingAlgorithmType algorithmType;
    private BatchJobStatus status;
    private List<String> sourceEntityIds;
    private List<String> targetEntityIds;
    private Integer totalCount;
    private Integer processedCount;
    private Integer successCount;
    private Integer failureCount;
    private Double minSimilarityThreshold;
    private Integer batchSize;
    private Map<String, Object> configuration;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String createdBy;
    private String updatedBy;
    private String errorMessage;
}
