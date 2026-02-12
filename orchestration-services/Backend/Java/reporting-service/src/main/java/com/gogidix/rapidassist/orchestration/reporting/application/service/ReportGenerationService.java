package com.gogidix.rapidassist.orchestration.reporting.application.service;

import com.gogidix.rapidassist.orchestration.reporting.application.dto.*;
import com.gogidix.rapidassist.orchestration.reporting.domain.model.Report;
import com.gogidix.rapidassist.orchestration.reporting.domain.model.ReportHistory;
import com.gogidix.rapidassist.orchestration.reporting.domain.port.in.ReportGenerationPort;
import com.gogidix.rapidassist.orchestration.reporting.domain.port.out.ReportHistoryRepositoryPort;
import com.gogidix.rapidassist.orchestration.reporting.domain.port.out.ReportRepositoryPort;
import com.gogidix.rapidassist.orchestration.reporting.infrastructure.reportengine.ReportEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Application service for report generation operations.
 * Implements the ReportGenerationPort interface.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportGenerationService implements ReportGenerationPort {

    private final ReportRepositoryPort reportRepository;
    private final ReportHistoryRepositoryPort historyRepository;
    private final ReportEngine reportEngine;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    @Transactional
    public Report generateReport(String reportType, String outputFormat, String templateId,
                                java.util.Map<String, Object> parameters, String generatedBy, String tenantId) {
        log.info("Generating report: type={}, format={}, template={}, tenant={}",
                reportType, outputFormat, templateId, tenantId);

        String reportId = generateReportId();

        // Create report entity
        Report report = Report.builder()
                .reportId(reportId)
                .name(generateReportName(reportType))
                .reportType(Report.ReportType.valueOf(reportType))
                .outputFormat(Report.OutputFormat.valueOf(outputFormat))
                .status(Report.ReportStatus.PENDING)
                .templateId(templateId)
                .parameters(parameters)
                .generatedBy(generatedBy)
                .tenantId(tenantId)
                .build();

        report.validate();
        report = reportRepository.save(report);

        // Record history
        ReportHistory history = ReportHistory.generationStarted(reportId, generatedBy, tenantId);
        historyRepository.save(history);

        // Publish to Kafka for async processing
        publishReportGenerationEvent(report);

        log.info("Report generation initiated: {}", reportId);
        return report;
    }

    @Override
    @Transactional
    public Report generateScheduledReport(String scheduleId, String tenantId) {
        log.info("Generating scheduled report: scheduleId={}, tenant={}", scheduleId, tenantId);

        String reportId = generateReportId();

        // Create report entity for scheduled generation
        Report report = Report.builder()
                .reportId(reportId)
                .name("Scheduled Report " + scheduleId)
                .reportType(Report.ReportType.CUSTOM)
                .outputFormat(Report.OutputFormat.PDF)
                .status(Report.ReportStatus.PENDING)
                .isScheduled(true)
                .scheduleId(scheduleId)
                .generatedBy("SYSTEM")
                .tenantId(tenantId)
                .build();

        report.validate();
        report = reportRepository.save(report);

        // Record history
        ReportHistory history = ReportHistory.scheduleTriggered(scheduleId, reportId, tenantId);
        historyRepository.save(history);

        log.info("Scheduled report generation initiated: {}", reportId);
        return report;
    }

    @Override
    public java.util.Optional<Report> getReport(String reportId) {
        return reportRepository.findByReportId(reportId);
    }

    @Override
    public List<Report> getReportsByTenant(String tenantId) {
        return reportRepository.findByTenantId(tenantId);
    }

    @Override
    public List<Report> getReportsByType(String tenantId, String reportType) {
        return reportRepository.findByTenantIdAndReportType(tenantId, Report.ReportType.valueOf(reportType));
    }

    @Override
    public List<Report> getReportsByStatus(String tenantId, String status) {
        return reportRepository.findByTenantIdAndStatus(tenantId, Report.ReportStatus.valueOf(status));
    }

    @Override
    public List<Report> getReportsByUser(String tenantId, String userId) {
        return reportRepository.findByTenantIdAndGeneratedBy(tenantId, userId);
    }

    @Override
    public java.util.Optional<String> getReportFilePath(String reportId) {
        return reportRepository.findByReportId(reportId)
                .map(Report::getFilePath);
    }

    @Override
    @Transactional
    public void deleteReport(String reportId) {
        log.info("Deleting report: {}", reportId);
        reportRepository.deleteByReportId(reportId);
        historyRepository.deleteByReportId(reportId);
    }

    @Override
    public ReportStatistics getReportStatistics(String tenantId) {
        long totalReports = reportRepository.countByTenantId(tenantId);
        long completedReports = reportRepository.countByTenantIdAndStatus(
                tenantId, Report.ReportStatus.COMPLETED);
        long failedReports = reportRepository.countByTenantIdAndStatus(
                tenantId, Report.ReportStatus.FAILED);
        long pendingReports = reportRepository.countByTenantIdAndStatus(
                tenantId, Report.ReportStatus.PENDING);

        // Calculate total file size
        long totalFileSize = reportRepository.findByTenantId(tenantId).stream()
                .filter(r -> r.getFileSizeBytes() != null)
                .mapToLong(Report::getFileSizeBytes)
                .sum();

        return new ReportStatistics(totalReports, completedReports, failedReports,
                pendingReports, totalFileSize);
    }

    @Override
    @Transactional
    public void recordDownload(String reportId, String userId) {
        log.info("Recording download for report: {} by user: {}", reportId, userId);

        ReportHistory history = ReportHistory.builder()
                .historyId(generateHistoryId())
                .reportId(reportId)
                .eventType(ReportHistory.HistoryEventType.REPORT_DOWNLOADED)
                .eventStatus(ReportHistory.HistoryEventStatus.SUCCESS)
                .eventDescription("Report downloaded by user: " + userId)
                .triggeredBy(userId)
                .tenantId(reportRepository.findByReportId(reportId)
                        .map(Report::getTenantId).orElse("UNKNOWN"))
                .build();

        historyRepository.save(history);
    }

    @Override
    @Transactional
    public void markAsExpired(String reportId) {
        reportRepository.findByReportId(reportId).ifPresent(report -> {
            report.setStatus(Report.ReportStatus.EXPIRED);
            reportRepository.save(report);

            ReportHistory history = ReportHistory.builder()
                    .historyId(generateHistoryId())
                    .reportId(reportId)
                    .eventType(ReportHistory.HistoryEventType.REPORT_EXPIRED)
                    .eventStatus(ReportHistory.HistoryEventStatus.SUCCESS)
                    .eventDescription("Report expired")
                    .tenantId(report.getTenantId())
                    .build();

            historyRepository.save(history);
        });
    }

    @Override
    @Transactional
    public int cleanupExpiredReports() {
        List<Report> expiredReports = reportRepository.findExpiredReports(LocalDateTime.now());
        int count = 0;

        for (Report report : expiredReports) {
            try {
                markAsExpired(report.getReportId());
                reportEngine.deleteReportFile(report.getFilePath());
                count++;
            } catch (Exception e) {
                log.error("Failed to cleanup expired report: {}", report.getReportId(), e);
            }
        }

        return count;
    }

    private String generateReportId() {
        return "RPT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String generateHistoryId() {
        return "HIS-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
    }

    private String generateReportName(String reportType) {
        return reportType.replace("_", " ") + " Report";
    }

    private void publishReportGenerationEvent(Report report) {
        try {
            kafkaTemplate.send("report-generation-requests", report.getReportId(), report);
        } catch (Exception e) {
            log.error("Failed to publish report generation event", e);
        }
    }
}
