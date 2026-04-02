package com.gogidix.rapidassist.dashboard.configuration.service.domain.port.in;

import com.gogidix.rapidassist.dashboard.configuration.service.domain.model.DashboardConfiguration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface DashboardConfigCommand {

    CompletableFuture<DashboardConfiguration> createDashboard(
        String tenantId,
        String dashboardId,
        String name,
        String description,
        String createdBy
    );

    CompletableFuture<Optional<DashboardConfiguration>> updateDashboard(
        String dashboardId,
        String name,
        String description,
        String updatedBy
    );

    CompletableFuture<Optional<DashboardConfiguration>> updateLayout(
        String dashboardId,
        DashboardConfiguration.DashboardLayout layout,
        String updatedBy
    );

    CompletableFuture<Optional<DashboardConfiguration>> addWidget(
        String dashboardId,
        DashboardConfiguration.DashboardWidget widget,
        String updatedBy
    );

    CompletableFuture<Optional<DashboardConfiguration>> updateWidget(
        String dashboardId,
        String widgetId,
        DashboardConfiguration.DashboardWidget widget,
        String updatedBy
    );

    CompletableFuture<Optional<DashboardConfiguration>> removeWidget(
        String dashboardId,
        String widgetId,
        String updatedBy
    );

    CompletableFuture<Optional<DashboardConfiguration>> updateTheme(
        String dashboardId,
        DashboardConfiguration.DashboardTheme theme,
        String updatedBy
    );

    CompletableFuture<Optional<DashboardConfiguration>> updatePermissions(
        String dashboardId,
        DashboardConfiguration.DashboardPermissions permissions,
        String updatedBy
    );

    CompletableFuture<Optional<DashboardConfiguration>> activateDashboard(
        String dashboardId,
        String updatedBy
    );

    CompletableFuture<Optional<DashboardConfiguration>> deactivateDashboard(
        String dashboardId,
        String updatedBy
    );

    CompletableFuture<Boolean> deleteDashboard(String dashboardId);

    CompletableFuture<Boolean> cloneDashboard(
        String sourceDashboardId,
        String newDashboardId,
        String clonedBy
    );
}
