package com.gogidix.rapidassist.ai.anomaly.domain.model;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DetectionRule domain model.
 */
class DetectionRuleTest {

    @Test
    void testDetectionRuleCreation() {
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("threshold", 100);

        DetectionRule rule = DetectionRule.builder()
                .id(java.util.UUID.randomUUID())
                .tenantId("tenant-1")
                .name("High CPU Usage Rule")
                .description("Detects high CPU usage")
                .ruleType(RuleType.THRESHOLD)
                .conditions(conditions)
                .dataSource("system-metrics")
                .priority(8)
                .isActive(true)
                .createAlert(true)
                .alertSeverity(AlertSeverity.CRITICAL)
                .build();

        assertNotNull(rule);
        assertEquals("High CPU Usage Rule", rule.getName());
        assertEquals(RuleType.THRESHOLD, rule.getRuleType());
        assertEquals(8, rule.getPriority());
        assertTrue(rule.getIsActive());
    }

    @Test
    void testActivateRule() {
        DetectionRule rule = DetectionRule.builder()
                .isActive(false)
                .build();

        rule.activate();

        assertTrue(rule.isActiveRule());
    }

    @Test
    void testDeactivateRule() {
        DetectionRule rule = DetectionRule.builder()
                .isActive(true)
                .build();

        rule.deactivate();

        assertFalse(rule.isActiveRule());
    }

    @Test
    void testIsHighPriority() {
        DetectionRule rule = DetectionRule.builder()
                .priority(9)
                .build();

        assertTrue(rule.isHighPriority());
    }

    @Test
    void testIsNotHighPriority() {
        DetectionRule rule = DetectionRule.builder()
                .priority(5)
                .build();

        assertFalse(rule.isHighPriority());
    }

    @Test
    void testShouldCreateAlert() {
        DetectionRule rule = DetectionRule.builder()
                .isActive(true)
                .createAlert(true)
                .build();

        assertTrue(rule.shouldCreateAlert());
    }

    @Test
    void testUpdateThreshold() {
        DetectionRule rule = DetectionRule.builder()
                .build();

        rule.updatePriority(10);

        assertEquals(10, rule.getPriority());
    }
}
