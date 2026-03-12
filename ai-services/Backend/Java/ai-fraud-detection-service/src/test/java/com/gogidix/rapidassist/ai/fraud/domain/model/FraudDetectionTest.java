package com.gogidix.rapidassist.ai.fraud.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FraudDetection domain model.
 * Tests business logic, builder pattern, and multi-tenancy fields.
 * Target Coverage: 95%
 */
@DisplayName("FraudDetection Domain Model Tests")
class FraudDetectionTest {

    private static final String TEST_TENANT_ID = "tenant-123";
    private static final String TEST_ENTITY_TYPE = "CLAIM";
    private static final String TEST_ENTITY_ID = "claim-456";

    @Test
    @DisplayName("Builder should create valid FraudDetection instance")
    void testBuilder_ValidConstruction() {
        UUID id = UUID.randomUUID();
        Map<String, Object> details = new HashMap<>();
        details.put("confidence", 0.95);

        FraudDetection detection = FraudDetection.builder()
                .id(id)
                .tenantId(TEST_TENANT_ID)
                .entityType(TEST_ENTITY_TYPE)
                .entityId(TEST_ENTITY_ID)
                .riskLevel(FraudRiskLevel.HIGH)
                .riskScore(0.85)
                .detectionMethod("ML_MODEL")
                .detectionDetails(details)
                .detectedPatterns(new java.util.ArrayList<>())
                .status("PENDING")
                .requiresReview(true)
                .detectedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        assertNotNull(detection);
        assertEquals(id, detection.getId());
        assertEquals(TEST_TENANT_ID, detection.getTenantId());
        assertEquals(TEST_ENTITY_TYPE, detection.getEntityType());
        assertEquals(TEST_ENTITY_ID, detection.getEntityId());
        assertEquals(FraudRiskLevel.HIGH, detection.getRiskLevel());
        assertEquals(0.85, detection.getRiskScore());
        assertEquals("ML_MODEL", detection.getDetectionMethod());
        assertEquals("PENDING", detection.getStatus());
        assertTrue(detection.getRequiresReview());
        assertNotNull(detection.getDetectedPatterns());
        assertNotNull(detection.getDetectionDetails());
    }

    @Test
    @DisplayName("isHighRisk should return true for HIGH risk level")
    void testIsHighRisk_HIGH_RiskLevel() {
        FraudDetection detection = FraudDetection.builder()
                .riskLevel(FraudRiskLevel.HIGH)
                .build();

        assertTrue(detection.isHighRisk());
    }

    @Test
    @DisplayName("isHighRisk should return true for CRITICAL risk level")
    void testIsHighRisk_CRITICAL_RiskLevel() {
        FraudDetection detection = FraudDetection.builder()
                .riskLevel(FraudRiskLevel.CRITICAL)
                .build();

        assertTrue(detection.isHighRisk());
    }

    @Test
    @DisplayName("isHighRisk should return false for MEDIUM risk level")
    void testIsHighRisk_MEDIUM_RiskLevel() {
        FraudDetection detection = FraudDetection.builder()
                .riskLevel(FraudRiskLevel.MEDIUM)
                .build();

        assertFalse(detection.isHighRisk());
    }

    @Test
    @DisplayName("isHighRisk should return false for LOW risk level")
    void testIsHighRisk_LOW_RiskLevel() {
        FraudDetection detection = FraudDetection.builder()
                .riskLevel(FraudRiskLevel.LOW)
                .build();

        assertFalse(detection.isHighRisk());
    }

    @Test
    @DisplayName("requiresImmediateAction should return true for CRITICAL risk level")
    void testRequiresImmediateAction_CRITICAL() {
        FraudDetection detection = FraudDetection.builder()
                .riskLevel(FraudRiskLevel.CRITICAL)
                .requiresReview(false)
                .build();

        assertTrue(detection.requiresImmediateAction());
    }

