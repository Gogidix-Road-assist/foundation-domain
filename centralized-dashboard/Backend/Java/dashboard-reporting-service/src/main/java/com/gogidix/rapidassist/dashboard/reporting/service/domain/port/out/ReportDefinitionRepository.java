package com.gogidix.rapidassist.dashboard.reporting.service.domain.port.out;

import com.gogidix.rapidassist.dashboard.reporting.service.domain.model.ReportDefinition;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface ReportDefinitionRepository {

    CompletableFuture<ReportDefinition> save(ReportDefinition definition);

    CompletableFuture<Optional<ReportDefinition>> findById(String reportId);

    CompletableFuture<List<ReportDefinition>> findByTenantId(String tenantId);

    CompletableFuture<List<ReportDefinition>> findAll();

    CompletableFuture<List<ReportDefinition>> findActiveByTenant(String tenantId);

    CompletableFuture<List<ReportDefinition>> findByType(String tenantId, ReportDefinition.ReportType type);

    CompletableFuture<Boolean> deleteById(String reportId);

    CompletableFuture<Boolean> existsById(String reportId);
}
