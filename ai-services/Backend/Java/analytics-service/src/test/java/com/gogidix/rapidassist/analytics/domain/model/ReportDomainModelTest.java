package com.gogidix.rapidassist.analytics.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for Report domain model.
 * Tests all business logic, builders, and validation.
 */
@DisplayName("Report Domain Model Tests")
class ReportDomainModelTest {

    @Test
    @DisplayName("Should create report with all fields using builder")
    void shouldCreateReportWithBuilder() {
        // Given
        UUID id = UUID.randomUUID();
        String tenantId = "tenant-123";
        LocalDateTime now = LocalDateTime.now();

        Report.ChartConfiguration chart = Report.ChartConfiguration.builder()
                .chartType("bar")
                .title("Sales Chart")
                .xAxis("product")
                .yAxis("revenue")
                .order(1)
                .build();

        // When
        Report report = Report.builder()
                .id(id)
                .tenantId(tenantId)
                .reportName("Sales Report")
                .reportType("SUMMARY")
                .reportCategory("BUSINESS")
                .startDate(now.minusDays(30))
                .endDate(now)
                .reportData(Map.of("totalSales", 100000))
                .includedMetrics(List.of("revenue", "orders"))
                .charts(List.of(chart))
                .format("PDF")
                .status("COMPLETED")
                .totalViews(100)
                .lastViewedAt(now)
                .createdBy("john.doe@example.com")
                .lastModifiedBy("jane.doe@example.com")
                .description("Monthly sales report")
                .schedule("0 0 0 * * ?")
                .recipients(List.of("manager@example.com"))
                .filters(Map.of("region", "US"))
                .metadata(Map.of("version", "1.0"))
                .createdAt(now.minusDays(1))
                .updatedAt(now)
                .build();

        // Then
        assertNotNull(report);
        assertEquals(id, report.getId());
        assertEquals(tenantId, report.getTenantId());
        assertEquals("Sales Report", report.getReportName());
        assertEquals("SUMMARY", report.getReportType());
        assertEquals("BUSINESS", report.getReportCategory());
        assertEquals("COMPLETED", report.getStatus());
        assertEquals(100, report.getTotalViews());
        assertEquals(1, report.getCharts().size());
    }

    @Test
    @DisplayName("Should check if report is scheduled")
    void shouldCheckIfReportIsScheduled() {
        // Given
        Report report = Report.builder()
                .schedule("0 0 0 * * ?")
                .build();

        // When
        boolean scheduled = report.isScheduled();

        // Then
        assertTrue(scheduled);
    }

    @Test
    @DisplayName("Should check if report is not scheduled")
    void shouldCheckIfReportIsNotScheduled() {
        // Given
        Report report = Report.builder()
                .schedule(null)
                .build();

        // When
        boolean scheduled = report.isScheduled();

        // Then
        assertFalse(scheduled);
    }

    @Test
    @DisplayName("Should check if report is ready for generation")
    void shouldCheckIfReportIsReadyForGeneration() {
        // Given
        Report report = Report.builder()
                .status("PENDING")
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now())
                .build();

        // When
        boolean ready = report.isReadyForGeneration();

