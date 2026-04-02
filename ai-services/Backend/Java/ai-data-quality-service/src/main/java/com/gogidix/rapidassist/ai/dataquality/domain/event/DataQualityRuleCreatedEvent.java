package com.gogidix.rapidassist.ai.dataquality.domain.event;

import com.gogidix.rapidassist.ai.dataquality.domain.model.DataQualityRule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain event published when a data quality rule is created
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataQualityRuleCreatedEvent {

    private UUID eventId;
    private String tenantId;
    private UUID ruleId;
    private String ruleName;
    private DataQualityRule.RuleType ruleType;
    private String entityType;
    private DataQualityRule.RuleSeverity severity;
    private LocalDateTime occurredAt;
    private String createdBy;
}
