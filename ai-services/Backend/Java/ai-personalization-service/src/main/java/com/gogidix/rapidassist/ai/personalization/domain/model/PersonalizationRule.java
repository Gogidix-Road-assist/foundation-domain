package com.gogidix.rapidassist.ai.personalization.domain.model;

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
 * Domain model representing a personalization rule.
 * Defines logic and conditions for applying personalization.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonalizationRule {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private String ruleName;
    private String ruleCode;
    private String description;

    // Rule definition
    private String ruleType;
    private String targetType;
    private Map<String, Object> conditions;
    private Map<String, Object> actions;
    private Map<String, Object> parameters;

    // Priority and execution
    @Builder.Default
    private Integer priority = 0;

    @Builder.Default
    private Integer executionOrder = 0;

    // Status
    @Builder.Default
    private RuleStatus status = RuleStatus.DRAFT;

    // Effectiveness tracking
    @Builder.Default
    private Double effectiveness = 0.0;

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

    /**
     * Check if rule is currently valid and active
     */
    public boolean isActive() {
        if (status != RuleStatus.ACTIVE) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();

        if (validFrom != null && now.isBefore(validFrom)) {
            return false;
        }

        if (validUntil != null && now.isAfter(validUntil)) {
            return false;
        }

        return true;
    }

    /**
     * Check if rule applies to a segment
     */
    public boolean appliesToSegment(String segmentId) {
        if (excludedSegmentIds != null && excludedSegmentIds.contains(segmentId)) {
            return false;
        }

        if (applicableSegmentIds == null || applicableSegmentIds.isEmpty()) {
            return true;
        }

        return applicableSegmentIds.contains(segmentId);
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
     * Archive rule
     */
    public void archive() {
        this.status = RuleStatus.ARCHIVED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Record execution
     */
    public void recordExecution(boolean success) {
        if (this.totalExecutions == null) {
            this.totalExecutions = 0;
        }
        if (this.successfulExecutions == null) {
            this.successfulExecutions = 0;
        }

        this.totalExecutions++;
        if (success) {
            this.successfulExecutions++;
        }

        this.lastExecutedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        // Update effectiveness
        if (this.totalExecutions > 0) {
            this.effectiveness = (double) this.successfulExecutions / this.totalExecutions * 100;
        }
    }

    /**
     * Update confidence
     */
    public void updateConfidence(Double confidence) {
        this.averageConfidence = confidence;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Set schedule
     */
    public void setSchedule(String scheduleExpression) {
        this.schedule = scheduleExpression;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Add applicable segment
     */
    public void addApplicableSegment(String segmentId) {
        if (this.applicableSegmentIds == null) {
            this.applicableSegmentIds = new java.util.ArrayList<>();
        }
        if (!this.applicableSegmentIds.contains(segmentId)) {
            this.applicableSegmentIds.add(segmentId);
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Remove applicable segment
     */
    public void removeApplicableSegment(String segmentId) {
        if (this.applicableSegmentIds != null) {
            this.applicableSegmentIds.remove(segmentId);
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Add excluded segment
     */
    public void addExcludedSegment(String segmentId) {
        if (this.excludedSegmentIds == null) {
            this.excludedSegmentIds = new java.util.ArrayList<>();
        }
        if (!this.excludedSegmentIds.contains(segmentId)) {
            this.excludedSegmentIds.add(segmentId);
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Remove excluded segment
     */
    public void removeExcludedSegment(String segmentId) {
        if (this.excludedSegmentIds != null) {
            this.excludedSegmentIds.remove(segmentId);
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Add tag
     */
    public void addTag(String tag) {
        if (this.tags == null) {
            this.tags = new java.util.ArrayList<>();
        }
        if (!this.tags.contains(tag)) {
            this.tags.add(tag);
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Remove tag
     */
    public void removeTag(String tag) {
        if (this.tags != null) {
            this.tags.remove(tag);
            this.updatedAt = LocalDateTime.now();
        }
    }
}
