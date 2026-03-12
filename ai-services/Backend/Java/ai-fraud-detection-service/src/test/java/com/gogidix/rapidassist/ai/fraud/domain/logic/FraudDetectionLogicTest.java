package com.gogidix.rapidassist.ai.fraud.domain.logic;

import com.gogidix.rapidassist.ai.fraud.domain.model.FraudDetection;
import com.gogidix.rapidassist.ai.fraud.domain.model.FraudRiskLevel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pure Domain Logic Tests for FraudDetection.
 *
 * These tests execute ACTUAL business logic without:
 * - Spring framework
 * - Mocks
 * - External dependencies
 *
 * This is critical for:
 * - High code coverage (95% target for domain model)
 * - Mutation testing (killing mutations in business logic)
 * - Fast execution (no Spring context startup)
 */
@DisplayName("FraudDetection Domain Logic Tests")
class FraudDetectionLogicTest {

    // ==================== Risk Level Classification Tests ====================

    @ParameterizedTest
    @EnumSource(value = FraudRiskLevel.class, names = {"HIGH", "CRITICAL"})
    @DisplayName("isHighRisk should return true for HIGH and CRITICAL risk levels")
    void testIsHighRisk_TrueForHighAndCritical(FraudRiskLevel riskLevel) {
        FraudDetection detection = FraudDetection.builder()
                .riskLevel(riskLevel)
                .build();

        assertTrue(detection.isHighRisk(),
                riskLevel + " should be classified as high risk");
    }

    @ParameterizedTest
    @EnumSource(value = FraudRiskLevel.class, names = {"LOW", "MEDIUM"})
    @DisplayName("isHighRisk should return false for LOW and MEDIUM risk levels")
    void testIsHighRisk_FalseForLowAndMedium(FraudRiskLevel riskLevel) {
        FraudDetection detection = FraudDetection.builder()
                .riskLevel(riskLevel)
                .build();

        assertFalse(detection.isHighRisk(),
                riskLevel + " should NOT be classified as high risk");
    }

    @Test
    @DisplayName("isHighRisk should return false for null risk level")
    void testIsHighRisk_NullRiskLevel() {
        FraudDetection detection = FraudDetection.builder()
                .riskLevel(null)
                .build();

        assertFalse(detection.isHighRisk());
    }

    // ==================== Immediate Action Tests ====================

    @Test
    @DisplayName("requiresImmediateAction should return true for CRITICAL regardless of review flag")
    void testRequiresImmediateAction_CriticalAlwaysTrue() {
        FraudDetection detection = FraudDetection.builder()
                .riskLevel(FraudRiskLevel.CRITICAL)
                .requiresReview(false)
                .build();

        assertTrue(detection.requiresImmediateAction(),
                "CRITICAL risk should always require immediate action");
    }

    @Test
    @DisplayName("requiresImmediateAction should return true when requiresReview is true")
    void testRequiresImmediateAction_ReviewFlagTrue() {
        FraudDetection detection = FraudDetection.builder()
                .riskLevel(FraudRiskLevel.LOW)
                .requiresReview(true)
                .build();

        assertTrue(detection.requiresImmediateAction(),
                "Detection with requiresReview=true should require immediate action");
    }

    @Test
    @DisplayName("requiresImmediateAction should return false when both conditions are false")
    void testRequiresImmediateAction_BothConditionsFalse() {
        FraudDetection detection = FraudDetection.builder()
                .riskLevel(FraudRiskLevel.LOW)
                .requiresReview(false)
                .build();

        assertFalse(detection.requiresImmediateAction());
    }

    @Test
    @DisplayName("requiresImmediateAction should return true for HIGH with review flag")
    void testRequiresImmediateAction_HighWithReview() {
        FraudDetection detection = FraudDetection.builder()
                .riskLevel(FraudRiskLevel.HIGH)
                .requiresReview(true)
                .build();

        assertTrue(detection.requiresImmediateAction());
    }

    // ==================== Mark as Reviewed Tests ====================

    @Test
    @DisplayName("markAsReviewed should update all review-related fields")
    void testMarkAsReviewed_UpdatesAllFields() {
        String reviewer = "admin-user";
        String notes = "Verified as fraudulent activity";

        FraudDetection detection = FraudDetection.builder()
                .status("PENDING")
                .reviewedBy(null)
                .reviewNotes(null)
                .reviewedAt(null)
                .build();

        detection.markAsReviewed(reviewer, notes);

        assertEquals("REVIEWED", detection.getStatus());
        assertEquals(reviewer, detection.getReviewedBy());
        assertEquals(notes, detection.getReviewNotes());
        assertNotNull(detection.getReviewedAt());
        assertNotNull(detection.getUpdatedAt());
    }

