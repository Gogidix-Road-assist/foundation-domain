package com.gogidix.rapidassist.reporting.read.model.service.adapters.in.web;

import com.gogidix.rapidassist.reporting.read.model.service.application.service.ReportService;
import com.gogidix.rapidassist.reporting.read.model.service.domain.model.Report;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for Report operations with CRITICAL tenant isolation.
 * <p>
 * CATASTROPHIC SECURITY WARNING: Reports aggregate data across the system.
 * Without tenant isolation, Tenant A could see ALL of Tenant B's data.
 * <p>
 * Tenant ID is automatically extracted from the RequestContext to ensure
 * ALL operations are tenant-isolated.
 *
 * @author Gogidix
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/reports")
@Tag(name = "Reports", description = "Report management endpoints with CRITICAL tenant isolation")
public class ReportController {

    private static final Logger log = LoggerFactory.getLogger(ReportController.class);

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Extract tenant ID from RequestContext.
     * <p>
     * CRITICAL: This method MUST be called by all endpoint methods to ensure
     * tenant isolation.
     *
     * @return Optional tenant ID from RequestContext
     */
    private Optional<String> extractTenantId() {
        return RequestContextHolder.get()
                .map(RequestContext::tenantId);
    }

    /**
     * Get tenant ID or throw exception.
     *
     * @return tenant ID
     * @throws IllegalStateException if tenantId is not present
     */
    private String requireTenantId() {
        return extractTenantId()
                .orElseThrow(() -> {
                    log.error("TenantId not found in RequestContext - CRITICAL SECURITY VIOLATION");
                    return new IllegalStateException("TenantId is required but not present in request context");
                });
    }

