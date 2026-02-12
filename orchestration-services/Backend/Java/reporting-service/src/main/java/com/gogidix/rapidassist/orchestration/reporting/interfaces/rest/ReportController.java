package com.gogidix.rapidassist.orchestration.reporting.interfaces.rest;

import com.gogidix.rapidassist.orchestration.reporting.application.dto.*;
import com.gogidix.rapidassist.orchestration.reporting.application.mapper.ReportMapper;
import com.gogidix.rapidassist.orchestration.reporting.domain.model.Report;
import com.gogidix.rapidassist.orchestration.reporting.domain.port.in.ReportGenerationPort;
import com.gogidix.rapidassist.orchestration.reporting.shared.exception.NotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for Report operations
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportGenerationPort reportGenerationPort;
    private final ReportMapper reportMapper;

    @PostMapping
    public ResponseEntity<ReportDTO> createReport(@Valid @RequestBody CreateReportRequest request) {
        log.info("Creating report: {}", request.getName());

        Report report = reportGenerationPort.generateReport(
                request.getReportType().name(),
                request.getOutputFormat().name(),
                request.getTemplateId(),
                request.getParameters(),
                request.getGeneratedBy(),
                request.getTenantId()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(reportMapper.toDTO(report));
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<ReportDTO> getReport(@PathVariable String reportId) {
        log.info("Fetching report: {}", reportId);

        Report report = reportGenerationPort.getReport(reportId)
                .orElseThrow(() -> new NotFoundException("Report not found: " + reportId));

        return ResponseEntity.ok(reportMapper.toDTO(report));
    }

    @GetMapping
    public ResponseEntity<List<ReportDTO>> getReportsByTenant(
            @RequestParam String tenantId,
            @RequestParam(required = false) String reportType,
            @RequestParam(required = false) String status) {

        log.info("Fetching reports for tenant: {}", tenantId);

        List<Report> reports;
        if (reportType != null) {
            reports = reportGenerationPort.getReportsByType(tenantId, reportType);
        } else if (status != null) {
            reports = reportGenerationPort.getReportsByStatus(tenantId, status);
        } else {
            reports = reportGenerationPort.getReportsByTenant(tenantId);
        }

        return ResponseEntity.ok(reports.stream()
                .map(reportMapper::toDTO)
                .collect(Collectors.toList()));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReportDTO>> getReportsByUser(
            @PathVariable String userId,
            @RequestParam String tenantId) {

        log.info("Fetching reports for user: {}", userId);

        List<Report> reports = reportGenerationPort.getReportsByUser(tenantId, userId);

        return ResponseEntity.ok(reports.stream()
                .map(reportMapper::toDTO)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{reportId}/download")
    public ResponseEntity<byte[]> downloadReport(@PathVariable String reportId) {
        log.info("Downloading report: {}", reportId);

        String filePath = reportGenerationPort.getReportFilePath(reportId)
                .orElseThrow(() -> new NotFoundException("Report file not found: " + reportId));

        // Record download
        reportGenerationPort.recordDownload(reportId, "SYSTEM");

        // In production, this would read and return the actual file
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + reportId + ".pdf\"")
                .body("Report content".getBytes());
    }

    @DeleteMapping("/{reportId}")
    public ResponseEntity<Void> deleteReport(@PathVariable String reportId) {
        log.info("Deleting report: {}", reportId);

        reportGenerationPort.deleteReport(reportId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/statistics")
    public ResponseEntity<ReportGenerationPort.ReportStatistics> getStatistics(
            @RequestParam String tenantId) {

        log.info("Fetching report statistics for tenant: {}", tenantId);

        ReportGenerationPort.ReportStatistics stats = reportGenerationPort.getReportStatistics(tenantId);

        return ResponseEntity.ok(stats);
    }

    @PostMapping("/{reportId}/expire")
    public ResponseEntity<Void> markAsExpired(@PathVariable String reportId) {
        log.info("Marking report as expired: {}", reportId);

        reportGenerationPort.markAsExpired(reportId);

        return ResponseEntity.ok().build();
    }
}
