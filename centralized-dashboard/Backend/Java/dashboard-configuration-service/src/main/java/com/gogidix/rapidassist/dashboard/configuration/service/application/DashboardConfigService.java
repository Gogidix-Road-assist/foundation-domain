package com.gogidix.rapidassist.dashboard.configuration.service.application;

import com.gogidix.rapidassist.dashboard.configuration.service.domain.model.DashboardConfiguration;
import com.gogidix.rapidassist.dashboard.configuration.service.domain.port.in.DashboardConfigCommand;
import com.gogidix.rapidassist.dashboard.configuration.service.domain.port.in.DashboardConfigQuery;
import com.gogidix.rapidassist.dashboard.configuration.service.domain.port.out.DashboardConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Service layer for dashboard configuration management.
 *
 * <p>This service implements both command and query ports for dashboard operations.
 * All command methods are transactional to ensure data consistency.</p>
 *
 * @author Rapid Assist Team
 * @since 1.0.0
 */
@Service
public class DashboardConfigService implements DashboardConfigCommand, DashboardConfigQuery {

    private static final Logger logger = LoggerFactory.getLogger(DashboardConfigService.class);
    private final DashboardConfigRepository repository;

    public DashboardConfigService(DashboardConfigRepository repository) {
        this.repository = repository;
    }

    // Command implementations
    @Override
    @Transactional
    public CompletableFuture<DashboardConfiguration> createDashboard(
        String tenantId, String dashboardId, String name, String description, String createdBy) {

        logger.info("Creating dashboard: {} for tenant: {}", dashboardId, tenantId);

        DashboardConfiguration dashboard = DashboardConfiguration.builder()
            .tenantId(tenantId)
            .dashboardId(dashboardId)
            .name(name)
            .description(description)
            .createdBy(createdBy)
            .build();

        return repository.save(dashboard);
    }

    @Override
    @Transactional
    public CompletableFuture<Optional<DashboardConfiguration>> updateDashboard(
        String dashboardId, String name, String description, String updatedBy) {

        logger.info("Updating dashboard: {}", dashboardId);

        return repository.findById(dashboardId).thenComposeAsync(opt -> {
            if (opt.isEmpty()) {
                logger.warn("Dashboard not found: {}", dashboardId);
                return CompletableFuture.completedFuture(Optional.empty());
            }

            DashboardConfiguration existing = opt.get();
            DashboardConfiguration updated = DashboardConfiguration.builder()
                .id(existing.id())
                .tenantId(existing.tenantId())
                .dashboardId(existing.dashboardId())
                .name(name)
                .description(description)
                .metadata(existing.metadata())
                .layout(existing.layout())
                .widgets(existing.widgets())
                .theme(existing.theme())
                .permissions(existing.permissions())
                .active(existing.active())
                .environment(existing.environment())
                .createdBy(existing.createdBy())
                .createdAt(existing.createdAt())
                .updatedBy(updatedBy)
                .updatedAt(Instant.now())
                .version(existing.version() + 1)
                .build();

            return repository.save(updated).thenApply(Optional::of);
        });
    }

    @Override
    public CompletableFuture<Optional<DashboardConfiguration>> updateLayout(
        String dashboardId, DashboardConfiguration.DashboardLayout layout, String updatedBy) {

        return repository.findById(dashboardId).thenComposeAsync(opt -> {
            if (opt.isEmpty()) return CompletableFuture.completedFuture(Optional.empty());

            DashboardConfiguration existing = opt.get();
            DashboardConfiguration updated = DashboardConfiguration.builder()
                .id(existing.id())
                .tenantId(existing.tenantId())
                .dashboardId(existing.dashboardId())
                .name(existing.name())
                .description(existing.description())
                .metadata(existing.metadata())
                .layout(layout)
                .widgets(existing.widgets())
                .theme(existing.theme())
                .permissions(existing.permissions())
                .active(existing.active())
                .environment(existing.environment())
                .createdBy(existing.createdBy())
                .createdAt(existing.createdAt())
                .updatedBy(updatedBy)
                .updatedAt(Instant.now())
                .version(existing.version() + 1)
                .build();

            return repository.save(updated).thenApply(Optional::of);
        });
    }

    @Override
    public CompletableFuture<Optional<DashboardConfiguration>> addWidget(
        String dashboardId, DashboardConfiguration.DashboardWidget widget, String updatedBy) {

        return repository.findById(dashboardId).thenComposeAsync(opt -> {
            if (opt.isEmpty()) return CompletableFuture.completedFuture(Optional.empty());

            DashboardConfiguration existing = opt.get();
            List<DashboardConfiguration.DashboardWidget> widgets = new ArrayList<>(existing.widgets());
            widgets.add(widget);

            DashboardConfiguration updated = DashboardConfiguration.builder()
                .id(existing.id())
                .tenantId(existing.tenantId())
                .dashboardId(existing.dashboardId())
                .name(existing.name())
                .description(existing.description())
                .metadata(existing.metadata())
                .layout(existing.layout())
                .widgets(widgets)
                .theme(existing.theme())
                .permissions(existing.permissions())
                .active(existing.active())
                .environment(existing.environment())
                .createdBy(existing.createdBy())
                .createdAt(existing.createdAt())
                .updatedBy(updatedBy)
                .updatedAt(Instant.now())
                .version(existing.version() + 1)
                .build();

            return repository.save(updated).thenApply(Optional::of);
        });
    }

