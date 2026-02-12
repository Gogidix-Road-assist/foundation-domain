package com.gogidix.rapidassist.ai.matching.application.command;

import com.gogidix.rapidassist.ai.matching.domain.model.MatchingRule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * Command to create a matching rule.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateMatchingRuleCommand {

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    @NotBlank(message = "Rule name is required")
    private String ruleName;

    private String ruleCode;

    private String description;

    @NotBlank(message = "Source entity type is required")
    private String sourceEntityType;

    @NotBlank(message = "Target entity type is required")
    private String targetEntityType;

    private Boolean active;

    @NotNull(message = "Priority is required")
    private Integer priority;

    private Double minimumSimilarityThreshold;

    private Double confidenceThreshold;

    private List<MatchingRule.RuleCondition> conditions;

    private com.gogidix.rapidassist.ai.matching.domain.model.MatchingAlgorithmType algorithmType;

    private Map<String, Object> algorithmParameters;

    private Map<String, Object> metadata;
}
