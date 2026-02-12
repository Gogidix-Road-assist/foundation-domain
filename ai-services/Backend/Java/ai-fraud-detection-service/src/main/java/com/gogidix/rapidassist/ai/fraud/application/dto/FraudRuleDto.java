package com.gogidix.rapidassist.ai.fraud.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for FraudRule
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FraudRuleDto {

    private UUID id;
    private String tenantId;
    private String ruleName;
    private String ruleCode;
    private String description;
    private String ruleType;
    private Map<String, Object> conditions;
    private String action;
    private Integer priority;
    private Boolean isActive;
    private Boolean autoBlock;
    private Boolean requireReview;
    private Integer executionCount;
    private Integer triggerCount;
    private Double falsePositiveRate;
    private String version;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime lastTriggeredAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Map<String, Object> metadata;
}
