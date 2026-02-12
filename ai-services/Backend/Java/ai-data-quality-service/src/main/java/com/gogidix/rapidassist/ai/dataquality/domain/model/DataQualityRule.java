package com.gogidix.rapidassist.ai.dataquality.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Domain Model representing a data quality rule.
 * Defines criteria and conditions for validating data quality.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataQualityRule {

    private UUID id;
    private String tenantId;
    private String name;
    private String description;
    private RuleType ruleType;
    private String entityType;
    private String attributeName;
    private ValidationOperator operator;
    private String thresholdValue;
    private Map<String, Object> parameters;
    private RuleSeverity severity;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    public enum RuleType {
        COMPLETENESS,
        ACCURACY,
        CONSISTENCY,
        VALIDITY,
        UNIQUENESS,
        TIMELINESS,
        INTEGRITY,
        CUSTOM
    }

    public enum ValidationOperator {
        EQUALS,
        NOT_EQUALS,
        GREATER_THAN,
        LESS_THAN,
        GREATER_THAN_OR_EQUAL,
        LESS_THAN_OR_EQUAL,
        CONTAINS,
        NOT_CONTAINS,
        MATCHES_REGEX,
        IN_RANGE,
        NOT_NULL,
        IS_NULL,
        IS_UNIQUE,
        HAS_FORMAT,
        CUSTOM_EXPRESSION
    }

    public enum RuleSeverity {
        CRITICAL,
        HIGH,
        MEDIUM,
        LOW,
        INFO
    }

    /**
     * Business logic: Activate rule
     */
    public void activate() {
        this.active = true;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Deactivate rule
     */
    public void deactivate() {
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Check if rule is active
     */
    public boolean isActive() {
        return this.active;
    }

    /**
     * Business logic: Update rule parameters
     */
    public void updateParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Update threshold value
     */
    public void updateThreshold(String thresholdValue) {
        this.thresholdValue = thresholdValue;
        this.updatedAt = LocalDateTime.now();
    }
}
