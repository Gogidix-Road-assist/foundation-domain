package com.gogidix.rapidassist.ai.matching.infrastructure.persistence.entity;

import com.gogidix.rapidassist.ai.matching.domain.model.MatchingAlgorithmType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * MongoDB Document for MatchingRule.
 * Maps to matching_rule collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "matching_rule")
public class MatchingRuleEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed(unique = true)
    private String ruleCode;

    private String ruleName;

    private String description;

    @Indexed
    private String sourceEntityType;

    @Indexed
    private String targetEntityType;

    @Indexed
    private boolean active;

    @Indexed
    private int priority;

    private double minimumSimilarityThreshold;

    private double confidenceThreshold;

    private List<RuleConditionEntity> conditions;

    @Indexed
    private MatchingAlgorithmType algorithmType;

    private Map<String, Object> algorithmParameters;

    private Map<String, Object> metadata;

    private String version;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String createdBy;

    private String updatedBy;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RuleConditionEntity {
        private String fieldName;
        private String operator;
        private Object value;
        private double weight;
        private boolean required;
    }
}
