package com.gogidix.rapidassist.ai.dataquality.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DataQualityIssue domain model
 */
class DataQualityIssueTest {

    @Test
    void testCreateIssue() {
        DataQualityIssue issue = DataQualityIssue.builder()
                .id(UUID.randomUUID())
                .tenantId("tenant-1")
                .checkId(UUID.randomUUID())
                .ruleId(UUID.randomUUID())
                .issueType("COMPLETENESS")
                .severity(DataQualityIssue.IssueSeverity.HIGH)
                .entityType("Customer")
                .entityId("customer-123")
                .attributeName("email")
                .description("Email is null")
                .status(DataQualityIssue.IssueStatus.OPEN)
                .detectedAt(LocalDateTime.now())
                .occurrenceCount(1)
                .build();

        assertNotNull(issue);
        assertEquals("COMPLETENESS", issue.getIssueType());
        assertEquals(DataQualityIssue.IssueSeverity.HIGH, issue.getSeverity());
        assertEquals(DataQualityIssue.IssueStatus.OPEN, issue.getStatus());
    }

    @Test
    void testResolveIssue() {
        DataQualityIssue issue = DataQualityIssue.builder()
                .status(DataQualityIssue.IssueStatus.OPEN)
                .detectedAt(LocalDateTime.now())
                .build();

        issue.resolve("admin-user", "Fixed missing email");

        assertEquals(DataQualityIssue.IssueStatus.RESOLVED, issue.getStatus());
        assertEquals("admin-user", issue.getResolvedBy());
        assertEquals("Fixed missing email", issue.getResolutionNotes());
        assertNotNull(issue.getResolvedAt());
    }

    @Test
    void testMarkInProgress() {
        DataQualityIssue issue = DataQualityIssue.builder()
                .status(DataQualityIssue.IssueStatus.OPEN)
                .build();

        issue.markInProgress();

        assertEquals(DataQualityIssue.IssueStatus.IN_PROGRESS, issue.getStatus());
    }

    @Test
    void testIncrementOccurrence() {
        DataQualityIssue issue = DataQualityIssue.builder()
                .occurrenceCount(1)
                .build();

        issue.incrementOccurrence();

        assertEquals(2, issue.getOccurrenceCount());
    }

    @Test
    void testIsResolved() {
        DataQualityIssue issue = DataQualityIssue.builder()
                .status(DataQualityIssue.IssueStatus.RESOLVED)
                .build();

        assertTrue(issue.isResolved());
    }

    @Test
    void testIsCritical() {
        DataQualityIssue issue = DataQualityIssue.builder()
                .severity(DataQualityIssue.IssueSeverity.CRITICAL)
                .build();

        assertTrue(issue.isCritical());
    }

    @Test
    void testIsStale() {
        DataQualityIssue issue = DataQualityIssue.builder()
                .status(DataQualityIssue.IssueStatus.OPEN)
                .detectedAt(LocalDateTime.now().minusDays(15))
                .build();

        assertTrue(issue.isStale(10));
        assertFalse(issue.isStale(20));
    }
}
