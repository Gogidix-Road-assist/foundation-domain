package com.gogidix.rapidassist.ai.tagging.infrastructure.persistence.entity;

import com.gogidix.rapidassist.ai.tagging.domain.model.TaggingRule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * MongoDB Document for TaggingRule.
 * Maps to tagging_rule collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "tagging_rule")
public class TaggingRuleEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private String name;

    private String description;

    private TaggingRule.RuleType ruleType;

    private String condition;

    private List<UUID> tagIds;

    private TaggingRule.RulePriority priority;

    @Indexed
    private TaggingRule.RuleStatus status;

    private Double confidenceThreshold;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String createdBy;

    private String updatedBy;

    private LocalDateTime lastExecutedAt;

    private Integer executionCount;

    @Indexed
    private Long version;
}
