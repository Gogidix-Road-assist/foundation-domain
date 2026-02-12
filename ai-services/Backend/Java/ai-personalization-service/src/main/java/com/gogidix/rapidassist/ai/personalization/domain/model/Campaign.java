package com.gogidix.rapidassist.ai.personalization.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing a personalization campaign.
 * Orchestrates multiple personalization rules and recommendations.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Campaign {

    private UUID id;
    private String tenantId;
    private String campaignName;
    private String campaignCode;
    private String description;

    // Campaign configuration
    private String campaignType;
    private String objective;
    private Map<String, Object> configuration;
    private List<String> ruleIds;
    private List<String> abTestIds;

    // Targeting
    private List<String> targetSegmentIds;
    private Map<String, Object> targetCriteria;
    private Integer targetAudienceSize;

    // Schedule
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String schedule;
    private Boolean isEvergreen;

    // Budget and spend
    private BigDecimal budget;
    private BigDecimal dailyBudget;
    private BigDecimal totalSpent;
    private BigDecimal dailySpent;

    // Performance metrics
    private Integer totalImpressions;
    private Integer totalClicks;
    private Integer totalConversions;
    private Double ctr;
    private Double conversionRate;
    private Double cpa;
    private Double roas;

    // Status
    @Builder.Default
    private CampaignStatus status = CampaignStatus.DRAFT;

    // Approval
    private Boolean needsApproval;
    private Boolean isApproved;
    private String approvedBy;
    private LocalDateTime approvedAt;

    // Metadata
    private String category;
    private List<String> tags;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Long version;

    /**
     * Check if campaign is currently active
     */
    public boolean isActive() {
        if (status != CampaignStatus.ACTIVE) {
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
     * Activate campaign
     */
    public void activate() {
        if (!isApproved) {
            throw new IllegalStateException("Campaign must be approved before activation");
        }

        this.status = CampaignStatus.ACTIVE;
        if (this.startDate == null) {
            this.startDate = LocalDateTime.now();
        }
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Pause campaign
     */
    public void pause() {
        if (status == CampaignStatus.ACTIVE) {
            this.status = CampaignStatus.PAUSED;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Resume campaign
     */
    public void resume() {
        if (status == CampaignStatus.PAUSED) {
            this.status = CampaignStatus.ACTIVE;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Complete campaign
     */
    public void complete() {
        this.status = CampaignStatus.COMPLETED;
        this.endDate = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Cancel campaign
     */
    public void cancel() {
        this.status = CampaignStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Approve campaign
     */
    public void approve(String approver) {
        this.isApproved = true;
        this.approvedBy = approver;
        this.approvedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Reject campaign
     */
    public void reject() {
        this.isApproved = false;
        this.status = CampaignStatus.REJECTED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Record impression
     */
    public void recordImpression() {
        if (this.totalImpressions == null) {
            this.totalImpressions = 0;
        }
        this.totalImpressions++;
        updateMetrics();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Record click
     */
    public void recordClick() {
        if (this.totalClicks == null) {
            this.totalClicks = 0;
        }
        this.totalClicks++;
        updateMetrics();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Record conversion
     */
    public void recordConversion(BigDecimal value) {
        if (this.totalConversions == null) {
            this.totalConversions = 0;
        }
        this.totalConversions++;

        if (this.totalSpent == null) {
            this.totalSpent = BigDecimal.ZERO;
        }

        updateMetrics();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Update performance metrics
     */
    private void updateMetrics() {
        // Calculate CTR
        if (totalImpressions != null && totalImpressions > 0 && totalClicks != null) {
            this.ctr = (double) totalClicks / totalImpressions * 100;
        }

        // Calculate conversion rate
        if (totalClicks != null && totalClicks > 0 && totalConversions != null) {
            this.conversionRate = (double) totalConversions / totalClicks * 100;
        }

        // Calculate CPA
        if (totalConversions != null && totalConversions > 0 &&
            totalSpent != null && totalSpent.compareTo(BigDecimal.ZERO) > 0) {
            this.cpa = totalSpent.divide(
                new BigDecimal(totalConversions), 2, java.math.RoundingMode.HALF_UP).doubleValue();
        }

        // Calculate ROAS
        if (totalSpent != null && totalSpent.compareTo(BigDecimal.ZERO) > 0) {
            // This would need actual revenue data
            // For now, placeholder logic
            this.roas = 0.0;
        }
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
     * Add rule
     */
    public void addRule(String ruleId) {
        if (this.ruleIds == null) {
            this.ruleIds = new java.util.ArrayList<>();
        }
        if (!this.ruleIds.contains(ruleId)) {
            this.ruleIds.add(ruleId);
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Remove rule
     */
    public void removeRule(String ruleId) {
        if (this.ruleIds != null) {
            this.ruleIds.remove(ruleId);
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Add A/B test
     */
    public void addABTest(String abTestId) {
        if (this.abTestIds == null) {
            this.abTestIds = new java.util.ArrayList<>();
        }
        if (!this.abTestIds.contains(abTestId)) {
            this.abTestIds.add(abTestId);
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
     * Check if budget is exhausted
     */
    public boolean isBudgetExhausted() {
        if (budget == null || totalSpent == null) {
            return false;
        }
        return totalSpent.compareTo(budget) >= 0;
    }

    /**
     * Check if daily budget is exhausted
     */
    public boolean isDailyBudgetExhausted() {
        if (dailyBudget == null || dailySpent == null) {
            return false;
        }
        return dailySpent.compareTo(dailyBudget) >= 0;
    }
}
