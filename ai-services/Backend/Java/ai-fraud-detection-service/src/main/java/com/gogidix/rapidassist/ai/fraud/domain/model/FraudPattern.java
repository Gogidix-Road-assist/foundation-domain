package com.gogidix.rapidassist.ai.fraud.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing a known fraud pattern.
 * Used for pattern matching and fraud detection.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class FraudPattern {

    @EqualsAndHashCode.Include
    private UUID id;
    private String tenantId;
    private String patternName;
    private String patternType;
    private String description;
    private Map<String, Object> patternDefinition;
    private String detectionAlgorithm;
    private Integer priority;
    private Boolean isActive;
    private Double confidenceThreshold;
    private Integer detectionCount;
    private Integer falsePositiveCount;
    private Integer truePositiveCount;
    private Double precision;
    private Double recall;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Map<String, Object> metadata;

    /**
     * Business logic: Calculate precision
     */
    public void calculatePrecision() {
        if (truePositiveCount + falsePositiveCount > 0) {
            this.precision = (double) truePositiveCount / (truePositiveCount + falsePositiveCount);
        } else {
            this.precision = 0.0;
        }
    }

    /**
     * Business logic: Increment detection count
     */
    public void incrementDetection(boolean isTruePositive) {
        this.detectionCount++;
        if (isTruePositive) {
            this.truePositiveCount++;
        } else {
            this.falsePositiveCount++;
        }
        calculatePrecision();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Check if pattern is active
     */
    public boolean isActive() {
        return isActive != null && isActive;
    }

    /**
     * Business logic: Deactivate pattern
     */
    public void deactivate() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Activate pattern
     */
    public void activate() {
        this.isActive = true;
        this.updatedAt = LocalDateTime.now();
    }
}