    /**
     * Create a new report.
     * <p>
     * Tenant ID is automatically extracted from the request context.
     *
     * @param request The report creation request
     * @return The created report
     */
    @PostMapping
    @Operation(
            summary = "Create report",
            description = "Create a new report for the current tenant. Tenant ID is automatically extracted from request context."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Report created successfully",
                    content = @Content(schema = @Schema(implementation = Report.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - missing or invalid tenant context"
            )
    })
    public ResponseEntity<Report> createReport(@Valid @RequestBody CreateReportRequest request) {
        String tenantId = requireTenantId();
        log.info("Creating report for tenant: {}", tenantId);

        Report report = reportService.createReport(
                tenantId,
                request.reportType(),
                request.title(),
                request.generatedBy()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(report);
    }

    /**
     * Get a report by ID.
     * <p>
     * CRITICAL: Tenant ID is automatically extracted and validated to ensure
     * the report belongs to the requesting tenant.
     *
     * @param id The report ID
     * @return The report if found and belongs to tenant
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Get report by ID",
            description = "Get a report by ID. Tenant ID is automatically validated."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Report found",
                    content = @Content(schema = @Schema(implementation = Report.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Report not found or does not belong to tenant"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - missing or invalid tenant context"
            )
    })
    public ResponseEntity<Report> getReport(
            @Parameter(description = "Report ID", required = true)
            @PathVariable String id
    ) {
        String tenantId = requireTenantId();
        log.info("Fetching report: {} for tenant: {}", id, tenantId);

        return reportService.getReportById(id, tenantId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all reports for the current tenant.
     * <p>
     * CRITICAL: All results are automatically filtered by tenant ID.
     *
     * @return List of reports for the tenant
     */
    @GetMapping
    @Operation(
            summary = "Get all reports",
            description = "Get all reports for the current tenant. Results are automatically filtered by tenant ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Reports retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Report.class, type = "array"))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - missing or invalid tenant context"
            )
    })
    public ResponseEntity<List<Report>> getReports(
            @Parameter(description = "Filter by report type")
            @RequestParam(required = false) String reportType,
            @Parameter(description = "Filter by status")
            @RequestParam(required = false) Report.ReportStatus status
    ) {
        String tenantId = requireTenantId();
        log.info("Fetching reports for tenant: {} with filters - type: {}, status: {}", tenantId, reportType, status);

        List<Report> reports;
        if (reportType != null && status != null) {
            reports = reportService.getReportsByTenantAndTypeAndStatus(tenantId, reportType, status);
        } else if (reportType != null) {
            reports = reportService.getReportsByTenantAndType(tenantId, reportType);
        } else if (status != null) {
            reports = reportService.getReportsByTenantAndStatus(tenantId, status);
        } else {
            reports = reportService.getReportsByTenant(tenantId);
        }

        return ResponseEntity.ok(reports);
    }

    /**
     * Get active (non-expired) reports for the current tenant.
     *
     * @return List of active reports
     */
    @GetMapping("/active")
    @Operation(
            summary = "Get active reports",
            description = "Get all active (non-expired) reports for the current tenant."
    )
    public ResponseEntity<List<Report>> getActiveReports() {
        String tenantId = requireTenantId();
        log.info("Fetching active reports for tenant: {}", tenantId);

        List<Report> reports = reportService.getActiveReportsByTenant(tenantId);
        return ResponseEntity.ok(reports);
    }

    /**
     * Get ready (completed and not expired) reports for the current tenant.
     *
     * @return List of ready reports
     */
    @GetMapping("/ready")
    @Operation(
            summary = "Get ready reports",
            description = "Get all ready (completed and not expired) reports for the current tenant."
    )
    public ResponseEntity<List<Report>> getReadyReports() {
        String tenantId = requireTenantId();
        log.info("Fetching ready reports for tenant: {}", tenantId);

        List<Report> reports = reportService.getReadyReportsByTenant(tenantId);
        return ResponseEntity.ok(reports);
    }

    /**
     * Get exported reports for the current tenant.
     *
     * @return List of exported reports
     */
    @GetMapping("/exported")
    @Operation(
            summary = "Get exported reports",
            description = "Get all exported reports (with file path) for the current tenant."
    )
    public ResponseEntity<List<Report>> getExportedReports() {
        String tenantId = requireTenantId();
        log.info("Fetching exported reports for tenant: {}", tenantId);

        List<Report> reports = reportService.getExportedReportsByTenant(tenantId);
        return ResponseEntity.ok(reports);
    }

    /**
     * Get reports by date range for the current tenant.
     *
     * @param startDate Start of date range
     * @param endDate   End of date range
     * @return List of reports in the date range
     */
    @GetMapping("/by-date-range")
    @Operation(
            summary = "Get reports by date range",
            description = "Get reports for the current tenant within the specified date range."
    )
    public ResponseEntity<List<Report>> getReportsByDateRange(
            @Parameter(description = "Start date (ISO 8601)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @Parameter(description = "End date (ISO 8601)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate
    ) {
        String tenantId = requireTenantId();
        log.info("Fetching reports for tenant: {} between {} and {}", tenantId, startDate, endDate);

        List<Report> reports = reportService.getReportsByTenantAndDateRange(tenantId, startDate, endDate);
        return ResponseEntity.ok(reports);
    }

    /**
     * Get report statistics for the current tenant.
     *
     * @return Report statistics
     */
    @GetMapping("/statistics")
    @Operation(
            summary = "Get report statistics",
            description = "Get report statistics for the current tenant."
    )
    public ResponseEntity<ReportService.ReportStatistics> getStatistics() {
        String tenantId = requireTenantId();
        log.info("Fetching report statistics for tenant: {}", tenantId);

        ReportService.ReportStatistics stats = reportService.getReportStatistics(tenantId);
        return ResponseEntity.ok(stats);
    }

    /**
     * Delete a report by ID.
     *
     * @param id The report ID
     * @return Response indicating success or failure
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete report",
            description = "Delete a report by ID. Tenant ID is automatically validated."
    )
    public ResponseEntity<Void> deleteReport(@PathVariable String id) {
        String tenantId = requireTenantId();
        log.warn("Deleting report: {} for tenant: {}", id, tenantId);

        boolean deleted = reportService.deleteReport(id, tenantId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    /**
     * Exception handler for IllegalStateException (missing tenantId).
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ProblemDetail> handleIllegalStateException(IllegalStateException ex) {
        log.error("Tenant context error: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                ex.getMessage()
        );
        problemDetail.setType(java.net.URI.create("https://api.gogidix.com/errors/tenant-context"));
        problemDetail.setTitle("Unauthorized - Missing Tenant Context");
        problemDetail.setProperty("timestamp", Instant.now().toString());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problemDetail);
    }

    // Request DTOs

    public record CreateReportRequest(
            @NotBlank(message = "Report type is required")
            String reportType,
            @NotBlank(message = "Title is required")
            String title,
            String description,
            @NotBlank(message = "Generated by is required")
            String generatedBy,
            String parameters
    ) {}
}
