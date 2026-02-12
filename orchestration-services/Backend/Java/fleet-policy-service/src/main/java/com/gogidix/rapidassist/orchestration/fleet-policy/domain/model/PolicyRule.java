package com.gogidix.rapidassist.orchestration.fleet_policy.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Domain entity representing an individual policy rule
 * Rules are the specific conditions that make up a policy
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "policy_rules")
public class PolicyRule {

    @Id
    private String id;

    @Indexed
    private String tenantId;

    @Indexed
    private String policyId;

    @Indexed
    private String ruleCode;

    private String name;
    private String description;

    @Indexed
    private RuleType ruleType;

    private String conditionExpression;

    @Builder.Default
    private RuleComparator comparator = RuleComparator.EQUALS;

    private Object thresholdValue;

    @Builder.Default
    private RuleUnit unit = RuleUnit.NONE;

    @Builder.Default
    private Integer priority = 0;

    @Builder.Default
    private Boolean isMandatory = true;

    @Builder.Default
    private ViolationAction violationAction = ViolationAction.ALERT;

    private String violationMessage;

    @Builder.Default
    private Integer points = 0;

    private String createdBy;
    private LocalDateTime createdAt;

    private String updatedBy;
    private LocalDateTime updatedAt;

    @Builder.Default
    private Boolean isActive = true;

    private Map<String, Object> metadata;

    public enum RuleType {
        SPEED_LIMIT,
        HOURS_OF_SERVICE,
        MAINTENANCE_INTERVAL,
        INSPECTION_REQUIREMENT,
        BEHAVIOR_THRESHOLD,
        DOCUMENT_VALIDITY,
        ZONE_RESTRICTION,
        TIME_RESTRICTION,
        CUSTOM
    }

    public enum RuleComparator {
        EQUALS,
        NOT_EQUALS,
        GREATER_THAN,
        LESS_THAN,
        GREATER_THAN_OR_EQUAL,
        LESS_THAN_OR_EQUAL,
        CONTAINS,
        BETWEEN,
        REGEX
    }

    public enum RuleUnit {
        NONE,
        KILOMETERS_PER_HOUR,
        MILES_PER_HOUR,
        HOURS,
        DAYS,
        KILOMETERS,
        MILES,
        PERCENTAGE,
        COUNT,
        DEGREES_CELSIUS,
        DEGREES_FAHRENHEIT
    }

    public enum ViolationAction {
        ALERT,
        WARNING,
        BLOCK_OPERATION,
        REQUIRE_ACKNOWLEDGEMENT,
        LOG_ONLY
    }

    /**
     * Evaluate a value against this rule
     */
    public boolean evaluate(Object actualValue) {
        if (thresholdValue == null || actualValue == null) {
            return false;
        }

        try {
            double actual = convertToDouble(actualValue);
            double threshold = convertToDouble(thresholdValue);

            return switch (comparator) {
                case EQUALS -> actual == threshold;
                case NOT_EQUALS -> actual != threshold;
                case GREATER_THAN -> actual > threshold;
                case LESS_THAN -> actual < threshold;
                case GREATER_THAN_OR_EQUAL -> actual >= threshold;
                case LESS_THAN_OR_EQUAL -> actual <= threshold;
                default -> false;
            };
        } catch (Exception e) {
            return false;
        }
    }

    private double convertToDouble(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return Double.parseDouble(value.toString());
    }

    /**
     * Get the violation points for this rule
     */
    public int getViolationPoints() {
        return points != null ? points : 0;
    }
}
