package com.gogidix.rapidassist.analytics.application.service;

import com.gogidix.rapidassist.analytics.application.dto.ReportDto;
import com.gogidix.rapidassist.analytics.application.mapper.ReportMapper;
import com.gogidix.rapidassist.analytics.domain.model.Report;
import com.gogidix.rapidassist.analytics.domain.port.out.ReportRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    public ReportDto createReport(String tenantId, ReportDto dto) {
        log.info("Creating report for tenant: {}", tenantId);

        var report = Report.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .reportName(dto.getReportName())
                .reportType(dto.getReportType())
                .reportCategory(dto.getReportCategory())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .reportData(dto.getReportData())
                .includedMetrics(dto.getIncludedMetrics())
                .charts(dto.getCharts())
                .format(dto.getFormat())
                .status("PENDING")
                .totalViews(0)
                .createdBy(dto.getCreatedBy())
                .description(dto.getDescription())
                .schedule(dto.getSchedule())
                .recipients(dto.getRecipients())
                .filters(dto.getFilters())
                .metadata(dto.getMetadata())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        var saved = reportRepository.save(tenantId, report);
        return mapper.toDto(saved);
    }

    public ReportDto getReport(String tenantId, UUID id) {
        log.info("Getting report: {} for tenant: {}", id, tenantId);

        var report = reportRepository.findById(tenantId, id)
                .orElseThrow(() -> new IllegalArgumentException("Report not found: " + id));

        // Increment view count
        report.incrementViewCount();
        reportRepository.save(tenantId, report);

        return mapper.toDto(report);
    }

    public List<ReportDto> getReportsByTenant(String tenantId) {
        log.info("Getting all reports for tenant: {}", tenantId);

        var reports = reportRepository.findByTenantId(tenantId);
        return mapper.toDtoList(reports);
    }

    public List<ReportDto> getReportsByType(String tenantId, String reportType) {
        log.info("Getting reports by type: {} for tenant: {}", reportType, tenantId);

        var reports = reportRepository.findByTenantIdAndReportType(tenantId, reportType);
        return mapper.toDtoList(reports);
    }

    public List<ReportDto> getReportsByStatus(String tenantId, String status) {
        log.info("Getting reports by status: {} for tenant: {}", status, tenantId);

        var reports = reportRepository.findByTenantIdAndStatus(tenantId, status);
        return mapper.toDtoList(reports);
    }

    public ReportDto generateReport(String tenantId, UUID id) {
        log.info("Generating report: {} for tenant: {}", id, tenantId);

        var report = reportRepository.findById(tenantId, id)
                .orElseThrow(() -> new IllegalArgumentException("Report not found: " + id));

        if (!report.isReadyForGeneration()) {
            throw new IllegalStateException("Report is not ready for generation");
        }

        // Simulate report generation
        var generatedData = generateReportData(report);
        report.setReportData(generatedData);
        report.markAsGenerated("system");

        var saved = reportRepository.save(tenantId, report);
        return mapper.toDto(saved);
    }

    public void deleteReport(String tenantId, UUID id) {
        log.info("Deleting report: {} for tenant: {}", id, tenantId);

        if (!reportRepository.exists(tenantId, id)) {
            throw new IllegalArgumentException("Report not found: " + id);
        }

        reportRepository.delete(tenantId, id);
    }

    private java.util.Map<String, Object> generateReportData(Report report) {
        // Simulate report data generation
        // In real implementation, this would aggregate metrics and generate visualizations
        return java.util.Map.of("totalRecords", 100, "summary", "Report generated successfully");
    }
}
