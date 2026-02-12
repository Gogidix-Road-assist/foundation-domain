package com.gogidix.rapidassist.ai.riskassessment.application.command;

import com.gogidix.rapidassist.ai.riskassessment.domain.model.RiskCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Command to create a new risk assessment
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRiskAssessmentCommand {

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    @NotBlank(message = "Subject ID is required")
    private String subjectId;

    @NotBlank(message = "Subject type is required")
    private String subjectType;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Category is required")
    private RiskCategory category;

    @NotBlank(message = "Created by is required")
    private String createdBy;

    private Map<String, Object> metadata;
}
