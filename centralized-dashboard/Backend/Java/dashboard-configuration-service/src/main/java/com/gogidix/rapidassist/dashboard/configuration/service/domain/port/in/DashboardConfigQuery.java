package com.gogidix.rapidassist.dashboard.configuration.service.domain.port.in;

import com.gogidix.rapidassist.dashboard.configuration.service.domain.model.DashboardConfiguration;
import java.util.Optional;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface DashboardConfigQuery {

    CompletableFuture<List<DashboardConfiguration>> getAllDashboards();

    CompletableFuture<List<DashboardConfiguration>> getDashboardsByTenant(String tenantId);

    CompletableFuture<Optional<DashboardConfiguration>> getDashboard(String dashboardId);

    CompletableFuture<List<DashboardConfiguration>> getDashboardsByCategory(String category);

    CompletableFuture<List<DashboardConfiguration>> getActiveDashboards(String tenantId);

    CompletableFuture<List<DashboardConfiguration>> searchDashboards(
        String tenantId,
        String keyword
    );

    CompletableFuture<Boolean> hasPermission(String dashboardId, String action, String role);
}
