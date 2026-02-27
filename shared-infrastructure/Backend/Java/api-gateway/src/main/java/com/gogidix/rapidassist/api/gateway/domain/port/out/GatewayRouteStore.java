package com.gogidix.rapidassist.api.gateway.domain.port.out;

import com.gogidix.rapidassist.api.gateway.domain.model.GatewayRoute;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface GatewayRouteStore {

    CompletableFuture<GatewayRoute> save(GatewayRoute route);

    CompletableFuture<Optional<GatewayRoute>> findById(String id);

    CompletableFuture<Optional<GatewayRoute>> findByRouteId(String tenantId, String routeId);

    CompletableFuture<List<GatewayRoute>> findByTenant(String tenantId);

    CompletableFuture<List<GatewayRoute>> findByService(String tenantId, String serviceId);

    CompletableFuture<List<GatewayRoute>> findByPath(String tenantId, String pathPattern);

    CompletableFuture<List<GatewayRoute>> findByStatus(String tenantId, GatewayRoute.RouteStatus status);

    CompletableFuture<List<GatewayRoute>> findByEnabled(String tenantId, boolean enabled);

    CompletableFuture<List<GatewayRoute>> findByTags(String tenantId, List<String> tags);

    CompletableFuture<List<GatewayRoute>> searchByKeyword(String tenantId, String keyword);

    CompletableFuture<List<GatewayRoute>> findAllOrdered(String tenantId);

    CompletableFuture<Boolean> deleteById(String id);

    CompletableFuture<Boolean> deleteByRouteId(String tenantId, String routeId);

    CompletableFuture<Long> countByTenant(String tenantId);
}
