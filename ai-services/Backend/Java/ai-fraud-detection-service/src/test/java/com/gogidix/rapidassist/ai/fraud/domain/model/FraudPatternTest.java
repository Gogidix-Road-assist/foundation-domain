package com.gogidix.rapidassist.ai.fraud.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FraudPattern Domain Model Tests")
class FraudPatternTest {

    private static final String TEST_TENANT_ID = "tenant-123";

    @Test
    @DisplayName("Builder should create valid FraudPattern instance")
    void testBuilder_ValidConstruction() {
        UUID id = UUID.randomUUID();
        Map<String, Object> definition = new HashMap<>();
        definition.put("threshold", 0.8);

        FraudPattern pattern = FraudPattern.builder()
                .id(id)
                .tenantId(TEST_TENANT_ID)
                .patternName("HIGH_VALUE_CLAIM")
                .patternType("THRESHOLD")
                .description("Claims above $10,000")
                .patternDefinition(definition)
                .detectionAlgorithm("NEURAL_NETWORK")
                .priority(1)
                .isActive(true)
                .confidenceThreshold(0.75)
                .detectionCount(100)
                .falsePositiveCount(10)
                .truePositiveCount(90)
                .createdAt(LocalDateTime.now())
                .build();

        assertNotNull(pattern);
        assertEquals(id, pattern.getId());
        assertEquals(TEST_TENANT_ID, pattern.getTenantId());
        assertEquals("HIGH_VALUE_CLAIM", pattern.getPatternName());
        assertEquals("THRESHOLD", pattern.getPatternType());
        assertEquals(1, pattern.getPriority());
        assertTrue(pattern.isActive());
        assertEquals(0.75, pattern.getConfidenceThreshold());
        assertEquals(100, pattern.getDetectionCount());
    }

    @Test
    @DisplayName("calculatePrecision should calculate correctly with valid counts")
    void testCalculatePrecision_CalculatesCorrectly() {
        FraudPattern pattern = FraudPattern.builder()
                .truePositiveCount(80)
                .falsePositiveCount(20)
                .build();

        pattern.calculatePrecision();

        assertEquals(0.8, pattern.getPrecision(), 0.001);
    }

    @Test
    @DisplayName("calculatePrecision should set 0.0 when total count is 0")
    void testCalculatePrecision_ZeroTotalCount() {
        FraudPattern pattern = FraudPattern.builder()
                .truePositiveCount(0)
                .falsePositiveCount(0)
                .build();

        pattern.calculatePrecision();

        assertEquals(0.0, pattern.getPrecision());
    }

    @Test
    @DisplayName("incrementDetection with true positive should update counts and recalculate precision")
    void testIncrementDetection_TruePositive() {
        FraudPattern pattern = FraudPattern.builder()
                .detectionCount(100)
                .truePositiveCount(80)
                .falsePositiveCount(20)
                .build();

        pattern.incrementDetection(true);

        assertEquals(101, pattern.getDetectionCount());
        assertEquals(81, pattern.getTruePositiveCount());
        assertEquals(20, pattern.getFalsePositiveCount());
        assertEquals(81.0 / 101.0, pattern.getPrecision(), 0.001);
    }

    @Test
    @DisplayName("incrementDetection with false positive should update counts and recalculate precision")
    void testIncrementDetection_FalsePositive() {
        FraudPattern pattern = FraudPattern.builder()
                .detectionCount(100)
                .truePositiveCount(80)
                .falsePositiveCount(20)
                .build();

        pattern.incrementDetection(false);

        assertEquals(101, pattern.getDetectionCount());
        assertEquals(80, pattern.getTruePositiveCount());
        assertEquals(21, pattern.getFalsePositiveCount());
        assertEquals(80.0 / 101.0, pattern.getPrecision(), 0.001);
    }