        // Then
        assertTrue(ready);
    }

    @Test
    @DisplayName("Should mark report as generated")
    void shouldMarkReportAsGenerated() {
        // Given
        Report report = Report.builder()
                .status("PENDING")
                .build();
        String generatedBy = "system";

        // When
        report.markAsGenerated(generatedBy);

        // Then
        assertEquals("COMPLETED", report.getStatus());
        assertEquals(generatedBy, report.getLastModifiedBy());
        assertNotNull(report.getUpdatedAt());
    }

    @Test
    @DisplayName("Should increment view count")
    void shouldIncrementViewCount() {
        // Given
        Report report = Report.builder()
                .totalViews(100)
                .build();

        // When
        report.incrementViewCount();

        // Then
        assertEquals(101, report.getTotalViews());
        assertNotNull(report.getLastViewedAt());
    }

    @Test
    @DisplayName("Should increment view count from null")
    void shouldIncrementViewCountFromNull() {
        // Given
        Report report = Report.builder()
                .totalViews(null)
                .build();

        // When
        report.incrementViewCount();

        // Then
        assertEquals(1, report.getTotalViews());
        assertNotNull(report.getLastViewedAt());
    }

    @Test
    @DisplayName("Should check if report is expired")
    void shouldCheckIfReportIsExpired() {
        // Given
        Report report = Report.builder()
                .endDate(LocalDateTime.now().minusMonths(2))
                .build();

        // When
        boolean expired = report.isExpired();

        // Then
        assertTrue(expired);
    }

    @Test
    @DisplayName("Should not be expired when endDate is null")
    void shouldNotBeExpiredWhenEndDateIsNull() {
        // Given
        Report report = Report.builder()
                .endDate(null)
                .build();

        // When
        boolean expired = report.isExpired();

        // Then
        assertFalse(expired);
    }

    @Test
    @DisplayName("Should get report age in days")
    void shouldGetReportAgeInDays() {
        // Given
        LocalDateTime createdAt = LocalDateTime.now().minusDays(10);
        Report report = Report.builder()
                .createdAt(createdAt)
                .build();

        // When
        long age = report.getAgeInDays();

        // Then
        assertEquals(10, age);
    }

    @Test
    @DisplayName("Should return zero age when createdAt is null")
    void shouldReturnZeroAgeWhenCreatedAtIsNull() {
        // Given
        Report report = Report.builder()
                .createdAt(null)
                .build();

        // When
        long age = report.getAgeInDays();

        // Then
        assertEquals(0, age);
    }

    @Test
    @DisplayName("Should create chart configuration")
    void shouldCreateChartConfiguration() {
        // Given
        Report.ChartConfiguration chart = Report.ChartConfiguration.builder()
                .chartType("line")
                .title("Trend Analysis")
                .xAxis("date")
                .yAxis("value")
                .order(1)
                .configuration(Map.of("showLegend", true))
                .build();

        // Then
        assertNotNull(chart);
        assertEquals("line", chart.getChartType());
        assertEquals("Trend Analysis", chart.getTitle());
        assertEquals("date", chart.getXAxis());
        assertEquals("value", chart.getYAxis());
        assertEquals(1, chart.getOrder());
        assertTrue(chart.getConfiguration().containsKey("showLegend"));
    }

    @Test
    @DisplayName("Should handle chart configuration with no-args constructor")
    void shouldHandleChartConfigurationWithNoArgsConstructor() {
        // When
        Report.ChartConfiguration chart = new Report.ChartConfiguration();

        // Then
        assertNotNull(chart);
        assertNull(chart.getChartType());
        assertNull(chart.getTitle());
    }

    @Test
    @DisplayName("Should handle chart configuration with all-args constructor")
    void shouldHandleChartConfigurationWithAllArgsConstructor() {
        // Given
        Report.ChartConfiguration chart = new Report.ChartConfiguration(
                "bar", "Sales", "product", "revenue",
                Map.of("color", "blue"), 1
        );

        // Then
        assertEquals("bar", chart.getChartType());
        assertEquals("Sales", chart.getTitle());
        assertEquals("product", chart.getXAxis());
        assertEquals("revenue", chart.getYAxis());
        assertEquals(1, chart.getOrder());
    }

    @Test
    @DisplayName("Should handle multiple charts in report")
    void shouldHandleMultipleChartsInReport() {
        // Given
        Report.ChartConfiguration chart1 = Report.ChartConfiguration.builder()
                .chartType("bar").order(1).build();
        Report.ChartConfiguration chart2 = Report.ChartConfiguration.builder()
                .chartType("line").order(2).build();
        Report.ChartConfiguration chart3 = Report.ChartConfiguration.builder()
                .chartType("pie").order(3).build();

        Report report = Report.builder()
                .charts(List.of(chart1, chart2, chart3))
                .build();

        // Then
        assertNotNull(report.getCharts());
        assertEquals(3, report.getCharts().size());
    }

    @Test
    @DisplayName("Should handle empty chart list")
    void shouldHandleEmptyChartList() {
        // Given
        Report report = Report.builder()
                .charts(List.of())
                .build();

        // Then
        assertNotNull(report.getCharts());
        assertTrue(report.getCharts().isEmpty());
    }

    @Test
    @DisplayName("Should handle different report statuses")
    void shouldHandleDifferentReportStatuses() {
        // Given
        Report pending = Report.builder().status("PENDING").build();
        Report generating = Report.builder().status("GENERATING").build();
        Report completed = Report.builder().status("COMPLETED").build();
        Report failed = Report.builder().status("FAILED").build();

        // Then
        assertEquals("PENDING", pending.getStatus());
        assertEquals("GENERATING", generating.getStatus());
        assertEquals("COMPLETED", completed.getStatus());
        assertEquals("FAILED", failed.getStatus());
    }

    @Test
    @DisplayName("Should handle different report formats")
    void shouldHandleDifferentReportFormats() {
        // Given
        Report pdf = Report.builder().format("PDF").build();
        Report excel = Report.builder().format("EXCEL").build();
        Report html = Report.builder().format("HTML").build();

        // Then
        assertEquals("PDF", pdf.getFormat());
        assertEquals("EXCEL", excel.getFormat());
        assertEquals("HTML", html.getFormat());
    }
}
