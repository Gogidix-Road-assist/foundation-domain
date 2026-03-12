package com.gogidix.rapidassist.ai.fraud.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.fraud.domain.model.FraudPattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FraudPatternRepositoryAdapter
 */
@DisplayName("FraudPatternRepositoryAdapter Tests")
class FraudPatternRepositoryAdapterTest {

    private FraudPatternRepositoryAdapter adapter;

    private static final String TENANT_ID = "tenant-123";
    private static final UUID TEST_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        adapter = new FraudPatternRepositoryAdapter();
    }

    // ==================== save() tests ====================

    @Test
    @DisplayName("save - Saves pattern successfully")
    void testSave_SavesPatternSuccessfully() {
        FraudPattern pattern = FraudPattern.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .patternName("Geoclustering")
                .patternType("GEOGRAPHIC")
                .isActive(true)
                .build();

        FraudPattern result = adapter.save(TENANT_ID, pattern);

        assertNotNull(result);
        assertEquals(TEST_ID, result.getId());
        assertEquals("Geoclustering", result.getPatternName());
    }

    // ==================== findById() tests ====================

    @Test
    @DisplayName("findById - Returns pattern when found")
    void testFindById_ReturnsPatternWhenFound() {
        FraudPattern pattern = FraudPattern.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .patternName("Velocity Pattern")
                .build();

        adapter.save(TENANT_ID, pattern);

        Optional<FraudPattern> result = adapter.findById(TENANT_ID, TEST_ID);

        assertTrue(result.isPresent());
        assertEquals("Velocity Pattern", result.get().getPatternName());
    }

    @Test
    @DisplayName("findById - Returns empty when not found")
    void testFindById_ReturnsEmptyWhenNotFound() {
        Optional<FraudPattern> result = adapter.findById(TENANT_ID, UUID.randomUUID());

        assertFalse(result.isPresent());
    }

    // ==================== findByTenantId() tests ====================

    @Test
    @DisplayName("findByTenantId - Returns patterns for tenant")
    void testFindByTenantId_ReturnsPatterns() {
        FraudPattern pattern = FraudPattern.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .patternName("Pattern1")
                .build();

        adapter.save(TENANT_ID, pattern);

        List<FraudPattern> result = adapter.findByTenantId(TENANT_ID);

        assertEquals(1, result.size());
        assertEquals(TENANT_ID, result.get(0).getTenantId());
    }

    // ==================== findByPatternType() tests ====================

    @Test
    @DisplayName("findByPatternType - Returns patterns by type")
    void testFindByPatternType_ReturnsPatternsByType() {
        FraudPattern pattern = FraudPattern.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .patternType("GEOGRAPHIC")
                .build();

        adapter.save(TENANT_ID, pattern);

        List<FraudPattern> result = adapter.findByPatternType(TENANT_ID, "GEOGRAPHIC");

        assertEquals(1, result.size());
        assertEquals("GEOGRAPHIC", result.get(0).getPatternType());
    }

    @Test
    @DisplayName("findByPatternType - All pattern types")
    void testFindByPatternType_AllPatternTypes() {
        String[] patternTypes = {
                "GEOGRAPHIC",
                "TEMPORAL",
                "BEHAVIORAL",
                "NETWORK",
                "VELOCITY"
        };

        for (String patternType : patternTypes) {
            UUID id = UUID.randomUUID();
            FraudPattern pattern = FraudPattern.builder()
                    .id(id)
                    .tenantId(TENANT_ID)
                    .patternType(patternType)
                    .build();
            adapter.save(TENANT_ID, pattern);

            List<FraudPattern> result = adapter.findByPatternType(TENANT_ID, patternType);
            assertEquals(1, result.size());
            assertEquals(patternType, result.get(0).getPatternType());
        }
    }

    // ==================== findActivePatterns() tests ====================

    @Test
    @DisplayName("findActivePatterns - Returns active patterns")
    void testFindActivePatterns_ReturnsActivePatterns() {
        FraudPattern pattern = FraudPattern.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .isActive(true)
                .build();

        adapter.save(TENANT_ID, pattern);

        List<FraudPattern> result = adapter.findActivePatterns(TENANT_ID);

        assertEquals(1, result.size());
        assertTrue(result.get(0).getIsActive());
    }

    @Test
    @DisplayName("findActivePatterns - Does not return inactive patterns")
    void testFindActivePatterns_DoesNotReturnInactivePatterns() {
        FraudPattern pattern = FraudPattern.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .isActive(false)
                .build();

        adapter.save(TENANT_ID, pattern);

        List<FraudPattern> result = adapter.findActivePatterns(TENANT_ID);

        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("findActivePatterns - Mixed active and inactive")
    void testFindActivePatterns_MixedActiveAndInactive() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        FraudPattern pattern1 = FraudPattern.builder()
                .id(id1)
                .tenantId(TENANT_ID)
                .isActive(true)
                .build();

        FraudPattern pattern2 = FraudPattern.builder()
                .id(id2)
                .tenantId(TENANT_ID)
                .isActive(false)
                .build();

        adapter.save(TENANT_ID, pattern1);
        adapter.save(TENANT_ID, pattern2);

        List<FraudPattern> result = adapter.findActivePatterns(TENANT_ID);

        assertEquals(1, result.size());
        assertTrue(result.get(0).getIsActive());
        assertEquals(id1, result.get(0).getId());
    }

    // ==================== delete() tests ====================

    @Test
    @DisplayName("delete - Removes pattern")
    void testDelete_RemovesPattern() {
        FraudPattern pattern = FraudPattern.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .patternName("Test Pattern")
                .build();

        adapter.save(TENANT_ID, pattern);
        adapter.delete(TENANT_ID, TEST_ID);

        Optional<FraudPattern> result = adapter.findById(TENANT_ID, TEST_ID);
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("delete - No-op for non-existent pattern")
    void testDelete_NoOpForNonExistentPattern() {
        assertDoesNotThrow(() -> adapter.delete(TENANT_ID, UUID.randomUUID()));
    }

    // ==================== exists() tests ====================

    @Test
    @DisplayName("exists - Returns true when pattern exists")
    void testExists_ReturnsTrueWhenExists() {
        FraudPattern pattern = FraudPattern.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .build();

        adapter.save(TENANT_ID, pattern);

        assertTrue(adapter.exists(TENANT_ID, TEST_ID));
    }

    @Test
    @DisplayName("exists - Returns false when pattern not found")
    void testExists_ReturnsFalseWhenNotFound() {
        assertFalse(adapter.exists(TENANT_ID, UUID.randomUUID()));
    }

    // ==================== Pattern details tests ====================

    @Test
    @DisplayName("Pattern details - All fields mapped")
    void testPatternDetails_AllFieldsMapped() {
        Map<String, Object> definition = Map.of("threshold", 3);
        Map<String, Object> metadata = Map.of("key", "value");

        FraudPattern pattern = FraudPattern.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .patternName("Complex Pattern")
                .patternType("COMPOSITE")
                .description("Complex fraud pattern")
                .patternDefinition(definition)
                .detectionAlgorithm("ML")
                .priority(1)
                .isActive(true)
                .confidenceThreshold(0.85)
                .detectionCount(100)
                .falsePositiveCount(10)
                .truePositiveCount(90)
                .precision(0.90)
                .recall(0.88)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .metadata(metadata)
                .build();

        adapter.save(TENANT_ID, pattern);

        Optional<FraudPattern> result = adapter.findById(TENANT_ID, TEST_ID);

        assertTrue(result.isPresent());
        assertEquals("Complex Pattern", result.get().getPatternName());
        assertEquals("COMPOSITE", result.get().getPatternType());
        assertEquals(definition, result.get().getPatternDefinition());
        assertEquals("ML", result.get().getDetectionAlgorithm());
        assertEquals(1, result.get().getPriority());
        assertTrue(result.get().getIsActive());
        assertEquals(0.85, result.get().getConfidenceThreshold());
    }

    @Test
    @DisplayName("Pattern details - Precision and recall")
    void testPatternDetails_PrecisionAndRecall() {
        FraudPattern pattern = FraudPattern.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .truePositiveCount(95)
                .falsePositiveCount(5)
                .precision(0.95)
                .recall(0.88)
                .build();

        adapter.save(TENANT_ID, pattern);

        Optional<FraudPattern> result = adapter.findById(TENANT_ID, TEST_ID);

        assertTrue(result.isPresent());
        assertEquals(95, result.get().getTruePositiveCount());
        assertEquals(5, result.get().getFalsePositiveCount());
        assertEquals(0.95, result.get().getPrecision());
        assertEquals(0.88, result.get().getRecall());
    }

    // ==================== Multi-tenant tests ====================

    @Test
    @DisplayName("Multi-tenant - No cross-tenant leakage")
    void testMultitenancy_NoCrossTenantLeakage() {
        String tenant1 = "tenant-1";
        String tenant2 = "tenant-2";

        FraudPattern pattern1 = FraudPattern.builder()
                .id(TEST_ID)
                .tenantId(tenant1)
                .patternName("Pattern1")
                .build();

        FraudPattern pattern2 = FraudPattern.builder()
                .id(UUID.randomUUID())
                .tenantId(tenant2)
                .patternName("Pattern2")
                .build();

        adapter.save(tenant1, pattern1);
        adapter.save(tenant2, pattern2);

        List<FraudPattern> tenant1Results = adapter.findByTenantId(tenant1);
        List<FraudPattern> tenant2Results = adapter.findByTenantId(tenant2);

        assertEquals(1, tenant1Results.size());
        assertEquals(1, tenant2Results.size());
        assertEquals(tenant1, tenant1Results.get(0).getTenantId());
        assertEquals(tenant2, tenant2Results.get(0).getTenantId());
    }

    // ==================== Priority tests ====================

    @Test
    @DisplayName("Priority - Various priority levels")
    void testPriority_VariousPriorityLevels() {
        for (int priority = 0; priority <= 10; priority++) {
            UUID id = UUID.randomUUID();
            FraudPattern pattern = FraudPattern.builder()
                    .id(id)
                    .tenantId(TENANT_ID)
                    .priority(priority)
                    .build();
            adapter.save(TENANT_ID, pattern);

            Optional<FraudPattern> result = adapter.findById(TENANT_ID, id);
            assertTrue(result.isPresent());
            assertEquals(priority, result.get().getPriority());
        }
    }

    // ==================== Active/Inactive tests ====================

    @Test
    @DisplayName("Active status - Can be toggled")
    void testActiveStatus_CanBeToggled() {
        UUID patternId = UUID.randomUUID();
        FraudPattern pattern = FraudPattern.builder()
                .id(patternId)
                .tenantId(TENANT_ID)
                .patternName("Test Pattern")
                .isActive(true)
                .build();

        adapter.save(TENANT_ID, pattern);

        // Create updated pattern with same ID but inactive status
        FraudPattern updated = FraudPattern.builder()
                .id(patternId)
                .tenantId(TENANT_ID)
                .patternName("Test Pattern")
                .isActive(false)
                .build();
        adapter.save(TENANT_ID, updated);

        List<FraudPattern> activeResult = adapter.findActivePatterns(TENANT_ID);
        assertEquals(0, activeResult.size());
    }

    // ==================== Edge cases ====================

    @Test
    @DisplayName("Edge case - Empty pattern name")
    void testEdgeCase_EmptyPatternName() {
        FraudPattern pattern = FraudPattern.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .patternName("")
                .build();

        adapter.save(TENANT_ID, pattern);

        Optional<FraudPattern> result = adapter.findById(TENANT_ID, TEST_ID);
        assertTrue(result.isPresent());
        assertEquals("", result.get().getPatternName());
    }

    @Test
    @DisplayName("Edge case - Zero priority")
    void testEdgeCase_ZeroPriority() {
        FraudPattern pattern = FraudPattern.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .priority(0)
                .build();

        adapter.save(TENANT_ID, pattern);

        Optional<FraudPattern> result = adapter.findById(TENANT_ID, TEST_ID);
        assertTrue(result.isPresent());
        assertEquals(0, result.get().getPriority());
    }

    @Test
    @DisplayName("Edge case - Negative priority")
    void testEdgeCase_NegativePriority() {
        FraudPattern pattern = FraudPattern.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .priority(-1)
                .build();

        adapter.save(TENANT_ID, pattern);

        Optional<FraudPattern> result = adapter.findById(TENANT_ID, TEST_ID);
        assertTrue(result.isPresent());
        assertEquals(-1, result.get().getPriority());
    }
}
