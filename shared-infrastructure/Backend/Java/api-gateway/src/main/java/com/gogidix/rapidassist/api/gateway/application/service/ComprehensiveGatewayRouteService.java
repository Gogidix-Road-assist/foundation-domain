package com.gogidix.rapidassist.api.gateway.application.service;

import com.gogidix.rapidassist.api.gateway.domain.model.GatewayRoute;
import com.gogidix.rapidassist.api.gateway.domain.model.RouteMetrics;
import com.gogidix.rapidassist.api.gateway.domain.port.in.GatewayRouteCommand;
import com.gogidix.rapidassist.api.gateway.domain.port.in.GatewayRouteQuery;
import com.gogidix.rapidassist.api.gateway.domain.port.out.GatewayRouteStore;
import com.gogidix.rapidassist.api.gateway.domain.port.out.RouteMetricsStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
public class ComprehensiveGatewayRouteService implements GatewayRouteCommand, GatewayRouteQuery {

    private static final Logger logger = LoggerFactory.getLogger(ComprehensiveGatewayRouteService.class);

    @Autowired
    private GatewayRouteStore routeStore;

    @Autowired
    private RouteMetricsStore metricsStore;

    @PostConstruct
    public void init() {
        logger.info("Comprehensive Gateway Route Service initialized");
    }

    @Override
    public CompletableFuture<GatewayRoute> createRoute(CreateRouteCommand command) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<GatewayRoute> existing = routeStore.findByRouteId(
                    command.tenantId(), command.routeId()).join();

                if (existing.isPresent()) {
                    throw new IllegalStateException("Route already exists: " + command.routeId());
                }

                GatewayRoute route = GatewayRoute.create(
                    command.tenantId(),
                    command.routeId(),
                    command.path(),
                    command.uri(),
                    command.createdBy()
                );

                GatewayRoute enhancedRoute = enhanceRoute(route, command);

                GatewayRoute saved = routeStore.save(enhancedRoute).join();

