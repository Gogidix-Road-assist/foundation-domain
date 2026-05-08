package com.gogidix.rapidassist.api.gateway.domain.port.in;

import com.gogidix.rapidassist.api.gateway.domain.model.GatewayRoute;
import com.gogidix.rapidassist.api.gateway.domain.model.RouteMetrics;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface GatewayRouteQuery {

    CompletableFuture<Optional<GatewayRoute>> getRouteById(String tenantId, String routeId);

    CompletableFuture<List<GatewayRoute>> getRoutesByTenant(String tenantId);

    CompletableFuture<List<GatewayRoute>> getRoutesByService(String tenantId, String serviceId);

    CompletableFuture<List<GatewayRoute>> getRoutesByPath(String tenantId, String pathPattern);

    CompletableFuture<List<GatewayRoute>> getActiveRoutes(String tenantId);

    CompletableFuture<List<GatewayRoute>> getRoutesByStatus(String tenantId, GatewayRoute.RouteStatus status);

    CompletableFuture<List<GatewayRoute>> getRoutesByTags(String tenantId, List<String> tags);

    CompletableFuture<List<GatewayRoute>> searchRoutes(String tenantId, String keyword);

    CompletableFuture<List<RouteMetrics>> getRouteMetrics(
        String tenantId, String routeId, Instant from, Instant to);

    CompletableFuture<RouteMetrics> getAggregatedMetrics(
        String tenantId, String routeId, Instant from, Instant to);

    CompletableFuture<List<GatewayRoute>> getRoutesOrdered(String tenantId);
}
