package com.gogidix.rapidassist.ai.summarization.application.dto;

import com.gogidix.rapidassist.ai.summarization.domain.model.SummarizationStatus;
import com.gogidix.rapidassist.ai.summarization.domain.model.SummarizationType;
import com.gogidix.rapidassist.ai.summarization.domain.model.SummaryLength;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchSummarizationJobDto {
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
