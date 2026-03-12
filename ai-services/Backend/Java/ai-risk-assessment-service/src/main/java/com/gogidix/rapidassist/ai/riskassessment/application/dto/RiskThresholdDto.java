package com.gogidix.rapidassist.ai.riskassessment.application.dto;

import com.gogidix.rapidassist.ai.riskassessment.domain.model.AlertPriority;
import com.gogidix.rapidassist.ai.riskassessment.domain.model.RiskCategory;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for Risk Threshold
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskThresholdDto {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private String name;
    private String description;
    private RiskCategory category;
    private double lowThreshold;
    private double mediumThreshold;
    private double highThreshold;
    private double criticalThreshold;
    private boolean alertEnabled;
    private AlertPriority defaultPriority;
    private boolean active;
    private boolean valid;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
