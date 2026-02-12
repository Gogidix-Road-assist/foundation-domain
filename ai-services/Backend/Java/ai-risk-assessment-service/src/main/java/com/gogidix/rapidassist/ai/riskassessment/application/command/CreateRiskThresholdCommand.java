package com.gogidix.rapidassist.ai.riskassessment.application.command;

import com.gogidix.rapidassist.ai.riskassessment.domain.model.AlertPriority;
import com.gogidix.rapidassist.ai.riskassessment.domain.model.RiskCategory;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Command to create a risk threshold
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRiskThresholdCommand {

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotNull(message = "Category is required")
    private RiskCategory category;

    @NotNull(message = "Low threshold is required")
    @DecimalMin(value = "0.0", message = "Low threshold must be at least 0.0")
    private Double lowThreshold;

    @NotNull(message = "Medium threshold is required")
    @DecimalMin(value = "0.0", message = "Medium threshold must be at least 0.0")
    private Double mediumThreshold;

    @NotNull(message = "High threshold is required")
    @DecimalMin(value = "0.0", message = "High threshold must be at least 0.0")
    private Double highThreshold;

    @NotNull(message = "Critical threshold is required")
    @DecimalMin(value = "0.0", message = "Critical threshold must be at least 0.0")
    @DecimalMax(value = "100.0", message = "Critical threshold must not exceed 100.0")
    private Double criticalThreshold;

    private Boolean alertEnabled;

    private AlertPriority defaultPriority;

    private Boolean active;

    @NotBlank(message = "Created by is required")
    private String createdBy;
}
