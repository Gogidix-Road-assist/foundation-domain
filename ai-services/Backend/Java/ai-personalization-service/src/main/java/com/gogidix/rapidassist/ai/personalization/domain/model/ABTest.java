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
 * Domain model representing an A/B test for personalization.
 * Used to test different personalization strategies.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ABTest {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private String testName;
    private String testCode;
    private String description;

    // Test configuration
    private String testType;
    private String hypothesis;
    private Map<String, Object> variants;
    private String metricType;
    private String targetMetric;

    // Traffic allocation
    @Builder.Default
    private Integer totalTrafficPercentage = 100;

    private Map<String, Integer> trafficAllocation;
    private Integer minSampleSize;
    private Integer maxSampleSize;

    // Duration and schedule
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    @Builder.Default
    private Integer durationDays = 14;

    // Status
    @Builder.Default
    private ABTestStatus status = ABTestStatus.DRAFT;

    // Results
    private String winningVariant;
    private Double confidenceLevel;
    private Double statisticalSignificance;
    private Boolean isStatisticallySignificant;
    private Map<String, Object> results;
    private String conclusion;

    // Targeting
    private List<String> targetSegmentIds;
    private List<String> excludedSegmentIds;
    private Map<String, Object> targetCriteria;

    // Metadata
    private String category;
    private List<String> tags;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String createdBy;
    private String updatedBy;
    private Long version;

    /**
     * Check if test is currently running
     */
    public boolean isRunning() {
        if (status != ABTestStatus.RUNNING) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();

        if (startDate != null && now.isBefore(startDate)) {
            return false;
        }

        if (endDate != null && now.isAfter(endDate)) {
            return false;
        }

        return true;
    }

    /**
     * Check if test is complete
     */
    public boolean isComplete() {
        return status == ABTestStatus.COMPLETED;
    }

    /**
     * Start test
     */
    public void start() {
        this.status = ABTestStatus.RUNNING;
        this.startedAt = LocalDateTime.now();
        if (this.startDate == null) {
            this.startDate = LocalDateTime.now();
        }
        if (this.endDate == null) {
            this.endDate = startDate.plusDays(durationDays);
        }
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Stop test
     */
    public void stop() {
        this.status = ABTestStatus.STOPPED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Complete test
     */
    public void complete() {
        this.status = ABTestStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Pause test
     */
    public void pause() {
        this.status = ABTestStatus.PAUSED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Resume test
     */
    public void resume() {
        if (status == ABTestStatus.PAUSED) {
            this.status = ABTestStatus.RUNNING;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Set winner
     */
    public void setWinner(String variantId, Double confidence, Double significance) {
        this.winningVariant = variantId;
        this.confidenceLevel = confidence;
        this.statisticalSignificance = significance;
        this.isStatisticallySignificant = significance >= 0.95;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Add target segment
     */
    public void addTargetSegment(String segmentId) {
        if (this.targetSegmentIds == null) {
            this.targetSegmentIds = new java.util.ArrayList<>();
        }
        if (!this.targetSegmentIds.contains(segmentId)) {
            this.targetSegmentIds.add(segmentId);
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Remove target segment
     */
    public void removeTargetSegment(String segmentId) {
        if (this.targetSegmentIds != null) {
            this.targetSegmentIds.remove(segmentId);
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
     * Get traffic allocation for variant
     */
    public Integer getVariantTraffic(String variantId) {
        return trafficAllocation != null ? trafficAllocation.getOrDefault(variantId, 0) : 0;
    }

    /**
     * Check if user is in target audience
     */
    public boolean isUserInTarget(String userId, List<String> userSegmentIds) {
        // Check exclusions first
        if (excludedSegmentIds != null && !excludedSegmentIds.isEmpty()) {
            for (String excludedId : excludedSegmentIds) {
                if (userSegmentIds.contains(excludedId)) {
                    return false;
                }
            }
        }

        // If no targeting specified, include everyone
        if (targetSegmentIds == null || targetSegmentIds.isEmpty()) {
            return true;
        }

        // Check if user is in any target segment
        for (String targetId : targetSegmentIds) {
            if (userSegmentIds.contains(targetId)) {
                return true;
            }
        }

        return false;
    }
}
