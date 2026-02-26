package com.gogidix.rapidassist.ai.anomaly.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DetectionRule domain model.
 */
@DisplayName("DetectionRule Domain Model Tests")
class DetectionRuleDomainModelTest {

    private final String tenantId = "tenant-123";
    private final UUID testId = UUID.randomUUID();

    @Test
    @DisplayName("Should create DetectionRule using builder")
    void shouldCreateDetectionRuleUsingBuilder() {
        // When
        DetectionRule rule = DetectionRule.builder()
                .id(testId)
                .tenantId(tenantId)
                .name("Test Rule")
                .description("Test detection rule")
                .ruleType(RuleType.STATISTICAL)
                .dataSource("logs")
                .priority(1)
                .isActive(true)
                .createAlert(true)
                .alertSeverity(AlertSeverity.CRITICAL)
                .category("security")
                .createdBy("admin")
                .version(1L)
                .build();

        // Then
        assertNotNull(rule);
        assertEquals(testId, rule.getId());
        assertEquals(tenantId, rule.getTenantId());
        assertEquals("Test Rule", rule.getName());
        assertEquals(RuleType.STATISTICAL, rule.getRuleType());
        assertEquals(AlertSeverity.CRITICAL, rule.getAlertSeverity());
        assertTrue(rule.getIsActive());
        assertTrue(rule.getCreateAlert());
    }

    @Test
    @DisplayName("Should activate rule successfully")
    void shouldActivateRuleSuccessfully() {
        // Given
        DetectionRule rule = DetectionRule.builder()
                .id(testId)
                .tenantId(tenantId)
                .isActive(false)
                .build();

        // When
        rule.activate();

        // Then
        assertTrue(rule.getIsActive());
        assertNotNull(rule.getUpdatedAt());
    }

    @Test
    @DisplayName("Should deactivate rule successfully")
    void shouldDeactivateRuleSuccessfully() {
        // Given
        DetectionRule rule = DetectionRule.builder()
                .id(testId)
                .tenantId(tenantId)
                .isActive(true)
                .build();

        // When
        rule.deactivate();

        // Then
        assertFalse(rule.getIsActive());
        assertNotNull(rule.getUpdatedAt());
    }

    @Test
    @DisplayName("Should update conditions successfully")
    void shouldUpdateConditionsSuccessfully() {
        // Given
        DetectionRule rule = DetectionRule.builder()
                .id(testId)
                .tenantId(tenantId)
                .conditions(new HashMap<>())
                .build();

        Map<String, Object> newConditions = new HashMap<>();
        newConditions.put("threshold", 100);
        newConditions.put("window", 300);

        // When
        rule.updateConditions(newConditions);

        // Then
        assertEquals(newConditions, rule.getConditions());
        assertNotNull(rule.getUpdatedAt());
    }

    @Test
    @DisplayName("Should check if rule is active")
    void shouldCheckIfRuleIsActive() {
        // Given
        DetectionRule rule = DetectionRule.builder()
                .id(testId)
                .tenantId(tenantId)
                .isActive(true)
                .build();

        // Then
        assertTrue(rule.isActiveRule());

        // When
        rule.deactivate();

        // Then
        assertFalse(rule.isActiveRule());
    }

    @Test
    @DisplayName("Should create DetectionRule using no-args constructor")
    void shouldCreateDetectionRuleUsingNoArgsConstructor() {
        // When
        DetectionRule rule = new DetectionRule();

        // Then
        assertNotNull(rule);
        assertNull(rule.getId());
        assertNull(rule.getTenantId());
        assertNull(rule.getName());
        assertNull(rule.getRuleType());
        assertNull(rule.getAlertSeverity());
        assertNull(rule.getIsActive());
        assertNull(rule.getCreateAlert());
        assertNull(rule.getVersion());
    }

    @Test
    @DisplayName("Should handle pattern IDs correctly")
    void shouldHandlePatternIdsCorrectly() {
        // Given
        List<String> patternIds = Arrays.asList("pattern-1", "pattern-2", "pattern-3");

        DetectionRule rule = DetectionRule.builder()
                .id(testId)
                .tenantId(tenantId)
                .patternIds(patternIds)
                .build();

        // Then
        assertEquals(3, rule.getPatternIds().size());
        assertEquals("pattern-1", rule.getPatternIds().get(0));
    }

    @Test
    @DisplayName("Should handle notification channels correctly")
    void shouldHandleNotificationChannelsCorrectly() {
        // Given
        List<String> channels = Arrays.asList("email", "slack", "webhook");

        DetectionRule rule = DetectionRule.builder()
                .id(testId)
                .tenantId(tenantId)
                .notificationChannels(channels)
                .build();

        // Then
        assertEquals(3, rule.getNotificationChannels().size());
        assertTrue(rule.getNotificationChannels().contains("email"));
    }

