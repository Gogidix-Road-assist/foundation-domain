package com.gogidix.rapidassist.ai.moderation.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ModerationResult domain model
 */
class ModerationResultTest {

    @Test
    void testCreateModerationResult() {
        ModerationResult result = ModerationResult.builder()
            .id("result1")
            .tenantId("tenant1")
            .contentId("content1")
            .contentType("comment")
            .content("Test content")
            .status(ModerationResult.ModerationStatus.APPROVED)
            .confidenceScore(0.95)
            .moderatedAt(LocalDateTime.now())
            .createdAt(LocalDateTime.now())
            .build();

        assertNotNull(result);
        assertEquals("result1", result.getId());
        assertEquals("content1", result.getContentId());
        assertEquals(ModerationResult.ModerationStatus.APPROVED, result.getStatus());
        assertEquals(0.95, result.getConfidenceScore());
    }

    @Test
    void testIsApproved() {
        ModerationResult approved = ModerationResult.builder()
            .status(ModerationResult.ModerationStatus.APPROVED)
            .build();

        ModerationResult autoApproved = ModerationResult.builder()
            .status(ModerationResult.ModerationStatus.AUTO_APPROVED)
            .build();

        ModerationResult rejected = ModerationResult.builder()
            .status(ModerationResult.ModerationStatus.REJECTED)
            .build();

        assertTrue(approved.isApproved());
        assertTrue(autoApproved.isApproved());
        assertFalse(rejected.isApproved());
    }

    @Test
    void testIsRejected() {
        ModerationResult rejected = ModerationResult.builder()
            .status(ModerationResult.ModerationStatus.REJECTED)
            .build();

        ModerationResult autoRejected = ModerationResult.builder()
            .status(ModerationResult.ModerationStatus.AUTO_REJECTED)
            .build();

        ModerationResult approved = ModerationResult.builder()
            .status(ModerationResult.ModerationStatus.APPROVED)
            .build();

        assertTrue(rejected.isRejected());
        assertTrue(autoRejected.isRejected());
        assertFalse(approved.isRejected());
    }

    @Test
    void testRequiresReview() {
        ModerationResult flagged = ModerationResult.builder()
            .status(ModerationResult.ModerationStatus.FLAGGED)
            .build();

        ModerationResult pending = ModerationResult.builder()
            .status(ModerationResult.ModerationStatus.PENDING_REVIEW)
            .build();

        ModerationResult approved = ModerationResult.builder()
            .status(ModerationResult.ModerationStatus.APPROVED)
            .build();

        assertTrue(flagged.requiresReview());
        assertTrue(pending.requiresReview());
        assertFalse(approved.requiresReview());
    }

    @Test
    void testGetHighestSeverity() {
        ModerationResult.RuleViolation violation1 = ModerationResult.RuleViolation.builder()
            .severity(ModerationRule.RuleSeverity.LOW)
            .build();

        ModerationResult.RuleViolation violation2 = ModerationResult.RuleViolation.builder()
            .severity(ModerationRule.RuleSeverity.HIGH)
            .build();

        ModerationResult result = ModerationResult.builder()
            .violations(List.of(violation1, violation2))
            .build();

        assertEquals(ModerationRule.RuleSeverity.HIGH, result.getHighestSeverity());
    }

    @Test
    void testGetHighestSeverityNoViolations() {
        ModerationResult result = ModerationResult.builder()
            .violations(List.of())
            .build();

        assertNull(result.getHighestSeverity());
    }

    @Test
    void testGetHighestSeverityWithNullViolations() {
        ModerationResult result = ModerationResult.builder()
            .violations(null)
            .build();

        assertNull(result.getHighestSeverity());
    }

    @Test
    void testGetHighestSeverityWithCriticalSeverity() {
        ModerationResult.RuleViolation violation1 = ModerationResult.RuleViolation.builder()
            .severity(ModerationRule.RuleSeverity.LOW)
            .build();

        ModerationResult.RuleViolation violation2 = ModerationResult.RuleViolation.builder()
            .severity(ModerationRule.RuleSeverity.CRITICAL)
            .build();

        ModerationResult result = ModerationResult.builder()
            .violations(List.of(violation1, violation2))
            .build();

        assertEquals(ModerationRule.RuleSeverity.CRITICAL, result.getHighestSeverity());
    }

    @Test
    void testGetHighestSeverityWithAllSeverities() {
        List<ModerationResult.RuleViolation> violations = List.of(
            ModerationResult.RuleViolation.builder()
                .severity(ModerationRule.RuleSeverity.LOW)
                .build(),
            ModerationResult.RuleViolation.builder()
                .severity(ModerationRule.RuleSeverity.MEDIUM)
                .build(),
            ModerationResult.RuleViolation.builder()
                .severity(ModerationRule.RuleSeverity.HIGH)
                .build(),
            ModerationResult.RuleViolation.builder()
                .severity(ModerationRule.RuleSeverity.CRITICAL)
                .build()
        );

        ModerationResult result = ModerationResult.builder()
            .violations(violations)
            .build();

        assertEquals(ModerationRule.RuleSeverity.CRITICAL, result.getHighestSeverity());
    }

