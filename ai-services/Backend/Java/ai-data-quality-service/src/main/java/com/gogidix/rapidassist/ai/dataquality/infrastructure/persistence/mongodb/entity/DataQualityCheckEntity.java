package com.gogidix.rapidassist.ai.dataquality.infrastructure.persistence.mongodb.entity;

import com.gogidix.rapidassist.ai.dataquality.domain.model.DataQualityCheck;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * MongoDB entity for DataQualityCheck
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "data_quality_checks")
public class DataQualityCheckEntity {

    @Id
    private UUID id;

    @Indexed
    private String tenantId;

    @Indexed
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
