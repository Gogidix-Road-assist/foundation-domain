package com.gogidix.rapidassist.ai.anomaly.application.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.util.Map;

/**
 * Command to update a detection rule
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDetectionRuleCommand {

    @NotNull(message = "Rule ID is required")
    private java.util.UUID ruleId;

    private String name;

    private String description;

    private Map<String, Object> conditions;

    private Integer priority;

    private Boolean isActive;

    private Map<String, Object> metadata;

    private String tenantId;

    private String updatedBy;
}
