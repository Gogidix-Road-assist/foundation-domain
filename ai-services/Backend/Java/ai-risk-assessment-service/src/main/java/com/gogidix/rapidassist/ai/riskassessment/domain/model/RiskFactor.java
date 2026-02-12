package com.gogidix.rapidassist.ai.riskassessment.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Risk Factor Domain Model
 * Represents an individual factor contributing to overall risk
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskFactor {

    private UUID id;
    private String tenantId;
    private UUID riskAssessmentId;
    private String name;
    private String description;
    private RiskCategory category;
    private double weight;
    private double score;
    private double impact;
    private double likelihood;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    /**
     * Business logic: Calculate weighted score
     */
    public double calculateWeightedScore() {
        return score * weight;
    }

    /**
     * Business logic: Check if factor is high risk
     */
    public boolean isHighRisk() {
        return score >= 60.0;
    }

    /**
     * Business logic: Check if factor is critical
     */
    public boolean isCritical() {
        return score >= 80.0;
    }

    /**
     * Business logic: Update factor score
     */
    public void updateScore(double newScore, String updatedBy) {
        if (newScore < 0.0 || newScore > 100.0) {
            throw new IllegalArgumentException("Score must be between 0 and 100");
        }
        this.score = newScore;
        this.updatedBy = updatedBy;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Get risk level
     */
    public RiskLevel getRiskLevel() {
        return RiskLevel.fromScore(this.score);
    }
}
