package com.gogidix.rapidassist.ai.matching.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing a Matching Rule.
 * Defines custom matching rules for specific entity types.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchingRule {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private String ruleName;
    private String ruleCode;
    private String description;
    private String sourceEntityType;
    private String targetEntityType;
    private boolean active;
    private int priority;
    private double minimumSimilarityThreshold;
    private double confidenceThreshold;
    private List<RuleCondition> conditions;
    private MatchingAlgorithmType algorithmType;
    private Map<String, Object> algorithmParameters;
    private Map<String, Object> metadata;
    private String version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    @Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RuleCondition {
        private String fieldName;
        private String operator;
        private Object value;
        private double weight;
        private boolean required;
    }
}