    @Override
    public CompletableFuture<Optional<DashboardConfiguration>> updateWidget(
        String dashboardId, String widgetId, DashboardConfiguration.DashboardWidget widget, String updatedBy) {

        return repository.findById(dashboardId).thenComposeAsync(opt -> {
            if (opt.isEmpty()) return CompletableFuture.completedFuture(Optional.empty());

            DashboardConfiguration existing = opt.get();
            List<DashboardConfiguration.DashboardWidget> widgets = new ArrayList<>(existing.widgets());

            for (int i = 0; i < widgets.size(); i++) {
                if (widgets.get(i).widgetId().equals(widgetId)) {
                    widgets.set(i, widget);
                    break;
                }
            }

            DashboardConfiguration updated = DashboardConfiguration.builder()
                .id(existing.id())
                .tenantId(existing.tenantId())
                .dashboardId(existing.dashboardId())
                .name(existing.name())
                .description(existing.description())
                .metadata(existing.metadata())
                .layout(existing.layout())
                .widgets(widgets)
                .theme(existing.theme())
                .permissions(existing.permissions())
                .active(existing.active())
                .environment(existing.environment())
                .createdBy(existing.createdBy())
                .createdAt(existing.createdAt())
                .updatedBy(updatedBy)
                .updatedAt(Instant.now())
                .version(existing.version() + 1)
                .build();

            return repository.save(updated).thenApply(Optional::of);
        });
    }

    @Override
    public CompletableFuture<Optional<DashboardConfiguration>> removeWidget(
        String dashboardId, String widgetId, String updatedBy) {

        return repository.findById(dashboardId).thenComposeAsync(opt -> {
            if (opt.isEmpty()) return CompletableFuture.completedFuture(Optional.empty());

            DashboardConfiguration existing = opt.get();
            List<DashboardConfiguration.DashboardWidget> widgets = new ArrayList<>(existing.widgets());
            widgets.removeIf(w -> w.widgetId().equals(widgetId));

            DashboardConfiguration updated = DashboardConfiguration.builder()
                .id(existing.id())
                .tenantId(existing.tenantId())
                .dashboardId(existing.dashboardId())
                .name(existing.name())
                .description(existing.description())
                .metadata(existing.metadata())
                .layout(existing.layout())
                .widgets(widgets)
                .theme(existing.theme())
                .permissions(existing.permissions())
                .active(existing.active())
                .environment(existing.environment())
                .createdBy(existing.createdBy())
                .createdAt(existing.createdAt())
                .updatedBy(updatedBy)
                .updatedAt(Instant.now())
                .version(existing.version() + 1)
                .build();

            return repository.save(updated).thenApply(Optional::of);
        });
    }

    @Override
    public CompletableFuture<Optional<DashboardConfiguration>> updateTheme(
        String dashboardId, DashboardConfiguration.DashboardTheme theme, String updatedBy) {

        return repository.findById(dashboardId).thenComposeAsync(opt -> {
            if (opt.isEmpty()) return CompletableFuture.completedFuture(Optional.empty());

            DashboardConfiguration existing = opt.get();
            DashboardConfiguration updated = DashboardConfiguration.builder()
                .id(existing.id())
                .tenantId(existing.tenantId())
                .dashboardId(existing.dashboardId())
                .name(existing.name())
                .description(existing.description())
                .metadata(existing.metadata())
                .layout(existing.layout())
                .widgets(existing.widgets())
                .theme(theme)
                .permissions(existing.permissions())
                .active(existing.active())
                .environment(existing.environment())
                .createdBy(existing.createdBy())
                .createdAt(existing.createdAt())
                .updatedBy(updatedBy)
                .updatedAt(Instant.now())
                .version(existing.version() + 1)
                .build();

            return repository.save(updated).thenApply(Optional::of);
        });
    }

    @Override
    public CompletableFuture<Optional<DashboardConfiguration>> updatePermissions(
        String dashboardId, DashboardConfiguration.DashboardPermissions permissions, String updatedBy) {

        return repository.findById(dashboardId).thenComposeAsync(opt -> {
            if (opt.isEmpty()) return CompletableFuture.completedFuture(Optional.empty());

            DashboardConfiguration existing = opt.get();
            DashboardConfiguration updated = DashboardConfiguration.builder()
                .id(existing.id())
                .tenantId(existing.tenantId())
                .dashboardId(existing.dashboardId())
                .name(existing.name())
                .description(existing.description())
                .metadata(existing.metadata())
                .layout(existing.layout())
                .widgets(existing.widgets())
                .theme(existing.theme())
                .permissions(permissions)
                .active(existing.active())
                .environment(existing.environment())
                .createdBy(existing.createdBy())
                .createdAt(existing.createdAt())
                .updatedBy(updatedBy)
                .updatedAt(Instant.now())
                .version(existing.version() + 1)
                .build();

            return repository.save(updated).thenApply(Optional::of);
        });
    }

