package com.gogidix.rapidassist.api.gateway.domain.port.out;

import com.gogidix.rapidassist.api.gateway.domain.model.RouteMetrics;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface RouteMetricsStore {

    CompletableFuture<RouteMetrics> save(RouteMetrics metrics);

    CompletableFuture<List<RouteMetrics>> findByRouteIdAndTimeRange(
        String tenantId, String routeId, Instant from, Instant to);

    CompletableFuture<Optional<RouteMetrics>> findLatestByRouteId(String tenantId, String routeId);

    CompletableFuture<List<RouteMetrics>> findByTenantAndTimeRange(
        String tenantId, Instant from, Instant to);

    CompletableFuture<List<RouteMetrics>> findAggregatedMetrics(
        String tenantId, String routeId, Instant from, Instant to);

    CompletableFuture<Boolean> deleteOlderThan(Instant threshold);
}