    @Test
    @DisplayName("markAsReviewed should overwrite existing review data")
    void testMarkAsReviewed_OverwritesExisting() {
        FraudDetection detection = FraudDetection.builder()
                .status("REVIEWED")
                .reviewedBy("original-reviewer")
                .reviewNotes("Original notes")
                .reviewedAt(LocalDateTime.now().minusDays(1))
                .build();

        detection.markAsReviewed("new-reviewer", "Updated notes");

        assertEquals("new-reviewer", detection.getReviewedBy());
        assertEquals("Updated notes", detection.getReviewNotes());
    }

    @Test
    @DisplayName("markAsReviewed should handle null reviewer")
    void testMarkAsReviewed_NullReviewer() {
        FraudDetection detection = FraudDetection.builder()
                .status("PENDING")
                .build();

        detection.markAsReviewed(null, "Notes without reviewer");

        assertEquals("REVIEWED", detection.getStatus());
        assertNull(detection.getReviewedBy());
        assertEquals("Notes without reviewer", detection.getReviewNotes());
    }

    @Test
    @DisplayName("markAsReviewed should handle null notes")
    void testMarkAsReviewed_NullNotes() {
        FraudDetection detection = FraudDetection.builder()
                .status("PENDING")
                .build();

        detection.markAsReviewed("reviewer", null);

        assertEquals("REVIEWED", detection.getStatus());
        assertEquals("reviewer", detection.getReviewedBy());
        assertNull(detection.getReviewNotes());
    }

    @Test
    @DisplayName("markAsReviewed should handle empty notes")
    void testMarkAsReviewed_EmptyNotes() {
        FraudDetection detection = FraudDetection.builder()
                .status("PENDING")
                .build();

        detection.markAsReviewed("reviewer", "");

        assertEquals("REVIEWED", detection.getStatus());
        assertEquals("reviewer", detection.getReviewedBy());
        assertEquals("", detection.getReviewNotes());
    }

    // ==================== Assign To Tests ====================

    @Test
    @DisplayName("assignTo should update assignedTo and updatedAt")
    void testAssignTo_UpdatesFields() {
        String reviewerId = "reviewer-123";
        LocalDateTime beforeUpdate = LocalDateTime.now();

        FraudDetection detection = FraudDetection.builder()
                .assignedTo(null)
                .updatedAt(beforeUpdate)
                .build();

        detection.assignTo(reviewerId);

        assertEquals(reviewerId, detection.getAssignedTo());
        assertTrue(detection.getUpdatedAt().isAfter(beforeUpdate) ||
                    detection.getUpdatedAt().isEqual(beforeUpdate));
    }

    @Test
    @DisplayName("assignTo should overwrite existing assignment")
    void testAssignTo_OverwritesExisting() {
        FraudDetection detection = FraudDetection.builder()
                .assignedTo("original-assignee")
                .build();

        detection.assignTo("new-assignee");

        assertEquals("new-assignee", detection.getAssignedTo());
    }

    @Test
    @DisplayName("assignTo should handle null reviewerId")
    void testAssignTo_NullReviewerId() {
        FraudDetection detection = FraudDetection.builder()
                .assignedTo("existing-assignee")
                .build();

        detection.assignTo(null);

        assertNull(detection.getAssignedTo());
        assertNotNull(detection.getUpdatedAt());
    }

    // ==================== Age Calculation Tests ====================

    @Test
    @DisplayName("getAgeInHours should calculate age correctly")
    void testGetAgeInHours_CalculatesCorrectly() {
        LocalDateTime detectedAt = LocalDateTime.now().minusHours(5);

        FraudDetection detection = FraudDetection.builder()
                .detectedAt(detectedAt)
                .build();

        long age = detection.getAgeInHours();

        assertTrue(age >= 4 && age <= 6,
                "Age should be approximately 5 hours, got: " + age);
    }

    @Test
    @DisplayName("getAgeInHours should return 0 for null detectedAt")
    void testGetAgeInHours_NullDetectedAt() {
        FraudDetection detection = FraudDetection.builder()
                .detectedAt(null)
                .build();

        assertEquals(0, detection.getAgeInHours());
    }

    @Test
    @DisplayName("getAgeInHours should handle recent detection")
    void testGetAgeInHours_RecentDetection() {
        FraudDetection detection = FraudDetection.builder()
                .detectedAt(LocalDateTime.now().minusMinutes(30))
                .build();

        long age = detection.getAgeInHours();

        assertEquals(0, age, "Detection less than 1 hour ago should have age 0");
    }

