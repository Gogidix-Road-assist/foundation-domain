package com.gogidix.rapidassist.ai.moderation.application.dto;

import com.gogidix.rapidassist.ai.moderation.domain.model.ModerationRule;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO for ModerationRule
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModerationRuleDto {

    private String id;
    private String tenantId;
    private String name;
    private String description;
    private ModerationRule.RuleType ruleType;
    private ModerationRule.RuleSeverity severity;
    private List<String> keywords;
    private List<String> patterns;
    private Map<String, Object> metadata;
    private boolean active;
    private Integer priority;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String updatedBy;
}
