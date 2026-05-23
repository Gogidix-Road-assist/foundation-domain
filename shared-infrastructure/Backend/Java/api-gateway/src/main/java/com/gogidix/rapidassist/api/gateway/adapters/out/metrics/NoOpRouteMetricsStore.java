package com.gogidix.rapidassist.api.gateway.adapters.out.metrics;

import com.gogidix.rapidassist.api.gateway.domain.model.RouteMetrics;
import com.gogidix.rapidassist.api.gateway.domain.port.out.RouteMetricsStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Component
public class NoOpRouteMetricsStore implements RouteMetricsStore {

    private static final Logger logger = LoggerFactory.getLogger(NoOpRouteMetricsStore.class);

    @Override
    public CompletableFuture<RouteMetrics> save(RouteMetrics metrics) {
        logger.debug("NoOpRouteMetricsStore: save called for route {}", metrics.routeId());
        return CompletableFuture.completedFuture(metrics);
    }

    @Override
    public CompletableFuture<List<RouteMetrics>> findByRouteIdAndTimeRange(
            String tenantId, String routeId, Instant from, Instant to) {
        return CompletableFuture.completedFuture(Collections.emptyList());
    }

    @Override
    public CompletableFuture<Optional<RouteMetrics>> findLatestByRouteId(String tenantId, String routeId) {
        return CompletableFuture.completedFuture(Optional.empty());
    }

    @Override
    public CompletableFuture<List<RouteMetrics>> findByTenantAndTimeRange(
            String tenantId, Instant from, Instant to) {
        return CompletableFuture.completedFuture(Collections.emptyList());
    }

    @Override
    public CompletableFuture<List<RouteMetrics>> findAggregatedMetrics(
            String tenantId, String routeId, Instant from, Instant to) {
        return CompletableFuture.completedFuture(Collections.emptyList());
    }

    @Override
    public CompletableFuture<Boolean> deleteOlderThan(Instant threshold) {
        return CompletableFuture.completedFuture(true);
    }
}
