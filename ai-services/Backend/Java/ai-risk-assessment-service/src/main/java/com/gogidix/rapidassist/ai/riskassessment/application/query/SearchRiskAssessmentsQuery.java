package com.gogidix.rapidassist.ai.riskassessment.application.query;

import com.gogidix.rapidassist.ai.riskassessment.domain.model.AssessmentStatus;
import com.gogidix.rapidassist.ai.riskassessment.domain.model.RiskCategory;
import com.gogidix.rapidassist.ai.riskassessment.domain.model.RiskLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Query to search risk assessments with filters
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchRiskAssessmentsQuery {

    private String tenantId;
    private String subjectId;
    private String subjectType;
    private RiskCategory category;
    private AssessmentStatus status;
    private RiskLevel riskLevel;
    private String assessedBy;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;
}
