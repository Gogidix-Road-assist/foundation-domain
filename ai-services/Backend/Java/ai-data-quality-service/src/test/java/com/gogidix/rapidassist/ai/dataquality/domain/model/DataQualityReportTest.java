package com.gogidix.rapidassist.ai.dataquality.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DataQualityReport domain model
 */
class DataQualityReportTest {

    @Test
    void testCreateReport() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalRecords", 1000);
        metrics.put("validRecords", 950);

        DataQualityReport report = DataQualityReport.builder()
                .id(UUID.randomUUID())
                .tenantId("tenant-1")
                .reportName("Monthly Quality Report")
                .reportType("SUMMARY")
                .reportPeriodStart(LocalDateTime.now().minusDays(30))
                .reportPeriodEnd(LocalDateTime.now())
                .status(DataQualityReport.ReportStatus.COMPLETED)
                .totalChecks(10)
                .passedChecks(8)
                .failedChecks(2)
                .criticalIssues(0)
                .highIssues(2)
                .mediumIssues(5)
                .lowIssues(10)
                .overallQualityScore(85.0)
                .summaryMetrics(metrics)
                .generatedAt(LocalDateTime.now())
                .build();

        assertNotNull(report);
        assertEquals("Monthly Quality Report", report.getReportName());
        assertEquals(10, report.getTotalChecks());
        assertEquals(85.0, report.getOverallQualityScore());
    }

    @Test
    void testStartGeneration() {
        DataQualityReport report = DataQualityReport.builder()
                .status(DataQualityReport.ReportStatus.GENERATING)
                .build();

        report.startGeneration();

        assertEquals(DataQualityReport.ReportStatus.GENERATING, report.getStatus());
        assertNotNull(report.getGeneratedAt());
    }

    @Test
    void testComplete() {
        DataQualityReport report = DataQualityReport.builder()
                .status(DataQualityReport.ReportStatus.GENERATING)
                .totalChecks(100)
                .passedChecks(85)
                .failedChecks(15)
                .build();

        report.complete();

        assertEquals(DataQualityReport.ReportStatus.COMPLETED, report.getStatus());
        assertEquals(85.0, report.getOverallQualityScore());
    }

    @Test
    void testUpdateStatistics() {
        DataQualityReport report = DataQualityReport.builder()
                .build();

        report.updateStatistics(50, 45, 5);

        assertEquals(50, report.getTotalChecks());
        assertEquals(45, report.getPassedChecks());
        assertEquals(5, report.getFailedChecks());
    }

    @Test
    void testUpdateIssueCounts() {
        DataQualityReport report = DataQualityReport.builder()
                .build();

        report.updateIssueCounts(1, 3, 10, 20);

        assertEquals(1, report.getCriticalIssues());
        assertEquals(3, report.getHighIssues());
        assertEquals(10, report.getMediumIssues());
        assertEquals(20, report.getLowIssues());
    }

    @Test
    void testGetTotalIssues() {
        DataQualityReport report = DataQualityReport.builder()
                .criticalIssues(1)
                .highIssues(2)
                .mediumIssues(5)
                .lowIssues(10)
                .build();

        assertEquals(18, report.getTotalIssues());
    }

    @Test
    void testGetQualityGrade() {
        DataQualityReport reportA = DataQualityReport.builder()
                .overallQualityScore(95.0)
                .build();
        assertEquals("A", reportA.getQualityGrade());

        DataQualityReport reportB = DataQualityReport.builder()
                .overallQualityScore(88.0)
                .build();
        assertEquals("B", reportB.getQualityGrade());

        DataQualityReport reportC = DataQualityReport.builder()
                .overallQualityScore(75.0)
                .build();
        assertEquals("C", reportC.getQualityGrade());

        DataQualityReport reportF = DataQualityReport.builder()
                .overallQualityScore(45.0)
                .build();
        assertEquals("F", reportF.getQualityGrade());
    }

    @Test
    void testIsQualityAcceptable() {
        DataQualityReport report = DataQualityReport.builder()
                .overallQualityScore(85.0)
                .build();

        assertTrue(report.isQualityAcceptable(80.0));
        assertFalse(report.isQualityAcceptable(90.0));
    }
}
