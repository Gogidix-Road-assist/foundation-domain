package com.gogidix.rapidassist.ai.dataquality.application.dto;

import com.gogidix.rapidassist.ai.dataquality.domain.model.DataQualityCheck;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for DataQualityCheck
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataQualityCheckDto {

    private UUID id;
    private String tenantId;
    private UUID ruleId;
    private String checkName;
    private String entityType;
    private String datasetIdentifier;
    private DataQualityCheck.CheckStatus status;
    private int totalRecords;
    private int recordsChecked;
    private int passedRecords;
    private int failedRecords;
    private double passPercentage;
    private Map<String, Object> checkParameters;
    private LocalDateTime executedAt;
    private LocalDateTime completedAt;
    private long executionDurationMs;
    private String errorMessage;
    private Map<String, Object> metadata;
    private String executedBy;
}
