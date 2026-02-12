package com.gogidix.rapidassist.orchestration.fleet_policy.application.dto.response;

import com.gogidix.rapidassist.orchestration.fleet_policy.domain.model.PolicyRule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO for policy rule responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyRuleResponseDto {

    private String id;

    private String tenantId;

    private String policyId;

    private String ruleCode;

    private String name;

    private String description;

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

    private String createdBy;

    private LocalDateTime createdAt;

    private String updatedBy;

    private LocalDateTime updatedAt;

    private Boolean isActive;

    private Map<String, Object> metadata;
}
