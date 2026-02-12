package com.gogidix.rapidassist.ai.fraud.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing a fraud detection result.
 * Contains risk scores, detected patterns, and analysis details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FraudDetection {

    private UUID id;
    private String tenantId;
    private String entityType;
    private String entityId;
    private FraudRiskLevel riskLevel;
    private Double riskScore;
    private String detectionMethod;
    private Map<String, Object> detectionDetails;
    private java.util.List<String> detectedPatterns;
    private String status;
    private Boolean requiresReview;
    private String assignedTo;
    private LocalDateTime detectedAt;
    private LocalDateTime reviewedAt;
    private String reviewedBy;
    private String reviewNotes;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Business logic: Check if detection is high risk
     */
    public boolean isHighRisk() {
        return riskLevel == FraudRiskLevel.HIGH || riskLevel == FraudRiskLevel.CRITICAL;
    }

    /**
     * Business logic: Check if detection requires immediate action
     */
    public boolean requiresImmediateAction() {
        return riskLevel == FraudRiskLevel.CRITICAL || requiresReview;
    }

    /**
     * Business logic: Mark as reviewed
     */
    public void markAsReviewed(String reviewer, String notes) {
        this.status = "REVIEWED";
        this.reviewedBy = reviewer;
        this.reviewNotes = notes;
        this.reviewedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Assign to reviewer
     */
    public void assignTo(String reviewerId) {
        this.assignedTo = reviewerId;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Calculate detection age in hours
     */
    public long getAgeInHours() {
        return detectedAt != null ? java.time.Duration.between(detectedAt, LocalDateTime.now()).toHours() : 0;
    }
}
