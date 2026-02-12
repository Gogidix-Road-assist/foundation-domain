package com.gogidix.rapidassist.ai.dataquality.infrastructure.persistence.mongodb.entity;

import com.gogidix.rapidassist.ai.dataquality.domain.model.DataQualityRule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * MongoDB entity for DataQualityRule
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "data_quality_rules")
public class DataQualityRuleEntity {

    @Id
    private UUID id;

    @Indexed
    private String tenantId;

    @Indexed
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
