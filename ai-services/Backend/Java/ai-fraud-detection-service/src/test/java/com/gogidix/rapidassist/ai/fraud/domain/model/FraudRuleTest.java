package com.gogidix.rapidassist.ai.fraud.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FraudRule Domain Model Tests")
class FraudRuleTest {

    private static final String TEST_TENANT_ID = "tenant-123";

    @Test
    @DisplayName("Builder should create valid FraudRule instance")
    void testBuilder_ValidConstruction() {
        UUID id = UUID.randomUUID();
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("amount", 10000);

        FraudRule rule = FraudRule.builder()
                .id(id)
                .tenantId(TEST_TENANT_ID)
                .ruleName("High Amount Threshold")
                .ruleCode("RULE_001")
                .description("Flag claims above $10,000")
                .ruleType("THRESHOLD")
                .conditions(conditions)
                .action("FLAG_FOR_REVIEW")
                .priority(1)
                .isActive(true)
                .autoBlock(false)
                .requireReview(true)
                .executionCount(0)
                .triggerCount(0)
                .falsePositiveRate(0.05)
                .version("1.0")
                .createdAt(LocalDateTime.now())
                .build();

        assertNotNull(rule);
        assertEquals(id, rule.getId());
        assertEquals(TEST_TENANT_ID, rule.getTenantId());
        assertEquals("High Amount Threshold", rule.getRuleName());
        assertEquals("RULE_001", rule.getRuleCode());
                assertEquals("THRESHOLD", rule.getRuleType());
        assertEquals("FLAG_FOR_REVIEW", rule.getAction());
        assertEquals(1, rule.getPriority());
        assertTrue(rule.isActive());
        assertFalse(rule.getAutoBlock());
        assertTrue(rule.getRequireReview());
    }

    @Test
    @DisplayName("trigger should increment executionCount, triggerCount and update lastTriggeredAt")
    void testTrigger_IncrementsCounts() {
        FraudRule rule = FraudRule.builder()
                .executionCount(10)
                .triggerCount(5)
                .build();

        rule.trigger();

        assertEquals(11, rule.getExecutionCount());
        assertEquals(6, rule.getTriggerCount());
        assertNotNull(rule.getLastTriggeredAt());
        assertNotNull(rule.getUpdatedAt());
    }

    @Test
    @DisplayName("execute should increment executionCount only")
    void testExecute_IncrementsExecutionCount() {
        FraudRule rule = FraudRule.builder()
                .executionCount(10)
                .triggerCount(5)
                .build();

        rule.execute();

        assertEquals(11, rule.getExecutionCount());
        assertEquals(5, rule.getTriggerCount());
        assertNotNull(rule.getUpdatedAt());
    }

    @Test
    @DisplayName("activate should set isActive to true")
    void testActivate_SetsIsActiveTrue() {
        FraudRule rule = FraudRule.builder()
                .isActive(false)
                .build();

        rule.activate();

        assertTrue(rule.isActive());
        assertNotNull(rule.getUpdatedAt());
    }

    @Test
    @DisplayName("deactivate should set isActive to false")
    void testDeactivate_SetsIsActiveFalse() {
        FraudRule rule = FraudRule.builder()
                .isActive(true)
                .build();

        rule.deactivate();

        assertFalse(rule.isActive());
        assertNotNull(rule.getUpdatedAt());
    }

    @Test
    @DisplayName("updateFalsePositiveRate should update the rate")
    void testUpdateFalsePositiveRate_UpdatesRate() {
        FraudRule rule = FraudRule.builder()
                .falsePositiveRate(0.1)
                .build();

        rule.updateFalsePositiveRate(0.15);

        assertEquals(0.15, rule.getFalsePositiveRate());
        assertNotNull(rule.getUpdatedAt());
    }

    @Test
    @DisplayName("shouldAutoBlock should return true when active and autoBlock enabled")
    void testShouldAutoBlock_ReturnsTrue() {
        FraudRule rule = FraudRule.builder()
                .isActive(true)
                .autoBlock(true)
                .build();

        assertTrue(rule.shouldAutoBlock());
    }

    @Test
    @DisplayName("shouldAutoBlock should return false when inactive")
    void testShouldAutoBlock_InactiveRule() {
        FraudRule rule = FraudRule.builder()
                .isActive(false)
                .autoBlock(true)
                .build();

        assertFalse(rule.shouldAutoBlock());
    }

