package com.gogidix.rapidassist.ai.inference.domain.event;

import com.gogidix.rapidassist.ai.inference.domain.model.InferenceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain event raised when an inference request is created
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InferenceRequestedEvent {

    private UUID eventId;
    private UUID inferenceRequestId;
    private String tenantId;
    private String modelId;
    private String modelVersion;
    private InferenceType inferenceType;
    private LocalDateTime timestamp;
    private String requestedBy;

    public static InferenceRequestedEvent create(UUID inferenceRequestId, String tenantId,
                                                String modelId, String modelVersion,
                                                InferenceType inferenceType, String requestedBy) {
        return InferenceRequestedEvent.builder()
                .eventId(UUID.randomUUID())
                .inferenceRequestId(inferenceRequestId)
                .tenantId(tenantId)
                .modelId(modelId)
                .modelVersion(modelVersion)
                .inferenceType(inferenceType)
                .timestamp(LocalDateTime.now())
                .requestedBy(requestedBy)
                .build();
    }
}
