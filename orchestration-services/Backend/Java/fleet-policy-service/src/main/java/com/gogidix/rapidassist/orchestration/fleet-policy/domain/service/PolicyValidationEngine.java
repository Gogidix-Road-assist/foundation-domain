package com.gogidix.rapidassist.orchestration.fleet_policy.domain.service;

import com.gogidix.rapidassist.orchestration.fleet_policy.domain.model.Policy;
import com.gogidix.rapidassist.orchestration.fleet_policy.domain.model.PolicyRule;
import com.gogidix.rapidassist.orchestration.fleet_policy.domain.model.PolicyViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Domain service for policy validation and violation detection
 * Core business logic for evaluating policies and rules
 */
@Slf4j
@Service
public class PolicyValidationEngine {

    /**
     * Validate data against a policy
     */
    public List<PolicyViolation> validatePolicy(
            Policy policy,
            PolicyViolation.ViolationEntityType entityType,
            String entityId,
            String entityName,
            Map<String, Object> data
    ) {
        List<PolicyViolation> violations = new ArrayList<>();

        if (policy == null || !policy.isEffective()) {
            return violations;
        }

        for (PolicyRule rule : policy.getRules()) {
            if (rule.getIsActive() != null && rule.getIsActive()) {
                PolicyViolation violation = validateRule(
                        policy,
                        rule,
                        entityType,
                        entityId,
                        entityName,
                        data
                );
                if (violation != null) {
                    violations.add(violation);
                }
            }
        }

        return violations;
    }

    /**
     * Validate data against a single rule
     */
    public PolicyViolation validateRule(
            Policy policy,
            PolicyRule rule,
            PolicyViolation.ViolationEntityType entityType,
            String entityId,
            String entityName,
            Map<String, Object> data
    ) {
        Object actualValue = extractValue(rule, data);

        if (!rule.evaluate(actualValue)) {
            return createViolation(
                    policy,
                    rule,
                    entityType,
                    entityId,
                    entityName,
                    actualValue,
                    data
            );
        }

        return null;
    }

    /**
     * Extract value from data based on rule configuration
     */
    private Object extractValue(PolicyRule rule, Map<String, Object> data) {
        if (data == null) {
            return null;
        }

        // Extract value based on rule type
        return switch (rule.getRuleType()) {
            case SPEED_LIMIT -> data.get("speed");
            case HOURS_OF_SERVICE -> data.get("hoursOfWork");
            case MAINTENANCE_INTERVAL -> data.get("mileage") != null ?
                    data.get("mileage") : data.get("lastMaintenanceDate");
            case BEHAVIOR_THRESHOLD -> data.get("behaviorScore");
            case DOCUMENT_VALIDITY -> data.get("documentExpiryDate");
            case ZONE_RESTRICTION -> data.get("zone");
            case TIME_RESTRICTION -> data.get("timestamp");
            default -> data.get(rule.getRuleCode().toLowerCase());
        };
    }

    /**
     * Create a violation record
     */
    private PolicyViolation createViolation(
            Policy policy,
            PolicyRule rule,
            PolicyViolation.ViolationEntityType entityType,
            String entityId,
            String entityName,
            Object actualValue,
            Map<String, Object> context
    ) {
        return PolicyViolation.builder()
                .tenantId(policy.getTenantId())
                .policyId(policy.getId())
                .policyCode(policy.getPolicyCode())
                .policyName(policy.getName())
                .ruleId(rule.getId())
                .ruleCode(rule.getRuleCode())
                .ruleName(rule.getName())
                .entityType(entityType)
                .entityId(entityId)
                .entityName(entityName)
                .severity(mapSeverity(policy.getSeverity(), rule.getPriority()))
                .status(PolicyViolation.ViolationStatus.OPEN)
                .points(rule.getViolationPoints())
                .violationMessage(rule.getViolationMessage() != null ?
                        rule.getViolationMessage() :
                        String.format("Violation of %s: %s", rule.getName(), rule.getDescription()))
                .detectedBy("SYSTEM")
                .detectedAt(LocalDateTime.now())
                .actualValue(actualValue)
                .expectedValue(rule.getThresholdValue())
                .unit(rule.getUnit().name())
                .context(context)
                .requiresImmediateAction(rule.getViolationAction() ==
                        PolicyRule.ViolationAction.BLOCK_OPERATION)
                .requiredAction(determineRequiredAction(rule))
                .actionDueBy(calculateActionDueBy(rule))
                .build();
    }

    /**
     * Map policy and rule severity to violation severity
     */
    private PolicyViolation.ViolationSeverity mapSeverity(
            Policy.PolicySeverity policySeverity,
            Integer rulePriority
    ) {
        if (policySeverity == Policy.PolicySeverity.CRITICAL ||
            (rulePriority != null && rulePriority >= 100)) {
            return PolicyViolation.ViolationSeverity.CRITICAL;
        } else if (policySeverity == Policy.PolicySeverity.HIGH ||
                   (rulePriority != null && rulePriority >= 70)) {
            return PolicyViolation.ViolationSeverity.HIGH;
        } else if (policySeverity == Policy.PolicySeverity.MEDIUM ||
                   (rulePriority != null && rulePriority >= 40)) {
            return PolicyViolation.ViolationSeverity.MEDIUM;
        }
        return PolicyViolation.ViolationSeverity.LOW;
    }

    /**
     * Determine required action based on rule configuration
     */
    private String determineRequiredAction(PolicyRule rule) {
        return switch (rule.getViolationAction()) {
            case BLOCK_OPERATION -> "Immediate operation blocked";
            case REQUIRE_ACKNOWLEDGEMENT -> "Acknowledge violation";
            case WARNING -> "Review warning";
            case ALERT -> "Review alert";
            case LOG_ONLY -> "No action required";
        };
    }

    /**
     * Calculate action due date based on severity
     */
    private LocalDateTime calculateActionDueBy(PolicyRule rule) {
        LocalDateTime now = LocalDateTime.now();

        if (rule.getViolationAction() == PolicyRule.ViolationAction.BLOCK_OPERATION) {
            return now.plusHours(1);
        } else if (rule.getViolationAction() == PolicyRule.ViolationAction.REQUIRE_ACKNOWLEDGEMENT) {
            return now.plusHours(24);
        }

        // Based on priority
        int priority = rule.getPriority() != null ? rule.getPriority() : 0;
        if (priority >= 100) {
            return now.plusHours(4);
        } else if (priority >= 70) {
            return now.plusHours(24);
        } else if (priority >= 40) {
            return now.plusDays(3);
        }
        return now.plusDays(7);
    }

    /**
     * Check if violation should be escalated
     */
    public boolean shouldEscalate(PolicyViolation violation) {
        return violation.requiresEscalation();
    }

    /**
     * Calculate compliance score
     */
    public double calculateComplianceScore(
            int totalChecks,
            int passedChecks,
            int failedChecks
    ) {
        if (totalChecks == 0) {
            return 0.0;
        }
        return ((double) passedChecks / totalChecks) * 100.0;
    }

    /**
     * Determine compliance status
     */
    public String determineComplianceStatus(double score) {
        if (score >= 100.0) {
            return "COMPLIANT";
        } else if (score >= 70.0) {
            return "PARTIALLY_COMPLIANT";
        } else {
            return "NON_COMPLIANT";
        }
    }
}
