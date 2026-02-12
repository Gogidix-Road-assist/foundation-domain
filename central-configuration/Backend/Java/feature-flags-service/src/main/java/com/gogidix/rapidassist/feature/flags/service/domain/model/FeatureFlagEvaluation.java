package com.gogidix.rapidassist.feature.flags.service.domain.model;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing a feature flag evaluation result
 */
public class FeatureFlagEvaluation {

    private final String id;
    private final String flagKey;
    private final String tenantId;
    private final String userId;
    private final String context;
    private final boolean enabled;
    private final String variant;
    private final Map<String, Object> parameters;
    private final String reason;
    private final Instant evaluatedAt;

    private FeatureFlagEvaluation(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID().toString();
        this.flagKey = builder.flagKey;
        this.tenantId = builder.tenantId;
        this.userId = builder.userId;
        this.context = builder.context;
        this.enabled = builder.enabled;
        this.variant = builder.variant;
        this.parameters = builder.parameters;
        this.reason = builder.reason;
        this.evaluatedAt = builder.evaluatedAt != null ? builder.evaluatedAt : Instant.now();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static FeatureFlagEvaluation enabled(String flagKey, String tenantId, String userId) {
        return builder()
            .flagKey(flagKey)
            .tenantId(tenantId)
            .userId(userId)
            .enabled(true)
            .reason("Feature is enabled")
            .build();
    }

    public static FeatureFlagEvaluation disabled(String flagKey, String tenantId, String userId) {
        return builder()
            .flagKey(flagKey)
            .tenantId(tenantId)
            .userId(userId)
            .enabled(false)
            .reason("Feature is disabled")
            .build();
    }

    public static FeatureFlagEvaluation notFound(String flagKey, String tenantId) {
        return builder()
            .flagKey(flagKey)
            .tenantId(tenantId)
            .enabled(false)
            .reason("Feature flag not found")
            .build();
    }

    // Getters
    public String id() { return id; }
    public String flagKey() { return flagKey; }
    public String tenantId() { return tenantId; }
    public String userId() { return userId; }
    public String context() { return context; }
    public boolean enabled() { return enabled; }
    public String variant() { return variant; }
    public Map<String, Object> parameters() { return parameters; }
    public String reason() { return reason; }
    public Instant evaluatedAt() { return evaluatedAt; }

    public static class Builder {
        private String id;
        private String flagKey;
        private String tenantId;
        private String userId;
        private String context;
        private boolean enabled;
        private String variant;
        private Map<String, Object> parameters;
        private String reason;
        private Instant evaluatedAt;

        public Builder id(String id) { this.id = id; return this; }
        public Builder flagKey(String flagKey) { this.flagKey = flagKey; return this; }
        public Builder tenantId(String tenantId) { this.tenantId = tenantId; return this; }
        public Builder userId(String userId) { this.userId = userId; return this; }
        public Builder context(String context) { this.context = context; return this; }
        public Builder enabled(boolean enabled) { this.enabled = enabled; return this; }
        public Builder variant(String variant) { this.variant = variant; return this; }
        public Builder parameters(Map<String, Object> parameters) { this.parameters = parameters; return this; }
        public Builder reason(String reason) { this.reason = reason; return this; }
        public Builder evaluatedAt(Instant evaluatedAt) { this.evaluatedAt = evaluatedAt; return this; }

        public FeatureFlagEvaluation build() {
            return new FeatureFlagEvaluation(this);
        }
    }
}