    @Test
    @DisplayName("Should handle metadata map correctly")
    void shouldHandleMetadataMapCorrectly() {
        // Given
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("key1", "value1");
        metadata.put("key2", 123);
        metadata.put("key3", true);

        DetectionRule rule = DetectionRule.builder()
                .id(testId)
                .tenantId(tenantId)
                .metadata(metadata)
                .build();

        // Then
        assertEquals(metadata, rule.getMetadata());
        assertEquals(3, rule.getMetadata().size());
    }

    @Test
    @DisplayName("Should handle different rule types")
    void shouldHandleDifferentRuleTypes() {
        // Given
        DetectionRule statisticalRule = DetectionRule.builder()
                .ruleType(RuleType.STATISTICAL)
                .build();

        DetectionRule mlRule = DetectionRule.builder()
                .ruleType(RuleType.MACHINE_LEARNING)
                .build();

        DetectionRule thresholdRule = DetectionRule.builder()
                .ruleType(RuleType.THRESHOLD)
                .build();

        // Then
        assertEquals(RuleType.STATISTICAL, statisticalRule.getRuleType());
        assertEquals(RuleType.MACHINE_LEARNING, mlRule.getRuleType());
        assertEquals(RuleType.THRESHOLD, thresholdRule.getRuleType());

        // Verify enum values
        assertEquals(3, RuleType.values().length);
    }

    @Test
    @DisplayName("Should handle different alert severities")
    void shouldHandleDifferentAlertSeverities() {
        // Given
        DetectionRule infoRule = DetectionRule.builder()
                .alertSeverity(AlertSeverity.INFO)
                .build();

        DetectionRule warningRule = DetectionRule.builder()
                .alertSeverity(AlertSeverity.WARNING)
                .build();

        DetectionRule errorRule = DetectionRule.builder()
                .alertSeverity(AlertSeverity.ERROR)
                .build();

        DetectionRule criticalRule = DetectionRule.builder()
                .alertSeverity(AlertSeverity.CRITICAL)
                .build();

        // Then
        assertEquals(AlertSeverity.INFO, infoRule.getAlertSeverity());
        assertEquals(AlertSeverity.WARNING, warningRule.getAlertSeverity());
        assertEquals(AlertSeverity.ERROR, errorRule.getAlertSeverity());
        assertEquals(AlertSeverity.CRITICAL, criticalRule.getAlertSeverity());

        // Verify enum values
        assertEquals(4, AlertSeverity.values().length);
    }

    @Test
    @DisplayName("Should update rule fields using setters")
    void shouldUpdateRuleFieldsUsingSetters() {
        // Given
        DetectionRule rule = new DetectionRule();

        // When
        rule.setId(testId);
        rule.setTenantId(tenantId);
        rule.setName("Updated Rule");
        rule.setDescription("Updated description");
        rule.setRuleType(RuleType.MACHINE_LEARNING);
        rule.setDataSource("metrics");
        rule.setPriority(5);
        rule.setIsActive(false);
        rule.setCreateAlert(false);
        rule.setAlertSeverity(AlertSeverity.INFO);
        rule.setCategory("performance");
        rule.setCreatedBy("user");
        rule.setUpdatedBy("admin");
        rule.setVersion(2L);

        // Then
        assertEquals(testId, rule.getId());
        assertEquals(tenantId, rule.getTenantId());
        assertEquals("Updated Rule", rule.getName());
        assertEquals(RuleType.MACHINE_LEARNING, rule.getRuleType());
        assertEquals(5, rule.getPriority());
        assertFalse(rule.getIsActive());
        assertEquals(2L, rule.getVersion());
    }

    @Test
    @DisplayName("Should verify equality and hashCode")
    void shouldVerifyEqualityAndHashCode() {
        // Given
        DetectionRule rule1 = DetectionRule.builder()
                .id(testId)
                .tenantId(tenantId)
                .name("Test Rule")
                .build();

        DetectionRule rule2 = DetectionRule.builder()
                .id(testId)
                .tenantId(tenantId)
                .name("Test Rule")
                .build();

        DetectionRule rule3 = DetectionRule.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .name("Different Rule")
                .build();

        // Then
        assertEquals(rule1, rule2);
        assertEquals(rule1.hashCode(), rule2.hashCode());
        assertNotEquals(rule1, rule3);
        assertNotEquals(rule1, null);
        assertNotEquals(rule1, new Object());
    }
}
