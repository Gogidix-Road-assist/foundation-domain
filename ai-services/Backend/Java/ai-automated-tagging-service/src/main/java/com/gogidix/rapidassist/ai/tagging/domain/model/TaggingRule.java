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
 * Domain model representing a Rule for automatic tag assignment.
 * Rules define conditions under which tags should be automatically applied.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaggingRule {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private String name;
    private String description;
    private RuleType ruleType;
    private String condition;
    private List<UUID> tagIds;
    private RulePriority priority;
    private RuleStatus status;
    private Double confidenceThreshold;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime lastExecutedAt;
    private Integer executionCount;
    private Long version;

    /**
     * Rule type enumeration
     */
    public enum RuleType {
        KEYWORD_BASED,
        AI_BASED,
        PATTERN_BASED,
        HYBRID
    }

    /**
     * Rule priority enumeration
     */
    public enum RulePriority {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }

    /**
     * Rule status enumeration
     */
    public enum RuleStatus {
        ACTIVE,
        INACTIVE,
        DRAFT,
        ARCHIVED
    }

    /**
     * Activate rule
     */
    public void activate() {
        this.status = RuleStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Deactivate rule
     */
    public void deactivate() {
        this.status = RuleStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Record execution
     */
    public void recordExecution() {
        this.lastExecutedAt = LocalDateTime.now();
        this.executionCount = (this.executionCount == null) ? 1 : this.executionCount + 1;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Check if rule is active
     */
    public boolean isActive() {
        return RuleStatus.ACTIVE.equals(this.status);
    }
}
