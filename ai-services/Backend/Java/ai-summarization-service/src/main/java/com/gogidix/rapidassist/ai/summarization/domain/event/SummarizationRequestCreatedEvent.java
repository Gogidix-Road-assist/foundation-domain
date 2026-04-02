package com.gogidix.rapidassist.ai.summarization.domain.event;

import com.gogidix.rapidassist.ai.summarization.domain.model.SummarizationRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SummarizationRequestCreatedEvent {
    private UUID requestId;
    private String tenantId;
    private String requestIdString;
    private String summarizationType;
    private String summaryLength;
    private int sourceTextCount;
    private String createdBy;
    private LocalDateTime createdAt;

    public static SummarizationRequestCreatedEvent from(SummarizationRequest request) {
        return SummarizationRequestCreatedEvent.builder()
                .requestId(request.getId())
                .tenantId(request.getTenantId())
                .requestIdString(request.getRequestId())
                .summarizationType(request.getSummarizationType().name())
                .summaryLength(request.getSummaryLength().name())
                .sourceTextCount(request.getSourceTexts() != null ? request.getSourceTexts().size() : 0)
                .createdBy(request.getCreatedBy())
                .createdAt(request.getCreatedAt())
                .build();
    }
}