    @Test
    @DisplayName("shouldAutoBlock should return false when autoBlock is disabled")
    void testShouldAutoBlock_AutoBlockDisabled() {
        FraudRule rule = FraudRule.builder()
                .isActive(true)
                .autoBlock(false)
                .build();

        assertFalse(rule.shouldAutoBlock());
    }

    @Test
    @DisplayName("requiresReview should return true when requireReview flag is true")
    void testRequiresReview_ReturnsTrue() {
        FraudRule rule = FraudRule.builder()
                .requireReview(true)
                .build();

        assertTrue(rule.requiresReview());
    }

    @Test
    @DisplayName("requiresReview should return false when requireReview flag is false")
    void testRequiresReview_ReturnsFalse() {
        FraudRule rule = FraudRule.builder()
                .requireReview(false)
                .build();

        assertFalse(rule.requiresReview());
    }

    @Test
    @DisplayName("isActive should return true when isActive flag is true")
    void testIsActive_ReturnsTrue() {
        FraudRule rule = FraudRule.builder()
                .isActive(true)
                .build();

        assertTrue(rule.isActive());
    }

    @Test
    @DisplayName("isActive should return false when isActive flag is false")
    void testIsActive_ReturnsFalse() {
        FraudRule rule = FraudRule.builder()
                .isActive(false)
                .build();

        assertFalse(rule.isActive());
    }

    @Test
    @DisplayName("getTriggerRate should calculate correctly")
    void testGetTriggerRate_CalculatesCorrectly() {
        FraudRule rule = FraudRule.builder()
                .executionCount(100)
                .triggerCount(25)
                .build();

        double rate = rule.getTriggerRate();

        assertEquals(0.25, rate, 0.001);
    }

    @Test
    @DisplayName("getTriggerRate should return 0.0 when executionCount is 0")
    void testGetTriggerRate_ZeroExecutionCount() {
        FraudRule rule = FraudRule.builder()
                .executionCount(0)
                .triggerCount(0)
                .build();

        assertEquals(0.0, rule.getTriggerRate());
    }

    @Test
    @DisplayName("getTriggerRate should return 0.0 when executionCount is null")
    void testGetTriggerRate_NullExecutionCount() {
        FraudRule rule = FraudRule.builder()
                .executionCount(null)
                .triggerCount(10)
                .build();

        assertEquals(0.0, rule.getTriggerRate());
    }

    @Test
    @DisplayName("tenantId field should be present for multi-tenancy")
    void testTenantId_Isolation() {
        FraudRule rule = FraudRule.builder()
                .tenantId(TEST_TENANT_ID)
                .build();

        assertEquals(TEST_TENANT_ID, rule.getTenantId());
    }

    @Test
    @DisplayName("NoArgsConstructor should create empty instance")
    void testNoArgsConstructor() {
        FraudRule rule = new FraudRule();

        assertNotNull(rule);
        assertNull(rule.getId());
        assertNull(rule.getTenantId());
    }

    @Test
    @DisplayName("AllArgsConstructor should create complete instance")
    void testAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        Map<String, Object> conditions = new HashMap<>();
        Map<String, Object> metadata = new HashMap<>();

        FraudRule rule = new FraudRule(
                id, TEST_TENANT_ID, "Test Rule", "RULE_001", "desc",
                "TYPE", conditions, "ACTION", 1,
                true, true, true,
                100, 50, 0.1, "1.0",
                "creator", "updater",
                LocalDateTime.now(),
                LocalDateTime.now(), LocalDateTime.now(),
                metadata
        );

