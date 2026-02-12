package com.gogidix.rapidassist.ai.fraud.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing a fraud detection rule.
 * Rules are used to automatically detect fraudulent activities.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FraudRule {

    private UUID id;
    private String tenantId;
    private String ruleName;
    private String ruleCode;
    private String description;
    private String ruleType;
    private Map<String, Object> conditions;
    private String action;
    private Integer priority;
    private Boolean isActive;
    private Boolean autoBlock;
    private Boolean requireReview;
    private Integer executionCount;
    private Integer triggerCount;
    private Double falsePositiveRate;
    private String version;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime lastTriggeredAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Map<String, Object> metadata;

    /**
     * Business logic: Trigger rule
     */
    public void trigger() {
        this.executionCount++;
        this.triggerCount++;
        this.lastTriggeredAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Execute without trigger
     */
    public void execute() {
        this.executionCount++;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Activate rule
     */
    public void activate() {
        this.isActive = true;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Deactivate rule
     */
    public void deactivate() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Update false positive rate
     */
    public void updateFalsePositiveRate(double rate) {
        this.falsePositiveRate = rate;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Check if rule should auto-block
     */
    public boolean shouldAutoBlock() {
        return isActive != null && isActive && autoBlock != null && autoBlock;
    }

    /**
     * Business logic: Check if rule requires review
     */
    public boolean requiresReview() {
        return requireReview != null && requireReview;
    }

    /**
     * Business logic: Check if rule is active
     */
    public boolean isActive() {
        return isActive != null && isActive;
    }

    /**
     * Business logic: Calculate trigger rate
     */
    public double getTriggerRate() {
        if (executionCount == null || executionCount == 0) {
            return 0.0;
        }
        return (double) triggerCount / executionCount;
    }
}
