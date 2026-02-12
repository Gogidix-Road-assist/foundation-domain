package com.gogidix.rapidassist.analytics.interfaces.rest.controller;

import com.gogidix.rapidassist.analytics.application.dto.ReportDto;
import com.gogidix.rapidassist.analytics.application.service.ReportApplicationService;
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
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Report generation APIs")
public class ReportController {

    private final ReportApplicationService applicationService;

    @PostMapping
    @Operation(summary = "Create report", description = "Creates a new report")
    public ResponseEntity<ReportDto> createReport(
            @Valid @RequestBody ReportDto request,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        log.info("Creating report for tenant: {}", tenantId);
        ReportDto result = applicationService.createReport(tenantId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get report", description = "Retrieves report by ID")
    public ResponseEntity<ReportDto> getReport(
            @PathVariable UUID id,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        log.info("Getting report: {} for tenant: {}", id, tenantId);
        ReportDto result = applicationService.getReport(tenantId, id);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    @Operation(summary = "Get all reports", description = "Retrieves all reports for a tenant")
    public ResponseEntity<List<ReportDto>> getReportsByTenant(
            @RequestHeader("X-Tenant-ID") String tenantId) {

        log.info("Getting all reports for tenant: {}", tenantId);
        List<ReportDto> results = applicationService.getReportsByTenant(tenantId);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/type/{reportType}")
    @Operation(summary = "Get reports by type", description = "Retrieves reports by type")
    public ResponseEntity<List<ReportDto>> getReportsByType(
            @PathVariable String reportType,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        log.info("Getting reports by type: {} for tenant: {}", reportType, tenantId);
        List<ReportDto> results = applicationService.getReportsByType(tenantId, reportType);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get reports by status", description = "Retrieves reports by status")
    public ResponseEntity<List<ReportDto>> getReportsByStatus(
            @PathVariable String status,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        log.info("Getting reports by status: {} for tenant: {}", status, tenantId);
        List<ReportDto> results = applicationService.getReportsByStatus(tenantId, status);
        return ResponseEntity.ok(results);
    }

    @PostMapping("/{id}/generate")
    @Operation(summary = "Generate report", description = "Triggers report generation")
    public ResponseEntity<ReportDto> generateReport(
            @PathVariable UUID id,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        log.info("Generating report: {} for tenant: {}", id, tenantId);
        ReportDto result = applicationService.generateReport(tenantId, id);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete report", description = "Deletes report by ID")
    public ResponseEntity<Void> deleteReport(
            @PathVariable UUID id,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        log.info("Deleting report: {} for tenant: {}", id, tenantId);
        applicationService.deleteReport(tenantId, id);
        return ResponseEntity.noContent().build();
    }
}
