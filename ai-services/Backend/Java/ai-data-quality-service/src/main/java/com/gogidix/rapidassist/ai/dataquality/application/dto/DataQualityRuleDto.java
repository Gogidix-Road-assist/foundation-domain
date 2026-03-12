package com.gogidix.rapidassist.ai.dataquality.application.dto;

import com.gogidix.rapidassist.ai.dataquality.domain.model.DataQualityRule;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for DataQualityRule
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataQualityRuleDto {

    @EqualsAndHashCode.Include


    private UUID id;
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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
