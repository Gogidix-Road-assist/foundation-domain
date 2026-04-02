package com.gogidix.rapidassist.ai.contentanalysis.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain event fired when content analysis is requested.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentAnalysisRequestedEvent {

    private UUID eventId;
    private UUID analysisId;
    private UUID requestId;
    private String tenantId;
    private String contentId;
    private String contentType;
    private String analysisType;
    private LocalDateTime occurredAt;
    private String requestedBy;
}
