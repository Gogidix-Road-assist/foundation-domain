package com.gogidix.rapidassist.ai.analytics.interfaces.rest.controller;

import com.gogidix.rapidassist.ai.analytics.application.command.*;
import com.gogidix.rapidassist.ai.analytics.application.dto.*;
import com.gogidix.rapidassist.ai.analytics.application.query.*;
import com.gogidix.rapidassist.ai.analytics.application.service.*;
import com.gogidix.rapidassist.ai.analytics.interfaces.rest.request.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * REST controller for Analytics operations
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics API", description = "API for business intelligence and analytics operations")
public class AnalyticsRestController {

    private final AnalyticsReportApplicationService reportService;
    private final MetricDefinitionApplicationService metricService;
    private final DashboardApplicationService dashboardService;
    private final DataQueryApplicationService queryService;

    /**
     * Generate analytics report
     */
    @PostMapping("/reports/generate")
    @Operation(summary = "Generate analytics report", description = "Generate a new analytics report with specified parameters")
    public ResponseEntity<AnalyticsReportDto> generateReport(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader(value = "X-User-ID", defaultValue = "system") String userId,
            @Valid @RequestBody GenerateReportRequest request) {

        GenerateReportCommand command = GenerateReportCommand.builder()
                .tenantId(tenantId)
                .name(request.getName())
                .description(request.getDescription())
                .reportType(request.getReportType())
                .triggeredBy(userId)
                .parameters(request.getParameters())
                .filters(request.getFilters())
                .metricIds(request.getMetricIds())
                .dashboardIds(request.getDashboardIds())
                .isScheduled(request.getIsScheduled())
                .scheduleExpression(request.getScheduleExpression())
                .scheduledFor(request.getScheduledFor() != null ? LocalDateTime.parse(request.getScheduledFor()) : null)
                .tags(request.getTags())
                .build();

        AnalyticsReportDto report = reportService.generateReport(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(report);
    }

    /**
     * Get report by ID
     */
    @GetMapping("/reports/{reportId}")
    @Operation(summary = "Get report by ID", description = "Retrieve a specific analytics report by its ID")
    public ResponseEntity<AnalyticsReportDto> getReport(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Report ID") @PathVariable String reportId) {

        GetReportQuery query = GetReportQuery.builder()
                .reportId(reportId)
                .tenantId(tenantId)
                .build();

        AnalyticsReportDto report = reportService.getReport(query);
        return ResponseEntity.ok(report);
    }

    /**
     * List all reports
     */
    @GetMapping("/reports")
    @Operation(summary = "List all reports", description = "Retrieve all analytics reports with pagination and filters")
    public ResponseEntity<Page<AnalyticsReportDto>> listReports(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Report status filter") @RequestParam(required = false) String status,
            @Parameter(description = "Report type filter") @RequestParam(required = false) String reportType,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "Sort by") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDirection) {

        ListReportsQuery query = ListReportsQuery.builder()
                .tenantId(tenantId)
                .status(status)
                .reportType(reportType)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();

        Page<AnalyticsReportDto> reports = reportService.listReports(query);
        return ResponseEntity.ok(reports);
    }