    @Test
    @DisplayName("requiresImmediateAction should return true when requiresReview is true")
    void testRequiresImmediateAction_RequiresReviewTrue() {
        FraudDetection detection = FraudDetection.builder()
                .riskLevel(FraudRiskLevel.MEDIUM)
                .requiresReview(true)
                .build();

        assertTrue(detection.requiresImmediateAction());
    }

    @Test
    @DisplayName("requiresImmediateAction should return false for non-critical without review flag")
    void testRequiresImmediateAction_NotRequired() {
        FraudDetection detection = FraudDetection.builder()
                .riskLevel(FraudRiskLevel.LOW)
                .requiresReview(false)
                .build();

        assertFalse(detection.requiresImmediateAction());
    }

    @Test
    @DisplayName("markAsReviewed should update status, reviewer, notes, and timestamps")
    void testMarkAsReviewed_UpdatesFields() {
        String reviewer = "admin-user";
        String notes = "Verified as false positive";

        FraudDetection detection = FraudDetection.builder()
                .status("PENDING")
                .build();

        detection.markAsReviewed(reviewer, notes);

        assertEquals("REVIEWED", detection.getStatus());
        assertEquals(reviewer, detection.getReviewedBy());
        assertEquals(notes, detection.getReviewNotes());
        assertNotNull(detection.getReviewedAt());
        assertNotNull(detection.getUpdatedAt());
    }

    @Test
    @DisplayName("assignTo should update assignedTo field and updatedAt timestamp")
    void testAssignTo_UpdatesAssignedTo() {
        String reviewerId = "reviewer-123";
        LocalDateTime beforeUpdate = LocalDateTime.now();

        FraudDetection detection = FraudDetection.builder()
                .assignedTo(null)
                .build();

        detection.assignTo(reviewerId);

        assertEquals(reviewerId, detection.getAssignedTo());
        assertInstanceOf(LocalDateTime.class, detection.getUpdatedAt());
    }

    @Test
    @DisplayName("getAgeInHours should calculate correct age when detectedAt is set")
    void testGetAgeInHours_WithValidDetectedAt() {
        LocalDateTime detectedAt = LocalDateTime.now().minusHours(5);

        FraudDetection detection = FraudDetection.builder()
                .detectedAt(detectedAt)
                .build();

        long age = detection.getAgeInHours();

        assertTrue(age >= 4 && age <= 6, "Age should be approximately 5 hours");
    }

    @Test
    @DisplayName("getAgeInHours should return 0 when detectedAt is null")
    void testGetAgeInHours_WithNullDetectedAt() {
        FraudDetection detection = FraudDetection.builder()
                .detectedAt(null)
                .build();

        assertEquals(0, detection.getAgeInHours());
    }

    @Test
    @DisplayName("FraudDetection should have tenantId field for multi-tenancy")
    void testTenantId_FieldPresent() {
        FraudDetection detection = FraudDetection.builder()
                .tenantId(TEST_TENANT_ID)
                .build();

        assertEquals(TEST_TENANT_ID, detection.getTenantId());
        assertNotNull(detection.getTenantId());
    }

    @Test
    @DisplayName("NoArgsConstructor should create instance with null fields")
    void testNoArgsConstructor_CreatesEmptyInstance() {
        FraudDetection detection = new FraudDetection();

        assertNotNull(detection);
        assertNull(detection.getId());
        assertNull(detection.getTenantId());
        assertNull(detection.getEntityType());
        assertNull(detection.getRiskLevel());
    }

