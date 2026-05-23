package com.gogidix.rapidassist.api.gateway.infrastructure.persistence;

import com.gogidix.rapidassist.api.gateway.domain.model.GatewayRoute;
import com.gogidix.rapidassist.api.gateway.domain.port.out.GatewayRouteStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Component
@ConditionalOnProperty(name = "gateway.mongodb.enabled", havingValue = "false", matchIfMissing = true)
public class NoOpGatewayRouteStore implements GatewayRouteStore {

    private static final Logger logger = LoggerFactory.getLogger(NoOpGatewayRouteStore.class);

    @Override
    public CompletableFuture<GatewayRoute> save(GatewayRoute route) {
        logger.warn("MongoDB not enabled - route save discarded for routeId={}", route.routeId());
        return CompletableFuture.completedFuture(route);
    }

    @Override
    public CompletableFuture<Optional<GatewayRoute>> findById(String id) {
        return CompletableFuture.completedFuture(Optional.empty());
    }

    @Override
    public CompletableFuture<Optional<GatewayRoute>> findByRouteId(String tenantId, String routeId) {
        return CompletableFuture.completedFuture(Optional.empty());
    }

    @Override
    public CompletableFuture<List<GatewayRoute>> findByTenant(String tenantId) {
        return CompletableFuture.completedFuture(Collections.emptyList());
    }

    @Override
    public CompletableFuture<List<GatewayRoute>> findByService(String tenantId, String serviceId) {
        return CompletableFuture.completedFuture(Collections.emptyList());
    }

    @Override
    public CompletableFuture<List<GatewayRoute>> findByPath(String tenantId, String pathPattern) {
        return CompletableFuture.completedFuture(Collections.emptyList());
    }

    @Override
    public CompletableFuture<List<GatewayRoute>> findByStatus(String tenantId, GatewayRoute.RouteStatus status) {
        return CompletableFuture.completedFuture(Collections.emptyList());
    }

    @Override
    public CompletableFuture<List<GatewayRoute>> findByEnabled(String tenantId, boolean enabled) {
        return CompletableFuture.completedFuture(Collections.emptyList());
    }

    @Override
    public CompletableFuture<List<GatewayRoute>> findByTags(String tenantId, List<String> tags) {
        return CompletableFuture.completedFuture(Collections.emptyList());
    }

    @Override
    public CompletableFuture<List<GatewayRoute>> searchByKeyword(String tenantId, String keyword) {
        return CompletableFuture.completedFuture(Collections.emptyList());
    }

    @Override
    public CompletableFuture<List<GatewayRoute>> findAllOrdered(String tenantId) {
        return CompletableFuture.completedFuture(Collections.emptyList());
    }

    @Override
    public CompletableFuture<Boolean> deleteById(String id) {
        return CompletableFuture.completedFuture(false);
    }

    @Override
    public CompletableFuture<Boolean> deleteByRouteId(String tenantId, String routeId) {
        return CompletableFuture.completedFuture(false);
    }

    @Override
    public CompletableFuture<Long> countByTenant(String tenantId) {
        return CompletableFuture.completedFuture(0L);
    }
}
