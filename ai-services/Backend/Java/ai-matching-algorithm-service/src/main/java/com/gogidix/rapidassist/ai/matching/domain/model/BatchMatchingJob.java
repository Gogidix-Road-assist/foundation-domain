package com.gogidix.rapidassist.ai.matching.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing a Batch Matching Job.
 * Handles bulk matching operations.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchMatchingJob {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private String jobName;
    private String jobCode;
    private String description;
    private String sourceEntityType;
    private String targetEntityType;
    private MatchingAlgorithmType algorithmType;
    private List<String> sourceEntityIds;
    private List<String> targetEntityIds;
    private BatchJobStatus status;
    private int totalRecords;
    private int processedRecords;
    private int successfulMatches;
    private int failedRecords;
    private double progressPercentage;
    private Map<String, Object> jobParameters;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime estimatedCompletionAt;
    private String errorMessage;
    private Map<String, Object> metadata;
    private String version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