    @Test
    @DisplayName("AllArgsConstructor should create instance with all fields")
    void testAllArgsConstructor_CreatesCompleteInstance() {
        UUID id = UUID.randomUUID();
        Map<String, Object> metadata = new HashMap<>();

        FraudDetection detection = new FraudDetection(
                id,
                TEST_TENANT_ID,
                TEST_ENTITY_TYPE,
                TEST_ENTITY_ID,
                FraudRiskLevel.HIGH,
                0.85,
                "ML_MODEL",
                metadata,
                new java.util.ArrayList<>(),
                "PENDING",
                true,
                "admin",
                LocalDateTime.now(),
                LocalDateTime.now(),
                "admin",
                "notes",
                metadata,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        assertEquals(id, detection.getId());
        assertEquals(TEST_TENANT_ID, detection.getTenantId());
        assertEquals(FraudRiskLevel.HIGH, detection.getRiskLevel());
    }

    @Test
    @DisplayName("Lombok @Data should generate proper equals and hashCode")
    void testEqualsAndHashCode() {
        UUID id = UUID.randomUUID();

        FraudDetection detection1 = FraudDetection.builder()
                .id(id)
                .tenantId(TEST_TENANT_ID)
                .build();

        FraudDetection detection2 = FraudDetection.builder()
                .id(id)
                .tenantId(TEST_TENANT_ID)
                .build();

        assertEquals(detection1, detection2);
        assertEquals(detection1.hashCode(), detection2.hashCode());
    }

    @Test
    @DisplayName("Lombok @Data should generate proper toString")
    void testToString() {
        FraudDetection detection = FraudDetection.builder()
                .id(UUID.randomUUID())
                .tenantId(TEST_TENANT_ID)
                .entityType(TEST_ENTITY_TYPE)
                .build();

        String toString = detection.toString();

        assertTrue(toString.contains("FraudDetection"));
        assertTrue(toString.contains(TEST_TENANT_ID) || toString.contains(TEST_ENTITY_TYPE));
    }

    // ========== Mutation-Killing Tests: All Field Combinations ==========

    @Test
    @DisplayName("All getters should return values set via builder")
    void testAllGetters_ReturnBuilderValues() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> details = Map.of("key", "value");
        Map<String, Object> metadata = Map.of("meta", "value");
        java.util.List<String> patterns = List.of("pattern1", "pattern2");

        FraudDetection detection = FraudDetection.builder()
                .id(id)
                .tenantId(TEST_TENANT_ID)
                .entityType(TEST_ENTITY_TYPE)
                .entityId(TEST_ENTITY_ID)
                .riskLevel(FraudRiskLevel.HIGH)
                .riskScore(0.85)
                .detectionMethod("ML_MODEL")
                .detectionDetails(details)
                .detectedPatterns(patterns)
                .status("PENDING")
                .requiresReview(true)
                .assignedTo("admin")
                .detectedAt(now)
                .reviewedBy("reviewer")
                .reviewedAt(now.plusHours(1))
                .reviewNotes("Confirmed")
                .metadata(metadata)
                .createdAt(now)
                .updatedAt(now.plusHours(1))
                .build();

        assertEquals(id, detection.getId());
        assertEquals(TEST_TENANT_ID, detection.getTenantId());
        assertEquals(TEST_ENTITY_TYPE, detection.getEntityType());
        assertEquals(TEST_ENTITY_ID, detection.getEntityId());
        assertEquals(FraudRiskLevel.HIGH, detection.getRiskLevel());
        assertEquals(0.85, detection.getRiskScore());
        assertEquals("ML_MODEL", detection.getDetectionMethod());
        assertEquals(details, detection.getDetectionDetails());
        assertEquals(patterns, detection.getDetectedPatterns());
        assertEquals("PENDING", detection.getStatus());
        assertTrue(detection.getRequiresReview());
        assertEquals("admin", detection.getAssignedTo());
        assertEquals(now, detection.getDetectedAt());
        assertEquals("reviewer", detection.getReviewedBy());
        assertEquals("Confirmed", detection.getReviewNotes());
        assertEquals(metadata, detection.getMetadata());
        assertEquals(now, detection.getCreatedAt());
        assertEquals(now.plusHours(1), detection.getUpdatedAt());
    }

    @Test
    @DisplayName("All risk levels should be correctly set")
    void testAllRiskLevels() {
        FraudRiskLevel[] levels = {
            FraudRiskLevel.LOW,
            FraudRiskLevel.MEDIUM,
            FraudRiskLevel.HIGH,
            FraudRiskLevel.CRITICAL
        };

        for (FraudRiskLevel level : levels) {
            FraudDetection detection = FraudDetection.builder()
                    .riskLevel(level)
                    .build();

            assertEquals(level, detection.getRiskLevel());
        }
    }