    @Test
    @DisplayName("activate should set isActive to true")
    void testActivate_SetsIsActiveTrue() {
        FraudPattern pattern = FraudPattern.builder()
                .isActive(false)
                .build();

        pattern.activate();

        assertTrue(pattern.isActive());
        assertNotNull(pattern.getUpdatedAt());
    }

    @Test
    @DisplayName("deactivate should set isActive to false")
    void testDeactivate_SetsIsActiveFalse() {
        FraudPattern pattern = FraudPattern.builder()
                .isActive(true)
                .build();

        pattern.deactivate();

        assertFalse(pattern.isActive());
        assertNotNull(pattern.getUpdatedAt());
    }

    @Test
    @DisplayName("isActive should return true when isActive flag is true")
    void testIsActive_ReturnsTrue() {
        FraudPattern pattern = FraudPattern.builder()
                .isActive(true)
                .build();

        assertTrue(pattern.isActive());
    }

    @Test
    @DisplayName("isActive should return false when isActive flag is false")
    void testIsActive_ReturnsFalse() {
        FraudPattern pattern = FraudPattern.builder()
                .isActive(false)
                .build();

        assertFalse(pattern.isActive());
    }

    @Test
    @DisplayName("isActive should return false when isActive flag is null")
    void testIsActive_ReturnsFalseWhenNull() {
        FraudPattern pattern = FraudPattern.builder()
                .isActive(null)
                .build();

        assertFalse(pattern.isActive());
    }

    @Test
    @DisplayName("tenantId field should be present for multi-tenancy")
    void testTenantId_Isolation() {
        FraudPattern pattern = FraudPattern.builder()
                .tenantId(TEST_TENANT_ID)
                .build();

        assertEquals(TEST_TENANT_ID, pattern.getTenantId());
    }

    @Test
    @DisplayName("NoArgsConstructor should create empty instance")
    void testNoArgsConstructor() {
        FraudPattern pattern = new FraudPattern();

        assertNotNull(pattern);
        assertNull(pattern.getId());
        assertNull(pattern.getTenantId());
    }

    @Test
    @DisplayName("AllArgsConstructor should create complete instance")
    void testAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        Map<String, Object> definition = new HashMap<>();
        Map<String, Object> metadata = new HashMap<>();

        FraudPattern pattern = new FraudPattern(
                id, TEST_TENANT_ID, "TEST", "TYPE", "desc",
                definition, "ALGO", 1, true, 0.8,
                100, 10, 90, 0.9, 0.85,
                "creator", "updater",
                LocalDateTime.now(), LocalDateTime.now(), metadata
        );

