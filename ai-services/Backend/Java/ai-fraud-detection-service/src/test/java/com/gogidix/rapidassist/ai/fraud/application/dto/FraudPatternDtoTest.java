package com.gogidix.rapidassist.ai.fraud.application.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FraudPatternDto
 */
@DisplayName("FraudPatternDto Tests")
class FraudPatternDtoTest {

    private static final UUID TEST_ID = UUID.randomUUID();
    private static final String TENANT_ID = "tenant-123";

    @Test
    @DisplayName("Builder - All fields")
    void testBuilder_AllFields() {
        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> definition = Map.of("min_occurrences", 3, "time_window_hours", 24);
        Map<String, Object> metadata = Map.of("key", "value");

        FraudPatternDto dto = FraudPatternDto.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .patternName("Geoclustering Pattern")
                .patternType("GEOGRAPHIC")
                .description("Claims from nearby locations")
                .patternDefinition(definition)
                .detectionAlgorithm("DBSCAN")
                .priority(1)
                .isActive(true)
                .confidenceThreshold(0.85)
                .detectionCount(150)
                .falsePositiveCount(10)
                .truePositiveCount(140)
                .precision(0.933)
                .recall(0.92)
                .createdBy("admin")
                .updatedBy("analyst")
                .createdAt(now)
                .updatedAt(now)
                .metadata(metadata)
                .build();

        assertEquals(TEST_ID, dto.getId());
        assertEquals(TENANT_ID, dto.getTenantId());
        assertEquals("Geoclustering Pattern", dto.getPatternName());
        assertEquals("GEOGRAPHIC", dto.getPatternType());
        assertEquals("Claims from nearby locations", dto.getDescription());
        assertEquals(definition, dto.getPatternDefinition());
        assertEquals("DBSCAN", dto.getDetectionAlgorithm());
        assertEquals(1, dto.getPriority());
        assertTrue(dto.getIsActive());
        assertEquals(0.85, dto.getConfidenceThreshold());
        assertEquals(150, dto.getDetectionCount());
        assertEquals(10, dto.getFalsePositiveCount());
        assertEquals(140, dto.getTruePositiveCount());
        assertEquals(0.933, dto.getPrecision());
        assertEquals(0.92, dto.getRecall());
        assertEquals("admin", dto.getCreatedBy());
        assertEquals("analyst", dto.getUpdatedBy());
    }

    @Test
    @DisplayName("Builder - Minimal fields")
    void testBuilder_MinimalFields() {
        FraudPatternDto dto = FraudPatternDto.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .build();

        assertEquals(TEST_ID, dto.getId());
        assertEquals(TENANT_ID, dto.getTenantId());
        assertNull(dto.getPatternName());
        assertNull(dto.getPatternType());
        assertNull(dto.getIsActive());
    }

    @Test
    @DisplayName("Getters and Setters")
    void testGettersSetters() {
        FraudPatternDto dto = new FraudPatternDto();
        dto.setId(TEST_ID);
        dto.setTenantId(TENANT_ID);
        dto.setPatternName("Velocity Pattern");
        dto.setPatternType("TEMPORAL");
        dto.setIsActive(true);
        dto.setPriority(2);
        dto.setPrecision(0.90);

        assertEquals(TEST_ID, dto.getId());
        assertEquals(TENANT_ID, dto.getTenantId());
        assertEquals("Velocity Pattern", dto.getPatternName());
        assertEquals("TEMPORAL", dto.getPatternType());
        assertTrue(dto.getIsActive());
        assertEquals(2, dto.getPriority());
        assertEquals(0.90, dto.getPrecision());
    }

