package com.gogidix.rapidassist.ai.summarization.interfaces.rest.response;

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
public class SummarizationRequestResponse {
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