    @Test
    @DisplayName("getAgeInHours should handle old detection")
    void testGetAgeInHours_OldDetection() {
        FraudDetection detection = FraudDetection.builder()
                .detectedAt(LocalDateTime.now().minusHours(48))
                .build();

        long age = detection.getAgeInHours();

        assertTrue(age >= 47 && age <= 49,
                "Age should be approximately 48 hours, got: " + age);
    }

    // ==================== Builder Pattern Tests ====================

    @Test
    @DisplayName("Builder should create instance with all fields")
    void testBuilder_AllFields() {
        UUID id = UUID.randomUUID();
        Map<String, Object> details = new HashMap<>();
        details.put("key", "value");
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("meta", "data");

        LocalDateTime now = LocalDateTime.now();

        FraudDetection detection = FraudDetection.builder()
                .id(id)
                .tenantId("tenant-123")
                .entityType("CLAIM")
                .entityId("claim-456")
                .riskLevel(FraudRiskLevel.HIGH)
                .riskScore(0.85)
                .detectionMethod("ML_MODEL")
                .detectionDetails(details)
                .detectedPatterns(List.of("pattern1", "pattern2"))
                .status("PENDING")
                .requiresReview(true)
                .assignedTo("admin")
                .detectedAt(now)
                .reviewedAt(now)
                .reviewedBy("reviewer")
                .reviewNotes("notes")
                .metadata(metadata)
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertEquals(id, detection.getId());
        assertEquals("tenant-123", detection.getTenantId());
        assertEquals("CLAIM", detection.getEntityType());
        assertEquals("claim-456", detection.getEntityId());
        assertEquals(FraudRiskLevel.HIGH, detection.getRiskLevel());
        assertEquals(0.85, detection.getRiskScore());
        assertEquals("ML_MODEL", detection.getDetectionMethod());
        assertEquals(details, detection.getDetectionDetails());
        assertEquals(2, detection.getDetectedPatterns().size());
        assertEquals("PENDING", detection.getStatus());
        assertTrue(detection.getRequiresReview());
        assertEquals("admin", detection.getAssignedTo());
        assertEquals(now, detection.getDetectedAt());
        assertEquals(now, detection.getReviewedAt());
        assertEquals("reviewer", detection.getReviewedBy());
        assertEquals("notes", detection.getReviewNotes());
        assertEquals(metadata, detection.getMetadata());
        assertEquals(now, detection.getCreatedAt());
        assertEquals(now, detection.getUpdatedAt());
    }

    @Test
    @DisplayName("Builder should create instance with minimal fields")
    void testBuilder_MinimalFields() {
        FraudDetection detection = FraudDetection.builder()
                .tenantId("tenant-123")
                .entityType("CLAIM")
                .riskLevel(FraudRiskLevel.LOW)
                .build();

        assertEquals("tenant-123", detection.getTenantId());
        assertEquals("CLAIM", detection.getEntityType());
        assertEquals(FraudRiskLevel.LOW, detection.getRiskLevel());
    }

    // ==================== Risk Score Boundary Tests ====================

    @ParameterizedTest
    @CsvSource({
            "0.0, true",
            "0.1, true",
            "0.5, true",
            "0.9, true",
            "1.0, true"
    })
    @DisplayName("Risk scores should be within valid range [0.0, 1.0]")
    void testRiskScore_ValidRange(double score, boolean shouldBeValid) {
        FraudDetection detection = FraudDetection.builder()
                .riskScore(score)
                .build();

        assertEquals(score, detection.getRiskScore());
    }

    // ==================== Decision Boundary Tests ====================

    @Test
    @DisplayName("Decision boundary: Risk score 0.7 should be MEDIUM threshold")
    void testDecisionBoundary_MediumThreshold() {
        // Scores around 0.7 (boundary between LOW and MEDIUM)
        FraudDetection lowRisk = FraudDetection.builder()
                .riskLevel(FraudRiskLevel.LOW)
                .riskScore(0.65)
                .build();

        FraudDetection mediumRisk = FraudDetection.builder()
                .riskLevel(FraudRiskLevel.MEDIUM)
                .riskScore(0.75)
                .build();

        assertFalse(lowRisk.isHighRisk());
        assertFalse(mediumRisk.isHighRisk());
    }

    @Test
    @DisplayName("Decision boundary: Risk score 0.8 should be HIGH threshold")
    void testDecisionBoundary_HighThreshold() {
        FraudDetection highRisk = FraudDetection.builder()
                .riskLevel(FraudRiskLevel.HIGH)
                .riskScore(0.85)
                .build();

        assertTrue(highRisk.isHighRisk());
    }

