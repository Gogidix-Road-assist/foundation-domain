package com.gogidix.rapidassist.ai.moderation.application.command;

import com.gogidix.rapidassist.ai.moderation.domain.model.ModerationRule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Command to create or update a moderation rule
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModerationRuleCommand {

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    @NotBlank(message = "Rule name is required")
    private String name;

    private String description;

    @NotNull(message = "Rule type is required")
    private ModerationRule.RuleType ruleType;

    @NotNull(message = "Severity is required")
    private ModerationRule.RuleSeverity severity;

    private List<String> keywords;

    private List<String> patterns;

    private Map<String, Object> metadata;

    private boolean active;

    private Integer priority;

    private String createdBy;
}
