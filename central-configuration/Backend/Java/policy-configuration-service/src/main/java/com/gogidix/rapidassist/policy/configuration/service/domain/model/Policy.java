package com.gogidix.rapidassist.policy.configuration.service.domain.model;

import java.time.Instant;
import java.util.*;

/**
 * Domain model representing a policy configuration
 */
public class Policy {

    private final String id;
    private final String tenantId;
    private final String policyKey;
    private final String name;
    private final String description;
    private final PolicyType type;
    private final PolicyScope scope;
    private final Map<String, Object> rules;
    private final PolicyConstraints constraints;
    private final boolean enforced;
    private final int priority;
    private final String environment;
    private final PolicyStatus status;
    private final String createdBy;
    private final Instant createdAt;
    private final String updatedBy;
    private final Instant updatedAt;
    private final Integer version;
    private final Set<String> tags;

    private Policy(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID().toString();
        this.tenantId = builder.tenantId;
        this.policyKey = builder.policyKey;
        this.name = builder.name;
        this.description = builder.description;
        this.type = builder.type;
        this.scope = builder.scope;
        this.rules = Map.copyOf(builder.rules);
        this.constraints = builder.constraints;
        this.enforced = builder.enforced;
        this.priority = builder.priority;
        this.environment = builder.environment;
        this.status = builder.status;
        this.createdBy = builder.createdBy;
        this.createdAt = builder.createdAt != null ? builder.createdAt : Instant.now();
        this.updatedBy = builder.updatedBy;
        this.updatedAt = builder.updatedAt != null ? builder.updatedAt : Instant.now();
        this.version = builder.version != null ? builder.version : 1;
        this.tags = Set.copyOf(builder.tags);
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean isActive() {
        return status == PolicyStatus.ACTIVE && enforced;
    }

    public boolean appliesTo(String entityType, String entityId) {
        return scope.appliesTo(entityType, entityId);
    }

    public <T> T getRule(String key, Class<T> type) {
        Object value = rules.get(key);
        if (value == null) return null;
        return type.cast(value);
    }

    // Getters
    public String id() { return id; }
    public String tenantId() { return tenantId; }
    public String policyKey() { return policyKey; }
    public String name() { return name; }
    public String description() { return description; }
    public PolicyType type() { return type; }
    public PolicyScope scope() { return scope; }
    public Map<String, Object> rules() { return rules; }
    public PolicyConstraints constraints() { return constraints; }
    public boolean enforced() { return enforced; }
    public int priority() { return priority; }
    public String environment() { return environment; }
    public PolicyStatus status() { return status; }
    public String createdBy() { return createdBy; }
    public Instant createdAt() { return createdAt; }
    public String updatedBy() { return updatedBy; }
    public Instant updatedAt() { return updatedAt; }
    public Integer version() { return version; }
    public Set<String> tags() { return tags; }

    public enum PolicyType {
        SECURITY, PRIVACY, BUSINESS_RULE, COMPLIANCE, RATE_LIMIT, ACCESS_CONTROL
    }

    public enum PolicyStatus {
        DRAFT, ACTIVE, INACTIVE, ARCHIVED
    }

    public record PolicyScope(
        ScopeType type,
        Set<String> entityTypes,
        Set<String> entityIds,
        Map<String, String> attributes
    ) {
        public enum ScopeType { GLOBAL, ENTITY_TYPE, SPECIFIC_ENTITIES, ATTRIBUTE_BASED }

        public boolean appliesTo(String entityType, String entityId) {
            return switch (type) {
                case GLOBAL -> true;
                case ENTITY_TYPE -> entityTypes.contains(entityType);
                case SPECIFIC_ENTITIES -> entityIds.contains(entityId);
                case ATTRIBUTE_BASED -> true; // Would check attributes
            };
        }

        public static PolicyScope global() {
            return new PolicyScope(ScopeType.GLOBAL, Set.of(), Set.of(), Map.of());
        }

        public static PolicyScope forEntityTypes(Set<String> types) {
            return new PolicyScope(ScopeType.ENTITY_TYPE, types, Set.of(), Map.of());
        }
    }

    public record PolicyConstraints(
        int maxRetries,
        long timeoutMs,
        boolean requireApproval,
        Set<String> requiredRoles
    ) {
        public static PolicyConstraints defaults() {
            return new PolicyConstraints(3, 30000, false, Set.of());
        }
    }

    public static class Builder {
        private String id;
        private String tenantId;
        private String policyKey;
        private String name;
        private String description;
        private PolicyType type;
        private PolicyScope scope = PolicyScope.global();
        private Map<String, Object> rules = Map.of();
        private PolicyConstraints constraints = PolicyConstraints.defaults();
        private boolean enforced = true;
        private int priority = 0;
        private String environment = "production";
        private PolicyStatus status = PolicyStatus.DRAFT;
        private String createdBy;
        private Instant createdAt;
        private String updatedBy;
        private Instant updatedAt;
        private Integer version;
        private Set<String> tags = Set.of();

        public Builder id(String id) { this.id = id; return this; }
        public Builder tenantId(String tenantId) { this.tenantId = tenantId; return this; }
        public Builder policyKey(String policyKey) { this.policyKey = policyKey; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder type(PolicyType type) { this.type = type; return this; }
        public Builder scope(PolicyScope scope) { this.scope = scope; return this; }
        public Builder rules(Map<String, Object> rules) { this.rules = rules; return this; }
        public Builder constraints(PolicyConstraints constraints) { this.constraints = constraints; return this; }
        public Builder enforced(boolean enforced) { this.enforced = enforced; return this; }
        public Builder priority(int priority) { this.priority = priority; return this; }
        public Builder environment(String environment) { this.environment = environment; return this; }
        public Builder status(PolicyStatus status) { this.status = status; return this; }
        public Builder createdBy(String createdBy) { this.createdBy = createdBy; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedBy(String updatedBy) { this.updatedBy = updatedBy; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }
        public Builder version(Integer version) { this.version = version; return this; }
        public Builder tags(Set<String> tags) { this.tags = tags; return this; }

        public Policy build() {
            return new Policy(this);
        }
    }
}
