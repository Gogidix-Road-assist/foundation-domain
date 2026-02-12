package com.gogidix.rapidassist.ai.riskassessment.domain.aggregate;

import com.gogidix.rapidassist.ai.riskassessment.domain.model.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Aggregate Root for Risk Assessment.
 * Manages the lifecycle and business logic of risk assessment operations.
 * Contains: RiskFactor, RiskAlert, and related mitigation recommendations as child entities.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskAssessment {

    private UUID id;
    private String tenantId;
    private String assessmentCode;
    private String subjectId;
    private String subjectType;
    private String title;
    private String description;
    private AssessmentStatus status;
    private RiskCategory category;
    private double overallRiskScore;
    private RiskLevel riskLevel;
    private String assessedBy;
    private String reviewedBy;
    private LocalDateTime reviewedAt;
    private String approvedBy;
    private LocalDateTime approvedAt;
    private String rejectionReason;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    // Child entities (part of aggregate)
    @Builder.Default
    private List<RiskFactor> riskFactors = new ArrayList<>();

    @Builder.Default
    private List<RiskAlert> alerts = new ArrayList<>();

    @Builder.Default
    private List<String> mitigationRecommendations = new ArrayList<>();

    /**
     * Business logic: Initialize a new risk assessment
     */
    public static RiskAssessment initialize(String tenantId, String subjectId, String subjectType,
                                           String title, String description, RiskCategory category, String createdBy) {
        return RiskAssessment.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .assessmentCode(generateAssessmentCode())
                .subjectId(subjectId)
                .subjectType(subjectType)
                .title(title)
                .description(description)
                .category(category)
                .status(AssessmentStatus.PENDING)
                .overallRiskScore(0.0)
                .riskLevel(RiskLevel.LOW)
                .createdBy(createdBy)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .riskFactors(new ArrayList<>())
                .alerts(new ArrayList<>())
                .mitigationRecommendations(new ArrayList<>())
                .build();
    }

    /**
     * Business logic: Start assessment
     */
    public void startAssessment(String assessedBy) {
        if (this.status != AssessmentStatus.PENDING) {
            throw new IllegalStateException("Cannot start assessment in status: " + this.status);
        }
        this.status = AssessmentStatus.IN_PROGRESS;
        this.assessedBy = assessedBy;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = assessedBy;
    }

    /**
     * Business logic: Complete assessment
     */
    public void completeAssessment(String completedBy) {
        if (this.status != AssessmentStatus.IN_PROGRESS) {
            throw new IllegalStateException("Cannot complete assessment in status: " + this.status);
        }
        if (this.riskFactors.isEmpty()) {
            throw new IllegalStateException("Cannot complete assessment without risk factors");
        }
        this.status = AssessmentStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = completedBy;
        calculateOverallRiskScore();
    }

    /**
     * Business logic: Review assessment
     */
    public void reviewAssessment(String reviewedBy) {
        if (this.status != AssessmentStatus.COMPLETED) {
            throw new IllegalStateException("Cannot review assessment in status: " + this.status);
        }
        this.status = AssessmentStatus.REVIEWED;
        this.reviewedBy = reviewedBy;
        this.reviewedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = reviewedBy;
    }

    /**
     * Business logic: Approve assessment
     */
    public void approveAssessment(String approvedBy) {
        if (this.status != AssessmentStatus.REVIEWED) {
            throw new IllegalStateException("Cannot approve assessment in status: " + this.status);
        }
        this.status = AssessmentStatus.APPROVED;
        this.approvedBy = approvedBy;
        this.approvedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = approvedBy;
    }

    /**
     * Business logic: Reject assessment
     */
    public void rejectAssessment(String reason, String rejectedBy) {
        if (this.status != AssessmentStatus.REVIEWED) {
            throw new IllegalStateException("Cannot reject assessment in status: " + this.status);
        }
        this.status = AssessmentStatus.REJECTED;
        this.rejectionReason = reason;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = rejectedBy;
    }

    /**
     * Business logic: Cancel assessment
     */
    public void cancelAssessment(String cancelledBy) {
        if (this.status == AssessmentStatus.APPROVED) {
            throw new IllegalStateException("Cannot cancel an approved assessment");
        }
        this.status = AssessmentStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = cancelledBy;
    }

    /**
     * Business logic: Add risk factor
     */
    public void addRiskFactor(RiskFactor factor) {
        factor.setRiskAssessmentId(this.id);
        factor.setTenantId(this.tenantId);
        this.riskFactors.add(factor);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Remove risk factor
     */
    public void removeRiskFactor(UUID factorId) {
        this.riskFactors.removeIf(factor -> factor.getId().equals(factorId));
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Add alert
     */
    public void addAlert(RiskAlert alert) {
        alert.setRiskAssessmentId(this.id);
        alert.setTenantId(this.tenantId);
        this.alerts.add(alert);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Add mitigation recommendation
     */
    public void addMitigationRecommendation(String recommendation) {
        this.mitigationRecommendations.add(recommendation);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Calculate overall risk score
     */
    public void calculateOverallRiskScore() {
        if (this.riskFactors.isEmpty()) {
            this.overallRiskScore = 0.0;
            this.riskLevel = RiskLevel.LOW;
            return;
        }

        double totalWeight = this.riskFactors.stream()
                .mapToDouble(RiskFactor::getWeight)
                .sum();

        if (totalWeight == 0.0) {
            this.overallRiskScore = this.riskFactors.stream()
                    .mapToDouble(RiskFactor::getScore)
                    .average()
                    .orElse(0.0);
        } else {
            this.overallRiskScore = this.riskFactors.stream()
                    .mapToDouble(RiskFactor::calculateWeightedScore)
                    .sum() / totalWeight;
        }

        this.riskLevel = RiskLevel.fromScore(this.overallRiskScore);
    }

    /**
     * Business logic: Generate alerts based on risk factors
     */
    public List<RiskAlert> generateAlerts(RiskThreshold threshold) {
        List<RiskAlert> newAlerts = new ArrayList<>();

        for (RiskFactor factor : this.riskFactors) {
            if (threshold.shouldGenerateAlert(factor.getScore())) {
                RiskAlert alert = RiskAlert.builder()
                        .id(UUID.randomUUID())
                        .tenantId(this.tenantId)
                        .riskAssessmentId(this.id)
                        .title("High Risk Factor Detected: " + factor.getName())
                        .description(String.format("Risk factor '%s' has a score of %.2f which exceeds threshold",
                                factor.getName(), factor.getScore()))
                        .priority(threshold.getAlertPriority(factor.getScore()))
                        .status(AlertStatus.ACTIVE)
                        .riskScore(factor.getScore())
                        .riskLevel(factor.getRiskLevel())
                        .category(factor.getCategory())
                        .createdBy(this.assessedBy)
                        .createdAt(LocalDateTime.now())
                        .build();

                newAlerts.add(alert);
                this.alerts.add(alert);
            }
        }

        this.updatedAt = LocalDateTime.now();
        return newAlerts;
    }

    /**
     * Business logic: Get active alerts
     */
    public List<RiskAlert> getActiveAlerts() {
        return this.alerts.stream()
                .filter(RiskAlert::isActive)
                .collect(Collectors.toList());
    }

    /**
     * Business logic: Get high risk factors
     */
    public List<RiskFactor> getHighRiskFactors() {
        return this.riskFactors.stream()
                .filter(RiskFactor::isHighRisk)
                .collect(Collectors.toList());
    }

    /**
     * Business logic: Get critical risk factors
     */
    public List<RiskFactor> getCriticalRiskFactors() {
        return this.riskFactors.stream()
                .filter(RiskFactor::isCritical)
                .collect(Collectors.toList());
    }

    /**
     * Business logic: Check if assessment is active
     */
    public boolean isActive() {
        return this.status == AssessmentStatus.IN_PROGRESS ||
               this.status == AssessmentStatus.COMPLETED ||
               this.status == AssessmentStatus.REVIEWED;
    }

    /**
     * Business logic: Check if assessment is final
     */
    public boolean isFinal() {
        return this.status == AssessmentStatus.APPROVED ||
               this.status == AssessmentStatus.REJECTED ||
               this.status == AssessmentStatus.CANCELLED;
    }

    /**
     * Business logic: Check if assessment has critical risks
     */
    public boolean hasCriticalRisks() {
        return this.riskLevel == RiskLevel.CRITICAL || !getCriticalRiskFactors().isEmpty();
    }

    /**
     * Business logic: Get assessment duration in days
     */
    public long getAssessmentDurationDays() {
        if (this.createdAt == null) {
            return 0;
        }
        LocalDateTime endTime = this.updatedAt != null ? this.updatedAt : LocalDateTime.now();
        return java.time.Duration.between(this.createdAt, endTime).toDays();
    }

    /**
     * Business logic: Update metadata
     */
    public void updateMetadata(String key, Object value) {
        if (this.metadata == null) {
            this.metadata = new java.util.HashMap<>();
        }
        this.metadata.put(key, value);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Generate assessment code
     */
    private static String generateAssessmentCode() {
        return "RA-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Business logic: Get risk factors by category
     */
    public List<RiskFactor> getRiskFactorsByCategory(RiskCategory category) {
        return this.riskFactors.stream()
                .filter(factor -> factor.getCategory() == category)
                .collect(Collectors.toList());
    }

    /**
     * Business logic: Get unresolved alerts
     */
    public List<RiskAlert> getUnresolvedAlerts() {
        return this.alerts.stream()
                .filter(alert -> alert.getStatus() != AlertStatus.RESOLVED &&
                               alert.getStatus() != AlertStatus.DISMISSED)
                .collect(Collectors.toList());
    }

    /**
     * Business logic: Check if all alerts are resolved
     */
    public boolean areAllAlertsResolved() {
        return this.alerts.isEmpty() || this.alerts.stream()
                .allMatch(alert -> alert.getStatus() == AlertStatus.RESOLVED ||
                                alert.getStatus() == AlertStatus.DISMISSED);
    }

    /**
     * Business logic: Get overdue alerts
     */
    public List<RiskAlert> getOverdueAlerts(int hoursThreshold) {
        return this.alerts.stream()
                .filter(alert -> alert.isOverdue(hoursThreshold))
                .collect(Collectors.toList());
    }
}
