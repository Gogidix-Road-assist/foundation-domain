package com.gogidix.rapidassist.dynamic.routing.config.service.domain.model;

import java.time.Instant;
import java.util.*;

/**
 * Domain model representing a dynamic routing rule
 */
public class RoutingRule {

    private final String id;
    private final String tenantId;
    private final String ruleName;
    private final RoutePattern pattern;
    private final RouteTarget target;
    private final RoutingStrategy strategy;
    private final List<Condition> conditions;
    private final RouteConfig config;
    private final int priority;
    private final boolean active;
    private final String environment;
    private final String createdBy;
    private final Instant createdAt;
    private final String updatedBy;
    private final Instant updatedAt;
    private final Integer version;

    private RoutingRule(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID().toString();
        this.tenantId = builder.tenantId;
        this.ruleName = builder.ruleName;
        this.pattern = builder.pattern;
        this.target = builder.target;
        this.strategy = builder.strategy;
        this.conditions = List.copyOf(builder.conditions);
        this.config = builder.config;
        this.priority = builder.priority;
        this.active = builder.active;
        this.environment = builder.environment;
        this.createdBy = builder.createdBy;
        this.createdAt = builder.createdAt != null ? builder.createdAt : Instant.now();
        this.updatedBy = builder.updatedBy;
        this.updatedAt = builder.updatedAt != null ? builder.updatedAt : Instant.now();
        this.version = builder.version != null ? builder.version : 1;
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean matches(String path, Map<String, String> headers, Map<String, String> queryParams) {
        if (!active) return false;

        // Check pattern match
        if (!pattern.matches(path)) {
            return false;
        }

        // Check conditions
        for (Condition condition : conditions) {
            if (!condition.evaluate(headers, queryParams)) {
                return false;
            }
        }

        return true;
    }

    // Getters
    public String id() { return id; }
    public String tenantId() { return tenantId; }
    public String ruleName() { return ruleName; }
    public RoutePattern pattern() { return pattern; }
    public RouteTarget target() { return target; }
    public RoutingStrategy strategy() { return strategy; }
    public List<Condition> conditions() { return conditions; }
    public RouteConfig config() { return config; }
    public int priority() { return priority; }
    public boolean active() { return active; }
    public String environment() { return environment; }
    public String createdBy() { return createdBy; }
    public Instant createdAt() { return createdAt; }
    public String updatedBy() { return updatedBy; }
    public Instant updatedAt() { return updatedAt; }
    public Integer version() { return version; }

    // Nested classes

    public record RoutePattern(
        PatternType type,
        String value,
        List<String> methods
    ) {
        public enum PatternType { PATH_PREFIX, PATH_REGEX, EXACT_PATH, WILDCARD }

        public boolean matches(String path) {
            return switch (type) {
                case PATH_PREFIX -> path.startsWith(value);
                case EXACT_PATH -> path.equals(value);
                case WILDCARD -> path.matches(value.replace("*", ".*"));
                case PATH_REGEX -> path.matches(value);
            };
        }

        public static RoutePattern prefix(String value) {
            return new RoutePattern(PatternType.PATH_PREFIX, value, List.of("GET", "POST", "PUT", "DELETE"));
        }

        public static RoutePattern exact(String value) {
            return new RoutePattern(PatternType.EXACT_PATH, value, List.of("GET"));
        }
    }

    public record RouteTarget(
        String type,
        String endpoint,
        Map<String, String> metadata
    ) {
        public static RouteTarget http(String url) {
            return new RouteTarget("HTTP", url, Map.of());
        }

        public static RouteTarget service(String serviceName, String path) {
            return new RouteTarget("SERVICE", serviceName + path, Map.of("service", serviceName));
        }
    }

    public enum RoutingStrategy {
        ROUND_ROBIN, LEAST_CONNECTIONS, IP_HASH, HEADER_BASED, RANDOM
    }

    public record Condition(
        ConditionType type,
        String key,
        String operator,
        String value
    ) {
        public enum ConditionType { HEADER, QUERY_PARAM, PATH_VARIABLE, CUSTOM }

        public boolean evaluate(Map<String, String> headers, Map<String, String> queryParams) {
            String actualValue = switch (type) {
                case HEADER -> headers.get(key);
                case QUERY_PARAM -> queryParams.get(key);
                default -> null;
            };

            if (actualValue == null) return false;

            return switch (operator) {
                case "equals" -> actualValue.equals(value);
                case "contains" -> actualValue.contains(value);
                case "matches" -> actualValue.matches(value);
                case "starts_with" -> actualValue.startsWith(value);
                case "ends_with" -> actualValue.endsWith(value);
                default -> true;
            };
        }
    }

    public record RouteConfig(
        int timeoutMs,
        int retryAttempts,
        boolean circuitBreakerEnabled,
        double circuitBreakerThreshold,
        boolean rateLimitEnabled,
        int rateLimitPerSecond
    ) {
        public static RouteConfig defaults() {
            return new RouteConfig(30000, 3, true, 0.5, false, 100);
        }
    }

    public static class Builder {
        private String id;
        private String tenantId;
        private String ruleName;
        private RoutePattern pattern;
        private RouteTarget target;
        private RoutingStrategy strategy = RoutingStrategy.ROUND_ROBIN;
        private List<Condition> conditions = List.of();
        private RouteConfig config = RouteConfig.defaults();
        private int priority = 0;
        private boolean active = true;
        private String environment = "production";
        private String createdBy;
        private Instant createdAt;
        private String updatedBy;
        private Instant updatedAt;
        private Integer version;

        public Builder id(String id) { this.id = id; return this; }
        public Builder tenantId(String tenantId) { this.tenantId = tenantId; return this; }
        public Builder ruleName(String ruleName) { this.ruleName = ruleName; return this; }
        public Builder pattern(RoutePattern pattern) { this.pattern = pattern; return this; }
        public Builder target(RouteTarget target) { this.target = target; return this; }
        public Builder strategy(RoutingStrategy strategy) { this.strategy = strategy; return this; }
        public Builder conditions(List<Condition> conditions) { this.conditions = conditions; return this; }
        public Builder config(RouteConfig config) { this.config = config; return this; }
        public Builder priority(int priority) { this.priority = priority; return this; }
        public Builder active(boolean active) { this.active = active; return this; }
        public Builder environment(String environment) { this.environment = environment; return this; }
        public Builder createdBy(String createdBy) { this.createdBy = createdBy; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedBy(String updatedBy) { this.updatedBy = updatedBy; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }
        public Builder version(Integer version) { this.version = version; return this; }

        public RoutingRule build() {
            return new RoutingRule(this);
        }
    }
}
