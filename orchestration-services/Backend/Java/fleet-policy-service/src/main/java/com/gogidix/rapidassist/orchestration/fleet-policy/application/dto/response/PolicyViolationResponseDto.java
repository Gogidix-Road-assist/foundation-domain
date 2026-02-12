package com.gogidix.rapidassist.orchestration.fleet_policy.application.dto.response;

import com.gogidix.rapidassist.orchestration.fleet_policy.domain.model.PolicyViolation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO for policy violation responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyViolationResponseDto {

    private String id;

    private String tenantId;

    private String policyId;

    private String policyCode;

    private String policyName;

    private String ruleId;

    private String ruleCode;

    private String ruleName;

    private PolicyViolation.ViolationEntityType entityType;

    private String entityId;

    private String entityName;

    private PolicyViolation.ViolationSeverity severity;

    private PolicyViolation.ViolationStatus status;

    private Integer points;

    private String violationMessage;

    private String detectedBy;

    private LocalDateTime detectedAt;

    private String location;

    private Double latitude;

    private Double longitude;

    private Object actualValue;

    private Object expectedValue;

    private String unit;

    private Map<String, Object> context;

    private String acknowledgedBy;

    private LocalDateTime acknowledgedAt;

    private String resolvedBy;

    private LocalDateTime resolvedAt;

    private String resolutionNotes;

    private Boolean requiresImmediateAction;

    private String requiredAction;

    private LocalDateTime actionDueBy;

    private Boolean actionCompleted;

    private String actionCompletedBy;

    private LocalDateTime actionCompletedAt;

    private Boolean overdue;

    private Boolean requiresEscalation;
}
