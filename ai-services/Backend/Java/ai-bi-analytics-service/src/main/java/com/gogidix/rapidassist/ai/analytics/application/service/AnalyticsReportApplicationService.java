package com.gogidix.rapidassist.ai.analytics.application.service;

import com.gogidix.rapidassist.ai.analytics.application.command.GenerateReportCommand;
import com.gogidix.rapidassist.ai.analytics.application.dto.AnalyticsReportDto;
import com.gogidix.rapidassist.ai.analytics.application.mapper.AnalyticsReportMapper;
import com.gogidix.rapidassist.ai.analytics.application.query.GetReportQuery;
import com.gogidix.rapidassist.ai.analytics.application.query.ListReportsQuery;
import com.gogidix.rapidassist.ai.analytics.domain.exception.AnalyticsReportNotFoundException;
import com.gogidix.rapidassist.ai.analytics.domain.exception.InvalidReportDataException;
import com.gogidix.rapidassist.ai.analytics.domain.model.AnalyticsReport;
import com.gogidix.rapidassist.ai.analytics.domain.model.ReportStatus;
import com.gogidix.rapidassist.ai.analytics.domain.model.ReportType;
import com.gogidix.rapidassist.ai.analytics.domain.repository.AnalyticsReportRepositoryPort;
import com.gogidix.rapidassist.ai.analytics.infrastructure.messaging.kafka.event.KafkaEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Application service for Analytics Report operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsReportApplicationService {

    private final AnalyticsReportRepositoryPort reportRepository;
    private final AnalyticsReportMapper reportMapper;
    private final KafkaEventPublisher eventPublisher;

    /**
     * Generate a new analytics report
     */
    @Transactional
    public AnalyticsReportDto generateReport(GenerateReportCommand command) {
        log.info("Generating analytics report: {} for tenant: {}", command.getName(), command.getTenantId());

        // Validate command
        validateGenerateReportCommand(command);

        // Create report
        AnalyticsReport report = createReportFromCommand(command);

        // Save report
        report = reportRepository.save(report);

        // Publish event
        eventPublisher.publishReportRequestedEvent(report);

        // Execute report generation (synchronously for now)
        report = executeReportGeneration(report);

        // Save updated report
        report = reportRepository.save(report);

        // Publish completion event
        eventPublisher.publishReportGeneratedEvent(report);

        log.info("Report generated successfully: {}", report.getId());
        return reportMapper.toDto(report);
    }

    /**
     * Get report by ID
     */
    @Transactional(readOnly = true)
    public AnalyticsReportDto getReport(GetReportQuery query) {
        log.info("Getting report: {} for tenant: {}", query.getReportId(), query.getTenantId());

        UUID reportId = UUID.fromString(query.getReportId());
        AnalyticsReport report = reportRepository.findByIdAndTenantId(reportId, query.getTenantId())
                .orElseThrow(() -> new AnalyticsReportNotFoundException(reportId));

        return reportMapper.toDto(report);
    }

    /**
     * List reports with pagination and filters
     */
    @Transactional(readOnly = true)
    public Page<AnalyticsReportDto> listReports(ListReportsQuery query) {
        log.info("Listing reports for tenant: {} with filters: {}", query.getTenantId(), query);

        int page = query.getPage() != null ? query.getPage() : 0;
        int size = query.getSize() != null ? query.getSize() : 20;

        List<AnalyticsReport> reports;
        long total;

        if (query.getStatus() != null && query.getReportType() != null) {
            reports = reportRepository.findByTenantIdAndStatus(query.getTenantId(), query.getStatus()).stream()
                    .filter(r -> r.getReportType().name().equals(query.getReportType()))
                    .collect(Collectors.toList());
            total = reports.size();
        } else if (query.getStatus() != null) {
            reports = reportRepository.findByTenantIdAndStatus(query.getTenantId(), query.getStatus());
            total = reports.size();
        } else if (query.getReportType() != null) {
            reports = reportRepository.findByTenantIdAndReportType(query.getTenantId(), query.getReportType());
            total = reports.size();
        } else {
            reports = reportRepository.findByTenantId(query.getTenantId());
            total = reports.size();
        }

        // Apply pagination
        int start = page * size;
        int end = Math.min(start + size, reports.size());
        List<AnalyticsReport> paginatedReports = reports.subList(start, end);

        List<AnalyticsReportDto> dtoList = reportMapper.toDtoList(paginatedReports);
        return new PageImpl<>(dtoList, PageRequest.of(page, size), total);
    }

    /**
     * Delete report by ID
     */
    @Transactional
    public void deleteReport(String reportId, String tenantId) {
        log.info("Deleting report: {} for tenant: {}", reportId, tenantId);

        UUID id = UUID.fromString(reportId);
        if (!reportRepository.existsById(id)) {
            throw new AnalyticsReportNotFoundException(id);
        }

        reportRepository.deleteById(id);
        log.info("Report deleted successfully: {}", reportId);
    }

    private void validateGenerateReportCommand(GenerateReportCommand command) {
        if (command.getTenantId() == null || command.getTenantId().isBlank()) {
            throw new InvalidReportDataException("Tenant ID is required");
        }
        if (command.getName() == null || command.getName().isBlank()) {
            throw new InvalidReportDataException("Report name is required");
        }
        if (command.getReportType() == null || command.getReportType().isBlank()) {
            throw new InvalidReportDataException("Report type is required");
        }
        try {
            ReportType.valueOf(command.getReportType());
        } catch (IllegalArgumentException e) {
            throw new InvalidReportDataException("Invalid report type: " + command.getReportType());
        }
    }

    private AnalyticsReport createReportFromCommand(GenerateReportCommand command) {
        return AnalyticsReport.builder()
                .id(UUID.randomUUID())
                .tenantId(command.getTenantId())
                .name(command.getName())
                .description(command.getDescription())
                .reportType(ReportType.valueOf(command.getReportType()))
                .status(command.getScheduledFor() != null ? ReportStatus.SCHEDULED : ReportStatus.PENDING)
                .createdBy(command.getTriggeredBy())
                .updatedBy(command.getTriggeredBy())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .scheduledFor(command.getScheduledFor())
                .parameters(command.getParameters())
                .filters(command.getFilters())
                .metricIds(command.getMetricIds() != null ? command.getMetricIds().stream().map(UUID::fromString).toList() : List.of())
                .dashboardIds(command.getDashboardIds() != null ? command.getDashboardIds().stream().map(UUID::fromString).toList() : List.of())
                .isScheduled(command.getIsScheduled() != null ? command.getIsScheduled() : Boolean.FALSE)
                .scheduleExpression(command.getScheduleExpression())
                .tags(command.getTags())
                .isActive(Boolean.TRUE)
                .generationCount(0)
                .build();
    }

    private AnalyticsReport executeReportGeneration(AnalyticsReport report) {
        log.info("Executing report generation for: {}", report.getId());

        report.setStatus(ReportStatus.GENERATING);
        report.setUpdatedAt(LocalDateTime.now());

        long startTime = System.currentTimeMillis();

        try {
            // Simulate report generation logic
            // In a real implementation, this would query data sources and generate the report
            Thread.sleep(100); // Simulate processing

            report.setStatus(ReportStatus.COMPLETED);
            report.setCompletedAt(LocalDateTime.now());
            report.setExecutionTimeMs(System.currentTimeMillis() - startTime);
            report.setRecordCount((int) (Math.random() * 1000));
            report.setGenerationCount((report.getGenerationCount() != null ? report.getGenerationCount() : 0) + 1);
            report.setLastGeneratedAt(LocalDateTime.now());

            // Set sample result data
            report.setResultData(java.util.Map.of(
                    "summary", "Report generated successfully",
                    "dataPoints", List.of("point1", "point2", "point3")
            ));

            log.info("Report generation completed successfully: {}", report.getId());
        } catch (Exception e) {
            log.error("Report generation failed for: {}", report.getId(), e);
            report.setStatus(ReportStatus.FAILED);
            report.setErrorMessage(e.getMessage());
            report.setCompletedAt(LocalDateTime.now());
            report.setExecutionTimeMs(System.currentTimeMillis() - startTime);
        }

        return report;
    }
}
