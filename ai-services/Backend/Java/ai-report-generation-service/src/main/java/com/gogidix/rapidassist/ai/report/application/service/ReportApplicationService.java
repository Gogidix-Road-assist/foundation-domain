package com.gogidix.rapidassist.ai.report.application.service;

import com.gogidix.rapidassist.ai.report.application.command.*;
import com.gogidix.rapidassist.ai.report.application.dto.ReportDto;
import com.gogidix.rapidassist.ai.report.application.mapper.ReportMapper;
import com.gogidix.rapidassist.ai.report.application.query.*;
import com.gogidix.rapidassist.ai.report.domain.aggregate.Report;
import com.gogidix.rapidassist.ai.report.domain.exception.ReportGenerationException;
import com.gogidix.rapidassist.ai.report.domain.exception.ReportNotFoundException;
import com.gogidix.rapidassist.ai.report.domain.model.ReportStatus;
import com.gogidix.rapidassist.ai.report.domain.repository.ReportRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Application Service for Report operations.
 * Implements business logic and orchestrates domain operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportApplicationService {

    private final ReportRepositoryPort reportRepository;
    private final ReportMapper mapper;

    /**
     * Create a new report generation request.
     */
    public ReportDto createReport(CreateReportCommand command) {
        log.info("Creating report: {} for tenant: {}", command.getName(), command.getTenantId());

        // Initialize report using domain factory
        var report = Report.initialize(
                command.getTenantId(),
                command.getName(),
                command.getFormat(),
                command.getRequestedBy()
        );

        // Set additional fields
        report.setDescription(command.getDescription());
        report.setParameters(command.getParameters());

        // Save report
        var savedReport = reportRepository.save(command.getTenantId(), report);

        log.info("Report created: {}", savedReport.getId());
        return mapper.toDtoWithComputedFields(savedReport);
    }

    /**
     * Get report by ID.
     */
    public ReportDto getReport(GetReportQuery query) {
        log.info("Getting report: {} for tenant: {}", query.getReportId(), query.getTenantId());

        var report = reportRepository.findById(query.getTenantId(), query.getReportId())
                .orElseThrow(() -> new ReportNotFoundException(
                        query.getReportId(), query.getTenantId()));

        return mapper.toDtoWithComputedFields(report);
    }

    /**
     * Get reports by status.
     */
    public List<ReportDto> getReportsByStatus(GetReportsByStatusQuery query) {
        log.info("Getting reports by status: {} for tenant: {}", query.getStatus(), query.getTenantId());

        var reports = reportRepository.findByStatus(query.getTenantId(), query.getStatus());

        return reports.stream()
                .map(mapper::toDtoWithComputedFields)
                .toList();
    }

    /**
     * Get all reports for a tenant.
     */
    public List<ReportDto> getTenantReports(String tenantId) {
        log.info("Getting all reports for tenant: {}", tenantId);

        var reports = reportRepository.findByTenantId(tenantId);

        return reports.stream()
                .map(mapper::toDtoWithComputedFields)
                .toList();
    }

    /**
     * Get reports requested by a user.
     */
    public List<ReportDto> getReportsByRequestedBy(String tenantId, String requestedBy) {
        log.info("Getting reports for requestedBy: {} in tenant: {}", requestedBy, tenantId);

        var reports = reportRepository.findByRequestedBy(tenantId, requestedBy);

        return reports.stream()
                .map(mapper::toDtoWithComputedFields)
                .toList();
    }

    /**
     * Generate a report (start generation process).
     */
    public ReportDto generateReport(GenerateReportCommand command) {
        log.info("Starting report generation: {} for tenant: {}", command.getReportId(), command.getTenantId());

        // Get report
        var report = reportRepository.findById(command.getTenantId(), command.getReportId())
                .orElseThrow(() -> new ReportNotFoundException(
                        command.getReportId(), command.getTenantId()));

        // Update parameters if provided
        if (command.getParameters() != null) {
            report.setParameters(command.getParameters());
        }

        // Start generation
        try {
            report.startGeneration();

            // In a real implementation, this would trigger the actual report generation
            // For now, we'll simulate completion
            simulateReportGeneration(report);

            var savedReport = reportRepository.save(command.getTenantId(), report);

            log.info("Report generation started: {}", savedReport.getId());
            return mapper.toDtoWithComputedFields(savedReport);
        } catch (Exception e) {
            report.failGeneration(e.getMessage());
            reportRepository.save(command.getTenantId(), report);
            throw new ReportGenerationException("Failed to start report generation: " + e.getMessage(), e);
        }
    }

    /**
     * Cancel a report generation.
     */
    public ReportDto cancelReport(CancelReportCommand command) {
        log.info("Cancelling report: {} for tenant: {}", command.getReportId(), command.getTenantId());

        // Get report
        var report = reportRepository.findById(command.getTenantId(), command.getReportId())
                .orElseThrow(() -> new ReportNotFoundException(
                        command.getReportId(), command.getTenantId()));

        // Check if can be cancelled
        if (!report.canBeCancelled()) {
            throw new ReportGenerationException(
                    "Report cannot be cancelled in status: " + report.getStatus());
        }

        // Cancel report
        report.cancel();

        // Save report
        var savedReport = reportRepository.save(command.getTenantId(), report);

        log.info("Report cancelled: {}", savedReport.getId());
        return mapper.toDtoWithComputedFields(savedReport);
    }

    /**
     * Delete a report.
     */
    public void deleteReport(String tenantId, UUID reportId) {
        log.info("Deleting report: {} for tenant: {}", reportId, tenantId);

        // Check if report exists
        if (!reportRepository.exists(tenantId, reportId)) {
            throw new ReportNotFoundException(reportId, tenantId);
        }

        // Delete report
        reportRepository.delete(tenantId, reportId);

        log.info("Report deleted: {}", reportId);
    }

    /**
     * Get pending reports for a tenant.
     */
    public List<ReportDto> getPendingReports(String tenantId) {
        log.info("Getting pending reports for tenant: {}", tenantId);

        var reports = reportRepository.findPendingReports(tenantId);

        return reports.stream()
                .map(mapper::toDtoWithComputedFields)
                .toList();
    }

    /**
     * Get reports ready for distribution.
     */
    public List<ReportDto> getReportsReadyForDistribution(String tenantId) {
        log.info("Getting reports ready for distribution for tenant: {}", tenantId);

        var reports = reportRepository.findReportsReadyForDistribution(tenantId);

        return reports.stream()
                .map(mapper::toDtoWithComputedFields)
                .toList();
    }

    /**
     * Simulate report generation (for demonstration purposes).
     * In a real implementation, this would integrate with a report generation engine.
     */
    private void simulateReportGeneration(Report report) {
        // Simulate generation time
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Simulate successful completion
        String fileLocation = "/tmp/reports/" + report.getId() + "." + report.getFormat().name().toLowerCase();
        report.completeGeneration(fileLocation, 1024L * 1024L, 1000);
    }
}
