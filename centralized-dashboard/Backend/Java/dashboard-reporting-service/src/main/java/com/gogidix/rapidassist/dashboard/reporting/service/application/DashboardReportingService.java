package com.gogidix.rapidassist.dashboard.reporting.service.application;

import com.gogidix.rapidassist.dashboard.reporting.service.domain.model.ReportDefinition;
import com.gogidix.rapidassist.dashboard.reporting.service.domain.model.ReportExecution;
import com.gogidix.rapidassist.dashboard.reporting.service.domain.port.in.ReportingCommand;
import com.gogidix.rapidassist.dashboard.reporting.service.domain.port.in.ReportingQuery;
import com.gogidix.rapidassist.dashboard.reporting.service.domain.port.out.ReportDefinitionRepository;
import com.gogidix.rapidassist.dashboard.reporting.service.domain.port.out.ReportExecutionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class DashboardReportingService implements ReportingCommand, ReportingQuery {

    private static final Logger logger = LoggerFactory.getLogger(DashboardReportingService.class);
    private final ReportDefinitionRepository definitionRepository;
    private final ReportExecutionRepository executionRepository;

    public DashboardReportingService(
        ReportDefinitionRepository definitionRepository,
        ReportExecutionRepository executionRepository) {
        this.definitionRepository = definitionRepository;
        this.executionRepository = executionRepository;
    }

    // Command implementations
    @Override
    public CompletableFuture<ReportDefinition> createReportDefinition(
        String tenantId, String reportId, String name, String description,
        ReportDefinition.ReportType reportType, String createdBy) {

        logger.info("Creating report definition: {} for tenant: {}", reportId, tenantId);

        ReportDefinition definition = ReportDefinition.builder()
            .tenantId(tenantId)
            .reportId(reportId)
            .name(name)
            .description(description)
            .reportType(reportType)
            .createdBy(createdBy)
            .build();

        return definitionRepository.save(definition);
    }

    @Override
    public CompletableFuture<Optional<ReportDefinition>> updateReportDefinition(
        String reportId, String name, String description, String updatedBy) {

        return definitionRepository.findById(reportId).thenComposeAsync(opt -> {
            if (opt.isEmpty()) return CompletableFuture.completedFuture(Optional.empty());

            ReportDefinition existing = opt.get();
            ReportDefinition updated = ReportDefinition.builder()
                .id(existing.id())
                .tenantId(existing.tenantId())
                .reportId(existing.reportId())
                .name(name)
                .description(description)
                .reportType(existing.reportType())
                .source(existing.source())
                .schedule(existing.schedule())
                .format(existing.format())
                .parameters(existing.parameters())
                .template(existing.template())
                .recipients(existing.recipients())
                .active(existing.active())
                .createdBy(existing.createdBy())
                .createdAt(existing.createdAt())
                .updatedBy(updatedBy)
                .updatedAt(Instant.now())
                .version(existing.version() + 1)
                .build();

            return definitionRepository.save(updated).thenApply(Optional::of);
        });
    }

    @Override
    public CompletableFuture<Optional<ReportDefinition>> updateSchedule(
        String reportId, ReportDefinition.ReportSchedule schedule, String updatedBy) {

        return definitionRepository.findById(reportId).thenComposeAsync(opt -> {
            if (opt.isEmpty()) return CompletableFuture.completedFuture(Optional.empty());

            ReportDefinition existing = opt.get();
            ReportDefinition updated = ReportDefinition.builder()
                .id(existing.id())
                .tenantId(existing.tenantId())
                .reportId(existing.reportId())
                .name(existing.name())
                .description(existing.description())
                .reportType(existing.reportType())
                .source(existing.source())
                .schedule(schedule)
                .format(existing.format())
                .parameters(existing.parameters())
                .template(existing.template())
                .recipients(existing.recipients())
                .active(existing.active())
                .createdBy(existing.createdBy())
                .createdAt(existing.createdAt())
                .updatedBy(updatedBy)
                .updatedAt(Instant.now())
                .version(existing.version() + 1)
                .build();

            return definitionRepository.save(updated).thenApply(Optional::of);
        });
    }

    @Override
    public CompletableFuture<Optional<ReportDefinition>> activateReportDefinition(String reportId, String updatedBy) {
        return definitionRepository.findById(reportId).thenComposeAsync(opt -> {
            if (opt.isEmpty()) return CompletableFuture.completedFuture(Optional.empty());

            ReportDefinition existing = opt.get();
            ReportDefinition updated = ReportDefinition.builder()
                .id(existing.id())
                .tenantId(existing.tenantId())
                .reportId(existing.reportId())
                .name(existing.name())
                .description(existing.description())
                .reportType(existing.reportType())
                .source(existing.source())
                .schedule(existing.schedule())
                .format(existing.format())
                .parameters(existing.parameters())
                .template(existing.template())
                .recipients(existing.recipients())
                .active(true)
                .createdBy(existing.createdBy())
                .createdAt(existing.createdAt())
                .updatedBy(updatedBy)
                .updatedAt(Instant.now())
                .version(existing.version() + 1)
                .build();

            return definitionRepository.save(updated).thenApply(Optional::of);
        });
    }

    @Override
    public CompletableFuture<Optional<ReportDefinition>> deactivateReportDefinition(String reportId, String updatedBy) {
        return definitionRepository.findById(reportId).thenComposeAsync(opt -> {
            if (opt.isEmpty()) return CompletableFuture.completedFuture(Optional.empty());

            ReportDefinition existing = opt.get();
            ReportDefinition updated = ReportDefinition.builder()
                .id(existing.id())
                .tenantId(existing.tenantId())
                .reportId(existing.reportId())
                .name(existing.name())
                .description(existing.description())
                .reportType(existing.reportType())
                .source(existing.source())
                .schedule(existing.schedule())
                .format(existing.format())
                .parameters(existing.parameters())
                .template(existing.template())
                .recipients(existing.recipients())
                .active(false)
                .createdBy(existing.createdBy())
                .createdAt(existing.createdAt())
                .updatedBy(updatedBy)
                .updatedAt(Instant.now())
                .version(existing.version() + 1)
                .build();

            return definitionRepository.save(updated).thenApply(Optional::of);
        });
    }

    @Override
    public CompletableFuture<Boolean> deleteReportDefinition(String reportId) {
        return definitionRepository.deleteById(reportId);
    }

    @Override
    public CompletableFuture<ReportExecution> generateReport(
        String reportId, ReportDefinition.ReportFormat format, String requestedBy) {

        logger.info("Generating report: {} in format: {}", reportId, format);

        String executionId = UUID.randomUUID().toString();
        Instant now = Instant.now();

        return definitionRepository.findById(reportId).thenComposeAsync(opt -> {
            if (opt.isEmpty()) {
                ReportExecution failed = ReportExecution.builder()
                    .reportId(reportId)
                    .executionId(executionId)
                    .status(ReportExecution.ExecutionStatus.FAILED)
                    .errorMessage("Report definition not found")
                    .requestedBy(requestedBy)
                    .requestedAt(now)
                    .startedAt(now)
                    .completedAt(now)
                    .build();

                return executionRepository.save(failed);
            }

            ReportDefinition definition = opt.get();

            ReportExecution execution = ReportExecution.builder()
                .tenantId(definition.tenantId())
                .reportId(reportId)
                .executionId(executionId)
                .status(ReportExecution.ExecutionStatus.IN_PROGRESS)
                .format(format)
                .requestedBy(requestedBy)
                .requestedAt(now)
                .startedAt(now)
                .metrics(new ReportExecution.ExecutionMetrics(
                    System.currentTimeMillis() - now.toEpochMilli(),
                    0L,
                    0L,
                    100
                ))
                .build();

            return executionRepository.save(execution).thenComposeAsync(saved -> {
                // Simulate async report generation
                ReportExecution completed = ReportExecution.builder()
                    .id(saved.id())
                    .tenantId(saved.tenantId())
                    .reportId(saved.reportId())
                    .executionId(saved.executionId())
                    .status(ReportExecution.ExecutionStatus.COMPLETED)
                    .format(format)
                    .result(new ReportExecution.ReportResult(
                        "/reports/" + executionId + "." + format.name().toLowerCase(),
                        1024L * 50L,
                        10,
                        UUID.randomUUID().toString()
                    ))
                    .requestedBy(requestedBy)
                    .requestedAt(saved.requestedAt())
                    .startedAt(saved.startedAt())
                    .completedAt(Instant.now())
                    .metrics(new ReportExecution.ExecutionMetrics(
                        2500L,
                        500L,
                        1500L,
                        100
                    ))
                    .build();

                return executionRepository.save(completed);
            });
        });
    }

    @Override
    public CompletableFuture<Optional<ReportExecution>> cancelReportExecution(String executionId, String cancelledBy) {
        return executionRepository.findById(executionId).thenComposeAsync(opt -> {
            if (opt.isEmpty()) return CompletableFuture.completedFuture(Optional.empty());

            ReportExecution existing = opt.get();

            if (existing.status() == ReportExecution.ExecutionStatus.COMPLETED) {
                return CompletableFuture.completedFuture(Optional.of(existing));
            }

            ReportExecution cancelled = ReportExecution.builder()
                .id(existing.id())
                .tenantId(existing.tenantId())
                .reportId(existing.reportId())
                .executionId(existing.executionId())
                .status(ReportExecution.ExecutionStatus.CANCELLED)
                .format(existing.format())
                .requestedBy(existing.requestedBy())
                .requestedAt(existing.requestedAt())
                .startedAt(existing.startedAt())
                .completedAt(Instant.now())
                .build();

            return executionRepository.save(cancelled).thenApply(Optional::of);
        });
    }

    // Query implementations
    @Override
    public CompletableFuture<List<ReportDefinition>> getAllReportDefinitions() {
        return definitionRepository.findAll();
    }

    @Override
    public CompletableFuture<List<ReportDefinition>> getReportDefinitionsByTenant(String tenantId) {
        return definitionRepository.findByTenantId(tenantId);
    }

    @Override
    public CompletableFuture<Optional<ReportDefinition>> getReportDefinition(String reportId) {
        return definitionRepository.findById(reportId);
    }

    @Override
    public CompletableFuture<List<ReportDefinition>> getActiveReportDefinitions(String tenantId) {
        return definitionRepository.findActiveByTenant(tenantId);
    }

    @Override
    public CompletableFuture<List<ReportDefinition>> getReportDefinitionsByType(String tenantId, ReportDefinition.ReportType type) {
        return definitionRepository.findByType(tenantId, type);
    }

    @Override
    public CompletableFuture<List<ReportExecution>> getExecutionsByReport(String reportId, int limit) {
        return executionRepository.findByReportId(reportId, limit);
    }

    @Override
    public CompletableFuture<List<ReportExecution>> getExecutionsByStatus(String tenantId, ReportExecution.ExecutionStatus status) {
        return executionRepository.findByTenantIdAndStatus(tenantId, status);
    }

    @Override
    public CompletableFuture<Optional<ReportExecution>> getExecution(String executionId) {
        return executionRepository.findById(executionId);
    }

    @Override
    public CompletableFuture<List<ReportExecution>> getExecutionsByUser(String tenantId, String requestedBy) {
        return executionRepository.findByTenantIdAndRequestedBy(tenantId, requestedBy);
    }
}
