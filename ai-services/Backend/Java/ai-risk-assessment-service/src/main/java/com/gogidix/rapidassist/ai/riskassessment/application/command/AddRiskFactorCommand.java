package com.gogidix.rapidassist.ai.riskassessment.application.command;

import com.gogidix.rapidassist.ai.riskassessment.domain.model.RiskCategory;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

/**
 * Command to add a risk factor to an assessment
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddRiskFactorCommand {

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    @NotNull(message = "Risk assessment ID is required")
    private UUID riskAssessmentId;

    @NotBlank(message = "Factor name is required")
    private String name;

    private String description;

    @NotNull(message = "Category is required")
    private RiskCategory category;

    @NotNull(message = "Weight is required")
    @DecimalMin(value = "0.0", message = "Weight must be at least 0.0")
    @DecimalMax(value = "1.0", message = "Weight must not exceed 1.0")
    private Double weight;

    @NotNull(message = "Score is required")
    @DecimalMin(value = "0.0", message = "Score must be at least 0.0")
    @DecimalMax(value = "100.0", message = "Score must not exceed 100.0")
    private Double score;

    @DecimalMin(value = "0.0", message = "Impact must be at least 0.0")
    @DecimalMax(value = "10.0", message = "Impact must not exceed 10.0")
    private Double impact;

    @DecimalMin(value = "0.0", message = "Likelihood must be at least 0.0")
    @DecimalMax(value = "10.0", message = "Likelihood must not exceed 10.0")
    private Double likelihood;

    private Map<String, Object> metadata;

    @NotBlank(message = "Created by is required")
    private String createdBy;
}
