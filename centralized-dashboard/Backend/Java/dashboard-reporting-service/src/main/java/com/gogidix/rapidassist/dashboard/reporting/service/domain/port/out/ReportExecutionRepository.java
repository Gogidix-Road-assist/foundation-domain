package com.gogidix.rapidassist.dashboard.reporting.service.domain.port.out;

import com.gogidix.rapidassist.dashboard.reporting.service.domain.model.ReportExecution;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface ReportExecutionRepository {

    CompletableFuture<ReportExecution> save(ReportExecution execution);

    CompletableFuture<Optional<ReportExecution>> findById(String executionId);

    CompletableFuture<List<ReportExecution>> findByReportId(String reportId, int limit);

    CompletableFuture<List<ReportExecution>> findByTenantIdAndStatus(String tenantId, ReportExecution.ExecutionStatus status);

    CompletableFuture<List<ReportExecution>> findByTenantIdAndRequestedBy(String tenantId, String requestedBy);

    CompletableFuture<List<ReportExecution>> findByTenantId(String tenantId, int limit);
}
