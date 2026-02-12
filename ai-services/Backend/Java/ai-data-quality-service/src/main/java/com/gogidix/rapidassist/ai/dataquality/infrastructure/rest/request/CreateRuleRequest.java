package com.gogidix.rapidassist.ai.dataquality.infrastructure.rest.request;

import com.gogidix.rapidassist.ai.dataquality.domain.model.DataQualityRule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * REST request to create a data quality rule
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRuleRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotNull(message = "Rule type is required")
    private DataQualityRule.RuleType ruleType;

    @NotBlank(message = "Entity type is required")
    private String entityType;

    private String attributeName;

    @NotNull(message = "Operator is required")
    private DataQualityRule.ValidationOperator operator;

    private String thresholdValue;
    private Map<String, Object> parameters;

    @NotNull(message = "Severity is required")
    private DataQualityRule.RuleSeverity severity;

    private Boolean active;
    private String createdBy;
}