    @Test
    @DisplayName("Status transitions")
    void testStatusTransitions() {
        FraudDetection detection = FraudDetection.builder()
                .status("PENDING")
                .build();

        assertEquals("PENDING", detection.getStatus());

        detection.markAsReviewed("reviewer", "notes");
        assertEquals("REVIEWED", detection.getStatus());
    }

    @Test
    @DisplayName("Detected patterns list should be modifiable")
    void testDetectedPatterns_Modifiable() {
        java.util.List<String> patterns = new java.util.ArrayList<>();
        patterns.add("pattern1");

        FraudDetection detection = FraudDetection.builder()
                .detectedPatterns(patterns)
                .build();

        assertEquals(1, detection.getDetectedPatterns().size());

        detection.getDetectedPatterns().add("pattern2");
        assertEquals(2, detection.getDetectedPatterns().size());
    }

    @Test
    @DisplayName("Detection details map should be modifiable")
    void testDetectionDetails_Modifiable() {
        Map<String, Object> details = new HashMap<>();
        details.put("key1", "value1");

        FraudDetection detection = FraudDetection.builder()
                .detectionDetails(details)
                .build();

        assertEquals(1, detection.getDetectionDetails().size());

        detection.getDetectionDetails().put("key2", "value2");
        assertEquals(2, detection.getDetectionDetails().size());
    }

    @Test
    @DisplayName("Metadata map should be modifiable")
    void testMetadata_Modifiable() {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("meta1", "value1");

        FraudDetection detection = FraudDetection.builder()
                .metadata(metadata)
                .build();

        assertEquals(1, detection.getMetadata().size());

        detection.getMetadata().put("meta2", "value2");
        assertEquals(2, detection.getMetadata().size());
    }

    @Test
    @DisplayName("assignTo should update assignedTo and updatedAt")
    void testAssignTo_UpdatesAllFields() {
        LocalDateTime beforeAssign = LocalDateTime.now();

        FraudDetection detection = FraudDetection.builder()
                .assignedTo(null)
                .updatedAt(beforeAssign)
                .build();

        detection.assignTo("new-assignee");

        assertEquals("new-assignee", detection.getAssignedTo());
        assertNotNull(detection.getUpdatedAt());
        assertTrue(detection.getUpdatedAt().isAfter(beforeAssign) ||
                   detection.getUpdatedAt().isEqual(beforeAssign));
    }

    @Test
    @DisplayName("markAsReviewed with all parameters")
    void testMarkAsReviewed_AllParameters() {
        LocalDateTime beforeReview = LocalDateTime.now();
        String reviewer = "reviewer-1";
        String notes = "Review notes";

        FraudDetection detection = FraudDetection.builder()
                .status("PENDING")
                .reviewedBy(null)
                .reviewedAt(null)
                .reviewNotes(null)
                .updatedAt(beforeReview)
                .build();

        detection.markAsReviewed(reviewer, notes);

        assertEquals("REVIEWED", detection.getStatus());
        assertEquals(reviewer, detection.getReviewedBy());
        assertEquals(notes, detection.getReviewNotes());
        assertNotNull(detection.getReviewedAt());
        assertNotNull(detection.getUpdatedAt());
    }

    @Test
    @DisplayName("getAgeInHours should handle edge cases")
    void testGetAgeInHours_EdgeCases() {
        LocalDateTime now = LocalDateTime.now();

        FraudDetection nullDetection = FraudDetection.builder()
                .detectedAt(null)
                .build();
        assertEquals(0, nullDetection.getAgeInHours());

        FraudDetection recentDetection = FraudDetection.builder()
                .detectedAt(now.minusMinutes(30))
                .build();
        assertTrue(recentDetection.getAgeInHours() >= 0);

        FraudDetection oldDetection = FraudDetection.builder()
                .detectedAt(now.minusDays(7))
                .build();
        assertTrue(oldDetection.getAgeInHours() >= 167);
    }

