package com.gogidix.rapidassist.ai.search.optimization.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event published when a query is optimized.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryOptimizedEvent {

    private UUID eventId;
    private UUID queryId;
    private String tenantId;
    private String originalQuery;
    private String optimizedQuery;
    private String queryIntent;
    private LocalDateTime timestamp;
}
