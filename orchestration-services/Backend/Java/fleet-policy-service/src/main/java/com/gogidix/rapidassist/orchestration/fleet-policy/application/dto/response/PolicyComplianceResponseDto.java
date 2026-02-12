package com.gogidix.rapidassist.orchestration.fleet_policy.application.dto.response;

import com.gogidix.rapidassist.orchestration.fleet_policy.domain.model.PolicyCompliance;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO for policy compliance responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyComplianceResponseDto {

    private String id;

    private String tenantId;

    private String policyId;

    private String policyCode;

    private String policyName;

    private PolicyCompliance.ComplianceEntityType entityType;

    private String entityId;

    private String entityName;

    private LocalDate complianceDate;

    private PolicyCompliance.ComplianceStatus status;

    private Double complianceScore;

    private Integer totalChecks;

    private Integer passedChecks;

    private Integer failedChecks;

    private Integer warningChecks;

    private Integer violationCount;

    private Integer totalPoints;

    private String lastCheckedBy;

    private LocalDateTime lastCheckedAt;

    private String checkedBy;

    private LocalDateTime checkedAt;

    private Map<String, Object> complianceDetails;

    private String notes;

    private Boolean requiresAction;

    private String requiredAction;

    private LocalDateTime actionDueBy;

    private Boolean actionCompleted;

    private String actionCompletedBy;

    private LocalDateTime actionCompletedAt;

    private Boolean actionOverdue;

    private Double compliancePercentage;

    private String createdBy;

    private LocalDateTime createdAt;

    private String updatedBy;

    private LocalDateTime updatedAt;
}
