package com.gogidix.rapidassist.rate.limit.policy.service.domain.model;

import java.time.Instant;
import java.util.*;

/**
 * Domain model for rate limiting policies
 */
public class RateLimitPolicy {

    private final String id;
    private final String tenantId;
    private final String policyKey;
    private final String name;
    private final String description;
    private final LimitType limitType;
    private final RateLimitConfig config;
    private final Scope scope;
    private final boolean enabled;
    private final String environment;
    private final String createdBy;
    private final Instant createdAt;
    private final String updatedBy;
    private final Instant updatedAt;
    private final Integer version;

    public enum LimitType {
        IP_BASED, USER_BASED, API_KEY_BASED, TENANT_BASED, GLOBAL
    }

    public record RateLimitConfig(
        int requestsPerMinute,
        int requestsPerHour,
        int requestsPerDay,
        int burstCapacity,
        long windowSizeMs,
        String algorithm
    ) {
        public static RateLimitConfig standard() {
            return new RateLimitConfig(60, 1000, 10000, 10, 60000, "token-bucket");
        }
    }

    public record Scope(
        Set<String> endpoints,
        Set<String> userIds,
        Set<String> apiKeys,
        Map<String, String> attributes
    ) {}

    private RateLimitPolicy(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID().toString();
        this.tenantId = builder.tenantId;
        this.policyKey = builder.policyKey;
        this.name = builder.name;
        this.description = builder.description;
        this.limitType = builder.limitType;
        this.config = builder.config;
        this.scope = builder.scope;
        this.enabled = builder.enabled;
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

    public boolean matchesEndpoint(String endpoint) {
        return scope.endpoints().contains("*") || scope.endpoints().stream()
            .anyMatch(pattern -> endpoint.matches(pattern.replace("*", ".*")));
    }

    // Getters
    public String id() { return id; }
    public String tenantId() { return tenantId; }
    public String policyKey() { return policyKey; }
    public String name() { return name; }
    public String description() { return description; }
    public LimitType limitType() { return limitType; }
    public RateLimitConfig config() { return config; }
    public Scope scope() { return scope; }
    public boolean enabled() { return enabled; }
    public String environment() { return environment; }
    public String createdBy() { return createdBy; }
    public Instant createdAt() { return createdAt; }
    public String updatedBy() { return updatedBy; }
    public Instant updatedAt() { return updatedAt; }
    public Integer version() { return version; }

    public static class Builder {
        private String id;
        private String tenantId;
        private String policyKey;
        private String name;
        private String description;
        private LimitType limitType;
        private RateLimitConfig config = RateLimitConfig.standard();
        private Scope scope = new Scope(Set.of("*"), Set.of(), Set.of(), Map.of());
        private boolean enabled = true;
        private String environment = "production";
        private String createdBy;
        private Instant createdAt;
        private String updatedBy;
        private Instant updatedAt;
        private Integer version;

        public Builder id(String id) { this.id = id; return this; }
        public Builder tenantId(String tenantId) { this.tenantId = tenantId; return this; }
        public Builder policyKey(String policyKey) { this.policyKey = policyKey; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder limitType(LimitType limitType) { this.limitType = limitType; return this; }
        public Builder config(RateLimitConfig config) { this.config = config; return this; }
        public Builder scope(Scope scope) { this.scope = scope; return this; }
        public Builder enabled(boolean enabled) { this.enabled = enabled; return this; }
        public Builder environment(String environment) { this.environment = environment; return this; }
        public Builder createdBy(String createdBy) { this.createdBy = createdBy; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedBy(String updatedBy) { this.updatedBy = updatedBy; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }
        public Builder version(Integer version) { this.version = version; return this; }

        public RateLimitPolicy build() {
            return new RateLimitPolicy(this);
        }
    }
}
