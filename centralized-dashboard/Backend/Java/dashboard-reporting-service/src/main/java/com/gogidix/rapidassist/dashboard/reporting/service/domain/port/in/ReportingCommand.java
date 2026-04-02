package com.gogidix.rapidassist.dashboard.reporting.service.domain.port.in;

import com.gogidix.rapidassist.dashboard.reporting.service.domain.model.ReportDefinition;
import com.gogidix.rapidassist.dashboard.reporting.service.domain.model.ReportExecution;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface ReportingCommand {

    CompletableFuture<ReportDefinition> createReportDefinition(
        String tenantId,
        String reportId,
        String name,
        String description,
        ReportDefinition.ReportType reportType,
        String createdBy
    );

    CompletableFuture<Optional<ReportDefinition>> updateReportDefinition(
        String reportId,
        String name,
        String description,
        String updatedBy
    );

    CompletableFuture<Optional<ReportDefinition>> updateSchedule(
        String reportId,
        ReportDefinition.ReportSchedule schedule,
        String updatedBy
    );

    CompletableFuture<Optional<ReportDefinition>> activateReportDefinition(String reportId, String updatedBy);

    CompletableFuture<Optional<ReportDefinition>> deactivateReportDefinition(String reportId, String updatedBy);

    CompletableFuture<Boolean> deleteReportDefinition(String reportId);

    CompletableFuture<ReportExecution> generateReport(
        String reportId,
        ReportDefinition.ReportFormat format,
        String requestedBy
    );

    CompletableFuture<Optional<ReportExecution>> cancelReportExecution(String executionId, String cancelledBy);
}
