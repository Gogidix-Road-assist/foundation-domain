package com.gogidix.rapidassist.orchestration.reporting;

import com.gogidix.rapidassist.orchestration.reporting.domain.model.Report;
import com.gogidix.rapidassist.orchestration.reporting.domain.port.in.ReportGenerationPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for ReportGenerationService
 */
@SpringBootTest
@ActiveProfiles("test")
class ReportGenerationServiceTest {

    @Autowired
    private ReportGenerationPort reportGenerationPort;

    @Test
    void contextLoads() {
        assertNotNull(reportGenerationPort);
    }

    @Test
    void generateReport_ShouldCreateReportSuccessfully() {
        // Given
        String reportType = "DISPATCH";
        String outputFormat = "PDF";
        String templateId = "template-001";
        Map<String, Object> parameters = Map.of("startDate", "2025-01-01", "endDate", "2025-01-31");
        String generatedBy = "user-001";
        String tenantId = "tenant-001";

        // When
        Report report = reportGenerationPort.generateReport(
                reportType, outputFormat, templateId, parameters, generatedBy, tenantId);

        // Then
        assertNotNull(report);
        assertNotNull(report.getReportId());
        assertEquals(Report.ReportStatus.PENDING, report.getStatus());
        assertEquals(tenantId, report.getTenantId());
        assertEquals(generatedBy, report.getGeneratedBy());
    }

    @Test
    void getReport_ShouldReturnReportWhenExists() {
        // Given
        String reportType = "PERFORMANCE";
        String outputFormat = "EXCEL";
        String templateId = "template-002";
        Map<String, Object> parameters = Map.of();
        String generatedBy = "user-001";
        String tenantId = "tenant-001";

        Report createdReport = reportGenerationPort.generateReport(
                reportType, outputFormat, templateId, parameters, generatedBy, tenantId);

        // When
        var foundReport = reportGenerationPort.getReport(createdReport.getReportId());

        // Then
        assertTrue(foundReport.isPresent());
        assertEquals(createdReport.getReportId(), foundReport.get().getReportId());
    }

    @Test
    void getReportsByTenant_ShouldReturnOnlyTenantReports() {
        // Given
        String tenantId = "tenant-002";
        reportGenerationPort.generateReport(
                "COMPLIANCE", "CSV", "template-003", Map.of(), "user-001", tenantId);

        // When
        var reports = reportGenerationPort.getReportsByTenant(tenantId);

        // Then
        assertNotNull(reports);
        assertFalse(reports.isEmpty());
        assertTrue(reports.stream().allMatch(r -> r.getTenantId().equals(tenantId)));
    }

    @Test
    void getReportStatistics_ShouldReturnCorrectStatistics() {
        // Given
        String tenantId = "tenant-003";

        // When
        var stats = reportGenerationPort.getReportStatistics(tenantId);

        // Then
        assertNotNull(stats);
        assertTrue(stats.totalReports() >= 0);
        assertTrue(stats.completedReports() >= 0);
    }
}
