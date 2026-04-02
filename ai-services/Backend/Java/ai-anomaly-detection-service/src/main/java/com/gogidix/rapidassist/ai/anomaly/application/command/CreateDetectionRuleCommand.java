package com.gogidix.rapidassist.ai.anomaly.application.command;

import com.gogidix.rapidassist.ai.anomaly.domain.model.AlertSeverity;
import com.gogidix.rapidassist.ai.anomaly.domain.model.RuleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * Command to create a detection rule
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDetectionRuleCommand {

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotNull(message = "Rule type is required")
    private RuleType ruleType;

    private List<String> patternIds;

    @NotNull(message = "Conditions are required")
    private Map<String, Object> conditions;

    @NotBlank(message = "Data source is required")
    private String dataSource;

    private Integer priority;

    private Boolean createAlert;

    private AlertSeverity alertSeverity;

    private List<String> notificationChannels;

    private String category;

    private Map<String, Object> metadata;

    private String tenantId;

    private String createdBy;
}