    @Test
    void testRuleViolationBuilder() {
        ModerationResult.RuleViolation violation = ModerationResult.RuleViolation.builder()
            .ruleId("rule-123")
            .ruleName("Test Rule")
            .ruleType(ModerationRule.RuleType.KEYWORD)
            .severity(ModerationRule.RuleSeverity.HIGH)
            .description("Test description")
            .matchedText("bad word")
            .position(10)
            .build();

        assertNotNull(violation);
        assertEquals("rule-123", violation.getRuleId());
        assertEquals("Test Rule", violation.getRuleName());
        assertEquals(ModerationRule.RuleType.KEYWORD, violation.getRuleType());
        assertEquals(ModerationRule.RuleSeverity.HIGH, violation.getSeverity());
        assertEquals("Test description", violation.getDescription());
        assertEquals("bad word", violation.getMatchedText());
        assertEquals(10, violation.getPosition());
    }

    @Test
    void testDefaultConfidenceScore() {
        ModerationResult result = ModerationResult.builder()
            .build();

        assertEquals(0.0, result.getConfidenceScore());
    }

    @Test
    void testAllModerationStatuses() {
        // Test APPROVED
        ModerationResult approved = ModerationResult.builder()
            .status(ModerationResult.ModerationStatus.APPROVED)
            .build();
        assertTrue(approved.isApproved());
        assertFalse(approved.isRejected());
        assertFalse(approved.requiresReview());

        // Test AUTO_APPROVED
        ModerationResult autoApproved = ModerationResult.builder()
            .status(ModerationResult.ModerationStatus.AUTO_APPROVED)
            .build();
        assertTrue(autoApproved.isApproved());
        assertFalse(autoApproved.isRejected());
        assertFalse(autoApproved.requiresReview());

        // Test REJECTED
        ModerationResult rejected = ModerationResult.builder()
            .status(ModerationResult.ModerationStatus.REJECTED)
            .build();
        assertFalse(rejected.isApproved());
        assertTrue(rejected.isRejected());
        assertFalse(rejected.requiresReview());

        // Test AUTO_REJECTED
        ModerationResult autoRejected = ModerationResult.builder()
            .status(ModerationResult.ModerationStatus.AUTO_REJECTED)
            .build();
        assertFalse(autoRejected.isApproved());
        assertTrue(autoRejected.isRejected());
        assertFalse(autoRejected.requiresReview());

        // Test FLAGGED
        ModerationResult flagged = ModerationResult.builder()
            .status(ModerationResult.ModerationStatus.FLAGGED)
            .build();
        assertFalse(flagged.isApproved());
        assertFalse(flagged.isRejected());
        assertTrue(flagged.requiresReview());

        // Test PENDING_REVIEW
        ModerationResult pending = ModerationResult.builder()
            .status(ModerationResult.ModerationStatus.PENDING_REVIEW)
            .build();
        assertFalse(pending.isApproved());
        assertFalse(pending.isRejected());
        assertTrue(pending.requiresReview());
    }

    @Test
    void testBuilderWithAllFields() {
        LocalDateTime now = LocalDateTime.now();

        ModerationResult result = ModerationResult.builder()
            .id("result-123")
            .tenantId("tenant-456")
            .contentId("content-789")
            .contentType("POST")
            .content("Test content with keywords")
            .status(ModerationResult.ModerationStatus.FLAGGED)
            .confidenceScore(0.75)
            .violations(List.of())
            .analysisDetails(java.util.Map.of("key", "value"))
            .moderatedBy("SYSTEM")
            .reviewedBy("ADMIN")
            .moderatedAt(now)
            .createdAt(now)
            .reviewNotes("Manual review required")
            .metadata(java.util.Map.of("meta", "data"))
            .build();

        assertNotNull(result);
        assertEquals("result-123", result.getId());
        assertEquals("tenant-456", result.getTenantId());
        assertEquals("content-789", result.getContentId());
        assertEquals("POST", result.getContentType());
        assertEquals("Test content with keywords", result.getContent());
        assertEquals(ModerationResult.ModerationStatus.FLAGGED, result.getStatus());
        assertEquals(0.75, result.getConfidenceScore());
        assertEquals("SYSTEM", result.getModeratedBy());
        assertEquals("ADMIN", result.getReviewedBy());
        assertEquals("Manual review required", result.getReviewNotes());
    }
}
