package com.gogidix.rapidassist.dashboard.configuration.service.domain.port.out;

import com.gogidix.rapidassist.dashboard.configuration.service.domain.model.DashboardConfiguration;
import java.util.Optional;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface DashboardConfigRepository {

    CompletableFuture<DashboardConfiguration> save(DashboardConfiguration dashboard);

    CompletableFuture<Optional<DashboardConfiguration>> findById(String dashboardId);

    CompletableFuture<List<DashboardConfiguration>> findByTenantId(String tenantId);

    CompletableFuture<List<DashboardConfiguration>> findAll();

    CompletableFuture<List<DashboardConfiguration>> findByCategory(String category);

    CompletableFuture<List<DashboardConfiguration>> findActiveByTenant(String tenantId);

    CompletableFuture<List<DashboardConfiguration>> searchByName(String tenantId, String keyword);

    CompletableFuture<Boolean> deleteById(String dashboardId);

    CompletableFuture<Boolean> existsById(String dashboardId);
}