    @Override
    public CompletableFuture<Optional<DashboardConfiguration>> activateDashboard(String dashboardId, String updatedBy) {
        return repository.findById(dashboardId).thenComposeAsync(opt -> {
            if (opt.isEmpty()) return CompletableFuture.completedFuture(Optional.empty());

            DashboardConfiguration existing = opt.get();
            DashboardConfiguration updated = DashboardConfiguration.builder()
                .id(existing.id())
                .tenantId(existing.tenantId())
                .dashboardId(existing.dashboardId())
                .name(existing.name())
                .description(existing.description())
                .metadata(existing.metadata())
                .layout(existing.layout())
                .widgets(existing.widgets())
                .theme(existing.theme())
                .permissions(existing.permissions())
                .active(true)
                .environment(existing.environment())
                .createdBy(existing.createdBy())
                .createdAt(existing.createdAt())
                .updatedBy(updatedBy)
                .updatedAt(Instant.now())
                .version(existing.version() + 1)
                .build();

            return repository.save(updated).thenApply(Optional::of);
        });
    }

    @Override
    public CompletableFuture<Optional<DashboardConfiguration>> deactivateDashboard(String dashboardId, String updatedBy) {
        return repository.findById(dashboardId).thenComposeAsync(opt -> {
            if (opt.isEmpty()) return CompletableFuture.completedFuture(Optional.empty());

            DashboardConfiguration existing = opt.get();
            DashboardConfiguration updated = DashboardConfiguration.builder()
                .id(existing.id())
                .tenantId(existing.tenantId())
                .dashboardId(existing.dashboardId())
                .name(existing.name())
                .description(existing.description())
                .metadata(existing.metadata())
                .layout(existing.layout())
                .widgets(existing.widgets())
                .theme(existing.theme())
                .permissions(existing.permissions())
                .active(false)
                .environment(existing.environment())
                .createdBy(existing.createdBy())
                .createdAt(existing.createdAt())
                .updatedBy(updatedBy)
                .updatedAt(Instant.now())
                .version(existing.version() + 1)
                .build();

            return repository.save(updated).thenApply(Optional::of);
        });
    }

    @Override
    public CompletableFuture<Boolean> deleteDashboard(String dashboardId) {
        return repository.deleteById(dashboardId);
    }

    @Override
    public CompletableFuture<Boolean> cloneDashboard(
        String sourceDashboardId, String newDashboardId, String clonedBy) {

        return repository.findById(sourceDashboardId).thenComposeAsync(opt -> {
            if (opt.isEmpty()) {
                return CompletableFuture.completedFuture(false);
            }

            DashboardConfiguration source = opt.get();
            DashboardConfiguration clone = DashboardConfiguration.builder()
                .tenantId(source.tenantId())
                .dashboardId(newDashboardId)
                .name(source.name() + " (Clone)")
                .description(source.description())
                .metadata(source.metadata())
                .layout(source.layout())
                .widgets(source.widgets())
                .theme(source.theme())
                .permissions(source.permissions())
                .active(false)
                .environment(source.environment())
                .createdBy(clonedBy)
                .build();

            repository.save(clone).thenRun(() -> {});
            return CompletableFuture.completedFuture(true);
        });
    }

    // Query implementations
    @Override
    public CompletableFuture<List<DashboardConfiguration>> getAllDashboards() {
        return repository.findAll();
    }

    @Override
    public CompletableFuture<List<DashboardConfiguration>> getDashboardsByTenant(String tenantId) {
        return repository.findByTenantId(tenantId);
    }

    @Override
    public CompletableFuture<Optional<DashboardConfiguration>> getDashboard(String dashboardId) {
        return repository.findById(dashboardId);
    }

    @Override
    public CompletableFuture<List<DashboardConfiguration>> getDashboardsByCategory(String category) {
        return repository.findByCategory(category);
    }

    @Override
    public CompletableFuture<List<DashboardConfiguration>> getActiveDashboards(String tenantId) {
        return repository.findActiveByTenant(tenantId);
    }

    @Override
    public CompletableFuture<List<DashboardConfiguration>> searchDashboards(String tenantId, String keyword) {
        return repository.searchByName(tenantId, keyword);
    }

    @Override
    public CompletableFuture<Boolean> hasPermission(String dashboardId, String action, String role) {
        return repository.findById(dashboardId).thenApplyAsync(opt ->
            opt.map(dashboard -> dashboard.hasPermission(action, role)).orElse(false)
        );
    }
}