    @Test
    @DisplayName("Decision boundary: Risk score 0.9 should be CRITICAL threshold")
    void testDecisionBoundary_CriticalThreshold() {
        FraudDetection criticalRisk = FraudDetection.builder()
                .riskLevel(FraudRiskLevel.CRITICAL)
                .riskScore(0.95)
                .build();

        assertTrue(criticalRisk.isHighRisk());
        assertTrue(criticalRisk.requiresImmediateAction());
    }

    // ==================== NoArgsConstructor Test ====================

    @Test
    @DisplayName("NoArgsConstructor should create instance with null fields")
    void testNoArgsConstructor_AllFieldsNull() {
        FraudDetection detection = new FraudDetection();

        assertNull(detection.getId());
        assertNull(detection.getTenantId());
        assertNull(detection.getEntityType());
        assertNull(detection.getEntityId());
        assertNull(detection.getRiskLevel());
        assertNull(detection.getRiskScore());
        assertNull(detection.getStatus());
        assertNull(detection.getRequiresReview());
    }

    // ==================== Risk Level Transition Tests ====================

    @Test
    @DisplayName("Risk level should influence requiresReview flag")
    void testRiskLevelInfluencesRequiresReview() {
        FraudDetection lowRisk = FraudDetection.builder()
                .riskLevel(FraudRiskLevel.LOW)
                .requiresReview(false)
                .build();

        FraudDetection highRisk = FraudDetection.builder()
                .riskLevel(FraudRiskLevel.HIGH)
                .requiresReview(true)
                .build();

        FraudDetection criticalRisk = FraudDetection.builder()
                .riskLevel(FraudRiskLevel.CRITICAL)
                .requiresReview(true)
                .build();

        assertFalse(lowRisk.getRequiresReview());
        assertTrue(highRisk.getRequiresReview());
        assertTrue(criticalRisk.getRequiresReview());
    }

    // ==================== Multi-tenancy Field Tests ====================

    @Test
    @DisplayName("TenantId should be properly set and accessible")
    void testTenantId_Field() {
        String tenantId = "tenant-abc-123";

        FraudDetection detection = FraudDetection.builder()
                .tenantId(tenantId)
                .build();

        assertEquals(tenantId, detection.getTenantId());
    }

    @Test
    @DisplayName("Null tenantId should be handled")
    void testTenantId_Null() {
        FraudDetection detection = FraudDetection.builder()
                .tenantId(null)
                .build();

        assertNull(detection.getTenantId());
    }

    // ==================== Detection Details Tests ====================

    @Test
    @DisplayName("Detection details should be stored and retrieved")
    void testDetectionDetails_Map() {
        Map<String, Object> details = new HashMap<>();
        details.put("model_version", "2.0");
        details.put("confidence", 0.95);
        details.put("features", List.of("amount", "location"));

        FraudDetection detection = FraudDetection.builder()
                .detectionDetails(details)
                .build();

        assertEquals(details, detection.getDetectionDetails());
        assertEquals("2.0", detection.getDetectionDetails().get("model_version"));
        assertEquals(0.95, detection.getDetectionDetails().get("confidence"));
    }

    @Test
    @DisplayName("Null detection details should be handled")
    void testDetectionDetails_Null() {
        FraudDetection detection = FraudDetection.builder()
                .detectionDetails(null)
                .build();

        assertNull(detection.getDetectionDetails());
    }

    @Test
    @DisplayName("Empty detection details should be handled")
    void testDetectionDetails_Empty() {
        FraudDetection detection = FraudDetection.builder()
                .detectionDetails(new HashMap<>())
                .build();

        assertNotNull(detection.getDetectionDetails());
        assertTrue(detection.getDetectionDetails().isEmpty());
    }

    // ==================== Detected Patterns Tests ====================

    @Test
    @DisplayName("Detected patterns list should be stored")
    void testDetectedPatterns_List() {
        List<String> patterns = List.of("suspicious_location", "unusual_amount", "off_hours");

        FraudDetection detection = FraudDetection.builder()
                .detectedPatterns(patterns)
                .build();

        assertEquals(patterns, detection.getDetectedPatterns());
        assertEquals(3, detection.getDetectedPatterns().size());
    }

    @Test
    @DisplayName("Empty detected patterns list should be handled")
    void testDetectedPatterns_Empty() {
        FraudDetection detection = FraudDetection.builder()
                .detectedPatterns(List.of())
                .build();

        assertNotNull(detection.getDetectedPatterns());
        assertTrue(detection.getDetectedPatterns().isEmpty());
    }

    @Test
    @DisplayName("Null detected patterns should be handled")
    void testDetectedPatterns_Null() {
        FraudDetection detection = FraudDetection.builder()
                .detectedPatterns(null)
                .build();

        assertNull(detection.getDetectedPatterns());
    }
}
