package com.gogidix.rapidassist.ai.contentanalysis.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain event fired when content analysis fails.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentAnalysisFailedEvent {

    private UUID eventId;
    private UUID analysisId;
    private UUID requestId;
    private String tenantId;
    private String contentId;
    private String errorMessage;
    private String errorCategory;
    private LocalDateTime occurredAt;
    private Integer retryCount;
}
