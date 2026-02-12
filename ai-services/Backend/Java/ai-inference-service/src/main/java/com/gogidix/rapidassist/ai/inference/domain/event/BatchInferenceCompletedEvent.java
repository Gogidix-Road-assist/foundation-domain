package com.gogidix.rapidassist.ai.inference.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain event raised when a batch inference request completes
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchInferenceCompletedEvent {

    private UUID eventId;
    private UUID batchRequestId;
    private String tenantId;
    private String modelId;
    private String modelVersion;
    private Integer totalItems;
    private Integer completedItems;
    private Integer failedItems;
    private LocalDateTime timestamp;
}
