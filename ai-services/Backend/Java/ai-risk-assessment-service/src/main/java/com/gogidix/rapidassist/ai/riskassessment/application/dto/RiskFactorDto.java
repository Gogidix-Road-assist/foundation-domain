package com.gogidix.rapidassist.ai.riskassessment.application.dto;

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
 * DTO for Risk Factor
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskFactorDto {

    private UUID id;
    private String tenantId;
    private UUID riskAssessmentId;
    private String name;
    private String description;
    private RiskCategory category;
    private double weight;
    private double score;
    private double impact;
    private double likelihood;
    private RiskLevel riskLevel;
    private double weightedScore;
    private boolean highRisk;
    private boolean critical;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
