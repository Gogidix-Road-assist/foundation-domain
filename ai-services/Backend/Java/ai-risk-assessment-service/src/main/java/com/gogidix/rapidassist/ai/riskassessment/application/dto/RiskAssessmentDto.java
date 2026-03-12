package com.gogidix.rapidassist.ai.riskassessment.application.dto;

import com.gogidix.rapidassist.ai.riskassessment.domain.model.*;
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
 * DTO for Risk Assessment
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskAssessmentDto {

    @EqualsAndHashCode.Include


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

    // Child entities
    private List<RiskFactorDto> riskFactors;
    private List<RiskAlertDto> alerts;
    private List<String> mitigationRecommendations;

    // Computed fields
    private int riskFactorCount;
    private int highRiskFactorCount;
    private int criticalFactorCount;
    private int activeAlertCount;
    private int unresolvedAlertCount;
    private long durationDays;
    private boolean hasCriticalRisks;
    private boolean allAlertsResolved;
}
