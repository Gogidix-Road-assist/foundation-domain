package com.gogidix.rapidassist.ai.summarization.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchSummarizationJob {
    @EqualsAndHashCode.Include

    private UUID id;
    private String tenantId;
    private String jobId;
    private SummarizationType summarizationType;
    private SummaryLength summaryLength;
    private List<UUID> requestIds;
    private int totalRequests;
    private int completedRequests;
    private int failedRequests;
    private SummarizationStatus status;
    private Map<String, Object> metadata;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
}
