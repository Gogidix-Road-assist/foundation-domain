package com.gogidix.rapidassist.ai.dataquality.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain event published when a data quality check is initiated
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataQualityCheckInitiatedEvent {

    private UUID eventId;
    private String tenantId;
    private UUID checkId;
    private UUID ruleId;
    private String entityType;
    private String datasetIdentifier;
    private LocalDateTime occurredAt;
    private String initiatedBy;
}
