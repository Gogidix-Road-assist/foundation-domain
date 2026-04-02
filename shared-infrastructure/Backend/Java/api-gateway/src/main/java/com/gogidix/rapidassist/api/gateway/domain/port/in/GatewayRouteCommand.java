package com.gogidix.rapidassist.api.gateway.domain.port.in;

import com.gogidix.rapidassist.api.gateway.domain.model.GatewayRoute;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface GatewayRouteCommand {

    CompletableFuture<GatewayRoute> createRoute(CreateRouteCommand command);

    CompletableFuture<Optional<GatewayRoute>> updateRoute(UpdateRouteCommand command);

    CompletableFuture<Boolean> deleteRoute(String tenantId, String routeId, String deletedBy);

    CompletableFuture<Optional<GatewayRoute>> enableRoute(String tenantId, String routeId, String enabledBy);

    CompletableFuture<Optional<GatewayRoute>> disableRoute(String tenantId, String routeId, String disabledBy);

    CompletableFuture<List<GatewayRoute>> bulkCreateRoutes(BulkCreateRoutesCommand command);

    CompletableFuture<Boolean> validateRouteConfiguration(String tenantId, String routeId);

    record CreateRouteCommand(
        String tenantId,
        String routeId,
        String path,
        String uri,
        String serviceId,
        java.util.Set<GatewayRoute.HttpMethod> methods,
        List<GatewayRoute.RoutePredicate> predicates,
        List<GatewayRoute.RouteFilter> filters,
        Integer order,
        java.util.Map<String, Object> metadata,
        int stripPrefix,
        GatewayRoute.RetryConfig retryConfig,
        GatewayRoute.TimeoutConfig timeoutConfig,
        GatewayRoute.RateLimitConfig rateLimitConfig,
        GatewayRoute.CircuitBreakerConfig circuitBreakerConfig,
        java.util.Set<String> tags,
        String createdBy
    ) {}

    record UpdateRouteCommand(
        String tenantId,
        String routeId,
        String path,
        String uri,
        String serviceId,
        java.util.Set<GatewayRoute.HttpMethod> methods,
        List<GatewayRoute.RoutePredicate> predicates,
        List<GatewayRoute.RouteFilter> filters,
        Integer order,
        java.util.Map<String, Object> metadata,
        int stripPrefix,
        GatewayRoute.RetryConfig retryConfig,
        GatewayRoute.TimeoutConfig timeoutConfig,
        GatewayRoute.RateLimitConfig rateLimitConfig,
        GatewayRoute.CircuitBreakerConfig circuitBreakerConfig,
        java.util.Set<String> tags,
        String updatedBy
    ) {}

    record BulkCreateRoutesCommand(
        String tenantId,
        List<CreateRouteCommand> routes,
        String createdBy
    ) {}
}
