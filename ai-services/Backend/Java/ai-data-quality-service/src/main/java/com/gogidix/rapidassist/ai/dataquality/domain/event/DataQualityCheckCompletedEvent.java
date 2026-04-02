package com.gogidix.rapidassist.ai.dataquality.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain event published when a data quality check is completed
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataQualityCheckCompletedEvent {

    private UUID eventId;
    private String tenantId;
    private UUID checkId;
    private UUID ruleId;
    private String entityType;
    private int totalRecords;
    private int passedRecords;
    private int failedRecords;
    private double passPercentage;
    private long executionDurationMs;
    private LocalDateTime occurredAt;
}
