package com.gogidix.rapidassist.ai.fraud.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for FraudPattern
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FraudPatternDto {

    private UUID id;
    private String tenantId;
    private String patternName;
    private String patternType;
    private String description;
    private Map<String, Object> patternDefinition;
    private String detectionAlgorithm;
    private Integer priority;
    private Boolean isActive;
    private Double confidenceThreshold;
    private Integer detectionCount;
    private Integer falsePositiveCount;
    private Integer truePositiveCount;
    private Double precision;
    private Double recall;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Map<String, Object> metadata;
}
