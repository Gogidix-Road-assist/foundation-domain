package com.gogidix.rapidassist.ai.tagging.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Domain model representing a TagDefinition.
 * Defines tag categories, rules, and AI tagging configuration.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagDefinition {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private String name;
    private String description;
    private TagCategory category;
    private List<String> keywords;
    private List<String> patterns;
    private TaggingRuleType ruleType;
    private Double minConfidenceThreshold;
    private Boolean isAutoApply;
    private Integer priority;
    private java.util.Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
