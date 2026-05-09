package com.gogidix.rapidassist.dashboard.reporting.service.domain.port.in;

import com.gogidix.rapidassist.dashboard.reporting.service.domain.model.ReportDefinition;
import com.gogidix.rapidassist.dashboard.reporting.service.domain.model.ReportExecution;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface ReportingQuery {

    CompletableFuture<List<ReportDefinition>> getAllReportDefinitions();

    CompletableFuture<List<ReportDefinition>> getReportDefinitionsByTenant(String tenantId);

    CompletableFuture<Optional<ReportDefinition>> getReportDefinition(String reportId);

    CompletableFuture<List<ReportDefinition>> getActiveReportDefinitions(String tenantId);

    CompletableFuture<List<ReportDefinition>> getReportDefinitionsByType(String tenantId, ReportDefinition.ReportType type);

    CompletableFuture<List<ReportExecution>> getExecutionsByReport(String reportId, int limit);

    CompletableFuture<List<ReportExecution>> getExecutionsByStatus(String tenantId, ReportExecution.ExecutionStatus status);

    CompletableFuture<Optional<ReportExecution>> getExecution(String executionId);

    CompletableFuture<List<ReportExecution>> getExecutionsByUser(String tenantId, String requestedBy);
}
