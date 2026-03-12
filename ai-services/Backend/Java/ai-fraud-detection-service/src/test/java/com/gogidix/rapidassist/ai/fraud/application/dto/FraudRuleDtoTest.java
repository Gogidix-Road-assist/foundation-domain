package com.gogidix.rapidassist.ai.fraud.application.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FraudRuleDto
 */
@DisplayName("FraudRuleDto Tests")
class FraudRuleDtoTest {

    private static final UUID TEST_ID = UUID.randomUUID();
    private static final String TENANT_ID = "tenant-123";

    @Test
    @DisplayName("Builder - All fields")
    void testBuilder_AllFields() {
        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> conditions = Map.of("min_amount", 10000);
        Map<String, Object> metadata = Map.of("key", "value");

        FraudRuleDto dto = FraudRuleDto.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .ruleName("High Amount Threshold")
                .ruleCode("HIGH_AMOUNT_001")
                .description("Flag claims above $10,000")
                .ruleType("THRESHOLD")
                .conditions(conditions)
                .action("REVIEW")
                .priority(1)
                .isActive(true)
                .autoBlock(false)
                .requireReview(true)
                .executionCount(1000)
                .triggerCount(150)
                .falsePositiveRate(0.15)
                .version("1.0")
                .createdBy("admin")
                .updatedBy("analyst")
                .lastTriggeredAt(now)
                .createdAt(now)
                .updatedAt(now)
                .metadata(metadata)
                .build();

        assertEquals(TEST_ID, dto.getId());
        assertEquals(TENANT_ID, dto.getTenantId());
        assertEquals("High Amount Threshold", dto.getRuleName());
        assertEquals("HIGH_AMOUNT_001", dto.getRuleCode());
        assertEquals("THRESHOLD", dto.getRuleType());
        assertEquals(conditions, dto.getConditions());
        assertEquals("REVIEW", dto.getAction());
        assertEquals(1, dto.getPriority());
        assertTrue(dto.getIsActive());
        assertFalse(dto.getAutoBlock());
        assertTrue(dto.getRequireReview());
        assertEquals(1000, dto.getExecutionCount());
        assertEquals(150, dto.getTriggerCount());
        assertEquals(0.15, dto.getFalsePositiveRate());
        assertEquals("1.0", dto.getVersion());
        assertEquals("admin", dto.getCreatedBy());
        assertEquals("analyst", dto.getUpdatedBy());
    }

    @Test
    @DisplayName("Builder - Minimal fields")
    void testBuilder_MinimalFields() {
        FraudRuleDto dto = FraudRuleDto.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .build();

        assertEquals(TEST_ID, dto.getId());
        assertEquals(TENANT_ID, dto.getTenantId());
        assertNull(dto.getRuleName());
        assertNull(dto.getRuleCode());
        assertNull(dto.getRuleType());
    }

    @Test
    @DisplayName("Getters and Setters")
    void testGettersSetters() {
        FraudRuleDto dto = new FraudRuleDto();
        dto.setId(TEST_ID);
        dto.setTenantId(TENANT_ID);
        dto.setRuleName("Velocity Check");
        dto.setRuleCode("VELOCITY_001");
        dto.setRuleType("VELOCITY");
        dto.setAction("BLOCK");
        dto.setIsActive(true);
        dto.setAutoBlock(true);
        dto.setRequireReview(false);

        assertEquals(TEST_ID, dto.getId());
        assertEquals(TENANT_ID, dto.getTenantId());
        assertEquals("Velocity Check", dto.getRuleName());
        assertEquals("VELOCITY_001", dto.getRuleCode());
        assertEquals("VELOCITY", dto.getRuleType());
        assertEquals("BLOCK", dto.getAction());
        assertTrue(dto.getIsActive());
        assertTrue(dto.getAutoBlock());
        assertFalse(dto.getRequireReview());
    }