    @Test
    @DisplayName("isHighRisk for all risk levels")
    void testIsHighRisk_AllLevels() {
        FraudDetection low = FraudDetection.builder()
                .riskLevel(FraudRiskLevel.LOW)
                .build();
        assertFalse(low.isHighRisk());

        FraudDetection medium = FraudDetection.builder()
                .riskLevel(FraudRiskLevel.MEDIUM)
                .build();
        assertFalse(medium.isHighRisk());

        FraudDetection high = FraudDetection.builder()
                .riskLevel(FraudRiskLevel.HIGH)
                .build();
        assertTrue(high.isHighRisk());

        FraudDetection critical = FraudDetection.builder()
                .riskLevel(FraudRiskLevel.CRITICAL)
                .build();
        assertTrue(critical.isHighRisk());
    }

    @Test
    @DisplayName("Equals with different combinations")
    void testEquals_DifferentCombinations() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        FraudDetection detection1 = FraudDetection.builder()
                .id(id1)
                .tenantId("tenant-1")
                .build();

        FraudDetection detection2 = FraudDetection.builder()
                .id(id1)
                .tenantId("tenant-1")
                .build();

        FraudDetection detection3 = FraudDetection.builder()
                .id(id2)
                .tenantId("tenant-1")
                .build();

        assertEquals(detection1, detection2);
        assertEquals(detection1.hashCode(), detection2.hashCode());
        assertNotEquals(detection1, detection3);
    }

    @Test
    @DisplayName("toString should contain key fields")
    void testToString_ContainsKeyFields() {
        UUID id = UUID.randomUUID();

        FraudDetection detection = FraudDetection.builder()
                .id(id)
                .tenantId(TEST_TENANT_ID)
                .entityType(TEST_ENTITY_TYPE)
                .entityId(TEST_ENTITY_ID)
                .riskLevel(FraudRiskLevel.HIGH)
                .status("PENDING")
                .build();

        String str = detection.toString();

        assertTrue(str.contains("FraudDetection"));
        assertTrue(str.contains(TEST_TENANT_ID));
        assertTrue(str.contains(TEST_ENTITY_TYPE));
        assertTrue(str.contains(TEST_ENTITY_ID));
    }

    // ========== Mutation-Killing Tests: Age Calculation ==========

    @Test
    @DisplayName("getAgeInHours should calculate correctly for various ages")
    void testGetAgeInHours_VariousAges() {
        // 1 hour old
        FraudDetection detection1 = FraudDetection.builder()
                .detectedAt(LocalDateTime.now().minusHours(1))
                .build();
        assertEquals(1, detection1.getAgeInHours());

        // 5 hours old
        FraudDetection detection2 = FraudDetection.builder()
                .detectedAt(LocalDateTime.now().minusHours(5))
                .build();
        assertEquals(5, detection2.getAgeInHours());

        // 24 hours old
        FraudDetection detection3 = FraudDetection.builder()
                .detectedAt(LocalDateTime.now().minusHours(24))
                .build();
        assertEquals(24, detection3.getAgeInHours());

        // 48 hours old
        FraudDetection detection4 = FraudDetection.builder()
                .detectedAt(LocalDateTime.now().minusHours(48))
                .build();
        assertEquals(48, detection4.getAgeInHours());
    }

    @Test
    @DisplayName("getAgeInHours should return 0 when detectedAt is null")
    void testGetAgeInHours_NullDetectedAt() {
        FraudDetection detection = FraudDetection.builder()
                .detectedAt(null)
                .build();

        assertEquals(0, detection.getAgeInHours());
    }

    @Test
    @DisplayName("getAgeInHours should return 0 for newly created detection")
    void testGetAgeInHours_NewlyCreated() {
        LocalDateTime justNow = LocalDateTime.now().minusMinutes(10);
        FraudDetection detection = FraudDetection.builder()
                .detectedAt(justNow)
                .build();

        assertEquals(0, detection.getAgeInHours());
    }

    // ========== Mutation-Killing Tests: Numeric Comparisons ==========

    @Test
    @DisplayName("isHighRisk should return correct value for all risk levels")
    void testIsHighRisk_AllRiskLevels() {
        FraudDetection highRisk = FraudDetection.builder()
                .riskLevel(FraudRiskLevel.HIGH)
                .build();
        assertTrue(highRisk.isHighRisk());

        FraudDetection criticalRisk = FraudDetection.builder()
                .riskLevel(FraudRiskLevel.CRITICAL)
                .build();
        assertTrue(criticalRisk.isHighRisk());

        FraudDetection mediumRisk = FraudDetection.builder()
                .riskLevel(FraudRiskLevel.MEDIUM)
                .build();
        assertFalse(mediumRisk.isHighRisk());

        FraudDetection lowRisk = FraudDetection.builder()
                .riskLevel(FraudRiskLevel.LOW)
                .build();
        assertFalse(lowRisk.isHighRisk());

        FraudDetection nullRisk = FraudDetection.builder()
                .riskLevel(null)
                .build();
        assertFalse(nullRisk.isHighRisk());
    }

    @Test
    @DisplayName("requiresImmediateAction should return correct value for all combinations")
    void testRequiresImmediateAction_AllCombinations() {
        // CRITICAL with requiresReview=true
        FraudDetection crit1 = FraudDetection.builder()
                .riskLevel(FraudRiskLevel.CRITICAL)
                .requiresReview(true)
                .build();
        assertTrue(crit1.requiresImmediateAction());

        // CRITICAL with requiresReview=false
        FraudDetection crit2 = FraudDetection.builder()
                .riskLevel(FraudRiskLevel.CRITICAL)
                .requiresReview(false)
                .build();
        assertTrue(crit2.requiresImmediateAction());

        // HIGH with requiresReview=true
        FraudDetection high1 = FraudDetection.builder()
                .riskLevel(FraudRiskLevel.HIGH)
                .requiresReview(true)
                .build();
        assertTrue(high1.requiresImmediateAction());

        // HIGH with requiresReview=false
        FraudDetection high2 = FraudDetection.builder()
                .riskLevel(FraudRiskLevel.HIGH)
                .requiresReview(false)
                .build();
        assertFalse(high2.requiresImmediateAction());

        // MEDIUM with requiresReview=true
        FraudDetection medium = FraudDetection.builder()
                .riskLevel(FraudRiskLevel.MEDIUM)
                .requiresReview(true)
                .build();
        assertTrue(medium.requiresImmediateAction());

        // MEDIUM with requiresReview=false
        FraudDetection medium2 = FraudDetection.builder()
                .riskLevel(FraudRiskLevel.MEDIUM)
                .requiresReview(false)
                .build();
        assertFalse(medium2.requiresImmediateAction());

        // LOW with requiresReview=true
        FraudDetection low = FraudDetection.builder()
                .riskLevel(FraudRiskLevel.LOW)
                .requiresReview(true)
                .build();
        assertTrue(low.requiresImmediateAction());

        // LOW with requiresReview=false
        FraudDetection low2 = FraudDetection.builder()
                .riskLevel(FraudRiskLevel.LOW)
                .requiresReview(false)
                .build();
        assertFalse(low2.requiresImmediateAction());
    }

    // ========== Mutation-Killing Tests: Numeric Field Operations ==========

    @Test
    @DisplayName("riskScore field should handle various numeric values")
    void testRiskScore_NumericValues() {
        FraudDetection detection1 = FraudDetection.builder()
                .riskScore(0.0)
                .build();
        assertEquals(0.0, detection1.getRiskScore());

        FraudDetection detection2 = FraudDetection.builder()
                .riskScore(0.5)
                .build();
        assertEquals(0.5, detection2.getRiskScore());

        FraudDetection detection3 = FraudDetection.builder()
                .riskScore(1.0)
                .build();
        assertEquals(1.0, detection3.getRiskScore());

        FraudDetection detection4 = FraudDetection.builder()
                .riskScore(0.95)
                .build();
        assertEquals(0.95, detection4.getRiskScore());

        FraudDetection detection5 = FraudDetection.builder()
                .riskScore(0.01)
                .build();
        assertEquals(0.01, detection5.getRiskScore());
    }

    // ========== Mutation-Killing Tests: hashCode() and equals() ==========

    @Test
    @DisplayName("hashCode - Objects with same ID should have same hashCode")
    void testHashCode_SameId_SameHashCode() {
        UUID id = UUID.randomUUID();
        FraudDetection detection1 = FraudDetection.builder().id(id).build();
        FraudDetection detection2 = FraudDetection.builder().id(id).build();

        // This kills Math mutants in hashCode()
        assertEquals(detection1.hashCode(), detection2.hashCode());
    }

    @Test
    @DisplayName("hashCode - Objects with different IDs should have different hashCodes")
    void testHashCode_DifferentId_DifferentHashCode() {
        FraudDetection detection1 = FraudDetection.builder().id(UUID.randomUUID()).build();
        FraudDetection detection2 = FraudDetection.builder().id(UUID.randomUUID()).build();

        // This kills Math mutants in hashCode()
        assertNotEquals(detection1.hashCode(), detection2.hashCode());
    }

    @Test
    @DisplayName("HashSet should work correctly with FraudDetection")
    void testHashSet_UsesHashCode() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        FraudDetection detection1 = FraudDetection.builder().id(id1).entityId("entity-001").build();
        FraudDetection detection2 = FraudDetection.builder().id(id2).entityId("entity-002").build();
        FraudDetection detection1Duplicate = FraudDetection.builder().id(id1).entityId("entity-001-DUP").build();

        java.util.HashSet<FraudDetection> set = new java.util.HashSet<>();
        assertTrue(set.add(detection1));
        assertTrue(set.add(detection2));
        assertFalse(set.add(detection1Duplicate)); // Should not add - same ID as detection1

        assertEquals(2, set.size());
        assertTrue(set.contains(detection1));
        assertTrue(set.contains(detection2));
    }

    @Test
    @DisplayName("HashMap should work correctly with FraudDetection as key")
    void testHashMap_UsesHashCodeAndEquals() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        FraudDetection key1 = FraudDetection.builder().id(id1).build();
        FraudDetection key2 = FraudDetection.builder().id(id2).build();
        FraudDetection key1Duplicate = FraudDetection.builder().id(id1).build();

        java.util.Map<FraudDetection, String> map = new java.util.HashMap<>();
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
        FraudDetection detection = FraudDetection.builder().id(UUID.randomUUID()).build();
        // This kills NegateConditionalsMutator in equals()
        assertEquals(detection, detection);
    }

    @Test
    @DisplayName("equals - Null comparison")
    void testEquals_NullComparison() {
        FraudDetection detection = FraudDetection.builder().id(UUID.randomUUID()).build();
        // This kills NegateConditionalsMutator in equals()
        assertNotEquals(null, detection);
    }

    @Test
    @DisplayName("equals - Different type comparison")
    void testEquals_DifferentType() {
        FraudDetection detection = FraudDetection.builder().id(UUID.randomUUID()).build();
        String other = "Not a FraudDetection";
        // This kills NegateConditionalsMutator in equals()
        assertNotEquals(detection, other);
    }

    @Test
    @DisplayName("equals - All fields same except ID")
    void testEquals_AllFieldsSameExceptId() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        FraudDetection detection1 = FraudDetection.builder()
                .id(id1)
                .entityType("CLAIM")
                .entityId("claim-123")
                .build();

        FraudDetection detection2 = FraudDetection.builder()
                .id(id2)
                .entityType("CLAIM")
                .entityId("claim-123")
                .build();

        // This kills NegateConditionalsMutator in equals()
        assertNotEquals(detection1, detection2);
    }

    @Test
    @DisplayName("equals - Both IDs null")
    void testEquals_BothIdsNull() {
        FraudDetection detection1 = FraudDetection.builder().build();
        FraudDetection detection2 = FraudDetection.builder().build();

        assertEquals(detection1, detection2);
        assertEquals(detection1.hashCode(), detection2.hashCode());
    }

    @Test
    @DisplayName("equals - One ID null")
    void testEquals_OneIdNull() {
        UUID id = UUID.randomUUID();
        FraudDetection detection1 = FraudDetection.builder().id(id).build();
        FraudDetection detection2 = FraudDetection.builder().id(null).build();

        assertNotEquals(detection1, detection2);
    }

    @Test
    @DisplayName("canEqual - Same class should return true")
    void testCanEqual_SameClass() {
        FraudDetection detection = FraudDetection.builder().build();
        FraudDetection other = FraudDetection.builder().build();

        // This kills BooleanReturnValsMutator in canEqual()
        assertTrue(detection.canEqual(other));
    }

    @Test
    @DisplayName("canEqual - Different class should return false")
    void testCanEqual_DifferentClass() {
        FraudDetection detection = FraudDetection.builder().build();
        String other = "Not a FraudDetection";

        // This kills BooleanReturnValsMutator in canEqual()
        assertFalse(detection.canEqual(other));
    }

    @Test
    @DisplayName("canEqual - Null should return false")
    void testCanEqual_Null() {
        FraudDetection detection = FraudDetection.builder().build();

        // This kills BooleanReturnValsMutator in canEqual()
        assertFalse(detection.canEqual(null));
    }

    @Test
    @DisplayName("equals - CanEqual check between different types")
    void testEquals_WithCanEqualCheck() {
        UUID id = UUID.randomUUID();
        FraudDetection detection = FraudDetection.builder().id(id).build();
        FraudAlert otherAlert = FraudAlert.builder().id(id).alertType("ALERT").build();

        // This tests canEqual within equals
        assertNotEquals(detection, otherAlert);
    }

    @Test
    @DisplayName("HashSet contains and remove operations")
    void testHashSet_ContainsAndRemove() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        FraudDetection detection1 = FraudDetection.builder().id(id1).entityId("entity-001").build();
        FraudDetection detection2 = FraudDetection.builder().id(id2).entityId("entity-002").build();

        java.util.HashSet<FraudDetection> set = new java.util.HashSet<>();
        set.add(detection1);
        set.add(detection2);

        // Test contains - uses hashCode() and equals()
        assertTrue(set.contains(detection1));
        assertTrue(set.contains(detection2));

        // Create duplicate with same ID
        FraudDetection detection1Dup = FraudDetection.builder().id(id1).entityId("entity-001-DUP").build();
        assertTrue(set.contains(detection1Dup));

        // Test remove - uses hashCode() and equals()
        assertTrue(set.remove(detection1Dup));
        assertEquals(1, set.size());
        assertFalse(set.contains(detection1));
    }

    @Test
    @DisplayName("hashCode consistency across multiple calls")
    void testHashCode_Consistency() {
        FraudDetection detection = FraudDetection.builder()
                .id(UUID.randomUUID())
                .entityType("CLAIM")
                .build();

        int hash1 = detection.hashCode();
        int hash2 = detection.hashCode();
        int hash3 = detection.hashCode();

        assertEquals(hash1, hash2);
        assertEquals(hash2, hash3);
    }

    @Test
    @DisplayName("hashCode with null fields")
    void testHashCode_NullFields() {
        UUID id = UUID.randomUUID();
        FraudDetection detection1 = FraudDetection.builder()
                .id(id)
                .entityType(null)
                .build();

        FraudDetection detection2 = FraudDetection.builder()
                .id(id)
                .entityType(null)
                .build();

        assertEquals(detection1.hashCode(), detection2.hashCode());
    }
}
