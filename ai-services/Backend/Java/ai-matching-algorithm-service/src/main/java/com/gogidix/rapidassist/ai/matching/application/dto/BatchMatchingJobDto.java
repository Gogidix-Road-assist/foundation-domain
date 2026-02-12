package com.gogidix.rapidassist.ai.matching.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for BatchMatchingJob.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchMatchingJobDto {

    private UUID id;
    private String tenantId;
    private String jobId;
    private String jobName;
    private String sourceEntityType;
    private String targetEntityType;
    private com.gogidix.rapidassist.ai.matching.domain.model.MatchingAlgorithmType algorithmType;
    private com.gogidix.rapidassist.ai.matching.domain.model.BatchJobStatus status;
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