                logger.info("Created gateway route: {} for tenant: {}",
                    command.routeId(), command.tenantId());
                return saved;

            } catch (Exception e) {
                logger.error("Error creating gateway route", e);
                throw new RuntimeException("Failed to create gateway route", e);
            }
        });
    }

    @Override
    public CompletableFuture<Optional<GatewayRoute>> updateRoute(UpdateRouteCommand command) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<GatewayRoute> existingOpt = routeStore.findByRouteId(
                    command.tenantId(), command.routeId()).join();

                if (existingOpt.isEmpty()) {
                    return Optional.empty();
                }

                GatewayRoute existing = existingOpt.get();

                GatewayRoute updated = new GatewayRoute(
                    existing.id(),
                    command.tenantId(),
                    command.routeId(),
                    command.path() != null ? command.path() : existing.path(),
                    command.serviceId() != null ? command.serviceId() : existing.serviceId(),
                    command.uri() != null ? command.uri() : existing.uri(),
                    command.methods() != null ? command.methods() : existing.methods(),
                    command.predicates() != null ? command.predicates() : existing.predicates(),
                    command.filters() != null ? command.filters() : existing.filters(),
                    command.order() != null ? command.order() : existing.order(),
                    command.metadata() != null ? command.metadata() : existing.metadata(),
                    existing.status(),
                    existing.enabled(),
                    command.stripPrefix() != 0 ? command.stripPrefix() : existing.stripPrefix(),
                    command.retryConfig() != null ? command.retryConfig() : existing.retryConfig(),
                    command.timeoutConfig() != null ? command.timeoutConfig() : existing.timeoutConfig(),
                    command.rateLimitConfig() != null ? command.rateLimitConfig() : existing.rateLimitConfig(),
                    command.circuitBreakerConfig() != null ? command.circuitBreakerConfig() : existing.circuitBreakerConfig(),
                    command.tags() != null ? command.tags() : existing.tags(),
                    existing.createdBy(),
                    existing.createdAt(),
                    command.updatedBy(),
                    Instant.now()
                );

                GatewayRoute saved = routeStore.save(updated).join();

                logger.info("Updated gateway route: {} for tenant: {}",
                    command.routeId(), command.tenantId());
                return Optional.of(saved);

            } catch (Exception e) {
                logger.error("Error updating gateway route", e);
                throw new RuntimeException("Failed to update gateway route", e);
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> deleteRoute(String tenantId, String routeId, String deletedBy) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<GatewayRoute> existingOpt = routeStore.findByRouteId(tenantId, routeId).join();

                if (existingOpt.isEmpty()) {
                    return false;
                }

                routeStore.deleteByRouteId(tenantId, routeId).join();

                logger.info("Deleted gateway route: {} for tenant: {}", routeId, tenantId);
                return true;

            } catch (Exception e) {
                logger.error("Error deleting gateway route", e);
                return false;
            }
        });
    }

    @Override
    public CompletableFuture<Optional<GatewayRoute>> enableRoute(String tenantId, String routeId, String enabledBy) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<GatewayRoute> existingOpt = routeStore.findByRouteId(tenantId, routeId).join();

                if (existingOpt.isEmpty()) {
                    return Optional.empty();
                }

                GatewayRoute existing = existingOpt.get();
                GatewayRoute enabled = existing.withEnabled(true, enabledBy);

                GatewayRoute saved = routeStore.save(enabled).join();

                logger.info("Enabled gateway route: {} for tenant: {}", routeId, tenantId);
                return Optional.of(saved);

            } catch (Exception e) {
                logger.error("Error enabling gateway route", e);
                throw new RuntimeException("Failed to enable gateway route", e);
            }
        });
    }

    @Override
    public CompletableFuture<Optional<GatewayRoute>> disableRoute(String tenantId, String routeId, String disabledBy) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<GatewayRoute> existingOpt = routeStore.findByRouteId(tenantId, routeId).join();

                if (existingOpt.isEmpty()) {
                    return Optional.empty();
                }

                GatewayRoute existing = existingOpt.get();
                GatewayRoute disabled = existing.withEnabled(false, disabledBy);

                GatewayRoute saved = routeStore.save(disabled).join();

                logger.info("Disabled gateway route: {} for tenant: {}", routeId, tenantId);
                return Optional.of(saved);

            } catch (Exception e) {
                logger.error("Error disabling gateway route", e);
                throw new RuntimeException("Failed to disable gateway route", e);
            }
        });
    }

    @Override
    public CompletableFuture<List<GatewayRoute>> bulkCreateRoutes(BulkCreateRoutesCommand command) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<GatewayRoute> results = command.routes().stream()
                    .map(createRouteCommand -> createRoute(createRouteCommand).join())
                    .toList();

                logger.info("Bulk created {} gateway routes for tenant: {}",
                    results.size(), command.tenantId());
                return results;

            } catch (Exception e) {
                logger.error("Error bulk creating gateway routes", e);
                throw new RuntimeException("Failed to bulk create gateway routes", e);
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> validateRouteConfiguration(String tenantId, String routeId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<GatewayRoute> routeOpt = routeStore.findByRouteId(tenantId, routeId).join();

                if (routeOpt.isEmpty()) {
                    logger.warn("Route not found for validation: {}", routeId);
                    return false;
                }

                GatewayRoute route = routeOpt.get();

                boolean valid = route.uri() != null && !route.uri().isBlank()
                    && route.path() != null && !route.path().isBlank()
                    && route.methods() != null && !route.methods().isEmpty();

                logger.info("Validated gateway route: {}, valid: {}", routeId, valid);
                return valid;

            } catch (Exception e) {
                logger.error("Error validating gateway route configuration", e);
                return false;
            }
        });
    }

    @Override
    public CompletableFuture<Optional<GatewayRoute>> getRouteById(String tenantId, String routeId) {
        return routeStore.findByRouteId(tenantId, routeId);
    }

    @Override
    public CompletableFuture<List<GatewayRoute>> getRoutesByTenant(String tenantId) {
        return routeStore.findByTenant(tenantId);
    }

    @Override
    public CompletableFuture<List<GatewayRoute>> getRoutesByService(String tenantId, String serviceId) {
        return routeStore.findByService(tenantId, serviceId);
    }

    @Override
    public CompletableFuture<List<GatewayRoute>> getRoutesByPath(String tenantId, String pathPattern) {
        return routeStore.findByPath(tenantId, pathPattern);
    }

    @Override
    public CompletableFuture<List<GatewayRoute>> getActiveRoutes(String tenantId) {
        return routeStore.findByEnabled(tenantId, true)
            .thenApply(routes -> routes.stream()
                .filter(route -> route.status() == GatewayRoute.RouteStatus.ACTIVE)
                .toList());
    }

    @Override
    public CompletableFuture<List<GatewayRoute>> getRoutesByStatus(String tenantId, GatewayRoute.RouteStatus status) {
        return routeStore.findByStatus(tenantId, status);
    }

    @Override
    public CompletableFuture<List<GatewayRoute>> getRoutesByTags(String tenantId, List<String> tags) {
        return routeStore.findByTags(tenantId, tags);
    }

    @Override
    public CompletableFuture<List<GatewayRoute>> searchRoutes(String tenantId, String keyword) {
        return routeStore.searchByKeyword(tenantId, keyword);
    }

    @Override
    public CompletableFuture<List<RouteMetrics>> getRouteMetrics(
        String tenantId, String routeId, Instant from, Instant to) {
        return metricsStore.findByRouteIdAndTimeRange(tenantId, routeId, from, to);
    }

    @Override
    public CompletableFuture<RouteMetrics> getAggregatedMetrics(
        String tenantId, String routeId, Instant from, Instant to) {
        return metricsStore.findAggregatedMetrics(tenantId, routeId, from, to)
            .thenApply(metricsList -> {
                if (metricsList.isEmpty()) {
                    return RouteMetrics.create(tenantId, routeId, 0, 0, 0);
                }
                return metricsList.get(0);
            });
    }

    @Override
    public CompletableFuture<List<GatewayRoute>> getRoutesOrdered(String tenantId) {
        return routeStore.findAllOrdered(tenantId);
    }

    private GatewayRoute enhanceRoute(GatewayRoute route, CreateRouteCommand command) {
        return new GatewayRoute(
            route.id(),
            route.tenantId(),
            route.routeId(),
            route.path(),
            command.serviceId(),
            route.uri(),
            command.methods(),
            command.predicates(),
            command.filters(),
            command.order(),
            command.metadata(),
            route.status(),
            route.enabled(),
            command.stripPrefix(),
            command.retryConfig(),
            command.timeoutConfig(),
            command.rateLimitConfig(),
            command.circuitBreakerConfig(),
            command.tags(),
            route.createdBy(),
            route.createdAt(),
            route.updatedBy(),
            route.updatedAt()
        );
    }
}
