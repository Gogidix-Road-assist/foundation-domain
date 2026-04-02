package com.gogidix.rapidassist.ai.inference.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain event raised when an inference request completes successfully
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InferenceCompletedEvent {

    private UUID eventId;
    private UUID inferenceRequestId;
    private String tenantId;
    private String modelId;
    private String modelVersion;
    private String outputData;
    private Long processingDurationMillis;
    private LocalDateTime timestamp;
}
