package com.gogidix.rapidassist.ai.personalization.infrastructure.persistence.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * MongoDB Document for PersonalizationRule.
 * Maps to personalization_rule collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "personalization_rule")
public class PersonalizationRuleEntity {

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

    // Rule definition
    @Indexed
    private String ruleType;
    private String targetType;
    private Map<String, Object> conditions;
    private Map<String, Object> actions;
    private Map<String, Object> parameters;

    // Priority and execution
    private Integer priority;
    private Integer executionOrder;

    // Status
    @Indexed
    private String status;

    // Effectiveness tracking
    private Double effectiveness;
    private Integer totalExecutions;
    private Integer successfulExecutions;
    private Double averageConfidence;

    // Schedule
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private String schedule;

    // Segments
    private List<String> applicableSegmentIds;
    private List<String> excludedSegmentIds;

    // Metadata
    private String category;
    private List<String> tags;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastExecutedAt;
    private String createdBy;
    private String updatedBy;
    private Long version;
}