    /**
     * Delete report
     */
    @DeleteMapping("/reports/{reportId}")
    @Operation(summary = "Delete report", description = "Delete an analytics report by its ID")
    public ResponseEntity<Void> deleteReport(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Report ID") @PathVariable String reportId) {

        reportService.deleteReport(reportId, tenantId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Create metric definition
     */
    @PostMapping("/metrics")
    @Operation(summary = "Create metric definition", description = "Create a new metric definition")
    public ResponseEntity<MetricDefinitionDto> createMetric(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader(value = "X-User-ID", defaultValue = "system") String userId,
            @Valid @RequestBody CreateMetricRequest request) {

        CreateMetricCommand command = CreateMetricCommand.builder()
                .tenantId(tenantId)
                .name(request.getName())
                .code(request.getCode())
                .description(request.getDescription())
                .metricType(request.getMetricType())
                .dataSource(request.getDataSource())
                .query(request.getQuery())
                .configuration(request.getConfiguration())
                .unit(request.getUnit())
                .aggregationFunction(request.getAggregationFunction())
                .createdBy(userId)
                .tags(request.getTags())
                .isActive(request.getIsActive())
                .category(request.getCategory())
                .thresholdWarning(request.getThresholdWarning())
                .thresholdCritical(request.getThresholdCritical())
                .formatPattern(request.getFormatPattern())
                .build();

        MetricDefinitionDto metric = metricService.createMetric(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(metric);
    }

    /**
     * Get metric by ID
     */
    @GetMapping("/metrics/{metricId}")
    @Operation(summary = "Get metric by ID", description = "Retrieve a specific metric definition by its ID")
    public ResponseEntity<MetricDefinitionDto> getMetric(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Metric ID") @PathVariable String metricId) {

        GetMetricQuery query = GetMetricQuery.builder()
                .metricId(metricId)
                .tenantId(tenantId)
                .build();

        MetricDefinitionDto metric = metricService.getMetric(query);
        return ResponseEntity.ok(metric);
    }

    /**
     * List all metrics
     */
    @GetMapping("/metrics")
    @Operation(summary = "List all metrics", description = "Retrieve all metric definitions with filters")
    public ResponseEntity<java.util.List<MetricDefinitionDto>> listMetrics(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Active filter") @RequestParam(required = false) Boolean isActive,
            @Parameter(description = "Category filter") @RequestParam(required = false) String category) {

        java.util.List<MetricDefinitionDto> metrics = metricService.listMetrics(tenantId, isActive, category);
        return ResponseEntity.ok(metrics);
    }

    /**
     * Delete metric
     */
    @DeleteMapping("/metrics/{metricId}")
    @Operation(summary = "Delete metric", description = "Delete a metric definition by its ID")
    public ResponseEntity<Void> deleteMetric(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Metric ID") @PathVariable String metricId) {

        metricService.deleteMetric(metricId, tenantId);
        return ResponseEntity.noContent().build();
    }

    /**
     * List all dashboards
     */
    @GetMapping("/dashboards")
    @Operation(summary = "List all dashboards", description = "Retrieve all dashboards with filters")
    public ResponseEntity<Page<DashboardDto>> listDashboards(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Public filter") @RequestParam(required = false) Boolean isPublic,
            @Parameter(description = "Active filter") @RequestParam(required = false) Boolean isActive,
            @Parameter(description = "Category filter") @RequestParam(required = false) String category,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") Integer size) {

        ListDashboardsQuery query = ListDashboardsQuery.builder()
                .tenantId(tenantId)
                .isPublic(isPublic)
                .isActive(isActive)
                .category(category)
                .page(page)
                .size(size)
                .build();

        Page<DashboardDto> dashboards = dashboardService.listDashboards(query);
        return ResponseEntity.ok(dashboards);
    }

    /**
     * Create dashboard
     */
    @PostMapping("/dashboards")
    @Operation(summary = "Create dashboard", description = "Create a new dashboard")
    public ResponseEntity<DashboardDto> createDashboard(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader(value = "X-User-ID", defaultValue = "system") String userId,
            @Valid @RequestBody CreateDashboardRequest request) {

        CreateDashboardCommand command = CreateDashboardCommand.builder()
                .tenantId(tenantId)
                .name(request.getName())
                .description(request.getDescription())
                .layout(request.getLayout())
                .theme(request.getTheme())
                .isPublic(request.getIsPublic())
                .createdBy(userId)
                .chartIds(request.getChartIds())
                .filters(request.getFilters())
                .refreshInterval(request.getRefreshInterval())
                .autoRefresh(request.getAutoRefresh())
                .tags(request.getTags())
                .isActive(request.getIsActive())
                .displayOrder(request.getDisplayOrder())
                .category(request.getCategory())
                .build();

        DashboardDto dashboard = dashboardService.createDashboard(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(dashboard);
    }

    /**
     * Get dashboard by ID
     */
    @GetMapping("/dashboards/{dashboardId}")
    @Operation(summary = "Get dashboard by ID", description = "Retrieve a specific dashboard by its ID")
    public ResponseEntity<DashboardDto> getDashboard(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Dashboard ID") @PathVariable String dashboardId) {

        DashboardDto dashboard = dashboardService.getDashboard(dashboardId, tenantId);
        return ResponseEntity.ok(dashboard);
    }

    /**
     * Update dashboard
     */
    @PutMapping("/dashboards/{dashboardId}")
    @Operation(summary = "Update dashboard", description = "Update an existing dashboard")
    public ResponseEntity<DashboardDto> updateDashboard(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader(value = "X-User-ID", defaultValue = "system") String userId,
            @Parameter(description = "Dashboard ID") @PathVariable String dashboardId,
            @Valid @RequestBody UpdateDashboardRequest request) {

        UpdateDashboardCommand command = UpdateDashboardCommand.builder()
                .dashboardId(dashboardId)
                .tenantId(tenantId)
                .name(request.getName())
                .description(request.getDescription())
                .layout(request.getLayout())
                .theme(request.getTheme())
                .isPublic(request.getIsPublic())
                .updatedBy(userId)
                .chartIds(request.getChartIds())
                .filters(request.getFilters())
                .refreshInterval(request.getRefreshInterval())
                .autoRefresh(request.getAutoRefresh())
                .tags(request.getTags())
                .isActive(request.getIsActive())
                .displayOrder(request.getDisplayOrder())
                .category(request.getCategory())
                .build();

        DashboardDto dashboard = dashboardService.updateDashboard(command);
        return ResponseEntity.ok(dashboard);
    }

    /**
     * Delete dashboard
     */
    @DeleteMapping("/dashboards/{dashboardId}")
    @Operation(summary = "Delete dashboard", description = "Delete a dashboard by its ID")
    public ResponseEntity<Void> deleteDashboard(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Dashboard ID") @PathVariable String dashboardId) {

        dashboardService.deleteDashboard(dashboardId, tenantId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Execute analytics query
     */
    @PostMapping("/data/query")
    @Operation(summary = "Execute analytics query", description = "Execute a data query against analytics data sources")
    public ResponseEntity<DataQueryDto> executeQuery(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader(value = "X-User-ID", defaultValue = "system") String userId,
            @Valid @RequestBody ExecuteQueryRequest request) {

        ExecuteQueryCommand command = ExecuteQueryCommand.builder()
                .tenantId(tenantId)
                .query(request.getQuery())
                .dataSource(request.getDataSource())
                .parameters(request.getParameters())
                .filters(request.getFilters())
                .groupBy(request.getGroupBy())
                .orderBy(request.getOrderBy())
                .limit(request.getLimit())
                .offset(request.getOffset())
                .executedBy(userId)
                .isCached(request.getIsCached())
                .build();

        DataQueryDto result = queryService.executeQuery(command);
        return ResponseEntity.ok(result);
    }

    /**
     * Get query by ID
     */
    @GetMapping("/data/queries/{queryId}")
    @Operation(summary = "Get query by ID", description = "Retrieve a specific data query by its ID")
    public ResponseEntity<DataQueryDto> getQuery(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Query ID") @PathVariable String queryId) {

        DataQueryDto query = queryService.getQuery(queryId, tenantId);
        return ResponseEntity.ok(query);
    }

    /**
     * List all queries
     */
    @GetMapping("/data/queries")
    @Operation(summary = "List all queries", description = "Retrieve all data queries with filters")
    public ResponseEntity<java.util.List<DataQueryDto>> listQueries(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Active filter") @RequestParam(required = false) Boolean isActive,
            @Parameter(description = "Category filter") @RequestParam(required = false) String category) {

        java.util.List<DataQueryDto> queries = queryService.listQueries(tenantId, isActive, category);
        return ResponseEntity.ok(queries);
    }
}
