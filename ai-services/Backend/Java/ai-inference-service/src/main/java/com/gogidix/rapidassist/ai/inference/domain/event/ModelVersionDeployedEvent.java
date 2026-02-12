package com.gogidix.rapidassist.ai.inference.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain event raised when a model version is deployed
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelVersionDeployedEvent {

    private UUID eventId;
    private UUID modelVersionId;
    private String tenantId;
    private String modelId;
    private String version;
    private LocalDateTime timestamp;
    private String deployedBy;
}
