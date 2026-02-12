package com.gogidix.rapidassist.ai.riskassessment.interfaces.rest.response;

import com.gogidix.rapidassist.ai.riskassessment.domain.model.AlertPriority;
import com.gogidix.rapidassist.ai.riskassessment.domain.model.AlertStatus;
import com.gogidix.rapidassist.ai.riskassessment.domain.model.RiskCategory;
import com.gogidix.rapidassist.ai.riskassessment.domain.model.RiskLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * REST Response for Alert Summary (embedded in assessment response)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertSummaryResponse {

    private UUID id;
    private String title;
    private AlertPriority priority;
    private AlertStatus status;
    private double riskScore;
    private RiskLevel riskLevel;
    private RiskCategory category;
    private LocalDateTime createdAt;
}
