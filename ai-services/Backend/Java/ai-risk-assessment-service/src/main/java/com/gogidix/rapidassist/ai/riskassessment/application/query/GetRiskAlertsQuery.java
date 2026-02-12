package com.gogidix.rapidassist.ai.riskassessment.application.query;

import com.gogidix.rapidassist.ai.riskassessment.domain.model.AlertPriority;
import com.gogidix.rapidassist.ai.riskassessment.domain.model.AlertStatus;
import com.gogidix.rapidassist.ai.riskassessment.domain.model.RiskCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Query to get risk alerts with filters
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetRiskAlertsQuery {

    private String tenantId;
    private UUID riskAssessmentId;
    private AlertStatus status;
    private AlertPriority priority;
    private RiskCategory category;
    private String assignedTo;
    private Boolean activeOnly;
    private Boolean unresolvedOnly;
    private Integer page;
    private Integer size;
}
