package com.gogidix.rapidassist.api.gateway.infrastructure.persistence.mongodb;

import com.gogidix.rapidassist.api.gateway.domain.model.GatewayRoute;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Document(collection = "gateway_routes")
public record GatewayRouteDocument(
    String id,
    String tenantId,
    String routeId,
    String path,
    String serviceId,
    String uri,
    Set<String> methods,
    List<PredicateData> predicates,
    List<FilterData> filters,
    Integer order,
    Map<String, Object> metadata,
    String status,
    boolean enabled,
    int stripPrefix,
    RetryConfigData retryConfig,
    TimeoutConfigData timeoutConfig,
    RateLimitConfigData rateLimitConfig,
    CircuitBreakerConfigData circuitBreakerConfig,
    Set<String> tags,
    String createdBy,
    Instant createdAt,
    String updatedBy,
    Instant updatedAt
) {

    public static GatewayRouteDocument fromDomain(GatewayRoute route) {
        return new GatewayRouteDocument(
            route.id(),
            route.tenantId(),
            route.routeId(),
            route.path(),
            route.serviceId(),
            route.uri(),
            route.methods().stream().map(Enum::name).collect(java.util.stream.Collectors.toSet()),
            route.predicates().stream()
                .map(p -> new PredicateData(p.name(), p.args()))
                .toList(),
            route.filters().stream()
                .map(f -> new FilterData(f.name(), f.args()))
                .toList(),
            route.order(),
            route.metadata(),
            route.status().name(),
            route.enabled(),
            route.stripPrefix(),
            route.retryConfig() != null ? new RetryConfigData(route.retryConfig()) : null,
            route.timeoutConfig() != null ? new TimeoutConfigData(route.timeoutConfig()) : null,
            route.rateLimitConfig() != null ? new RateLimitConfigData(route.rateLimitConfig()) : null,
            route.circuitBreakerConfig() != null ? new CircuitBreakerConfigData(route.circuitBreakerConfig()) : null,
            route.tags(),
            route.createdBy(),
            route.createdAt(),
            route.updatedBy(),
            route.updatedAt()
        );
    }

    public GatewayRoute toDomain() {
        return new GatewayRoute(
            id,
            tenantId,
            routeId,
            path,
            serviceId,
            uri,
            methods.stream()
                .map(m -> GatewayRoute.HttpMethod.valueOf(m))
                .collect(java.util.stream.Collectors.toSet()),
            predicates.stream()
                .map(p -> new GatewayRoute.RoutePredicate(p.name(), p.args()))
                .toList(),
            filters.stream()
                .map(f -> new GatewayRoute.RouteFilter(f.name(), f.args()))
                .toList(),
            order,
            metadata,
            GatewayRoute.RouteStatus.valueOf(status),
            enabled,
            stripPrefix,
            retryConfig != null ? retryConfig.toDomain() : null,
            timeoutConfig != null ? timeoutConfig.toDomain() : null,
            rateLimitConfig != null ? rateLimitConfig.toDomain() : null,
            circuitBreakerConfig != null ? circuitBreakerConfig.toDomain() : null,
            tags,
            createdBy,
            createdAt,
            updatedBy,
            updatedAt
        );
    }

    public record PredicateData(String name, Map<String, Object> args) {}
    public record FilterData(String name, Map<String, Object> args) {}

    public record RetryConfigData(
        int retries, long backoff, String backoffType
    ) {
        public RetryConfigData(GatewayRoute.RetryConfig config) {
            this(config.retries(), config.backoff(), config.backoffType().name());
        }

        public GatewayRoute.RetryConfig toDomain() {
            return new GatewayRoute.RetryConfig(
                retries, backoff,
                GatewayRoute.RetryConfig.RetryBackoffType.valueOf(backoffType)
            );
        }
    }

    public record TimeoutConfigData(
        long connectTimeout, long responseTimeout
    ) {
        public TimeoutConfigData(GatewayRoute.TimeoutConfig config) {
            this(config.connectTimeout(), config.responseTimeout());
        }

        public GatewayRoute.TimeoutConfig toDomain() {
            return new GatewayRoute.TimeoutConfig(connectTimeout, responseTimeout);
        }
    }

    public record RateLimitConfigData(
        int replenishRate, int burstCapacity, long requestedTokens
    ) {
        public RateLimitConfigData(GatewayRoute.RateLimitConfig config) {
            this(config.replenishRate(), config.burstCapacity(), config.requestedTokens());
        }

        public GatewayRoute.RateLimitConfig toDomain() {
            return new GatewayRoute.RateLimitConfig(replenishRate, burstCapacity, requestedTokens);
        }
    }

    public record CircuitBreakerConfigData(
        int failureThreshold, int timeoutThreshold, long halfOpenAfter
    ) {
        public CircuitBreakerConfigData(GatewayRoute.CircuitBreakerConfig config) {
            this(config.failureThreshold(), config.timeoutThreshold(), config.halfOpenAfter());
        }

        public GatewayRoute.CircuitBreakerConfig toDomain() {
            return new GatewayRoute.CircuitBreakerConfig(failureThreshold, timeoutThreshold, halfOpenAfter);
        }
    }
}
