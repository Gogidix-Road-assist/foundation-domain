package com.gogidix.rapidassist.ai.contentanalysis.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Domain event fired when topics are extracted from content.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentTopicsExtractedEvent {

    private UUID eventId;
    private UUID analysisId;
    private String tenantId;
    private String contentId;
    private Integer topicCount;
    private List<String> primaryTopics;
    private String mainTopic;
    private LocalDateTime occurredAt;
}
