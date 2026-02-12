package com.gogidix.rapidassist.orchestration.fleet_policy.application.dto.request;

import com.gogidix.rapidassist.orchestration.fleet_policy.domain.model.PolicyRule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO for creating/updating policy rules
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyRuleRequestDto {

    @NotBlank(message = "Rule code is required")
    private String ruleCode;

    @NotBlank(message = "Rule name is required")
    private String name;

    private String description;

    @NotNull(message = "Rule type is required")
    private PolicyRule.RuleType ruleType;

    private String conditionExpression;

    private PolicyRule.RuleComparator comparator;

    private Object thresholdValue;

    private PolicyRule.RuleUnit unit;

    private Integer priority;

    private Boolean isMandatory;

    private PolicyRule.ViolationAction violationAction;

    private String violationMessage;

    private Integer points;

    private Boolean isActive;

    private Map<String, Object> metadata;
}