    @Test
    @DisplayName("Equals - Same ID")
    void testEquals_SameId() {
        FraudPatternDto dto1 = FraudPatternDto.builder().id(TEST_ID).build();
        FraudPatternDto dto2 = FraudPatternDto.builder().id(TEST_ID).build();

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("Equals - Different ID")
    void testEquals_DifferentId() {
        FraudPatternDto dto1 = FraudPatternDto.builder().id(UUID.randomUUID()).build();
        FraudPatternDto dto2 = FraudPatternDto.builder().id(UUID.randomUUID()).build();

        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("Pattern types")
    void testPatternTypes() {
        String[] patternTypes = {"GEOGRAPHIC", "TEMPORAL", "BEHAVIORAL", "NETWORK", "VELOCITY"};

        for (String patternType : patternTypes) {
            FraudPatternDto dto = FraudPatternDto.builder().patternType(patternType).build();
            assertEquals(patternType, dto.getPatternType());
        }
    }

    @Test
    @DisplayName("Active status")
    void testActiveStatus() {
        FraudPatternDto active = FraudPatternDto.builder().isActive(true).build();
        FraudPatternDto inactive = FraudPatternDto.builder().isActive(false).build();

        assertTrue(active.getIsActive());
        assertFalse(inactive.getIsActive());
    }

    @Test
    @DisplayName("Precision and recall values")
    void testPrecisionAndRecall() {
        FraudPatternDto dto = FraudPatternDto.builder()
                .precision(0.95)
                .recall(0.88)
                .truePositiveCount(95)
                .falsePositiveCount(5)
                .build();

        assertEquals(0.95, dto.getPrecision());
        assertEquals(0.88, dto.getRecall());
        assertEquals(95, dto.getTruePositiveCount());
        assertEquals(5, dto.getFalsePositiveCount());
    }

    @Test
    @DisplayName("toString - Contains ID")
    void testToString_ContainsId() {
        FraudPatternDto dto = FraudPatternDto.builder().id(TEST_ID).build();
        assertTrue(dto.toString().contains(TEST_ID.toString()));
    }

    // ========== Mutation-Killing Tests: All Getter/Setter Combinations ==========

    @Test
    @DisplayName("All getters should return values set via builder")
    void testAllGetters_ReturnBuilderValues() {
        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> definition = Map.of("key", "value");
        Map<String, Object> metadata = Map.of("meta", "value");

        FraudPatternDto dto = FraudPatternDto.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .patternName("TEST_PATTERN")
                .patternType("TYPE")
                .description("Description")
                .patternDefinition(definition)
                .detectionAlgorithm("Algorithm")
                .priority(5)
                .isActive(true)
                .confidenceThreshold(0.75)
                .detectionCount(100)
                .truePositiveCount(80)
                .falsePositiveCount(20)
                .precision(0.8)
                .recall(0.85)
                .createdBy("creator")
                .updatedBy("updater")
                .metadata(metadata)
                .createdAt(now)
                .updatedAt(now.plusHours(1))
                .build();

        assertEquals(TEST_ID, dto.getId());
        assertEquals(TENANT_ID, dto.getTenantId());
        assertEquals("TEST_PATTERN", dto.getPatternName());
        assertEquals("TYPE", dto.getPatternType());
        assertEquals("Description", dto.getDescription());
        assertEquals(definition, dto.getPatternDefinition());
        assertEquals("Algorithm", dto.getDetectionAlgorithm());
        assertEquals(5, dto.getPriority());
        assertTrue(dto.getIsActive());
        assertEquals(0.75, dto.getConfidenceThreshold());
        assertEquals(100, dto.getDetectionCount());
        assertEquals(80, dto.getTruePositiveCount());
        assertEquals(20, dto.getFalsePositiveCount());
        assertEquals(0.8, dto.getPrecision());
        assertEquals(0.85, dto.getRecall());
        assertEquals("creator", dto.getCreatedBy());
        assertEquals("updater", dto.getUpdatedBy());
        assertEquals(metadata, dto.getMetadata());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(now.plusHours(1), dto.getUpdatedAt());
    }

    @Test
    @DisplayName("Setters should handle null values")
    void testSetters_NullValues() {
        FraudPatternDto dto = FraudPatternDto.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .patternName("PATTERN")
                .build();

        dto.setPatternType(null);
        dto.setDescription(null);
        dto.setPatternDefinition(null);
        dto.setDetectionAlgorithm(null);
        dto.setIsActive(null);
        dto.setCreatedBy(null);
        dto.setUpdatedBy(null);
        dto.setMetadata(null);

        assertNull(dto.getPatternType());
        assertNull(dto.getDescription());
        assertNull(dto.getPatternDefinition());
        assertNull(dto.getDetectionAlgorithm());
        assertNull(dto.getIsActive());
        assertNull(dto.getCreatedBy());
        assertNull(dto.getUpdatedBy());
        assertNull(dto.getMetadata());
    }

    @Test
    @DisplayName("All setters should update fields")
    void testAllSetters_UpdateFields() {
        FraudPatternDto dto = new FraudPatternDto();
        dto.setId(TEST_ID);
        dto.setTenantId(TENANT_ID);
        dto.setPatternName("Modified");
        dto.setPatternType("TYPE");
        dto.setDescription("Desc");
        dto.setDetectionAlgorithm("Algo");
        dto.setPriority(10);
        dto.setIsActive(true);
        dto.setConfidenceThreshold(0.95);
        dto.setDetectionCount(200);
        dto.setTruePositiveCount(180);
        dto.setFalsePositiveCount(20);
        dto.setPrecision(0.9);
        dto.setRecall(0.95);

        assertEquals(TEST_ID, dto.getId());
        assertEquals("Modified", dto.getPatternName());
        assertEquals(10, dto.getPriority());
        assertTrue(dto.getIsActive());
        assertEquals(0.95, dto.getConfidenceThreshold());
        assertEquals(200, dto.getDetectionCount());
        assertEquals(180, dto.getTruePositiveCount());
        assertEquals(20, dto.getFalsePositiveCount());
        assertEquals(0.9, dto.getPrecision());
        assertEquals(0.95, dto.getRecall());
    }

    @Test
    @DisplayName("Double fields: confidenceThreshold, precision, recall")
    void testDoubleFields() {
        FraudPatternDto dto = FraudPatternDto.builder()
                .confidenceThreshold(0.75)
                .precision(0.85)
                .recall(0.90)
                .build();

        assertEquals(0.75, dto.getConfidenceThreshold());
        assertEquals(0.85, dto.getPrecision());
        assertEquals(0.90, dto.getRecall());

        dto.setConfidenceThreshold(0.80);
        dto.setPrecision(0.90);
        dto.setRecall(0.95);

        assertEquals(0.80, dto.getConfidenceThreshold());
        assertEquals(0.90, dto.getPrecision());
        assertEquals(0.95, dto.getRecall());
    }

    @Test
    @DisplayName("Integer fields: priority, detectionCount, truePositiveCount, falsePositiveCount")
    void testIntegerFields() {
        FraudPatternDto dto = FraudPatternDto.builder()
                .priority(1)
                .detectionCount(100)
                .truePositiveCount(80)
                .falsePositiveCount(20)
                .build();

        assertEquals(1, dto.getPriority());
        assertEquals(100, dto.getDetectionCount());
        assertEquals(80, dto.getTruePositiveCount());
        assertEquals(20, dto.getFalsePositiveCount());

        dto.setPriority(10);
        dto.setDetectionCount(200);
        dto.setTruePositiveCount(180);
        dto.setFalsePositiveCount(20);

        assertEquals(10, dto.getPriority());
        assertEquals(200, dto.getDetectionCount());
        assertEquals(180, dto.getTruePositiveCount());
        assertEquals(20, dto.getFalsePositiveCount());
    }

    @Test
    @DisplayName("Boolean field: isActive should handle all values")
    void testIsActive_AllValues() {
        FraudPatternDto dto1 = FraudPatternDto.builder().isActive(true).build();
        FraudPatternDto dto2 = FraudPatternDto.builder().isActive(false).build();
        FraudPatternDto dto3 = FraudPatternDto.builder().isActive(null).build();

        assertTrue(dto1.getIsActive());
        assertFalse(dto2.getIsActive());
        assertNull(dto3.getIsActive());
    }

    @Test
    @DisplayName("Map fields: patternDefinition and metadata")
    void testMapFields() {
        Map<String, Object> definition = Map.of("key1", "value1", "key2", 100);
        Map<String, Object> metadata = Map.of("source", "api");

        FraudPatternDto dto = FraudPatternDto.builder()
                .patternDefinition(definition)
                .metadata(metadata)
                .build();

        assertEquals(2, dto.getPatternDefinition().size());
        assertEquals("value1", dto.getPatternDefinition().get("key1"));
        assertEquals("api", dto.getMetadata().get("source"));

        dto.setPatternDefinition(Map.of());
        dto.setMetadata(null);

        assertTrue(dto.getPatternDefinition().isEmpty());
        assertNull(dto.getMetadata());
    }

    @Test
    @DisplayName("Priority levels")
    void testPriorityLevels() {
        Integer[] priorities = {0, 1, 5, 10};

        for (Integer priority : priorities) {
            FraudPatternDto dto = FraudPatternDto.builder().priority(priority).build();
            assertEquals(priority, dto.getPriority());
        }
    }

    @Test
    @DisplayName("Detection algorithms")
    void testDetectionAlgorithms() {
        String[] algorithms = {"DBSCAN", "K_MEANS", "NEURAL_NETWORK", "RANDOM_FOREST"};

        for (String algorithm : algorithms) {
            FraudPatternDto dto = FraudPatternDto.builder().detectionAlgorithm(algorithm).build();
            assertEquals(algorithm, dto.getDetectionAlgorithm());
        }
    }

    // ========== Mutation-Killing Tests: Numeric Field Operations ==========

    @Test
    @DisplayName("Confidence threshold should handle various decimal values")
    void testConfidenceThreshold_NumericValues() {
        FraudPatternDto dto1 = FraudPatternDto.builder()
                .confidenceThreshold(0.0)
                .build();
        assertEquals(0.0, dto1.getConfidenceThreshold());

        FraudPatternDto dto2 = FraudPatternDto.builder()
                .confidenceThreshold(0.25)
                .build();
        assertEquals(0.25, dto2.getConfidenceThreshold());

        FraudPatternDto dto3 = FraudPatternDto.builder()
                .confidenceThreshold(0.5)
                .build();
        assertEquals(0.5, dto3.getConfidenceThreshold());

        FraudPatternDto dto4 = FraudPatternDto.builder()
                .confidenceThreshold(0.75)
                .build();
        assertEquals(0.75, dto4.getConfidenceThreshold());

        FraudPatternDto dto5 = FraudPatternDto.builder()
                .confidenceThreshold(1.0)
                .build();
        assertEquals(1.0, dto5.getConfidenceThreshold());
    }

    @Test
    @DisplayName("Precision field should handle various decimal values")
    void testPrecision_NumericValues() {
        FraudPatternDto dto1 = FraudPatternDto.builder()
                .precision(0.0)
                .build();
        assertEquals(0.0, dto1.getPrecision());

        FraudPatternDto dto2 = FraudPatternDto.builder()
                .precision(0.5)
                .build();
        assertEquals(0.5, dto2.getPrecision());

        FraudPatternDto dto3 = FraudPatternDto.builder()
                .precision(0.85)
                .build();
        assertEquals(0.85, dto3.getPrecision());

        FraudPatternDto dto4 = FraudPatternDto.builder()
                .precision(0.95)
                .build();
        assertEquals(0.95, dto4.getPrecision());

        FraudPatternDto dto5 = FraudPatternDto.builder()
                .precision(1.0)
                .build();
        assertEquals(1.0, dto5.getPrecision());
    }

    @Test
    @DisplayName("Recall field should handle various decimal values")
    void testRecall_NumericValues() {
        FraudPatternDto dto1 = FraudPatternDto.builder()
                .recall(0.0)
                .build();
        assertEquals(0.0, dto1.getRecall());

        FraudPatternDto dto2 = FraudPatternDto.builder()
                .recall(0.3)
                .build();
        assertEquals(0.3, dto2.getRecall());

        FraudPatternDto dto3 = FraudPatternDto.builder()
                .recall(0.7)
                .build();
        assertEquals(0.7, dto3.getRecall());

        FraudPatternDto dto4 = FraudPatternDto.builder()
                .recall(1.0)
                .build();
        assertEquals(1.0, dto4.getRecall());
    }

    @Test
    @DisplayName("Detection count should handle various integer values")
    void testDetectionCount_NumericValues() {
        FraudPatternDto dto1 = FraudPatternDto.builder()
                .detectionCount(0)
                .build();
        assertEquals(0, dto1.getDetectionCount());

        FraudPatternDto dto2 = FraudPatternDto.builder()
                .detectionCount(1)
                .build();
        assertEquals(1, dto2.getDetectionCount());

        FraudPatternDto dto3 = FraudPatternDto.builder()
                .detectionCount(100)
                .build();
        assertEquals(100, dto3.getDetectionCount());

        FraudPatternDto dto4 = FraudPatternDto.builder()
                .detectionCount(1000)
                .build();
        assertEquals(1000, dto4.getDetectionCount());

        FraudPatternDto dto5 = FraudPatternDto.builder()
                .detectionCount(10000)
                .build();
        assertEquals(10000, dto5.getDetectionCount());
    }

    @Test
    @DisplayName("True positive count should handle various integer values")
    void testTruePositiveCount_NumericValues() {
        FraudPatternDto dto1 = FraudPatternDto.builder()
                .truePositiveCount(0)
                .build();
        assertEquals(0, dto1.getTruePositiveCount());

        FraudPatternDto dto2 = FraudPatternDto.builder()
                .truePositiveCount(50)
                .build();
        assertEquals(50, dto2.getTruePositiveCount());

        FraudPatternDto dto3 = FraudPatternDto.builder()
                .truePositiveCount(100)
                .build();
        assertEquals(100, dto3.getTruePositiveCount());

        FraudPatternDto dto4 = FraudPatternDto.builder()
                .truePositiveCount(500)
                .build();
        assertEquals(500, dto4.getTruePositiveCount());
    }

    @Test
    @DisplayName("False positive count should handle various integer values")
    void testFalsePositiveCount_NumericValues() {
        FraudPatternDto dto1 = FraudPatternDto.builder()
                .falsePositiveCount(0)
                .build();
        assertEquals(0, dto1.getFalsePositiveCount());

        FraudPatternDto dto2 = FraudPatternDto.builder()
                .falsePositiveCount(10)
                .build();
        assertEquals(10, dto2.getFalsePositiveCount());

        FraudPatternDto dto3 = FraudPatternDto.builder()
                .falsePositiveCount(50)
                .build();
        assertEquals(50, dto3.getFalsePositiveCount());

        FraudPatternDto dto4 = FraudPatternDto.builder()
                .falsePositiveCount(200)
                .build();
        assertEquals(200, dto4.getFalsePositiveCount());
    }

    @Test
    @DisplayName("All numeric fields should work together correctly")
    void testAllNumericFields_Together() {
        FraudPatternDto dto = FraudPatternDto.builder()
                .priority(5)
                .confidenceThreshold(0.85)
                .precision(0.92)
                .recall(0.88)
                .detectionCount(1500)
                .truePositiveCount(1380)
                .falsePositiveCount(120)
                .build();

        assertEquals(5, dto.getPriority());
        assertEquals(0.85, dto.getConfidenceThreshold());
        assertEquals(0.92, dto.getPrecision());
        assertEquals(0.88, dto.getRecall());
        assertEquals(1500, dto.getDetectionCount());
        assertEquals(1380, dto.getTruePositiveCount());
        assertEquals(120, dto.getFalsePositiveCount());
    }

    // ========== Mutation-Killing Tests: hashCode() and equals() ==========

    @Test
    @DisplayName("hashCode - Objects with same ID should have same hashCode")
    void testHashCode_SameId_SameHashCode() {
        UUID id = UUID.randomUUID();
        FraudPatternDto dto1 = FraudPatternDto.builder().id(id).build();
        FraudPatternDto dto2 = FraudPatternDto.builder().id(id).build();

        // This kills Math mutants in hashCode()
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("hashCode - Objects with different IDs should have different hashCodes")
    void testHashCode_DifferentId_DifferentHashCode() {
        FraudPatternDto dto1 = FraudPatternDto.builder().id(UUID.randomUUID()).build();
        FraudPatternDto dto2 = FraudPatternDto.builder().id(UUID.randomUUID()).build();

        // This kills Math mutants in hashCode()
        assertNotEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("HashSet should work correctly with FraudPatternDto")
    void testHashSet_UsesHashCode() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        FraudPatternDto dto1 = FraudPatternDto.builder().id(id1).patternName("PATTERN-001").build();
        FraudPatternDto dto2 = FraudPatternDto.builder().id(id2).patternName("PATTERN-002").build();
        FraudPatternDto dto1Duplicate = FraudPatternDto.builder().id(id1).patternName("PATTERN-001-DUP").build();

        java.util.HashSet<FraudPatternDto> set = new java.util.HashSet<>();
        assertTrue(set.add(dto1));
        assertTrue(set.add(dto2));
        assertFalse(set.add(dto1Duplicate)); // Should not add - same ID as dto1

        assertEquals(2, set.size());
        assertTrue(set.contains(dto1));
        assertTrue(set.contains(dto2));
    }

    @Test
    @DisplayName("HashMap should work correctly with FraudPatternDto as key")
    void testHashMap_UsesHashCodeAndEquals() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        FraudPatternDto key1 = FraudPatternDto.builder().id(id1).build();
        FraudPatternDto key2 = FraudPatternDto.builder().id(id2).build();
        FraudPatternDto key1Duplicate = FraudPatternDto.builder().id(id1).build();

        java.util.Map<FraudPatternDto, String> map = new java.util.HashMap<>();
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
        FraudPatternDto dto = FraudPatternDto.builder().id(TEST_ID).build();
        // This kills NegateConditionalsMutator in equals()
        assertEquals(dto, dto);
    }

    @Test
    @DisplayName("equals - Null comparison")
    void testEquals_NullComparison() {
        FraudPatternDto dto = FraudPatternDto.builder().id(TEST_ID).build();
        // This kills NegateConditionalsMutator in equals()
        assertNotEquals(null, dto);
    }

    @Test
    @DisplayName("equals - Different type comparison")
    void testEquals_DifferentType() {
        FraudPatternDto dto = FraudPatternDto.builder().id(TEST_ID).build();
        String other = "Not a FraudPatternDto";
        // This kills NegateConditionalsMutator in equals()
        assertNotEquals(dto, other);
    }

    @Test
    @DisplayName("equals - All fields same except ID")
    void testEquals_AllFieldsSameExceptId() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        FraudPatternDto dto1 = FraudPatternDto.builder()
                .id(id1)
                .patternName("PATTERN")
                .patternType("TYPE")
                .build();

        FraudPatternDto dto2 = FraudPatternDto.builder()
                .id(id2)
                .patternName("PATTERN")
                .patternType("TYPE")
                .build();

        // This kills NegateConditionalsMutator in equals()
        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("equals - Both IDs null")
    void testEquals_BothIdsNull() {
        FraudPatternDto dto1 = FraudPatternDto.builder().build();
        FraudPatternDto dto2 = FraudPatternDto.builder().build();

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("equals - One ID null")
    void testEquals_OneIdNull() {
        UUID id = UUID.randomUUID();
        FraudPatternDto dto1 = FraudPatternDto.builder().id(id).build();
        FraudPatternDto dto2 = FraudPatternDto.builder().id(null).build();

        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("canEqual - Same class should return true")
    void testCanEqual_SameClass() {
        FraudPatternDto dto = FraudPatternDto.builder().build();
        FraudPatternDto other = FraudPatternDto.builder().build();

        // This kills BooleanReturnValsMutator in canEqual()
        assertTrue(dto.canEqual(other));
    }

    @Test
    @DisplayName("canEqual - Different class should return false")
    void testCanEqual_DifferentClass() {
        FraudPatternDto dto = FraudPatternDto.builder().build();
        String other = "Not a FraudPatternDto";

        // This kills BooleanReturnValsMutator in canEqual()
        assertFalse(dto.canEqual(other));
    }

    @Test
    @DisplayName("canEqual - Null should return false")
    void testCanEqual_Null() {
        FraudPatternDto dto = FraudPatternDto.builder().build();

        // This kills BooleanReturnValsMutator in canEqual()
        assertFalse(dto.canEqual(null));
    }

    @Test
    @DisplayName("equals - CanEqual check between different types")
    void testEquals_WithCanEqualCheck() {
        UUID id = UUID.randomUUID();
        FraudPatternDto dto = FraudPatternDto.builder()
                .id(id)
                .patternName("PATTERN")
                .build();
        FraudRuleDto otherDto = FraudRuleDto.builder()
                .id(id)
                .ruleName("RULE")
                .build();

        // This tests canEqual within equals
        assertNotEquals(dto, otherDto);
    }

    @Test
    @DisplayName("HashSet contains and remove operations")
    void testHashSet_ContainsAndRemove() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        FraudPatternDto dto1 = FraudPatternDto.builder().id(id1).patternName("PATTERN-001").build();
        FraudPatternDto dto2 = FraudPatternDto.builder().id(id2).patternName("PATTERN-002").build();

        java.util.HashSet<FraudPatternDto> set = new java.util.HashSet<>();
        set.add(dto1);
        set.add(dto2);

        // Test contains - uses hashCode() and equals()
        assertTrue(set.contains(dto1));
        assertTrue(set.contains(dto2));

        // Create duplicate with same ID
        FraudPatternDto dto1Dup = FraudPatternDto.builder().id(id1).patternName("PATTERN-001-DUP").build();
        assertTrue(set.contains(dto1Dup));

        // Test remove - uses hashCode() and equals()
        assertTrue(set.remove(dto1Dup));
        assertEquals(1, set.size());
        assertFalse(set.contains(dto1));
    }

    @Test
    @DisplayName("hashCode consistency across multiple calls")
    void testHashCode_Consistency() {
        FraudPatternDto dto = FraudPatternDto.builder()
                .id(TEST_ID)
                .patternName("PATTERN")
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
        FraudPatternDto dto1 = FraudPatternDto.builder()
                .id(id)
                .patternName(null)
                .build();

        FraudPatternDto dto2 = FraudPatternDto.builder()
                .id(id)
                .patternName(null)
                .build();

        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    // ========== Additional Getter Return Value Tests ==========

    @Test
    @DisplayName("Getters should return exact values set via builder")
    void testGetters_ExactValues() {
        String patternName = "SUSPICIOUS_PATTERN";
        String patternType = "BEHAVIORAL";
        String description = "Pattern description";
        String detectionAlgorithm = "NEURAL_NETWORK";

        FraudPatternDto dto = FraudPatternDto.builder()
                .patternName(patternName)
                .patternType(patternType)
                .description(description)
                .detectionAlgorithm(detectionAlgorithm)
                .build();

        // This kills EmptyObjectReturnValsMutator for string getters
        assertEquals(patternName, dto.getPatternName());
        assertEquals(patternType, dto.getPatternType());
        assertEquals(description, dto.getDescription());
        assertEquals(detectionAlgorithm, dto.getDetectionAlgorithm());
    }

    @Test
    @DisplayName("Setter for createdBy and updatedBy should work correctly")
    void testCreatedByUpdatedBy_Setters() {
        FraudPatternDto dto = new FraudPatternDto();

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
    @DisplayName("Setter for patternDefinition and metadata should work correctly")
    void testPatternDefinitionMetadata_Setters() {
        Map<String, Object> definition = Map.of("key", "value");
        Map<String, Object> metadata = Map.of("source", "api");
        FraudPatternDto dto = new FraudPatternDto();

        dto.setPatternDefinition(definition);
        assertEquals(definition, dto.getPatternDefinition());

        dto.setMetadata(metadata);
        assertEquals(metadata, dto.getMetadata());

        dto.setPatternDefinition(null);
        assertNull(dto.getPatternDefinition());

        dto.setMetadata(null);
        assertNull(dto.getMetadata());
    }

    @Test
    @DisplayName("Setter for createdAt and updatedAt should work correctly")
    void testCreatedAtUpdatedAt_Setters() {
        LocalDateTime now = LocalDateTime.now();
        FraudPatternDto dto = new FraudPatternDto();

        dto.setCreatedAt(now);
        assertEquals(now, dto.getCreatedAt());

        dto.setUpdatedAt(now.plusHours(1));
        assertEquals(now.plusHours(1), dto.getUpdatedAt());

        dto.setCreatedAt(null);
        assertNull(dto.getCreatedAt());

        dto.setUpdatedAt(null);
        assertNull(dto.getUpdatedAt());
    }
}
