package com.gogidix.rapidassist.ai.dataquality.application.command;

import com.gogidix.rapidassist.ai.dataquality.domain.model.DataQualityRule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

/**
 * Command to update an existing data quality rule
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDataQualityRuleCommand {

    private String tenantId;
    private UUID ruleId;
    private String name;
    private String description;
    private String thresholdValue;
    private Map<String, Object> parameters;
    private String updatedBy;
}
