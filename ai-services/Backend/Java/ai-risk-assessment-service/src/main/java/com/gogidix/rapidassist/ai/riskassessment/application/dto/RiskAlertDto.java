package com.gogidix.rapidassist.ai.riskassessment.application.dto;

import com.gogidix.rapidassist.ai.riskassessment.domain.model.AlertPriority;
import com.gogidix.rapidassist.ai.riskassessment.domain.model.AlertStatus;
import com.gogidix.rapidassist.ai.riskassessment.domain.model.RiskCategory;
import com.gogidix.rapidassist.ai.riskassessment.domain.model.RiskLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for Risk Alert
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskAlertDto {

    private UUID id;
    private String tenantId;
    private UUID riskAssessmentId;
    private String title;
    private String description;
    private AlertPriority priority;
    private AlertStatus status;
    private double riskScore;
    private RiskLevel riskLevel;
    private RiskCategory category;
    private String assignedTo;
    private String acknowledgedBy;
    private LocalDateTime acknowledgedAt;
    private LocalDateTime resolvedAt;
    private String resolution;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    // Computed fields
    private boolean active;
    private boolean overdue;
    private long timeSinceCreationHours;
}
