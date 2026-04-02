package com.gogidix.rapidassist.ai.dataquality.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DataQualityRule domain model
 */
class DataQualityRuleTest {

    @Test
    void testCreateRule() {
        DataQualityRule rule = DataQualityRule.builder()
                .id(UUID.randomUUID())
                .tenantId("tenant-1")
                .name("Test Rule")
                .description("Test Description")
                .ruleType(DataQualityRule.RuleType.COMPLETENESS)
                .entityType("Customer")
                .attributeName("email")
                .operator(DataQualityRule.ValidationOperator.NOT_NULL)
                .severity(DataQualityRule.RuleSeverity.HIGH)
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        assertNotNull(rule);
        assertEquals("Test Rule", rule.getName());
        assertEquals(DataQualityRule.RuleType.COMPLETENESS, rule.getRuleType());
        assertTrue(rule.isActive());
    }

    @Test
    void testActivateRule() {
        DataQualityRule rule = DataQualityRule.builder()
                .active(false)
                .build();

        rule.activate();

        assertTrue(rule.isActive());
    }

    @Test
    void testDeactivateRule() {
        DataQualityRule rule = DataQualityRule.builder()
                .active(true)
                .build();

        rule.deactivate();

        assertFalse(rule.isActive());
    }

    @Test
    void testUpdateThreshold() {
        DataQualityRule rule = DataQualityRule.builder()
                .thresholdValue("100")
                .build();

        rule.updateThreshold("200");

        assertEquals("200", rule.getThresholdValue());
    }

    @Test
    void testUpdateParameters() {
        DataQualityRule rule = DataQualityRule.builder()
                .parameters(new HashMap<>())
                .build();

        Map<String, Object> newParams = new HashMap<>();
        newParams.put("minLength", 5);
        newParams.put("maxLength", 50);

        rule.updateParameters(newParams);

        assertEquals(2, rule.getParameters().size());
        assertEquals(5, rule.getParameters().get("minLength"));
    }
}
