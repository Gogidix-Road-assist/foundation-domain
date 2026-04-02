package com.gogidix.rapidassist.ai.report.interfaces.rest.controller;

import com.gogidix.rapidassist.ai.report.application.command.CancelReportCommand;
import com.gogidix.rapidassist.ai.report.application.command.CreateReportCommand;
import com.gogidix.rapidassist.ai.report.application.command.GenerateReportCommand;
import com.gogidix.rapidassist.ai.report.application.dto.ReportDto;
import com.gogidix.rapidassist.ai.report.application.service.ReportApplicationService;
import com.gogidix.rapidassist.ai.report.domain.tenant.TenantContext;
import com.gogidix.rapidassist.ai.report.interfaces.rest.request.CreateReportRequest;
import com.gogidix.rapidassist.ai.report.interfaces.rest.request.GenerateReportRequest;
import com.gogidix.rapidassist.ai.report.interfaces.rest.request.CancelReportRequest;
import com.gogidix.rapidassist.ai.report.interfaces.rest.response.ReportResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for Report operations.
 * API endpoint: /api/v1/reports
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Report Generation", description = "AI Report Generation APIs")
@RequestMapping("/api/v1/reports")
public class ReportRestController {

    private final ReportApplicationService applicationService;

    /**
     * Create a new report generation request.
     * POST /api/v1/reports
     */
    @PostMapping
    @Operation(summary = "Create report", description = "Creates a new report generation request")
    public ResponseEntity<ReportResponse> createReport(
            @Valid @RequestBody CreateReportRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        var command = CreateReportCommand.builder()
                .tenantId(tenantId)
                .name(request.getName())
                .description(request.getDescription())
                .format(request.getFormat())
                .parameters(request.getParameters())
                .templateId(request.getTemplateId())
                .scheduleId(request.getScheduleId())
                .requestedBy(request.getRequestedBy())
                .build();

        ReportDto reportDto = applicationService.createReport(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(reportDto));
    }

    /**
     * Get a report by ID.
     * GET /api/v1/reports/{reportId}
     */
    @GetMapping("/{reportId}")
    @Operation(summary = "Get report", description = "Retrieves a report by ID")
    public ResponseEntity<ReportResponse> getReport(
            @PathVariable UUID reportId,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Include distributions in response") @RequestParam(defaultValue = "false") Boolean includeDistributions) {

        TenantContext.setTenantId(tenantId);

        var query = com.gogidix.rapidassist.ai.report.application.query.GetReportQuery.builder()
                .tenantId(tenantId)
                .reportId(reportId)
                .includeDistributions(includeDistributions)
                .build();

        ReportDto reportDto = applicationService.getReport(query);

        return ResponseEntity.ok(toResponse(reportDto));
    }

    /**
     * Get all reports for a tenant.
     * GET /api/v1/reports
     */
    @GetMapping
    @Operation(summary = "Get tenant reports", description = "Retrieves all reports for a tenant")
    public ResponseEntity<List<ReportResponse>> getTenantReports(
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        List<ReportDto> reports = applicationService.getTenantReports(tenantId);

        return ResponseEntity.ok(reports.stream()
                .map(this::toResponse)
                .toList());
    }

    /**
     * Get reports by status.
     * GET /api/v1/reports/status/{status}
     */
    @GetMapping("/status/{status}")
    @Operation(summary = "Get reports by status", description = "Retrieves reports by status")
    public ResponseEntity<List<ReportResponse>> getReportsByStatus(
            @PathVariable String status,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") Integer size) {

        TenantContext.setTenantId(tenantId);

        var query = com.gogidix.rapidassist.ai.report.application.query.GetReportsByStatusQuery.builder()
                .tenantId(tenantId)
                .status(com.gogidix.rapidassist.ai.report.domain.model.ReportStatus.valueOf(status.toUpperCase()))
                .page(page)
                .size(size)
                .build();

        List<ReportDto> reports = applicationService.getReportsByStatus(query);

        return ResponseEntity.ok(reports.stream()
                .map(this::toResponse)
                .toList());
    }

    /**
     * Generate a report.
     * POST /api/v1/reports/{reportId}/generate
     */
    @PostMapping("/{reportId}/generate")
    @Operation(summary = "Generate report", description = "Starts the report generation process")
    public ResponseEntity<ReportResponse> generateReport(
            @PathVariable UUID reportId,
            @Valid @RequestBody GenerateReportRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        var command = GenerateReportCommand.builder()
                .tenantId(tenantId)
                .reportId(reportId)
                .parameters(request.getParameters())
                .requestedBy(request.getRequestedBy())
                .build();

        ReportDto reportDto = applicationService.generateReport(command);

        return ResponseEntity.ok(toResponse(reportDto));
    }

    /**
     * Cancel a report generation.
     * POST /api/v1/reports/{reportId}/cancel
     */
    @PostMapping("/{reportId}/cancel")
    @Operation(summary = "Cancel report", description = "Cancels a report generation")
    public ResponseEntity<ReportResponse> cancelReport(
            @PathVariable UUID reportId,
            @Valid @RequestBody CancelReportRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        var command = CancelReportCommand.builder()
                .tenantId(tenantId)
                .reportId(reportId)
                .reason(request.getReason())
                .build();

        ReportDto reportDto = applicationService.cancelReport(command);

        return ResponseEntity.ok(toResponse(reportDto));
    }

    /**
     * Delete a report.
     * DELETE /api/v1/reports/{reportId}
     */
    @DeleteMapping("/{reportId}")
    @Operation(summary = "Delete report", description = "Deletes a report")
    public ResponseEntity<Void> deleteReport(
            @PathVariable UUID reportId,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        applicationService.deleteReport(tenantId, reportId);

        return ResponseEntity.noContent().build();
    }

    /**
     * Get pending reports.
     * GET /api/v1/reports/pending
     */
    @GetMapping("/pending")
    @Operation(summary = "Get pending reports", description = "Retrieves all pending reports")
    public ResponseEntity<List<ReportResponse>> getPendingReports(
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        List<ReportDto> reports = applicationService.getPendingReports(tenantId);

        return ResponseEntity.ok(reports.stream()
                .map(this::toResponse)
                .toList());
    }

    /**
     * Convert DTO to Response.
     */
    private ReportResponse toResponse(ReportDto dto) {
        return ReportResponse.builder()
                .id(dto.getId())
                .tenantId(dto.getTenantId())
                .name(dto.getName())
                .description(dto.getDescription())
                .status(dto.getStatus())
                .format(dto.getFormat())
                .parameters(dto.getParameters())
                .fileLocation(dto.getFileLocation())
                .fileSizeBytes(dto.getFileSizeBytes())
                .recordCount(dto.getRecordCount())
                .requestedBy(dto.getRequestedBy())
                .createdBy(dto.getCreatedBy())
                .updatedBy(dto.getUpdatedBy())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .completedAt(dto.getCompletedAt())
                .durationSeconds(dto.getDurationSeconds())
                .readyForDownload(dto.getReadyForDownload())
                .successfulDistributions(dto.getSuccessfulDistributions())
                .failedDistributions(dto.getFailedDistributions())
                .build();
    }
}