    @Test
    @DisplayName("Equals - Same ID")
    void testEquals_SameId() {
        FraudRuleDto dto1 = FraudRuleDto.builder().id(TEST_ID).build();
        FraudRuleDto dto2 = FraudRuleDto.builder().id(TEST_ID).build();

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("Equals - Different ID")
    void testEquals_DifferentId() {
        FraudRuleDto dto1 = FraudRuleDto.builder().id(UUID.randomUUID()).build();
        FraudRuleDto dto2 = FraudRuleDto.builder().id(UUID.randomUUID()).build();

        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("Rule types")
    void testRuleTypes() {
        String[] ruleTypes = {"THRESHOLD", "VELOCITY", "PATTERN", "ANOMALY", "COMPOSITE"};

        for (String ruleType : ruleTypes) {
            FraudRuleDto dto = FraudRuleDto.builder().ruleType(ruleType).build();
            assertEquals(ruleType, dto.getRuleType());
        }
    }

    @Test
    @DisplayName("Actions")
    void testActions() {
        String[] actions = {"ALLOW", "BLOCK", "REVIEW", "FLAG", "CHALLENGE"};

        for (String action : actions) {
            FraudRuleDto dto = FraudRuleDto.builder().action(action).build();
            assertEquals(action, dto.getAction());
        }
    }

    @Test
    @DisplayName("Active status")
    void testActiveStatus() {
        FraudRuleDto active = FraudRuleDto.builder().isActive(true).build();
        FraudRuleDto inactive = FraudRuleDto.builder().isActive(false).build();

        assertTrue(active.getIsActive());
        assertFalse(inactive.getIsActive());
    }

    @Test
    @DisplayName("Auto block and require review")
    void testAutoBlockAndRequireReview() {
        FraudRuleDto dto = FraudRuleDto.builder()
                .autoBlock(true)
                .requireReview(true)
                .build();

        assertTrue(dto.getAutoBlock());
        assertTrue(dto.getRequireReview());
    }

    @Test
    @DisplayName("Execution and trigger counts")
    void testExecutionAndTriggerCounts() {
        FraudRuleDto dto = FraudRuleDto.builder()
                .executionCount(5000)
                .triggerCount(500)
                .falsePositiveRate(0.10)
                .build();

        assertEquals(5000, dto.getExecutionCount());
        assertEquals(500, dto.getTriggerCount());
        assertEquals(0.10, dto.getFalsePositiveRate());
    }

    @Test
    @DisplayName("toString - Contains ID")
    void testToString_ContainsId() {
        FraudRuleDto dto = FraudRuleDto.builder().id(TEST_ID).build();
        assertTrue(dto.toString().contains(TEST_ID.toString()));
    }

    @Test
    @DisplayName("Conditions map")
    void testConditionsMap() {
        Map<String, Object> conditions = Map.of(
                "min_amount", 1000,
                "max_amount", 50000,
                "risk_factors", List.of("high_risk_country")
        );

        FraudRuleDto dto = FraudRuleDto.builder()
                .conditions(conditions)
                .build();

        assertEquals(conditions, dto.getConditions());
        assertEquals(3, dto.getConditions().size());
    }

    // ========== Mutation-Killing Tests: All Getter/Setter Combinations ==========

    @Test
    @DisplayName("All getters should return values set via builder")
    void testAllGetters_ReturnBuilderValues() {
        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> conditions = Map.of("key", "value");
        Map<String, Object> metadata = Map.of("meta", "value");

        FraudRuleDto dto = FraudRuleDto.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .ruleName("Test Rule")
                .ruleCode("RULE_001")
                .description("Test Description")
                .ruleType("TYPE")
                .conditions(conditions)
                .action("ACTION")
                .priority(5)
                .isActive(true)
                .autoBlock(false)
                .requireReview(true)
                .executionCount(100)
                .triggerCount(50)
                .falsePositiveRate(0.1)
                .version("2.0")
                .createdBy("creator")
                .updatedBy("updater")
                .lastTriggeredAt(now)
                .metadata(metadata)
                .createdAt(now)
                .updatedAt(now.plusHours(1))
                .build();

        assertEquals(TEST_ID, dto.getId());
        assertEquals(TENANT_ID, dto.getTenantId());
        assertEquals("Test Rule", dto.getRuleName());
        assertEquals("RULE_001", dto.getRuleCode());
        assertEquals("Test Description", dto.getDescription());
        assertEquals("TYPE", dto.getRuleType());
        assertEquals(conditions, dto.getConditions());
        assertEquals("ACTION", dto.getAction());
        assertEquals(5, dto.getPriority());
        assertTrue(dto.getIsActive());
        assertFalse(dto.getAutoBlock());
        assertTrue(dto.getRequireReview());
        assertEquals(100, dto.getExecutionCount());
        assertEquals(50, dto.getTriggerCount());
        assertEquals(0.1, dto.getFalsePositiveRate());
        assertEquals("2.0", dto.getVersion());
        assertEquals("creator", dto.getCreatedBy());
        assertEquals("updater", dto.getUpdatedBy());
        assertEquals(now, dto.getLastTriggeredAt());
        assertEquals(metadata, dto.getMetadata());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(now.plusHours(1), dto.getUpdatedAt());
    }

    @Test
    @DisplayName("Setters should handle null values")
    void testSetters_NullValues() {
        FraudRuleDto dto = FraudRuleDto.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .ruleName("RULE")
                .build();

        dto.setRuleCode(null);
        dto.setDescription(null);
        dto.setRuleType(null);
        dto.setConditions(null);
        dto.setAction(null);
        dto.setIsActive(null);
        dto.setAutoBlock(null);
        dto.setRequireReview(null);
        dto.setVersion(null);
        dto.setCreatedBy(null);
        dto.setUpdatedBy(null);
        dto.setMetadata(null);

        assertNull(dto.getRuleCode());
        assertNull(dto.getDescription());
        assertNull(dto.getRuleType());
        assertNull(dto.getConditions());
        assertNull(dto.getAction());
        assertNull(dto.getIsActive());
        assertNull(dto.getAutoBlock());
        assertNull(dto.getRequireReview());
        assertNull(dto.getVersion());
        assertNull(dto.getCreatedBy());
        assertNull(dto.getUpdatedBy());
        assertNull(dto.getMetadata());
    }

    @Test
    @DisplayName("All setters should update fields")
    void testAllSetters_UpdateFields() {
        FraudRuleDto dto = new FraudRuleDto();
        dto.setId(TEST_ID);
        dto.setTenantId(TENANT_ID);
        dto.setRuleName("Modified Rule");
        dto.setRuleCode("MOD_001");
        dto.setRuleType("TYPE");
        dto.setAction("BLOCK");
        dto.setPriority(10);
        dto.setIsActive(true);
        dto.setAutoBlock(true);
        dto.setRequireReview(false);
        dto.setExecutionCount(200);
        dto.setTriggerCount(100);
        dto.setFalsePositiveRate(0.05);
        dto.setVersion("3.0");

        assertEquals(TEST_ID, dto.getId());
        assertEquals("Modified Rule", dto.getRuleName());
        assertEquals("MOD_001", dto.getRuleCode());
        assertEquals(10, dto.getPriority());
        assertTrue(dto.getIsActive());
        assertTrue(dto.getAutoBlock());
        assertFalse(dto.getRequireReview());
        assertEquals(200, dto.getExecutionCount());
        assertEquals(100, dto.getTriggerCount());
        assertEquals(0.05, dto.getFalsePositiveRate());
        assertEquals("3.0", dto.getVersion());
    }

    @Test
    @DisplayName("Double field: falsePositiveRate")
    void testFalsePositiveRate() {
        FraudRuleDto dto = FraudRuleDto.builder()
                .falsePositiveRate(0.15)
                .build();

        assertEquals(0.15, dto.getFalsePositiveRate());

        dto.setFalsePositiveRate(0.25);
        assertEquals(0.25, dto.getFalsePositiveRate());
    }

    @Test
    @DisplayName("Integer fields: priority, executionCount, triggerCount")
    void testIntegerFields() {
        FraudRuleDto dto = FraudRuleDto.builder()
                .priority(1)
                .executionCount(1000)
                .triggerCount(100)
                .build();

        assertEquals(1, dto.getPriority());
        assertEquals(1000, dto.getExecutionCount());
        assertEquals(100, dto.getTriggerCount());

        dto.setPriority(10);
        dto.setExecutionCount(2000);
        dto.setTriggerCount(200);

        assertEquals(10, dto.getPriority());
        assertEquals(2000, dto.getExecutionCount());
        assertEquals(200, dto.getTriggerCount());
    }

    @Test
    @DisplayName("Boolean fields: isActive, autoBlock, requireReview")
    void testBooleanFields() {
        FraudRuleDto dto1 = FraudRuleDto.builder()
                .isActive(true)
                .autoBlock(true)
                .requireReview(true)
                .build();

        FraudRuleDto dto2 = FraudRuleDto.builder()
                .isActive(false)
                .autoBlock(false)
                .requireReview(false)
                .build();

        assertTrue(dto1.getIsActive());
        assertTrue(dto1.getAutoBlock());
        assertTrue(dto1.getRequireReview());

        assertFalse(dto2.getIsActive());
        assertFalse(dto2.getAutoBlock());
        assertFalse(dto2.getRequireReview());
    }

    @Test
    @DisplayName("Map fields: conditions and metadata")
    void testMapFields() {
        Map<String, Object> conditions = Map.of("key1", "value1", "key2", 100);
        Map<String, Object> metadata = Map.of("source", "api");

        FraudRuleDto dto = FraudRuleDto.builder()
                .conditions(conditions)
                .metadata(metadata)
                .build();

        assertEquals(2, dto.getConditions().size());
        assertEquals("value1", dto.getConditions().get("key1"));
        assertEquals("api", dto.getMetadata().get("source"));

        dto.setConditions(Map.of());
        dto.setMetadata(null);

        assertTrue(dto.getConditions().isEmpty());
        assertNull(dto.getMetadata());
    }

    @Test
    @DisplayName("Version field")
    void testVersionField() {
        String[] versions = {"1.0", "1.1", "2.0", "3.5"};

        for (String version : versions) {
            FraudRuleDto dto = FraudRuleDto.builder().version(version).build();
            assertEquals(version, dto.getVersion());
        }
    }

    @Test
    @DisplayName("LastTriggeredAt timestamp")
    void testLastTriggeredAt() {
        LocalDateTime now = LocalDateTime.now();

        FraudRuleDto dto = FraudRuleDto.builder()
                .lastTriggeredAt(now)
                .build();

        assertEquals(now, dto.getLastTriggeredAt());

        LocalDateTime later = now.plusHours(1);
        dto.setLastTriggeredAt(later);
        assertEquals(later, dto.getLastTriggeredAt());
    }

    // ========== Mutation-Killing Tests: hashCode() and equals() ==========

    @Test
    @DisplayName("hashCode - Objects with same ID should have same hashCode")
    void testHashCode_SameId_SameHashCode() {
        UUID id = UUID.randomUUID();
        FraudRuleDto dto1 = FraudRuleDto.builder().id(id).build();
        FraudRuleDto dto2 = FraudRuleDto.builder().id(id).build();

        // This kills Math mutants in hashCode()
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("hashCode - Objects with different IDs should have different hashCodes")
    void testHashCode_DifferentId_DifferentHashCode() {
        FraudRuleDto dto1 = FraudRuleDto.builder().id(UUID.randomUUID()).build();
        FraudRuleDto dto2 = FraudRuleDto.builder().id(UUID.randomUUID()).build();

        // This kills Math mutants in hashCode()
        assertNotEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("HashSet should work correctly with FraudRuleDto")
    void testHashSet_UsesHashCode() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        FraudRuleDto dto1 = FraudRuleDto.builder().id(id1).ruleName("RULE-001").build();
        FraudRuleDto dto2 = FraudRuleDto.builder().id(id2).ruleName("RULE-002").build();
        FraudRuleDto dto1Duplicate = FraudRuleDto.builder().id(id1).ruleName("RULE-001-DUP").build();

        java.util.HashSet<FraudRuleDto> set = new java.util.HashSet<>();
        assertTrue(set.add(dto1));
        assertTrue(set.add(dto2));
        assertFalse(set.add(dto1Duplicate)); // Should not add - same ID as dto1

        assertEquals(2, set.size());
        assertTrue(set.contains(dto1));
        assertTrue(set.contains(dto2));
    }

    @Test
    @DisplayName("HashMap should work correctly with FraudRuleDto as key")
    void testHashMap_UsesHashCodeAndEquals() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        FraudRuleDto key1 = FraudRuleDto.builder().id(id1).build();
        FraudRuleDto key2 = FraudRuleDto.builder().id(id2).build();
        FraudRuleDto key1Duplicate = FraudRuleDto.builder().id(id1).build();

        java.util.Map<FraudRuleDto, String> map = new java.util.HashMap<>();
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
        FraudRuleDto dto = FraudRuleDto.builder().id(TEST_ID).build();
        // This kills NegateConditionalsMutator in equals()
        assertEquals(dto, dto);
    }

    @Test
    @DisplayName("equals - Null comparison")
    void testEquals_NullComparison() {
        FraudRuleDto dto = FraudRuleDto.builder().id(TEST_ID).build();
        // This kills NegateConditionalsMutator in equals()
        assertNotEquals(null, dto);
    }

    @Test
    @DisplayName("equals - Different type comparison")
    void testEquals_DifferentType() {
        FraudRuleDto dto = FraudRuleDto.builder().id(TEST_ID).build();
        String other = "Not a FraudRuleDto";
        // This kills NegateConditionalsMutator in equals()
        assertNotEquals(dto, other);
    }

    @Test
    @DisplayName("equals - All fields same except ID")
    void testEquals_AllFieldsSameExceptId() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        FraudRuleDto dto1 = FraudRuleDto.builder()
                .id(id1)
                .ruleName("RULE")
                .ruleCode("CODE")
                .build();

        FraudRuleDto dto2 = FraudRuleDto.builder()
                .id(id2)
                .ruleName("RULE")
                .ruleCode("CODE")
                .build();

        // This kills NegateConditionalsMutator in equals()
        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("equals - Both IDs null")
    void testEquals_BothIdsNull() {
        FraudRuleDto dto1 = FraudRuleDto.builder().build();
        FraudRuleDto dto2 = FraudRuleDto.builder().build();

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("equals - One ID null")
    void testEquals_OneIdNull() {
        UUID id = UUID.randomUUID();
        FraudRuleDto dto1 = FraudRuleDto.builder().id(id).build();
        FraudRuleDto dto2 = FraudRuleDto.builder().id(null).build();

        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("canEqual - Same class should return true")
    void testCanEqual_SameClass() {
        FraudRuleDto dto = FraudRuleDto.builder().build();
        FraudRuleDto other = FraudRuleDto.builder().build();

        // This kills BooleanReturnValsMutator in canEqual()
        assertTrue(dto.canEqual(other));
    }

    @Test
    @DisplayName("canEqual - Different class should return false")
    void testCanEqual_DifferentClass() {
        FraudRuleDto dto = FraudRuleDto.builder().build();
        String other = "Not a FraudRuleDto";

        // This kills BooleanReturnValsMutator in canEqual()
        assertFalse(dto.canEqual(other));
    }

    @Test
    @DisplayName("canEqual - Null should return false")
    void testCanEqual_Null() {
        FraudRuleDto dto = FraudRuleDto.builder().build();

        // This kills BooleanReturnValsMutator in canEqual()
        assertFalse(dto.canEqual(null));
    }

    @Test
    @DisplayName("equals - CanEqual check between different types")
    void testEquals_WithCanEqualCheck() {
        UUID id = UUID.randomUUID();
        FraudRuleDto dto = FraudRuleDto.builder()
                .id(id)
                .ruleName("RULE")
                .build();
        FraudPatternDto otherDto = FraudPatternDto.builder()
                .id(id)
                .patternName("PATTERN")
                .build();

        // This tests canEqual within equals
        assertNotEquals(dto, otherDto);
    }

    @Test
    @DisplayName("HashSet contains and remove operations")
    void testHashSet_ContainsAndRemove() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        FraudRuleDto dto1 = FraudRuleDto.builder().id(id1).ruleName("RULE-001").build();
        FraudRuleDto dto2 = FraudRuleDto.builder().id(id2).ruleName("RULE-002").build();

        java.util.HashSet<FraudRuleDto> set = new java.util.HashSet<>();
        set.add(dto1);
        set.add(dto2);

        // Test contains - uses hashCode() and equals()
        assertTrue(set.contains(dto1));
        assertTrue(set.contains(dto2));

        // Create duplicate with same ID
        FraudRuleDto dto1Dup = FraudRuleDto.builder().id(id1).ruleName("RULE-001-DUP").build();
        assertTrue(set.contains(dto1Dup));

        // Test remove - uses hashCode() and equals()
        assertTrue(set.remove(dto1Dup));
        assertEquals(1, set.size());
        assertFalse(set.contains(dto1));
    }

    @Test
    @DisplayName("hashCode consistency across multiple calls")
    void testHashCode_Consistency() {
        FraudRuleDto dto = FraudRuleDto.builder()
                .id(TEST_ID)
                .ruleName("RULE")
                .build();

        int hash1 = dto.hashCode();
        int hash2 = dto.hashCode();
        int hash3 = dto.hashCode();

        assertEquals(hash1, hash2);
        assertEquals(hash2, hash3);
    }

    @Test
    @DisplayName("hashCode with null fields")
    void testHashCode_NullFields() {
        UUID id = UUID.randomUUID();
        FraudRuleDto dto1 = FraudRuleDto.builder()
                .id(id)
                .ruleName(null)
                .build();

        FraudRuleDto dto2 = FraudRuleDto.builder()
                .id(id)
                .ruleName(null)
                .build();

        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    // ========== Additional Getter Return Value Tests ==========

    @Test
    @DisplayName("Getters should return exact values set via builder")
    void testGetters_ExactValues() {
        String ruleName = "SUSPICIOUS_RULE";
        String ruleCode = "RULE-001";
        String ruleType = "THRESHOLD";
        String description = "Rule description";

        FraudRuleDto dto = FraudRuleDto.builder()
                .ruleName(ruleName)
                .ruleCode(ruleCode)
                .ruleType(ruleType)
                .description(description)
                .build();

        // This kills EmptyObjectReturnValsMutator for string getters
        assertEquals(ruleName, dto.getRuleName());
        assertEquals(ruleCode, dto.getRuleCode());
        assertEquals(ruleType, dto.getRuleType());
        assertEquals(description, dto.getDescription());
    }

    @Test
    @DisplayName("Setter for createdBy and updatedBy should work correctly")
    void testCreatedByUpdatedBy_Setters() {
        FraudRuleDto dto = new FraudRuleDto();

        dto.setCreatedBy("admin-001");
        assertEquals("admin-001", dto.getCreatedBy());

        dto.setUpdatedBy("user-002");
        assertEquals("user-002", dto.getUpdatedBy());

        dto.setCreatedBy(null);
        assertNull(dto.getCreatedBy());

        dto.setUpdatedBy(null);
        assertNull(dto.getUpdatedBy());
    }

    @Test
    @DisplayName("Setter for conditions and metadata should work correctly")
    void testConditionsMetadata_Setters() {
        Map<String, Object> conditions = Map.of("key", "value");
        Map<String, Object> metadata = Map.of("source", "api");
        FraudRuleDto dto = new FraudRuleDto();

        dto.setConditions(conditions);
        assertEquals(conditions, dto.getConditions());

        dto.setMetadata(metadata);
        assertEquals(metadata, dto.getMetadata());

        dto.setConditions(null);
        assertNull(dto.getConditions());

        dto.setMetadata(null);
        assertNull(dto.getMetadata());
    }

    @Test
    @DisplayName("Setter for createdAt and updatedAt should work correctly")
    void testCreatedAtUpdatedAt_Setters() {
        LocalDateTime now = LocalDateTime.now();
        FraudRuleDto dto = new FraudRuleDto();

        dto.setCreatedAt(now);
        assertEquals(now, dto.getCreatedAt());

        dto.setUpdatedAt(now.plusHours(1));
        assertEquals(now.plusHours(1), dto.getUpdatedAt());

        dto.setCreatedAt(null);
        assertNull(dto.getCreatedAt());

        dto.setUpdatedAt(null);
        assertNull(dto.getUpdatedAt());
    }

    @Test
    @DisplayName("Setter for lastTriggeredAt should work correctly")
    void testLastTriggeredAt_Setter() {
        LocalDateTime now = LocalDateTime.now();
        FraudRuleDto dto = new FraudRuleDto();

        dto.setLastTriggeredAt(now);
        assertEquals(now, dto.getLastTriggeredAt());

        dto.setLastTriggeredAt(null);
        assertNull(dto.getLastTriggeredAt());
    }
}
