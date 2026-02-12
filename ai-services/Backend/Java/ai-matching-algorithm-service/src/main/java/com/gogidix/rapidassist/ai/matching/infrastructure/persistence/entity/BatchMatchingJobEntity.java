package com.gogidix.rapidassist.ai.matching.infrastructure.persistence.entity;

import com.gogidix.rapidassist.ai.matching.domain.model.BatchJobStatus;
import com.gogidix.rapidassist.ai.matching.domain.model.MatchingAlgorithmType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * MongoDB Document for BatchMatchingJob.
 * Maps to batch_matching_job collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "batch_matching_job")
public class BatchMatchingJobEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed(unique = true)
    private String jobCode;

    private String jobName;

    private String description;

    @Indexed
    private String sourceEntityType;

    @Indexed
    private String targetEntityType;

    @Indexed
    private MatchingAlgorithmType algorithmType;

    private List<String> sourceEntityIds;

    private List<String> targetEntityIds;

    @Indexed
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
