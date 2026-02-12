package com.gogidix.rapidassist.orchestration.fleet_policy.unit.domain;

import com.gogidix.rapidassist.orchestration.fleet_policy.domain.model.Policy;
import com.gogidix.rapidassist.orchestration.fleet_policy.domain.model.PolicyRule;
import com.gogidix.rapidassist.orchestration.fleet_policy.domain.model.PolicyViolation;
import com.gogidix.rapidassist.orchestration.fleet_policy.domain.service.PolicyValidationEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PolicyValidationEngine
 */
class PolicyValidationEngineTest {

    private PolicyValidationEngine validationEngine;

    @BeforeEach
    void setUp() {
        validationEngine = new PolicyValidationEngine();
    }

    @Test
    void testValidatePolicy_NoViolations() {
        // Arrange
        Policy policy = createTestPolicy();
        Map<String, Object> data = new HashMap<>();
        data.put("speed", 80);

        // Act
        var violations = validationEngine.validatePolicy(
                policy,
                PolicyViolation.ViolationEntityType.VEHICLE,
                "vehicle-1",
                "Test Vehicle",
                data
        );

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidatePolicy_WithViolations() {
        // Arrange
        Policy policy = createTestPolicy();
        Map<String, Object> data = new HashMap<>();
        data.put("speed", 120); // Exceeds limit

        // Act
        var violations = validationEngine.validatePolicy(
                policy,
                PolicyViolation.ViolationEntityType.VEHICLE,
                "vehicle-1",
                "Test Vehicle",
                data
        );

        // Assert
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("SPEED_LIMIT", violations.get(0).getRuleCode());
    }

    @Test
    void testPolicyRule_Evaluate_GreaterThan() {
        // Arrange
        PolicyRule rule = PolicyRule.builder()
                .ruleCode("SPEED_LIMIT")
                .ruleType(PolicyRule.RuleType.SPEED_LIMIT)
                .comparator(PolicyRule.RuleComparator.GREATER_THAN)
                .thresholdValue(100)
                .unit(PolicyRule.RuleUnit.KILOMETERS_PER_HOUR)
                .build();

        // Act & Assert
        assertTrue(rule.evaluate(110));
        assertFalse(rule.evaluate(90));
        assertFalse(rule.evaluate(100));
    }

    @Test
    void testPolicyRule_Evaluate_LessThan() {
        // Arrange
        PolicyRule rule = PolicyRule.builder()
                .ruleCode("FUEL_LIMIT")
                .ruleType(PolicyRule.RuleType.CUSTOM)
                .comparator(PolicyRule.RuleComparator.LESS_THAN)
                .thresholdValue(10)
                .unit(PolicyRule.RuleUnit.PERCENTAGE)
                .build();

        // Act & Assert
        assertTrue(rule.evaluate(5));
        assertFalse(rule.evaluate(15));
        assertFalse(rule.evaluate(10));
    }

    @Test
    void testCalculateComplianceScore() {
        // Act
        double score = validationEngine.calculateComplianceScore(100, 85, 15);

        // Assert
        assertEquals(85.0, score);
    }

    @Test
    void testDetermineComplianceStatus() {
        // Act & Assert
        assertEquals("COMPLIANT", validationEngine.determineComplianceStatus(100.0));
        assertEquals("PARTIALLY_COMPLIANT", validationEngine.determineComplianceStatus(75.0));
        assertEquals("NON_COMPLIANT", validationEngine.determineComplianceStatus(50.0));
    }

    private Policy createTestPolicy() {
        PolicyRule speedRule = PolicyRule.builder()
                .id("rule-1")
                .tenantId("tenant-1")
                .ruleCode("SPEED_LIMIT")
                .name("Speed Limit")
                .description("Maximum speed limit")
                .ruleType(PolicyRule.RuleType.SPEED_LIMIT)
                .comparator(PolicyRule.RuleComparator.GREATER_THAN)
                .thresholdValue(100)
                .unit(PolicyRule.RuleUnit.KILOMETERS_PER_HOUR)
                .priority(80)
                .isMandatory(true)
                .violationAction(PolicyRule.ViolationAction.ALERT)
                .violationMessage("Speed limit exceeded")
                .points(10)
                .isActive(true)
                .build();

        return Policy.builder()
                .id("policy-1")
                .tenantId("tenant-1")
                .policyCode("SAFETY-001")
                .name("Safety Policy")
                .description("Fleet safety rules")
                .policyType(Policy.PolicyType.SAFETY)
                .severity(Policy.PolicySeverity.HIGH)
                .status(Policy.PolicyStatus.ACTIVE)
                .isActive(true)
                .effectiveFrom(java.time.LocalDateTime.now().minusDays(1))
                .build();
    }
}