        assertEquals(id, rule.getId());
        assertEquals(TEST_TENANT_ID, rule.getTenantId());
        assertEquals("Test Rule", rule.getRuleName());
    }

    // ========== Mutation-Killing Tests: Getter Verification ==========

    @Test
    @DisplayName("All getters should return values set via builder")
    void testAllGetters_ReturnBuilderValues() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> conditions = Map.of("amount", 10000);
        Map<String, Object> metadata = Map.of("meta", "value");

        FraudRule rule = FraudRule.builder()
                .id(id)
                .tenantId(TEST_TENANT_ID)
                .ruleName("Test Rule")
                .ruleCode("RULE_001")
                .description("Test Description")
                .ruleType("THRESHOLD")
                .conditions(conditions)
                .action("FLAG")
                .priority(1)
                .isActive(true)
                .autoBlock(false)
                .requireReview(true)
                .executionCount(100)
                .triggerCount(50)
                .falsePositiveRate(0.05)
                .version("1.0")
                .createdBy("creator")
                .updatedBy("updater")
                .lastTriggeredAt(now)
                .createdAt(now)
                .updatedAt(now)
                .metadata(metadata)
                .build();

        assertEquals(id, rule.getId());
        assertEquals(TEST_TENANT_ID, rule.getTenantId());
        assertEquals("Test Rule", rule.getRuleName());
        assertEquals("RULE_001", rule.getRuleCode());
        assertEquals("Test Description", rule.getDescription());
        assertEquals("THRESHOLD", rule.getRuleType());
        assertEquals(conditions, rule.getConditions());
        assertEquals("FLAG", rule.getAction());
        assertEquals(1, rule.getPriority());
        assertTrue(rule.getIsActive());
        assertFalse(rule.getAutoBlock());
        assertTrue(rule.getRequireReview());
        assertEquals(100, rule.getExecutionCount());
        assertEquals(50, rule.getTriggerCount());
        assertEquals(0.05, rule.getFalsePositiveRate());
        assertEquals("1.0", rule.getVersion());
        assertEquals("creator", rule.getCreatedBy());
        assertEquals("updater", rule.getUpdatedBy());
        assertEquals(now, rule.getLastTriggeredAt());
        assertEquals(now, rule.getCreatedAt());
        assertEquals(now, rule.getUpdatedAt());
        assertEquals(metadata, rule.getMetadata());
    }

    // ========== Mutation-Killing Tests: Edge Cases ==========

    @Test
    @DisplayName("shouldAutoBlock should return false when isActive is null")
    void testShouldAutoBlock_NullIsActive() {
        FraudRule rule = FraudRule.builder()
                .isActive(null)
                .autoBlock(true)
                .build();

        assertFalse(rule.shouldAutoBlock());
    }

    @Test
    @DisplayName("shouldAutoBlock should return false when autoBlock is null")
    void testShouldAutoBlock_NullAutoBlock() {
        FraudRule rule = FraudRule.builder()
                .isActive(true)
                .autoBlock(null)
                .build();

        assertFalse(rule.shouldAutoBlock());
    }

    @Test
    @DisplayName("shouldAutoBlock should return false when both are null")
    void testShouldAutoBlock_BothNull() {
        FraudRule rule = FraudRule.builder()
                .isActive(null)
                .autoBlock(null)
                .build();

        assertFalse(rule.shouldAutoBlock());
    }

    @Test
    @DisplayName("requiresReview should return false when requireReview is null")
    void testRequiresReview_NullRequireReview() {
        FraudRule rule = FraudRule.builder()
                .requireReview(null)
                .build();

        assertFalse(rule.requiresReview());
    }

    @Test
    @DisplayName("isActive method should return false when isActive field is null")
    void testIsActiveMethod_NullField() {
        FraudRule rule = FraudRule.builder()
                .isActive(null)
                .build();

        assertFalse(rule.isActive());
    }

    @Test
    @DisplayName("trigger should preserve other fields")
    void testTrigger_PreservesOtherFields() {
        FraudRule rule = FraudRule.builder()
                .id(UUID.randomUUID())
                .tenantId(TEST_TENANT_ID)
                .ruleName("Test Rule")
                .executionCount(10)
                .triggerCount(5)
                .priority(1)
                .build();

        rule.trigger();

        assertEquals("Test Rule", rule.getRuleName());
        assertEquals(TEST_TENANT_ID, rule.getTenantId());
        assertEquals(1, rule.getPriority());
        assertEquals(11, rule.getExecutionCount());
        assertEquals(6, rule.getTriggerCount());
    }

    @Test
    @DisplayName("execute should preserve other fields")
    void testExecute_PreservesOtherFields() {
        FraudRule rule = FraudRule.builder()
                .id(UUID.randomUUID())
                .tenantId(TEST_TENANT_ID)
                .ruleName("Test Rule")
                .executionCount(10)
                .triggerCount(5)
                .priority(1)
                .build();

        rule.execute();

        assertEquals("Test Rule", rule.getRuleName());
        assertEquals(TEST_TENANT_ID, rule.getTenantId());
        assertEquals(1, rule.getPriority());
        assertEquals(11, rule.getExecutionCount());
        assertEquals(5, rule.getTriggerCount());
    }

    @Test
    @DisplayName("activate should preserve other fields")
    void testActivate_PreservesOtherFields() {
        FraudRule rule = FraudRule.builder()
                .id(UUID.randomUUID())
                .tenantId(TEST_TENANT_ID)
                .ruleName("Test Rule")
                .isActive(false)
                .priority(1)
                .build();

        rule.activate();

        assertEquals("Test Rule", rule.getRuleName());
        assertEquals(TEST_TENANT_ID, rule.getTenantId());
        assertEquals(1, rule.getPriority());
        assertTrue(rule.getIsActive());
    }

    @Test
    @DisplayName("deactivate should preserve other fields")
    void testDeactivate_PreservesOtherFields() {
        FraudRule rule = FraudRule.builder()
                .id(UUID.randomUUID())
                .tenantId(TEST_TENANT_ID)
                .ruleName("Test Rule")
                .isActive(true)
                .priority(1)
                .build();

        rule.deactivate();

        assertEquals("Test Rule", rule.getRuleName());
        assertEquals(TEST_TENANT_ID, rule.getTenantId());
        assertEquals(1, rule.getPriority());
        assertFalse(rule.getIsActive());
    }

    @Test
    @DisplayName("updateFalsePositiveRate should preserve other fields")
    void testUpdateFalsePositiveRate_PreservesOtherFields() {
        FraudRule rule = FraudRule.builder()
                .id(UUID.randomUUID())
                .tenantId(TEST_TENANT_ID)
                .ruleName("Test Rule")
                .falsePositiveRate(0.1)
                .priority(1)
                .build();

        rule.updateFalsePositiveRate(0.15);

        assertEquals("Test Rule", rule.getRuleName());
        assertEquals(TEST_TENANT_ID, rule.getTenantId());
        assertEquals(1, rule.getPriority());
        assertEquals(0.15, rule.getFalsePositiveRate());
    }

    @Test
    @DisplayName("activate should set updatedAt timestamp")
    void testActivate_SetsUpdatedAt() {
        FraudRule rule = FraudRule.builder()
                .updatedAt(null)
                .build();

        rule.activate();

        assertNotNull(rule.getUpdatedAt());
    }

    @Test
    @DisplayName("deactivate should set updatedAt timestamp")
    void testDeactivate_SetsUpdatedAt() {
        FraudRule rule = FraudRule.builder()
                .updatedAt(null)
                .build();

        rule.deactivate();

        assertNotNull(rule.getUpdatedAt());
    }

    @Test
    @DisplayName("updateFalsePositiveRate should set updatedAt timestamp")
    void testUpdateFalsePositiveRate_SetsUpdatedAt() {
        FraudRule rule = FraudRule.builder()
                .updatedAt(null)
                .build();

        rule.updateFalsePositiveRate(0.1);

        assertNotNull(rule.getUpdatedAt());
    }

    // ========== Mutation-Killing Tests: Arithmetic Operations ==========

    @Test
    @DisplayName("getTriggerRate with different execution counts should calculate correctly")
    void testGetTriggerRate_DifferentExecutionCounts() {
        // Test with executionCount = 1, triggerCount = 1
        FraudRule rule1 = FraudRule.builder()
                .executionCount(1)
                .triggerCount(1)
                .build();
        assertEquals(1.0, rule1.getTriggerRate(), 0.001);

        // Test with executionCount = 100, triggerCount = 75
        FraudRule rule2 = FraudRule.builder()
                .executionCount(100)
                .triggerCount(75)
                .build();
        assertEquals(0.75, rule2.getTriggerRate(), 0.001);

        // Test with executionCount = 50, triggerCount = 25
        FraudRule rule3 = FraudRule.builder()
                .executionCount(50)
                .triggerCount(25)
                .build();
        assertEquals(0.5, rule3.getTriggerRate(), 0.001);

        // Test with executionCount = 200, triggerCount = 150
        FraudRule rule4 = FraudRule.builder()
                .executionCount(200)
                .triggerCount(150)
                .build();
        assertEquals(0.75, rule4.getTriggerRate(), 0.001);
    }

    @Test
    @DisplayName("getTriggerRate should handle triggerCount = 0")
    void testGetTriggerRate_ZeroTriggerCount() {
        FraudRule rule = FraudRule.builder()
                .executionCount(100)
                .triggerCount(0)
                .build();

        assertEquals(0.0, rule.getTriggerRate());
    }

    @Test
    @DisplayName("getTriggerRate should handle triggerCount > executionCount")
    void testGetTriggerRate_TriggerCountExceedsExecution() {
        FraudRule rule = FraudRule.builder()
                .executionCount(50)
                .triggerCount(100)
                .build();

        assertEquals(2.0, rule.getTriggerRate(), 0.001);
    }

    @Test
    @DisplayName("getTriggerRate with large numbers should calculate correctly")
    void testGetTriggerRate_LargeNumbers() {
        FraudRule rule = FraudRule.builder()
                .executionCount(1000000)
                .triggerCount(250000)
                .build();

        assertEquals(0.25, rule.getTriggerRate(), 0.001);
    }

    @Test
    @DisplayName("trigger should increment both execution and trigger counts from zero")
    void testTrigger_IncrementsFromZero() {
        FraudRule rule = FraudRule.builder()
                .executionCount(0)
                .triggerCount(0)
                .build();

        rule.trigger();

        assertEquals(1, rule.getExecutionCount());
        assertEquals(1, rule.getTriggerCount());
    }

    @Test
    @DisplayName("trigger should increment both counts multiple times")
    void testTrigger_MultipleIncrements() {
        FraudRule rule = FraudRule.builder()
                .executionCount(5)
                .triggerCount(3)
                .build();

        rule.trigger();
        assertEquals(6, rule.getExecutionCount());
        assertEquals(4, rule.getTriggerCount());

        rule.trigger();
        assertEquals(7, rule.getExecutionCount());
        assertEquals(5, rule.getTriggerCount());
    }

    @Test
    @DisplayName("execute should increment executionCount from zero")
    void testExecute_IncrementsFromZero() {
        FraudRule rule = FraudRule.builder()
                .executionCount(0)
                .build();

        rule.execute();

        assertEquals(1, rule.getExecutionCount());
    }

    @Test
    @DisplayName("execute should increment executionCount multiple times")
    void testExecute_MultipleIncrements() {
        FraudRule rule = FraudRule.builder()
                .executionCount(10)
                .build();

        rule.execute();
        assertEquals(11, rule.getExecutionCount());

        rule.execute();
        assertEquals(12, rule.getExecutionCount());

        rule.execute();
        assertEquals(13, rule.getExecutionCount());
    }

    // ========== Mutation-Killing Tests: Boolean Combinations ==========

    @Test
    @DisplayName("shouldAutoBlock should return true only when both conditions are true")
    void testShouldAutoBlock_AllCombinations() {
        // isActive=true, autoBlock=true => true
        FraudRule rule1 = FraudRule.builder()
                .isActive(true)
                .autoBlock(true)
                .build();
        assertTrue(rule1.shouldAutoBlock());

        // isActive=true, autoBlock=false => false
        FraudRule rule2 = FraudRule.builder()
                .isActive(true)
                .autoBlock(false)
                .build();
        assertFalse(rule2.shouldAutoBlock());

        // isActive=false, autoBlock=true => false
        FraudRule rule3 = FraudRule.builder()
                .isActive(false)
                .autoBlock(true)
                .build();
        assertFalse(rule3.shouldAutoBlock());

        // isActive=false, autoBlock=false => false
        FraudRule rule4 = FraudRule.builder()
                .isActive(false)
                .autoBlock(false)
                .build();
        assertFalse(rule4.shouldAutoBlock());

        // isActive=null, autoBlock=true => false
        FraudRule rule5 = FraudRule.builder()
                .isActive(null)
                .autoBlock(true)
                .build();
        assertFalse(rule5.shouldAutoBlock());

        // isActive=true, autoBlock=null => false
        FraudRule rule6 = FraudRule.builder()
                .isActive(true)
                .autoBlock(null)
                .build();
        assertFalse(rule6.shouldAutoBlock());
    }

    @Test
    @DisplayName("isActive should return false for all non-true values")
    void testIsActiveMethod_AllValues() {
        FraudRule ruleTrue = FraudRule.builder()
                .isActive(true)
                .build();
        assertTrue(ruleTrue.isActive());

        FraudRule ruleFalse = FraudRule.builder()
                .isActive(false)
                .build();
        assertFalse(ruleFalse.isActive());

        FraudRule ruleNull = FraudRule.builder()
                .isActive(null)
                .build();
        assertFalse(ruleNull.isActive());
    }

    @Test
    @DisplayName("requiresReview should return false for all non-true values")
    void testRequiresReview_AllValues() {
        FraudRule ruleTrue = FraudRule.builder()
                .requireReview(true)
                .build();
        assertTrue(ruleTrue.requiresReview());

        FraudRule ruleFalse = FraudRule.builder()
                .requireReview(false)
                .build();
        assertFalse(ruleFalse.requiresReview());

        FraudRule ruleNull = FraudRule.builder()
                .requireReview(null)
                .build();
        assertFalse(ruleNull.requiresReview());
    }

    // ========== Mutation-Killing Tests: hashCode() and equals() ==========

    @Test
    @DisplayName("hashCode - Objects with same ID should have same hashCode")
    void testHashCode_SameId_SameHashCode() {
        UUID id = UUID.randomUUID();
        FraudRule rule1 = FraudRule.builder().id(id).build();
        FraudRule rule2 = FraudRule.builder().id(id).build();

        // This kills Math mutants in hashCode()
        assertEquals(rule1.hashCode(), rule2.hashCode());
    }

    @Test
    @DisplayName("hashCode - Objects with different IDs should have different hashCodes")
    void testHashCode_DifferentId_DifferentHashCode() {
        FraudRule rule1 = FraudRule.builder().id(UUID.randomUUID()).build();
        FraudRule rule2 = FraudRule.builder().id(UUID.randomUUID()).build();

        // This kills Math mutants in hashCode()
        assertNotEquals(rule1.hashCode(), rule2.hashCode());
    }

    @Test
    @DisplayName("HashSet should work correctly with FraudRule")
    void testHashSet_UsesHashCode() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        FraudRule rule1 = FraudRule.builder().id(id1).ruleName("RULE-001").build();
        FraudRule rule2 = FraudRule.builder().id(id2).ruleName("RULE-002").build();
        FraudRule rule1Duplicate = FraudRule.builder().id(id1).ruleName("RULE-001-DUP").build();

        java.util.HashSet<FraudRule> set = new java.util.HashSet<>();
        assertTrue(set.add(rule1));
        assertTrue(set.add(rule2));
        assertFalse(set.add(rule1Duplicate)); // Should not add - same ID as rule1

        assertEquals(2, set.size());
        assertTrue(set.contains(rule1));
        assertTrue(set.contains(rule2));
    }

    @Test
    @DisplayName("HashMap should work correctly with FraudRule as key")
    void testHashMap_UsesHashCodeAndEquals() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        FraudRule key1 = FraudRule.builder().id(id1).build();
        FraudRule key2 = FraudRule.builder().id(id2).build();
        FraudRule key1Duplicate = FraudRule.builder().id(id1).build();

        java.util.Map<FraudRule, String> map = new java.util.HashMap<>();
        map.put(key1, "First");
        map.put(key2, "Second");
        map.put(key1Duplicate, "First Updated");

        // This kills Math mutants in hashCode() and NegateConditionalsMutator in equals()
        assertEquals(2, map.size());
        assertEquals("First Updated", map.get(key1));
        assertEquals("Second", map.get(key2));
        assertEquals("First Updated", map.get(key1Duplicate));
    }

    @Test
    @DisplayName("equals - Same object reference")
    void testEquals_SameObjectReference() {
        FraudRule rule = FraudRule.builder().id(UUID.randomUUID()).build();
        // This kills NegateConditionalsMutator in equals()
        assertEquals(rule, rule);
    }

    @Test
    @DisplayName("equals - Null comparison")
    void testEquals_NullComparison() {
        FraudRule rule = FraudRule.builder().id(UUID.randomUUID()).build();
        // This kills NegateConditionalsMutator in equals()
        assertNotEquals(null, rule);
    }

    @Test
    @DisplayName("equals - Different type comparison")
    void testEquals_DifferentType() {
        FraudRule rule = FraudRule.builder().id(UUID.randomUUID()).build();
        String other = "Not a FraudRule";
        // This kills NegateConditionalsMutator in equals()
        assertNotEquals(rule, other);
    }

    @Test
    @DisplayName("equals - All fields same except ID")
    void testEquals_AllFieldsSameExceptId() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        FraudRule rule1 = FraudRule.builder()
                .id(id1)
                .ruleName("RULE")
                .ruleCode("CODE")
                .build();

        FraudRule rule2 = FraudRule.builder()
                .id(id2)
                .ruleName("RULE")
                .ruleCode("CODE")
                .build();

        // This kills NegateConditionalsMutator in equals()
        assertNotEquals(rule1, rule2);
    }

    @Test
    @DisplayName("equals - Both IDs null")
    void testEquals_BothIdsNull() {
        FraudRule rule1 = FraudRule.builder().build();
        FraudRule rule2 = FraudRule.builder().build();

        assertEquals(rule1, rule2);
        assertEquals(rule1.hashCode(), rule2.hashCode());
    }

    @Test
    @DisplayName("equals - One ID null")
    void testEquals_OneIdNull() {
        UUID id = UUID.randomUUID();
        FraudRule rule1 = FraudRule.builder().id(id).build();
        FraudRule rule2 = FraudRule.builder().id(null).build();

        assertNotEquals(rule1, rule2);
    }

    @Test
    @DisplayName("canEqual - Same class should return true")
    void testCanEqual_SameClass() {
        FraudRule rule = FraudRule.builder().build();
        FraudRule other = FraudRule.builder().build();

        // This kills BooleanReturnValsMutator in canEqual()
        assertTrue(rule.canEqual(other));
    }

    @Test
    @DisplayName("canEqual - Different class should return false")
    void testCanEqual_DifferentClass() {
        FraudRule rule = FraudRule.builder().build();
        String other = "Not a FraudRule";

        // This kills BooleanReturnValsMutator in canEqual()
        assertFalse(rule.canEqual(other));
    }

    @Test
    @DisplayName("canEqual - Null should return false")
    void testCanEqual_Null() {
        FraudRule rule = FraudRule.builder().build();

        // This kills BooleanReturnValsMutator in canEqual()
        assertFalse(rule.canEqual(null));
    }

    @Test
    @DisplayName("equals - CanEqual check between different types")
    void testEquals_WithCanEqualCheck() {
        UUID id = UUID.randomUUID();
        FraudRule rule = FraudRule.builder().id(id).build();
        FraudPattern otherPattern = FraudPattern.builder().id(id).patternName("PATTERN").build();

        // This tests canEqual within equals
        assertNotEquals(rule, otherPattern);
    }

    @Test
    @DisplayName("HashSet contains and remove operations")
    void testHashSet_ContainsAndRemove() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        FraudRule rule1 = FraudRule.builder().id(id1).ruleName("RULE-001").build();
        FraudRule rule2 = FraudRule.builder().id(id2).ruleName("RULE-002").build();

        java.util.HashSet<FraudRule> set = new java.util.HashSet<>();
        set.add(rule1);
        set.add(rule2);

        // Test contains - uses hashCode() and equals()
        assertTrue(set.contains(rule1));
        assertTrue(set.contains(rule2));

        // Create duplicate with same ID
        FraudRule rule1Dup = FraudRule.builder().id(id1).ruleName("RULE-001-DUP").build();
        assertTrue(set.contains(rule1Dup));

        // Test remove - uses hashCode() and equals()
        assertTrue(set.remove(rule1Dup));
        assertEquals(1, set.size());
        assertFalse(set.contains(rule1));
    }

    @Test
    @DisplayName("hashCode consistency across multiple calls")
    void testHashCode_Consistency() {
        FraudRule rule = FraudRule.builder()
                .id(UUID.randomUUID())
                .ruleName("RULE-001")
                .build();

        int hash1 = rule.hashCode();
        int hash2 = rule.hashCode();
        int hash3 = rule.hashCode();

        assertEquals(hash1, hash2);
        assertEquals(hash2, hash3);
    }

    @Test
    @DisplayName("hashCode with null fields")
    void testHashCode_NullFields() {
        UUID id = UUID.randomUUID();
        FraudRule rule1 = FraudRule.builder()
                .id(id)
                .ruleName(null)
                .build();

        FraudRule rule2 = FraudRule.builder()
                .id(id)
                .ruleName(null)
                .build();

        assertEquals(rule1.hashCode(), rule2.hashCode());
    }
}
