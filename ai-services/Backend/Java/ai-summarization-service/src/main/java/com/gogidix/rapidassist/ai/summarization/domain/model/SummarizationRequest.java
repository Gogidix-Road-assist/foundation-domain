package com.gogidix.rapidassist.ai.summarization.domain.model;

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
public class SummarizationRequest {
    private UUID id;
    private String tenantId;
    private String requestId;
    private SummarizationType summarizationType;
    private SummaryLength summaryLength;
    private List<String> sourceTexts;
    private List<String> documentUrls;
    private Map<String, Object> options;
    private SummarizationStatus status;
    private String errorMessage;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
}
