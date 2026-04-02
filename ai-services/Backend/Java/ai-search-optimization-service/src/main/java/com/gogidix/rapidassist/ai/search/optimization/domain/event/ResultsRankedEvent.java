package com.gogidix.rapidassist.ai.search.optimization.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event published when search results are ranked.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultsRankedEvent {

    private UUID eventId;
    private UUID queryId;
    private String tenantId;
    private Integer resultCount;
    private Double averageRelevanceScore;
    private LocalDateTime timestamp;
}
