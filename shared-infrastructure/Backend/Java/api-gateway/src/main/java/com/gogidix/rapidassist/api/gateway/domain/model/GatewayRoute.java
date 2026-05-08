package com.gogidix.rapidassist.api.gateway.domain.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Document(collection = "gateway_routes")
public record GatewayRoute(

    @Id
    String id,

    @Field("tenantId")
    @NotBlank(message = "Tenant ID is required")
    String tenantId,

    @Field("routeId")
    @NotBlank(message = "Route ID is required")
    String routeId,

    @Field("path")
    @NotBlank(message = "Path is required")
    String path,

    @Field("serviceId")
    String serviceId,

    @Field("uri")
    @NotBlank(message = "URI is required")
    String uri,

    @Field("method")
    Set<HttpMethod> methods,

    @Field("predicates")
    List<RoutePredicate> predicates,

    @Field("filters")
    List<RouteFilter> filters,

    @Field("order")
    Integer order,

    @Field("metadata")
    Map<String, Object> metadata,

    @Field("status")
    RouteStatus status,

    @Field("enabled")
    boolean enabled,

    @Field("stripPrefix")
    int stripPrefix,

    @Field("retryConfig")
    RetryConfig retryConfig,

    @Field("timeoutConfig")
    TimeoutConfig timeoutConfig,

    @Field("rateLimitConfig")
    RateLimitConfig rateLimitConfig,

    @Field("circuitBreakerConfig")
    CircuitBreakerConfig circuitBreakerConfig,

    @Field("tags")
    Set<String> tags,

    @Field("createdBy")
    String createdBy,

    @Field("createdAt")
    Instant createdAt,

    @Field("updatedBy")
    String updatedBy,

    @Field("updatedAt")
    Instant updatedAt

) {

    public static GatewayRoute create(
        String tenantId, String routeId, String path, String uri,
        String createdBy
    ) {
        return new GatewayRoute(
            null, tenantId, routeId, path, null, uri,
            Set.of(HttpMethod.GET, HttpMethod.POST),
            List.of(), List.of(), 0, Map.of(),
            RouteStatus.ACTIVE, true, 1, null, null, null, null,
            Set.of(), createdBy, Instant.now(),
            createdBy, Instant.now()
        );
    }

    public GatewayRoute withStatus(RouteStatus newStatus, String updatedBy) {
        return new GatewayRoute(
            id, tenantId, routeId, path, serviceId, uri,
            methods, predicates, filters, order, metadata,
            newStatus, enabled, stripPrefix, retryConfig,
            timeoutConfig, rateLimitConfig, circuitBreakerConfig,
            tags, createdBy, createdAt, updatedBy, Instant.now()
        );
    }

    public GatewayRoute withEnabled(boolean newEnabled, String updatedBy) {
        return new GatewayRoute(
            id, tenantId, routeId, path, serviceId, uri,
            methods, predicates, filters, order, metadata,
            status, newEnabled, stripPrefix, retryConfig,
            timeoutConfig, rateLimitConfig, circuitBreakerConfig,
            tags, createdBy, createdAt, updatedBy, Instant.now()
        );
    }

    public enum HttpMethod {
        GET, POST, PUT, DELETE, PATCH, OPTIONS, HEAD, TRACE, CONNECT
    }

    public enum RouteStatus {
        ACTIVE, INACTIVE, DRAFT, ARCHIVED
    }

    public record RoutePredicate(
        String name,
        Map<String, Object> args
    ) {}

    public record RouteFilter(
        String name,
        Map<String, Object> args
    ) {}

    public record RetryConfig(
        int retries,
        long backoff,
        RetryBackoffType backoffType
    ) {
        public enum RetryBackoffType {
            FIXED, EXPONENTIAL, RANDOM
        }
    }

    public record TimeoutConfig(
        long connectTimeout,
        long responseTimeout
    ) {}

    public record RateLimitConfig(
        int replenishRate,
        int burstCapacity,
        long requestedTokens
    ) {}

    public record CircuitBreakerConfig(
        int failureThreshold,
        int timeoutThreshold,
        long halfOpenAfter
    ) {}
}
