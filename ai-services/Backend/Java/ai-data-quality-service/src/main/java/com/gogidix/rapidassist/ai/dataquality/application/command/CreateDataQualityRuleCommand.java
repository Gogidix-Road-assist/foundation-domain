package com.gogidix.rapidassist.ai.dataquality.application.command;

import com.gogidix.rapidassist.ai.dataquality.domain.model.DataQualityRule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

/**
 * Command to create a new data quality rule
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDataQualityRuleCommand {

    private String tenantId;
    private String name;
    private String description;
    private DataQualityRule.RuleType ruleType;
    private String entityType;
    private String attributeName;
    private DataQualityRule.ValidationOperator operator;
    private String thresholdValue;
    private Map<String, Object> parameters;
    private DataQualityRule.RuleSeverity severity;
    private boolean active;
    private String createdBy;
}
