package com.gogidix.rapidassist.ai.contentanalysis.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain event fired when content analysis is completed.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentAnalysisCompletedEvent {

    private UUID eventId;
    private UUID analysisId;
    private UUID requestId;
    private String tenantId;
    private String contentId;
    private Double overallScore;
    private String qualityGrade;
    private LocalDateTime occurredAt;
    private Long processingDurationSeconds;
}
