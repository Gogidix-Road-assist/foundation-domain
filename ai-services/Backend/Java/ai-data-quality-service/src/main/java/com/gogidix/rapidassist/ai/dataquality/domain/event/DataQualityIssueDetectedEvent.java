package com.gogidix.rapidassist.ai.dataquality.domain.event;

import com.gogidix.rapidassist.ai.dataquality.domain.model.DataQualityIssue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain event published when a data quality issue is detected
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataQualityIssueDetectedEvent {

    private UUID eventId;
    private String tenantId;
    private UUID issueId;
    private UUID checkId;
    private UUID ruleId;
    private String issueType;
    private DataQualityIssue.IssueSeverity severity;
    private String entityType;
    private String entityId;
    private String attributeName;
    private String description;
    private LocalDateTime occurredAt;
}
