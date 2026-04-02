package com.gogidix.rapidassist.ai.dataquality.application.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

/**
 * Command to execute a data quality check
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteDataQualityCheckCommand {

    private String tenantId;
    private UUID ruleId;
    private String checkName;
    private String entityType;
    private String datasetIdentifier;
    private Map<String, Object> checkParameters;
    private String executedBy;
}
