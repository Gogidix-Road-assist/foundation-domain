package com.gogidix.rapidassist.ai.analytics.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AnalyticsReport domain model
 */
class AnalyticsReportTest {

    @Test
    void builder_ShouldCreateValidReport() {
        // Given/When
        AnalyticsReport report = AnalyticsReport.builder()
                .id(UUID.randomUUID())
                .tenantId("tenant-123")
                .name("Test Report")
                .description("Test Description")
                .reportType(ReportType.SUMMARY)
                .status(ReportStatus.PENDING)
                .createdBy("user-123")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isActive(true)
                .build();

        // Then
        assertNotNull(report);
        assertEquals("Test Report", report.getName());
        assertEquals("SUMMARY", report.getReportType().name());
        assertEquals("PENDING", report.getStatus().name());
        assertTrue(report.getIsActive());
    }

    @Test
    void builderWithAllFields_ShouldCreateCompleteReport() {
        // Given/When
        AnalyticsReport report = AnalyticsReport.builder()
                .id(UUID.randomUUID())
                .tenantId("tenant-123")
                .name("Test Report")
                .description("Test Description")
                .reportType(ReportType.DETAILED)
                .status(ReportStatus.COMPLETED)
                .createdBy("user-123")
                .updatedBy("user-456")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .completedAt(LocalDateTime.now())
                .parameters(Map.of("param1", "value1"))
                .filters(Map.of("filter1", "value1"))
                .executionTimeMs(500L)
                .recordCount(100)
                .generationCount(1)
                .isScheduled(true)
                .scheduleExpression("0 0 * * *")
                .tags(List.of("tag1", "tag2"))
                .isActive(true)
                .build();

        // Then
        assertNotNull(report);
        assertEquals("Test Report", report.getName());
        assertEquals(500L, report.getExecutionTimeMs());
        assertEquals(100, report.getRecordCount());
        assertEquals(2, report.getTags().size());
        assertTrue(report.getIsScheduled());
    }

    @Test
    void reportTypeEnum_ShouldHaveAllRequiredTypes() {
        // When/Then
        assertEquals(6, ReportType.values().length);
        assertNotNull(ReportType.valueOf("SUMMARY"));
        assertNotNull(ReportType.valueOf("DETAILED"));
        assertNotNull(ReportType.valueOf("TREND_ANALYSIS"));
        assertNotNull(ReportType.valueOf("COMPARATIVE"));
        assertNotNull(ReportType.valueOf("REALTIME"));
        assertNotNull(ReportType.valueOf("CUSTOM"));
    }

    @Test
    void reportStatusEnum_ShouldHaveAllRequiredStatuses() {
        // When/Then
        assertEquals(5, ReportStatus.values().length);
        assertNotNull(ReportStatus.valueOf("PENDING"));
        assertNotNull(ReportStatus.valueOf("GENERATING"));
        assertNotNull(ReportStatus.valueOf("COMPLETED"));
        assertNotNull(ReportStatus.valueOf("FAILED"));
        assertNotNull(ReportStatus.valueOf("SCHEDULED"));
    }

    @Test
    void metricTypeEnum_ShouldHaveAllRequiredTypes() {
        // When/Then
        assertEquals(5, MetricType.values().length);
        assertNotNull(MetricType.valueOf("COUNTER"));
        assertNotNull(MetricType.valueOf("GAUGE"));
        assertNotNull(MetricType.valueOf("HISTOGRAM"));
        assertNotNull(MetricType.valueOf("SUMMARY"));
        assertNotNull(MetricType.valueOf("CUSTOM"));
    }

    @Test
    void chartTypeEnum_ShouldHaveAllRequiredTypes() {
        // When/Then
        assertEquals(10, ChartType.values().length);
        assertNotNull(ChartType.valueOf("LINE"));
        assertNotNull(ChartType.valueOf("BAR"));
        assertNotNull(ChartType.valueOf("PIE"));
        assertNotNull(ChartType.valueOf("DOUGHNUT"));
        assertNotNull(ChartType.valueOf("AREA"));
        assertNotNull(ChartType.valueOf("SCATTER"));
        assertNotNull(ChartType.valueOf("HEATMAP"));
        assertNotNull(ChartType.valueOf("TABLE"));
        assertNotNull(ChartType.valueOf("METRIC_CARD"));
        assertNotNull(ChartType.valueOf("GAUGE"));
    }

    @Test
    void queryStatusEnum_ShouldHaveAllRequiredStatuses() {
        // When/Then
        assertEquals(5, QueryStatus.values().length);
        assertNotNull(QueryStatus.valueOf("PENDING"));
        assertNotNull(QueryStatus.valueOf("EXECUTING"));
        assertNotNull(QueryStatus.valueOf("COMPLETED"));
        assertNotNull(QueryStatus.valueOf("FAILED"));
        assertNotNull(QueryStatus.valueOf("CANCELLED"));
    }
}
