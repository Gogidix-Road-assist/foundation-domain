package com.gogidix.rapidassist.ai.riskassessment.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Risk Threshold Domain Model
 * Defines threshold rules for risk assessment and alert generation
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskThreshold {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private String name;
    private String description;
    private RiskCategory category;
    private double lowThreshold;
    private double mediumThreshold;
    private double highThreshold;
    private double criticalThreshold;
    private boolean alertEnabled;
    private AlertPriority defaultPriority;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    /**
     * Business logic: Get risk level from score
     */
    public RiskLevel getRiskLevel(double score) {
        if (score >= criticalThreshold) return RiskLevel.CRITICAL;
        if (score >= highThreshold) return RiskLevel.HIGH;
        if (score >= mediumThreshold) return RiskLevel.MEDIUM;
        return RiskLevel.LOW;
    }

    /**
     * Business logic: Check if alert should be generated
     */
    public boolean shouldGenerateAlert(double score) {
        return alertEnabled && active && score >= highThreshold;
    }

    /**
     * Business logic: Get alert priority for score
     */
    public AlertPriority getAlertPriority(double score) {
        RiskLevel level = getRiskLevel(score);
        return switch (level) {
            case CRITICAL -> AlertPriority.CRITICAL;
            case HIGH -> AlertPriority.HIGH;
            case MEDIUM -> AlertPriority.MEDIUM;
            case LOW -> AlertPriority.LOW;
        };
    }

    /**
     * Business logic: Validate threshold values
     */
    public boolean isValid() {
        return lowThreshold < mediumThreshold &&
               mediumThreshold < highThreshold &&
               highThreshold < criticalThreshold &&
               criticalThreshold <= 100.0;
    }

    /**
     * Business logic: Activate threshold
     */
    public void activate() {
        this.active = true;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Deactivate threshold
     */
    public void deactivate() {
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Enable alerts
     */
    public void enableAlerts() {
        this.alertEnabled = true;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Disable alerts
     */
    public void disableAlerts() {
        this.alertEnabled = false;
        this.updatedAt = LocalDateTime.now();
    }
}
