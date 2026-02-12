package com.gogidix.rapidassist.anti.fraud.rules.service.domain.model;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Domain model representing an anti-fraud rule.
 * CRITICAL: tenantId is mandatory for tenant isolation - prevents data leakage between tenants.
 */
public class AntiFraudRule {

    private final String id;
    private final String tenantId;
    private final String name;
    private final String description;
    private final RuleType ruleType;
    private final boolean active;
    private final int priority;
    private final Map<String, Object> conditions;
    private final Map<String, Object> actions;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final String createdBy;
    private final String updatedBy;

    private AntiFraudRule(Builder builder) {
        this.id = builder.id;
        this.tenantId = builder.tenantId;
        this.name = builder.name;
        this.description = builder.description;
        this.ruleType = builder.ruleType;
        this.active = builder.active;
        this.priority = builder.priority;
        this.conditions = builder.conditions;
        this.actions = builder.actions;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
        this.createdBy = builder.createdBy;
        this.updatedBy = builder.updatedBy;
    }

    public String id() {
        return id;
    }

    public String tenantId() {
        return tenantId;
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public RuleType ruleType() {
        return ruleType;
    }

    public boolean active() {
        return active;
    }

    public int priority() {
        return priority;
    }

    public Map<String, Object> conditions() {
        return conditions;
    }

    public Map<String, Object> actions() {
        return actions;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public String createdBy() {
        return createdBy;
    }

    public String updatedBy() {
        return updatedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AntiFraudRule that = (AntiFraudRule) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AntiFraudRule{" +
                "id='" + id + '\'' +
                ", tenantId='" + tenantId + '\'' +
                ", name='" + name + '\'' +
                ", ruleType=" + ruleType +
                ", active=" + active +
                ", priority=" + priority +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String tenantId;
        private String name;
        private String description;
        private RuleType ruleType;
        private boolean active = true;
        private int priority = 0;
        private Map<String, Object> conditions;
        private Map<String, Object> actions;
        private Instant createdAt;
        private Instant updatedAt;
        private String createdBy;
        private String updatedBy;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder tenantId(String tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder ruleType(RuleType ruleType) {
            this.ruleType = ruleType;
            return this;
        }

        public Builder active(boolean active) {
            this.active = active;
            return this;
        }

        public Builder priority(int priority) {
            this.priority = priority;
            return this;
        }

        public Builder conditions(Map<String, Object> conditions) {
            this.conditions = conditions;
            return this;
        }

        public Builder actions(Map<String, Object> actions) {
            this.actions = actions;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public Builder updatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
            return this;
        }

        public AntiFraudRule build() {
            if (tenantId == null || tenantId.isBlank()) {
                throw new IllegalArgumentException("tenantId cannot be null or blank");
            }
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("name cannot be null or blank");
            }
            if (ruleType == null) {
                throw new IllegalArgumentException("ruleType cannot be null");
            }
            Instant now = Instant.now();
            if (createdAt == null) {
                createdAt = now;
            }
            if (updatedAt == null) {
                updatedAt = now;
            }
            return new AntiFraudRule(this);
        }
    }

    public enum RuleType {
        THRESHOLD,
        PATTERN,
        VELOCITY,
        BLACKLIST,
        WHITELIST,
        MACHINE_LEARNING,
        CUSTOM
    }
}
