package com.gogidix.rapidassist.ai.dataquality.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DataQualityCheck domain model
 */
class DataQualityCheckTest {

    @Test
    void testCreateCheck() {
        DataQualityCheck check = DataQualityCheck.builder()
                .id(UUID.randomUUID())
                .tenantId("tenant-1")
                .ruleId(UUID.randomUUID())
                .checkName("Email Validation Check")
                .entityType("Customer")
                .status(DataQualityCheck.CheckStatus.PENDING)
                .build();

        assertNotNull(check);
        assertEquals("Email Validation Check", check.getCheckName());
        assertEquals(DataQualityCheck.CheckStatus.PENDING, check.getStatus());
    }

    @Test
    void testStartCheck() {
        DataQualityCheck check = DataQualityCheck.builder()
                .status(DataQualityCheck.CheckStatus.PENDING)
                .build();

        check.start();

        assertEquals(DataQualityCheck.CheckStatus.RUNNING, check.getStatus());
        assertNotNull(check.getExecutedAt());
    }

    @Test
    void testCompleteCheck() {
        DataQualityCheck check = DataQualityCheck.builder()
                .status(DataQualityCheck.CheckStatus.RUNNING)
                .executedAt(LocalDateTime.now())
                .build();

        check.complete(95, 5, 100);

        assertEquals(DataQualityCheck.CheckStatus.COMPLETED, check.getStatus());
        assertEquals(95, check.getPassedRecords());
        assertEquals(5, check.getFailedRecords());
        assertEquals(100, check.getTotalRecords());
        assertEquals(95.0, check.getPassPercentage());
    }

    @Test
    void testFailCheck() {
        DataQualityCheck check = DataQualityCheck.builder()
                .status(DataQualityCheck.CheckStatus.RUNNING)
                .executedAt(LocalDateTime.now())
                .build();

        check.fail("Database connection error");

        assertEquals(DataQualityCheck.CheckStatus.FAILED, check.getStatus());
        assertEquals("Database connection error", check.getErrorMessage());
        assertNotNull(check.getCompletedAt());
    }

    @Test
    void testIsPassed() {
        DataQualityCheck check = DataQualityCheck.builder()
                .status(DataQualityCheck.CheckStatus.COMPLETED)
                .failedRecords(0)
                .build();

        assertTrue(check.isPassed());
    }

    @Test
    void testIsFailed() {
        DataQualityCheck check1 = DataQualityCheck.builder()
                .status(DataQualityCheck.CheckStatus.FAILED)
                .build();

        assertTrue(check1.isFailed());

        DataQualityCheck check2 = DataQualityCheck.builder()
                .status(DataQualityCheck.CheckStatus.COMPLETED)
                .failedRecords(5)
                .build();

        assertTrue(check2.isFailed());
    }

    @Test
    void testGetPassRate() {
        DataQualityCheck check = DataQualityCheck.builder()
                .passPercentage(85.5)
                .build();

        assertEquals(85.5, check.getPassRate());
    }
}