        assertEquals(id, pattern.getId());
        assertEquals(TEST_TENANT_ID, pattern.getTenantId());
    }

    // ========== Mutation-Killing Tests: Getter Verification ==========

    @Test
    @DisplayName("All getters should return values set via builder")
    void testAllGetters_ReturnBuilderValues() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> definition = Map.of("threshold", 0.8);
        Map<String, Object> metadata = Map.of("meta", "value");

        FraudPattern pattern = FraudPattern.builder()
                .id(id)
                .tenantId(TEST_TENANT_ID)
                .patternName("TEST_PATTERN")
                .patternType("THRESHOLD")
                .description("Test Description")
                .patternDefinition(definition)
                .detectionAlgorithm("NEURAL_NETWORK")
                .priority(1)
                .isActive(true)
                .confidenceThreshold(0.75)
                .detectionCount(100)
                .falsePositiveCount(10)
                .truePositiveCount(90)
                .precision(0.9)
                .recall(0.85)
                .createdBy("creator")
                .updatedBy("updater")
                .createdAt(now)
                .updatedAt(now)
                .metadata(metadata)
                .build();

        assertEquals(id, pattern.getId());
        assertEquals(TEST_TENANT_ID, pattern.getTenantId());
        assertEquals("TEST_PATTERN", pattern.getPatternName());
        assertEquals("THRESHOLD", pattern.getPatternType());
        assertEquals("Test Description", pattern.getDescription());
        assertEquals(definition, pattern.getPatternDefinition());
        assertEquals("NEURAL_NETWORK", pattern.getDetectionAlgorithm());
        assertEquals(1, pattern.getPriority());
        assertTrue(pattern.getIsActive());
        assertEquals(0.75, pattern.getConfidenceThreshold());
        assertEquals(100, pattern.getDetectionCount());
        assertEquals(10, pattern.getFalsePositiveCount());
        assertEquals(90, pattern.getTruePositiveCount());
        assertEquals(0.9, pattern.getPrecision());
        assertEquals(0.85, pattern.getRecall());
        assertEquals("creator", pattern.getCreatedBy());
        assertEquals("updater", pattern.getUpdatedBy());
        assertEquals(now, pattern.getCreatedAt());
        assertEquals(now, pattern.getUpdatedAt());
        assertEquals(metadata, pattern.getMetadata());
    }

    // ========== Mutation-Killing Tests: Edge Cases ==========

    @Test
    @DisplayName("activate should preserve other fields")
    void testActivate_PreservesOtherFields() {
        FraudPattern pattern = FraudPattern.builder()
                .id(UUID.randomUUID())
                .tenantId(TEST_TENANT_ID)
                .patternName("Test Pattern")
                .isActive(false)
                .priority(1)
                .build();

        pattern.activate();

        assertEquals("Test Pattern", pattern.getPatternName());
        assertEquals(TEST_TENANT_ID, pattern.getTenantId());
        assertEquals(1, pattern.getPriority());
        assertTrue(pattern.getIsActive());
    }

    @Test
    @DisplayName("deactivate should preserve other fields")
    void testDeactivate_PreservesOtherFields() {
        FraudPattern pattern = FraudPattern.builder()
                .id(UUID.randomUUID())
                .tenantId(TEST_TENANT_ID)
                .patternName("Test Pattern")
                .isActive(true)
                .priority(1)
                .build();

        pattern.deactivate();

        assertEquals("Test Pattern", pattern.getPatternName());
        assertEquals(TEST_TENANT_ID, pattern.getTenantId());
        assertEquals(1, pattern.getPriority());
        assertFalse(pattern.getIsActive());
    }

    @Test
    @DisplayName("incrementDetection should preserve other fields")
    void testIncrementDetection_PreservesOtherFields() {
        FraudPattern pattern = FraudPattern.builder()
                .id(UUID.randomUUID())
                .tenantId(TEST_TENANT_ID)
                .patternName("Test Pattern")
                .detectionCount(100)
                .truePositiveCount(80)
                .falsePositiveCount(20)
                .priority(1)
                .build();

        pattern.incrementDetection(true);

        assertEquals("Test Pattern", pattern.getPatternName());
        assertEquals(TEST_TENANT_ID, pattern.getTenantId());
        assertEquals(1, pattern.getPriority());
        assertEquals(101, pattern.getDetectionCount());
        assertEquals(81, pattern.getTruePositiveCount());
    }

    @Test
    @DisplayName("calculatePrecision should preserve other fields")
    void testCalculatePrecision_PreservesOtherFields() {
        FraudPattern pattern = FraudPattern.builder()
                .id(UUID.randomUUID())
                .tenantId(TEST_TENANT_ID)
                .patternName("Test Pattern")
                .truePositiveCount(80)
                .falsePositiveCount(20)
                .priority(1)
                .build();

        pattern.calculatePrecision();

        assertEquals("Test Pattern", pattern.getPatternName());
        assertEquals(TEST_TENANT_ID, pattern.getTenantId());
        assertEquals(1, pattern.getPriority());
        assertEquals(80, pattern.getTruePositiveCount());
        assertEquals(20, pattern.getFalsePositiveCount());
        assertEquals(0.8, pattern.getPrecision());
    }

    @Test
    @DisplayName("activate should set updatedAt timestamp")
    void testActivate_SetsUpdatedAt() {
        FraudPattern pattern = FraudPattern.builder()
                .updatedAt(null)
                .build();

        pattern.activate();

        assertNotNull(pattern.getUpdatedAt());
    }

    @Test
    @DisplayName("deactivate should set updatedAt timestamp")
    void testDeactivate_SetsUpdatedAt() {
        FraudPattern pattern = FraudPattern.builder()
                .updatedAt(null)
                .build();

        pattern.deactivate();

        assertNotNull(pattern.getUpdatedAt());
    }

    @Test
    @DisplayName("incrementDetection should set updatedAt timestamp")
    void testIncrementDetection_SetsUpdatedAt() {
        FraudPattern pattern = FraudPattern.builder()
                .detectionCount(100)
                .truePositiveCount(90)
                .falsePositiveCount(10)
                .updatedAt(null)
                .build();

        pattern.incrementDetection(true);

        assertNotNull(pattern.getUpdatedAt());
    }

    @Test
    @DisplayName("incrementDetection should recalculate precision correctly")
    void testIncrementDetection_RecalculatesPrecision() {
        FraudPattern pattern = FraudPattern.builder()
                .detectionCount(100)
                .truePositiveCount(90)
                .falsePositiveCount(10)
                .precision(0.9)
                .build();

        pattern.incrementDetection(false);

        assertEquals(101, pattern.getDetectionCount());
        assertEquals(90, pattern.getTruePositiveCount());
        assertEquals(11, pattern.getFalsePositiveCount());
        assertEquals(90.0 / 101.0, pattern.getPrecision(), 0.001);
    }

    @Test
    @DisplayName("calculatePrecision with only true positives should return 1.0")
    void testCalculatePrecision_OnlyTruePositives() {
        FraudPattern pattern = FraudPattern.builder()
                .truePositiveCount(100)
                .falsePositiveCount(0)
                .build();

        pattern.calculatePrecision();

        assertEquals(1.0, pattern.getPrecision());
    }

    @Test
    @DisplayName("calculatePrecision with only false positives should return 0.0")
    void testCalculatePrecision_OnlyFalsePositives() {
        FraudPattern pattern = FraudPattern.builder()
                .truePositiveCount(0)
                .falsePositiveCount(100)
                .build();

        pattern.calculatePrecision();

        assertEquals(0.0, pattern.getPrecision());
    }

    // ========== Mutation-Killing Tests: Arithmetic Operations ==========

    @Test
    @DisplayName("calculatePrecision with various ratios should calculate correctly")
    void testCalculatePrecision_VariousRatios() {
        // 50/50 split
        FraudPattern pattern1 = FraudPattern.builder()
                .truePositiveCount(50)
                .falsePositiveCount(50)
                .build();
        pattern1.calculatePrecision();
        assertEquals(0.5, pattern1.getPrecision(), 0.001);

        // 75/25 split
        FraudPattern pattern2 = FraudPattern.builder()
                .truePositiveCount(75)
                .falsePositiveCount(25)
                .build();
        pattern2.calculatePrecision();
        assertEquals(0.75, pattern2.getPrecision(), 0.001);

        // 25/75 split
        FraudPattern pattern3 = FraudPattern.builder()
                .truePositiveCount(25)
                .falsePositiveCount(75)
                .build();
        pattern3.calculatePrecision();
        assertEquals(0.25, pattern3.getPrecision(), 0.001);

        // 90/10 split
        FraudPattern pattern4 = FraudPattern.builder()
                .truePositiveCount(90)
                .falsePositiveCount(10)
                .build();
        pattern4.calculatePrecision();
        assertEquals(0.9, pattern4.getPrecision(), 0.001);

        // 10/90 split
        FraudPattern pattern5 = FraudPattern.builder()
                .truePositiveCount(10)
                .falsePositiveCount(90)
                .build();
        pattern5.calculatePrecision();
        assertEquals(0.1, pattern5.getPrecision(), 0.001);
    }

    @Test
    @DisplayName("calculatePrecision with large numbers should calculate correctly")
    void testCalculatePrecision_LargeNumbers() {
        FraudPattern pattern = FraudPattern.builder()
                .truePositiveCount(985432)
                .falsePositiveCount(14568)
                .build();

        pattern.calculatePrecision();

        double expected = 985432.0 / (985432.0 + 14568.0);
        assertEquals(expected, pattern.getPrecision(), 0.0001);
    }

    @Test
    @DisplayName("calculatePrecision with small numbers should calculate correctly")
    void testCalculatePrecision_SmallNumbers() {
        // 1 true, 0 false
        FraudPattern pattern1 = FraudPattern.builder()
                .truePositiveCount(1)
                .falsePositiveCount(0)
                .build();
        pattern1.calculatePrecision();
        assertEquals(1.0, pattern1.getPrecision());

        // 1 true, 1 false
        FraudPattern pattern2 = FraudPattern.builder()
                .truePositiveCount(1)
                .falsePositiveCount(1)
                .build();
        pattern2.calculatePrecision();
        assertEquals(0.5, pattern2.getPrecision(), 0.001);

        // 0 true, 1 false
        FraudPattern pattern3 = FraudPattern.builder()
                .truePositiveCount(0)
                .falsePositiveCount(1)
                .build();
        pattern3.calculatePrecision();
        assertEquals(0.0, pattern3.getPrecision());
    }

    @Test
    @DisplayName("incrementDetection should update counts and precision correctly")
    void testIncrementDetection_CountAndPrecisionUpdates() {
        FraudPattern pattern = FraudPattern.builder()
                .detectionCount(1000)
                .truePositiveCount(800)
                .falsePositiveCount(200)
                .precision(0.8)
                .build();

        // After true positive
        pattern.incrementDetection(true);
        assertEquals(1001, pattern.getDetectionCount());
        assertEquals(801, pattern.getTruePositiveCount());
        assertEquals(200, pattern.getFalsePositiveCount());
        assertEquals(801.0 / 1001.0, pattern.getPrecision(), 0.001);

        // After false positive
        pattern.incrementDetection(false);
        assertEquals(1002, pattern.getDetectionCount());
        assertEquals(801, pattern.getTruePositiveCount());
        assertEquals(201, pattern.getFalsePositiveCount());
        assertEquals(801.0 / 1002.0, pattern.getPrecision(), 0.001);
    }

    @Test
    @DisplayName("incrementDetection from zero should work correctly")
    void testIncrementDetection_FromZero() {
        FraudPattern pattern = FraudPattern.builder()
                .detectionCount(0)
                .truePositiveCount(0)
                .falsePositiveCount(0)
                .build();

        pattern.incrementDetection(true);
        assertEquals(1, pattern.getDetectionCount());
        assertEquals(1, pattern.getTruePositiveCount());
        assertEquals(0, pattern.getFalsePositiveCount());
        assertEquals(1.0, pattern.getPrecision());
    }

    // ========== Mutation-Killing Tests: Boolean Combinations ==========

    @Test
    @DisplayName("isActive should return correct value for all boolean states")
    void testIsActive_AllBooleanStates() {
        FraudPattern patternTrue = FraudPattern.builder()
                .isActive(true)
                .build();
        assertTrue(patternTrue.isActive());

        FraudPattern patternFalse = FraudPattern.builder()
                .isActive(false)
                .build();
        assertFalse(patternFalse.isActive());

        FraudPattern patternNull = FraudPattern.builder()
                .isActive(null)
                .build();
        assertFalse(patternNull.isActive());
    }

    @Test
    @DisplayName("activate and deactivate should toggle isActive correctly")
    void testActivateDeactivate_Toggle() {
        FraudPattern pattern = FraudPattern.builder()
                .isActive(false)
                .build();

        assertFalse(pattern.isActive());

        pattern.activate();
        assertTrue(pattern.isActive());

        pattern.deactivate();
        assertFalse(pattern.isActive());

        pattern.activate();
        assertTrue(pattern.isActive());
    }

    // ========== Mutation-Killing Tests: hashCode() and equals() ==========

    @Test
    @DisplayName("hashCode - Objects with same ID should have same hashCode")
    void testHashCode_SameId_SameHashCode() {
        UUID id = UUID.randomUUID();
        FraudPattern pattern1 = FraudPattern.builder().id(id).build();
        FraudPattern pattern2 = FraudPattern.builder().id(id).build();

        // This kills Math mutants in hashCode()
        assertEquals(pattern1.hashCode(), pattern2.hashCode());
    }

    @Test
    @DisplayName("hashCode - Objects with different IDs should have different hashCodes")
    void testHashCode_DifferentId_DifferentHashCode() {
        FraudPattern pattern1 = FraudPattern.builder().id(UUID.randomUUID()).build();
        FraudPattern pattern2 = FraudPattern.builder().id(UUID.randomUUID()).build();

        // This kills Math mutants in hashCode()
        assertNotEquals(pattern1.hashCode(), pattern2.hashCode());
    }

    @Test
    @DisplayName("HashSet should work correctly with FraudPattern")
    void testHashSet_UsesHashCode() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        FraudPattern pattern1 = FraudPattern.builder().id(id1).patternName("PATTERN-001").build();
        FraudPattern pattern2 = FraudPattern.builder().id(id2).patternName("PATTERN-002").build();
        FraudPattern pattern1Duplicate = FraudPattern.builder().id(id1).patternName("PATTERN-001-DUP").build();

        java.util.HashSet<FraudPattern> set = new java.util.HashSet<>();
        assertTrue(set.add(pattern1));
        assertTrue(set.add(pattern2));
        assertFalse(set.add(pattern1Duplicate)); // Should not add - same ID as pattern1

        assertEquals(2, set.size());
        assertTrue(set.contains(pattern1));
        assertTrue(set.contains(pattern2));
    }

    @Test
    @DisplayName("HashMap should work correctly with FraudPattern as key")
    void testHashMap_UsesHashCodeAndEquals() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        FraudPattern key1 = FraudPattern.builder().id(id1).build();
        FraudPattern key2 = FraudPattern.builder().id(id2).build();
        FraudPattern key1Duplicate = FraudPattern.builder().id(id1).build();

        java.util.Map<FraudPattern, String> map = new java.util.HashMap<>();
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
        FraudPattern pattern = FraudPattern.builder().id(UUID.randomUUID()).build();
        // This kills NegateConditionalsMutator in equals()
        assertEquals(pattern, pattern);
    }

    @Test
    @DisplayName("equals - Null comparison")
    void testEquals_NullComparison() {
        FraudPattern pattern = FraudPattern.builder().id(UUID.randomUUID()).build();
        // This kills NegateConditionalsMutator in equals()
        assertNotEquals(null, pattern);
    }

    @Test
    @DisplayName("equals - Different type comparison")
    void testEquals_DifferentType() {
        FraudPattern pattern = FraudPattern.builder().id(UUID.randomUUID()).build();
        String other = "Not a FraudPattern";
        // This kills NegateConditionalsMutator in equals()
        assertNotEquals(pattern, other);
    }

    @Test
    @DisplayName("equals - All fields same except ID")
    void testEquals_AllFieldsSameExceptId() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        FraudPattern pattern1 = FraudPattern.builder()
                .id(id1)
                .patternName("PATTERN")
                .patternType("TYPE")
                .build();

        FraudPattern pattern2 = FraudPattern.builder()
                .id(id2)
                .patternName("PATTERN")
                .patternType("TYPE")
                .build();

        // This kills NegateConditionalsMutator in equals()
        assertNotEquals(pattern1, pattern2);
    }

    @Test
    @DisplayName("equals - Both IDs null")
    void testEquals_BothIdsNull() {
        FraudPattern pattern1 = FraudPattern.builder().build();
        FraudPattern pattern2 = FraudPattern.builder().build();

        assertEquals(pattern1, pattern2);
        assertEquals(pattern1.hashCode(), pattern2.hashCode());
    }

    @Test
    @DisplayName("equals - One ID null")
    void testEquals_OneIdNull() {
        UUID id = UUID.randomUUID();
        FraudPattern pattern1 = FraudPattern.builder().id(id).build();
        FraudPattern pattern2 = FraudPattern.builder().id(null).build();

        assertNotEquals(pattern1, pattern2);
    }

    @Test
    @DisplayName("canEqual - Same class should return true")
    void testCanEqual_SameClass() {
        FraudPattern pattern = FraudPattern.builder().build();
        FraudPattern other = FraudPattern.builder().build();

        // This kills BooleanReturnValsMutator in canEqual()
        assertTrue(pattern.canEqual(other));
    }

    @Test
    @DisplayName("canEqual - Different class should return false")
    void testCanEqual_DifferentClass() {
        FraudPattern pattern = FraudPattern.builder().build();
        String other = "Not a FraudPattern";

        // This kills BooleanReturnValsMutator in canEqual()
        assertFalse(pattern.canEqual(other));
    }

    @Test
    @DisplayName("canEqual - Null should return false")
    void testCanEqual_Null() {
        FraudPattern pattern = FraudPattern.builder().build();

        // This kills BooleanReturnValsMutator in canEqual()
        assertFalse(pattern.canEqual(null));
    }

    @Test
    @DisplayName("equals - CanEqual check between different types")
    void testEquals_WithCanEqualCheck() {
        UUID id = UUID.randomUUID();
        FraudPattern pattern = FraudPattern.builder().id(id).build();
        FraudCase otherCase = FraudCase.builder().id(id).caseNumber("FC-123").build();

        // This tests canEqual within equals
        assertNotEquals(pattern, otherCase);
    }

    @Test
    @DisplayName("HashSet contains and remove operations")
    void testHashSet_ContainsAndRemove() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        FraudPattern pattern1 = FraudPattern.builder().id(id1).patternName("PATTERN-001").build();
        FraudPattern pattern2 = FraudPattern.builder().id(id2).patternName("PATTERN-002").build();

        java.util.HashSet<FraudPattern> set = new java.util.HashSet<>();
        set.add(pattern1);
        set.add(pattern2);

        // Test contains - uses hashCode() and equals()
        assertTrue(set.contains(pattern1));
        assertTrue(set.contains(pattern2));

        // Create duplicate with same ID
        FraudPattern pattern1Dup = FraudPattern.builder().id(id1).patternName("PATTERN-001-DUP").build();
        assertTrue(set.contains(pattern1Dup));

        // Test remove - uses hashCode() and equals()
        assertTrue(set.remove(pattern1Dup));
        assertEquals(1, set.size());
        assertFalse(set.contains(pattern1));
    }

    @Test
    @DisplayName("hashCode consistency across multiple calls")
    void testHashCode_Consistency() {
        FraudPattern pattern = FraudPattern.builder()
                .id(UUID.randomUUID())
                .patternName("PATTERN-001")
                .build();

        int hash1 = pattern.hashCode();
        int hash2 = pattern.hashCode();
        int hash3 = pattern.hashCode();

        assertEquals(hash1, hash2);
        assertEquals(hash2, hash3);
    }

    @Test
    @DisplayName("hashCode with null fields")
    void testHashCode_NullFields() {
        UUID id = UUID.randomUUID();
        FraudPattern pattern1 = FraudPattern.builder()
                .id(id)
                .patternName(null)
                .build();

        FraudPattern pattern2 = FraudPattern.builder()
                .id(id)
                .patternName(null)
                .build();

        assertEquals(pattern1.hashCode(), pattern2.hashCode());
    }
}
