package com.gogidix.rapidassist.ai.fraud.application.dto;

import com.gogidix.rapidassist.ai.fraud.domain.model.FraudRiskLevel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for FraudDetectionDto.
 *
 * Tests:
 * - Builder patterns
 * - Field access
 * - Null constraints
 * - Default values
 * - Boundary values
 *
 * Target: DTO coverage ≥ 85%
 */
@DisplayName("FraudDetectionDto Tests")
class FraudDetectionDtoTest {

    // ==================== Builder Pattern Tests ====================

    @Test
    @DisplayName("builder should create instance with all fields")
    void testBuilder_AllFields() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> details = Map.of("key", "value");
        List<String> patterns = List.of("pattern1", "pattern2");

        FraudDetectionDto dto = FraudDetectionDto.builder()
                .id(id)
                .tenantId("tenant-1")
                .entityType("CLAIM")
                .entityId("claim-123")
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
                .metadata(Map.of("source", "api"))
                .createdAt(now)
                .updatedAt(now.plusHours(1))
                .build();

        assertEquals(id, dto.getId());
        assertEquals("tenant-1", dto.getTenantId());
        assertEquals("CLAIM", dto.getEntityType());
        assertEquals("claim-123", dto.getEntityId());
        assertEquals(FraudRiskLevel.HIGH, dto.getRiskLevel());
        assertEquals(0.85, dto.getRiskScore());
        assertEquals("ML_MODEL", dto.getDetectionMethod());
        assertEquals(details, dto.getDetectionDetails());
        assertEquals(patterns, dto.getDetectedPatterns());
        assertEquals("PENDING", dto.getStatus());
        assertTrue(dto.getRequiresReview());
        assertEquals("admin", dto.getAssignedTo());
        assertEquals(now, dto.getDetectedAt());
        assertEquals("reviewer", dto.getReviewedBy());
        assertEquals("Confirmed", dto.getReviewNotes());
        assertEquals("PENDING", dto.getStatus());
    }

    @Test
    @DisplayName("builder with null fields should create instance")
    void testBuilder_NullFields() {
        FraudDetectionDto dto = FraudDetectionDto.builder()
                .tenantId("tenant-1")
                .entityType("CLAIM")
                .entityId("claim-456")
                .build();

        assertEquals("tenant-1", dto.getTenantId());
        assertEquals("CLAIM", dto.getEntityType());
        assertEquals("claim-456", dto.getEntityId());
        assertNull(dto.getId());
        assertNull(dto.getRiskLevel());
        assertNull(dto.getRiskScore());
    }

    // ==================== toBuilder Tests ====================

    @Test
    @DisplayName("toBuilder should create copy with modified fields")
    void testToBuilder_ModifyFields() {
        FraudDetectionDto original = FraudDetectionDto.builder()
                .tenantId("tenant-1")
                .entityType("CLAIM")
                .entityId("claim-789")
                .riskLevel(FraudRiskLevel.LOW)
                .riskScore(0.2)
                .build();

        FraudDetectionDto modified = original.toBuilder()
                .riskLevel(FraudRiskLevel.HIGH)
                .riskScore(0.9)
                .build();

        assertEquals(original.getTenantId(), modified.getTenantId());
        assertEquals(original.getEntityType(), modified.getEntityType());
        assertEquals(FraudRiskLevel.LOW, original.getRiskLevel());
        assertEquals(FraudRiskLevel.HIGH, modified.getRiskLevel());
        assertEquals(0.2, original.getRiskScore());
        assertEquals(0.9, modified.getRiskScore());
    }

    @Test
    @DisplayName("toBuilder with empty builder should preserve nulls")
    void testToBuilder_PreservesNulls() {
        FraudDetectionDto original = FraudDetectionDto.builder()
                .tenantId("tenant-1")
                .entityType("CLAIM")
                .build();

        FraudDetectionDto copy = original.toBuilder().build();

        assertEquals(original.getTenantId(), copy.getTenantId());
        assertEquals(original.getEntityType(), copy.getEntityType());
        assertNull(copy.getRiskLevel());
        assertNull(copy.getRiskScore());
    }

    // ==================== Getter/Setter Tests ====================

    @Test
    @DisplayName("setters should update fields")
    void testSetters() {
        FraudDetectionDto dto = new FraudDetectionDto();
        dto.setTenantId("tenant-modified");
        dto.setEntityType("TRANSACTION");
        dto.setEntityId("txn-999");

        assertEquals("tenant-modified", dto.getTenantId());
        assertEquals("TRANSACTION", dto.getEntityType());
        assertEquals("txn-999", dto.getEntityId());
    }

    // ==================== Boundary Value Tests ====================

    @Test
    @DisplayName("riskScore boundary: minimum 0.0")
    void testRiskScore_MinBoundary() {
        FraudDetectionDto dto = FraudDetectionDto.builder()
                .riskScore(0.0)
                .build();

        assertEquals(0.0, dto.getRiskScore());
    }

    @Test
    @DisplayName("riskScore boundary: maximum 1.0")
    void testRiskScore_MaxBoundary() {
        FraudDetectionDto dto = FraudDetectionDto.builder()
                .riskScore(1.0)
                .build();

        assertEquals(1.0, dto.getRiskScore());
    }

    @Test
    @DisplayName("riskScore: negative value allowed in DTO")
    void testRiskScore_NegativeAllowed() {
        FraudDetectionDto dto = FraudDetectionDto.builder()
                .riskScore(-0.5)
                .build();

        assertEquals(-0.5, dto.getRiskScore());
    }

    @Test
    @DisplayName("riskScore: value above 1.0 allowed in DTO")
    void testRiskScore_AboveOneAllowed() {
        FraudDetectionDto dto = FraudDetectionDto.builder()
                .riskScore(1.5)
                .build();

        assertEquals(1.5, dto.getRiskScore());
    }

    // ==================== Enum Tests ====================

    @Test
    @DisplayName("all FraudRiskLevel enum values should be valid")
    void testAllRiskLevelValues() {
        FraudRiskLevel[] levels = FraudRiskLevel.values();

        assertEquals(4, levels.length);
        assertTrue(List.of(levels).contains(FraudRiskLevel.LOW));
        assertTrue(List.of(levels).contains(FraudRiskLevel.MEDIUM));
        assertTrue(List.of(levels).contains(FraudRiskLevel.HIGH));
        assertTrue(List.of(levels).contains(FraudRiskLevel.CRITICAL));
    }

    @Test
    @DisplayName("valueOf should return correct enum")
    void testRiskLevelValueOf() {
        assertEquals(FraudRiskLevel.LOW, FraudRiskLevel.valueOf("LOW"));
        assertEquals(FraudRiskLevel.MEDIUM, FraudRiskLevel.valueOf("MEDIUM"));
        assertEquals(FraudRiskLevel.HIGH, FraudRiskLevel.valueOf("HIGH"));
        assertEquals(FraudRiskLevel.CRITICAL, FraudRiskLevel.valueOf("CRITICAL"));
    }

    @Test
    @DisplayName("valueOf with invalid value should throw exception")
    void testRiskLevelValueOf_Invalid() {
        assertThrows(IllegalArgumentException.class, () -> {
            FraudRiskLevel.valueOf("INVALID");
        });
    }

    // ==================== Collection Tests ====================

    @Test
    @DisplayName("empty detectionDetails should be allowed")
    void testEmptyDetectionDetails() {
        FraudDetectionDto dto = FraudDetectionDto.builder()
                .detectionDetails(Map.of())
                .build();

        assertNotNull(dto.getDetectionDetails());
        assertTrue(dto.getDetectionDetails().isEmpty());
    }

    @Test
    @DisplayName("empty detectedPatterns should be allowed")
    void testEmptyDetectedPatterns() {
        FraudDetectionDto dto = FraudDetectionDto.builder()
                .detectedPatterns(List.of())
                .build();

        assertNotNull(dto.getDetectedPatterns());
        assertTrue(dto.getDetectedPatterns().isEmpty());
    }

    @Test
    @DisplayName("null collections should be allowed")
    void testNullCollections() {
        FraudDetectionDto dto = FraudDetectionDto.builder()
                .tenantId("tenant-1")
                .entityType("CLAIM")
                .detectionDetails(null)
                .detectedPatterns(null)
                .metadata(null)
                .build();

        assertNull(dto.getDetectionDetails());
        assertNull(dto.getDetectedPatterns());
        assertNull(dto.getMetadata());
    }

    // ==================== Boolean Field Tests ====================

    @Test
    @DisplayName("requiresReview should handle true")
    void testRequiresReview_True() {
        FraudDetectionDto dto = FraudDetectionDto.builder()
                .requiresReview(true)
                .build();

        assertTrue(dto.getRequiresReview());
    }

    @Test
    @DisplayName("requiresReview should handle false")
    void testRequiresReview_False() {
        FraudDetectionDto builder = FraudDetectionDto.builder()
                .requiresReview(false)
                .build();

        assertFalse(builder.getRequiresReview());
    }

    @Test
    @DisplayName("requiresReview should handle null")
    void testRequiresReview_Null() {
        FraudDetectionDto dto = FraudDetectionDto.builder()
                .requiresReview(null)
                .build();

        assertNull(dto.getRequiresReview());
    }

    // ==================== Equals/HashCode Tests ====================

    @Test
    @DisplayName("two DTOs with same ID should be considered equal")
    void testEquals_SameId() {
        UUID id = UUID.randomUUID();

        FraudDetectionDto dto1 = FraudDetectionDto.builder()
                .id(id)
                .tenantId("tenant-1")
                .build();

        FraudDetectionDto dto2 = FraudDetectionDto.builder()
                .id(id)
                .tenantId("tenant-2")
                .build();

        // DTOs with same ID are equal (entity equality based on ID)
        assertEquals(dto1, dto2);
    }

    @Test
    @DisplayName("same DTO should equal itself")
    void testEquals_SameInstance() {
        FraudDetectionDto dto = FraudDetectionDto.builder()
                .id(UUID.randomUUID())
                .build();

        assertEquals(dto, dto);
    }

    // ==================== ToString Tests ====================

    @Test
    @DisplayName("toString should contain relevant fields")
    void testToString() {
        FraudDetectionDto dto = FraudDetectionDto.builder()
                .id(UUID.randomUUID())
                .tenantId("tenant-tostring")
                .entityType("CLAIM")
                .build();

        String result = dto.toString();

        assertTrue(result.contains("tenant-tostring") || result.contains("CLAIM"));
    }

    // ==================== No-Args Constructor Test ====================

    @Test
    @DisplayName("no-args constructor should create instance with nulls")
    void testNoArgsConstructor() {
        FraudDetectionDto dto = new FraudDetectionDto();

        assertNull(dto.getId());
        assertNull(dto.getTenantId());
        assertNull(dto.getEntityType());
        assertNull(dto.getEntityId());
    }

    // ========== Mutation-Killing Tests: Numeric Field Operations ==========

    @Test
    @DisplayName("Risk score should handle various decimal values")
    void testRiskScore_NumericValues() {
        FraudDetectionDto dto1 = FraudDetectionDto.builder()
                .riskScore(0.0)
                .build();
        assertEquals(0.0, dto1.getRiskScore());

        FraudDetectionDto dto2 = FraudDetectionDto.builder()
                .riskScore(0.1)
                .build();
        assertEquals(0.1, dto2.getRiskScore());

        FraudDetectionDto dto3 = FraudDetectionDto.builder()
                .riskScore(0.5)
                .build();
        assertEquals(0.5, dto3.getRiskScore());

        FraudDetectionDto dto4 = FraudDetectionDto.builder()
                .riskScore(0.85)
                .build();
        assertEquals(0.85, dto4.getRiskScore());

        FraudDetectionDto dto5 = FraudDetectionDto.builder()
                .riskScore(0.95)
                .build();
        assertEquals(0.95, dto5.getRiskScore());

        FraudDetectionDto dto6 = FraudDetectionDto.builder()
                .riskScore(1.0)
                .build();
        assertEquals(1.0, dto6.getRiskScore());
    }

    @Test
    @DisplayName("Risk score setter should update correctly")
    void testRiskScore_Setter() {
        FraudDetectionDto dto = new FraudDetectionDto();
        dto.setRiskScore(0.0);
        assertEquals(0.0, dto.getRiskScore());

        dto.setRiskScore(0.5);
        assertEquals(0.5, dto.getRiskScore());

        dto.setRiskScore(1.0);
        assertEquals(1.0, dto.getRiskScore());

        dto.setRiskScore(null);
        assertNull(dto.getRiskScore());
    }

    // ========== Mutation-Killing Tests: hashCode() and equals() ==========

    @Test
    @DisplayName("hashCode - Objects with same ID should have same hashCode")
    void testHashCode_SameId_SameHashCode() {
        UUID id = UUID.randomUUID();
        FraudDetectionDto dto1 = FraudDetectionDto.builder().id(id).build();
        FraudDetectionDto dto2 = FraudDetectionDto.builder().id(id).build();

        // This kills Math mutants in hashCode()
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("hashCode - Objects with different IDs should have different hashCodes")
    void testHashCode_DifferentId_DifferentHashCode() {
        FraudDetectionDto dto1 = FraudDetectionDto.builder().id(UUID.randomUUID()).build();
        FraudDetectionDto dto2 = FraudDetectionDto.builder().id(UUID.randomUUID()).build();

        // This kills Math mutants in hashCode()
        assertNotEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("HashSet should work correctly with FraudDetectionDto")
    void testHashSet_UsesHashCode() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        FraudDetectionDto dto1 = FraudDetectionDto.builder().id(id1).entityId("entity-001").build();
        FraudDetectionDto dto2 = FraudDetectionDto.builder().id(id2).entityId("entity-002").build();
        FraudDetectionDto dto1Duplicate = FraudDetectionDto.builder().id(id1).entityId("entity-001-DUP").build();

        java.util.HashSet<FraudDetectionDto> set = new java.util.HashSet<>();
        assertTrue(set.add(dto1));
        assertTrue(set.add(dto2));
        assertFalse(set.add(dto1Duplicate)); // Should not add - same ID as dto1

        assertEquals(2, set.size());
        assertTrue(set.contains(dto1));
        assertTrue(set.contains(dto2));
    }

    @Test
    @DisplayName("HashMap should work correctly with FraudDetectionDto as key")
    void testHashMap_UsesHashCodeAndEquals() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        FraudDetectionDto key1 = FraudDetectionDto.builder().id(id1).build();
        FraudDetectionDto key2 = FraudDetectionDto.builder().id(id2).build();
        FraudDetectionDto key1Duplicate = FraudDetectionDto.builder().id(id1).build();

        java.util.Map<FraudDetectionDto, String> map = new java.util.HashMap<>();
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
    @DisplayName("equals - Null comparison")
    void testEquals_NullComparison() {
        FraudDetectionDto dto = FraudDetectionDto.builder().id(UUID.randomUUID()).build();
        // This kills NegateConditionalsMutator in equals()
        assertNotEquals(null, dto);
    }

    @Test
    @DisplayName("equals - Different type comparison")
    void testEquals_DifferentType() {
        FraudDetectionDto dto = FraudDetectionDto.builder().id(UUID.randomUUID()).build();
        String other = "Not a FraudDetectionDto";
        // This kills NegateConditionalsMutator in equals()
        assertNotEquals(dto, other);
    }

    @Test
    @DisplayName("equals - All fields same except ID")
    void testEquals_AllFieldsSameExceptId() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        FraudDetectionDto dto1 = FraudDetectionDto.builder()
                .id(id1)
                .entityType("CLAIM")
                .entityId("claim-123")
                .build();

        FraudDetectionDto dto2 = FraudDetectionDto.builder()
                .id(id2)
                .entityType("CLAIM")
                .entityId("claim-123")
                .build();

        // This kills NegateConditionalsMutator in equals()
        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("equals - Both IDs null")
    void testEquals_BothIdsNull() {
        FraudDetectionDto dto1 = FraudDetectionDto.builder().build();
        FraudDetectionDto dto2 = FraudDetectionDto.builder().build();

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("equals - One ID null")
    void testEquals_OneIdNull() {
        UUID id = UUID.randomUUID();
        FraudDetectionDto dto1 = FraudDetectionDto.builder().id(id).build();
        FraudDetectionDto dto2 = FraudDetectionDto.builder().id(null).build();

        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("canEqual - Same class should return true")
    void testCanEqual_SameClass() {
        FraudDetectionDto dto = FraudDetectionDto.builder().build();
        FraudDetectionDto other = FraudDetectionDto.builder().build();

        // This kills BooleanReturnValsMutator in canEqual()
        assertTrue(dto.canEqual(other));
    }

    @Test
    @DisplayName("canEqual - Different class should return false")
    void testCanEqual_DifferentClass() {
        FraudDetectionDto dto = FraudDetectionDto.builder().build();
        String other = "Not a FraudDetectionDto";

        // This kills BooleanReturnValsMutator in canEqual()
        assertFalse(dto.canEqual(other));
    }

    @Test
    @DisplayName("canEqual - Null should return false")
    void testCanEqual_Null() {
        FraudDetectionDto dto = FraudDetectionDto.builder().build();

        // This kills BooleanReturnValsMutator in canEqual()
        assertFalse(dto.canEqual(null));
    }

    @Test
    @DisplayName("equals - CanEqual check between different types")
    void testEquals_WithCanEqualCheck() {
        UUID id = UUID.randomUUID();
        FraudDetectionDto dto = FraudDetectionDto.builder()
                .id(id)
                .entityType("CLAIM")
                .entityId("claim-123")
                .build();
        FraudAlertDto otherDto = FraudAlertDto.builder()
                .id(id)
                .alertType("ALERT")
                .build();

        // This tests canEqual within equals
        assertNotEquals(dto, otherDto);
    }

    @Test
    @DisplayName("HashSet contains and remove operations")
    void testHashSet_ContainsAndRemove() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        FraudDetectionDto dto1 = FraudDetectionDto.builder().id(id1).entityId("entity-001").build();
        FraudDetectionDto dto2 = FraudDetectionDto.builder().id(id2).entityId("entity-002").build();

        java.util.HashSet<FraudDetectionDto> set = new java.util.HashSet<>();
        set.add(dto1);
        set.add(dto2);

        // Test contains - uses hashCode() and equals()
        assertTrue(set.contains(dto1));
        assertTrue(set.contains(dto2));

        // Create duplicate with same ID
        FraudDetectionDto dto1Dup = FraudDetectionDto.builder().id(id1).entityId("entity-001-DUP").build();
        assertTrue(set.contains(dto1Dup));

        // Test remove - uses hashCode() and equals()
        assertTrue(set.remove(dto1Dup));
        assertEquals(1, set.size());
        assertFalse(set.contains(dto1));
    }

    @Test
    @DisplayName("hashCode consistency across multiple calls")
    void testHashCode_Consistency() {
        FraudDetectionDto dto = FraudDetectionDto.builder()
                .id(UUID.randomUUID())
                .entityType("CLAIM")
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
        FraudDetectionDto dto1 = FraudDetectionDto.builder()
                .id(id)
                .entityType(null)
                .build();

        FraudDetectionDto dto2 = FraudDetectionDto.builder()
                .id(id)
                .entityType(null)
                .build();

        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    // ========== Additional Getter Return Value Tests ==========

    @Test
    @DisplayName("Getters should return exact values set via builder")
    void testGetters_ExactValues() {
        String entityType = "TRANSACTION";
        String entityId = "txn-12345";
        String detectionMethod = "ML_MODEL_V2";
        String status = "REVIEWED";
        String assignedTo = "analyst-007";

        FraudDetectionDto dto = FraudDetectionDto.builder()
                .entityType(entityType)
                .entityId(entityId)
                .detectionMethod(detectionMethod)
                .status(status)
                .assignedTo(assignedTo)
                .build();

        // This kills EmptyObjectReturnValsMutator for string getters
        assertEquals(entityType, dto.getEntityType());
        assertEquals(entityId, dto.getEntityId());
        assertEquals(detectionMethod, dto.getDetectionMethod());
        assertEquals(status, dto.getStatus());
        assertEquals(assignedTo, dto.getAssignedTo());
    }

    @Test
    @DisplayName("Setter for detectedAt and reviewedAt should work correctly")
    void testDetectedAtReviewedAt_Setters() {
        LocalDateTime now = LocalDateTime.now();
        FraudDetectionDto dto = new FraudDetectionDto();

        dto.setDetectedAt(now);
        assertEquals(now, dto.getDetectedAt());

        dto.setReviewedAt(now.plusHours(1));
        assertEquals(now.plusHours(1), dto.getReviewedAt());

        dto.setDetectedAt(null);
        assertNull(dto.getDetectedAt());

        dto.setReviewedAt(null);
        assertNull(dto.getReviewedAt());
    }

    @Test
    @DisplayName("Setter for reviewedBy and reviewNotes should work correctly")
    void testReviewedByReviewNotes_Setters() {
        FraudDetectionDto dto = new FraudDetectionDto();

        dto.setReviewedBy("reviewer-001");
        assertEquals("reviewer-001", dto.getReviewedBy());

        dto.setReviewNotes("Suspicious pattern confirmed");
        assertEquals("Suspicious pattern confirmed", dto.getReviewNotes());

        dto.setReviewedBy(null);
        assertNull(dto.getReviewedBy());

        dto.setReviewNotes(null);
        assertNull(dto.getReviewNotes());
    }

    @Test
    @DisplayName("Setter for metadata should work correctly")
    void testMetadata_Setter() {
        Map<String, Object> metadata = Map.of("source", "api", "version", "1.0");
        FraudDetectionDto dto = new FraudDetectionDto();

        dto.setMetadata(metadata);
        assertEquals(metadata, dto.getMetadata());
        assertEquals("api", dto.getMetadata().get("source"));

        dto.setMetadata(null);
        assertNull(dto.getMetadata());
    }

    @Test
    @DisplayName("Setter for detectionDetails and detectedPatterns should work correctly")
    void testDetectionDetailsDetectedPatterns_Setters() {
        Map<String, Object> details = Map.of("confidence", 0.95);
        List<String> patterns = List.of("pattern1", "pattern2");
        FraudDetectionDto dto = new FraudDetectionDto();

        dto.setDetectionDetails(details);
        assertEquals(details, dto.getDetectionDetails());

        dto.setDetectedPatterns(patterns);
        assertEquals(patterns, dto.getDetectedPatterns());

        dto.setDetectionDetails(null);
        assertNull(dto.getDetectionDetails());

        dto.setDetectedPatterns(null);
        assertNull(dto.getDetectedPatterns());
    }
}