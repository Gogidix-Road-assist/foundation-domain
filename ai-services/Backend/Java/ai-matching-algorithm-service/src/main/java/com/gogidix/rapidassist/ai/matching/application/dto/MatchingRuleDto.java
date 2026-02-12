package com.gogidix.rapidassist.ai.matching.application.dto;

import com.gogidix.rapidassist.ai.matching.domain.model.MatchingRule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for MatchingRule.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchingRuleDto {

    private UUID id;
    private String tenantId;
    private String ruleName;
    private String ruleCode;
    private String description;
    private String sourceEntityType;
    private String targetEntityType;
    private Boolean active;
    private Integer priority;
    private Double minimumSimilarityThreshold;
    private Double confidenceThreshold;
    private List<MatchingRule.RuleCondition> conditions;
    private com.gogidix.rapidassist.ai.matching.domain.model.MatchingAlgorithmType algorithmType;
    private Map<String, Object> algorithmParameters;
    private Map<String, Object> metadata;
    private String version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
