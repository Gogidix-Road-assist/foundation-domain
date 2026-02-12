package com.gogidix.rapidassist.ai.riskassessment.application.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Query to get a risk assessment by ID
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetRiskAssessmentQuery {

    private String tenantId;
    private UUID assessmentId;
    private Boolean includeRiskFactors;
    private Boolean includeAlerts;
    private Boolean includeRecommendations;
}
