package com.gogidix.rapidassist.tenancy.configuration.service.domain.model;

import java.time.Instant;
import java.util.*;

/**
 * Domain model for tenant configuration
 */
public class TenantConfig {

    private final String id;
    private final String tenantId;
    private final String name;
    private final String domain;
    private final TenantSettings settings;
    private final TenantLimits limits;
    private final TenantFeatures features;
    private final boolean active;
    private final String environment;
    private final String createdBy;
    private final Instant createdAt;
    private final String updatedBy;
    private final Instant updatedAt;
    private final Integer version;

    public record TenantSettings(
        String timezone,
        String locale,
        String currency,
        String dateFormat,
        String timeFormat,
        Map<String, String> customSettings
    ) {
        public static TenantSettings defaults() {
            return new TenantSettings("Europe/Dublin", "en-GB", "EUR", "dd/MM/yyyy", "HH:mm", Map.of());
        }
    }

    public record TenantLimits(
        int maxUsers,
        long maxStorageGB,
        int maxRequestsPerMinute,
        java.time.LocalDate subscriptionExpiry
    ) {
        public static TenantLimits defaults() {
            return new TenantLimits(100, 100, 10000, java.time.LocalDate.now().plusYears(1));
        }
    }

    public record TenantFeatures(
        Set<String> enabledFeatures,
        Set<String> disabledFeatures,
        Map<String, String> featureConfig
    ) {
        public static TenantFeatures defaults() {
            return new TenantFeatures(Set.of("basic", "dashboard"), Set.of(), Map.of());
        }
    }

    private TenantConfig(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID().toString();
        this.tenantId = builder.tenantId;
        this.name = builder.name;
        this.domain = builder.domain;
        this.settings = builder.settings;
        this.limits = builder.limits;
        this.features = builder.features;
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

    public boolean hasFeature(String feature) {
        return features.enabledFeatures().contains(feature) && !features.disabledFeatures().contains(feature);
    }

    // Getters
    public String id() { return id; }
    public String tenantId() { return tenantId; }
    public String name() { return name; }
    public String domain() { return domain; }
    public TenantSettings settings() { return settings; }
    public TenantLimits limits() { return limits; }
    public TenantFeatures features() { return features; }
    public boolean active() { return active; }
    public String environment() { return environment; }
    public String createdBy() { return createdBy; }
    public Instant createdAt() { return createdAt; }
    public String updatedBy() { return updatedBy; }
    public Instant updatedAt() { return updatedAt; }
    public Integer version() { return version; }

    public static class Builder {
        private String id;
        private String tenantId;
        private String name;
        private String domain;
        private TenantSettings settings = TenantSettings.defaults();
        private TenantLimits limits = TenantLimits.defaults();
        private TenantFeatures features = TenantFeatures.defaults();
        private boolean active = true;
        private String environment = "production";
        private String createdBy;
        private Instant createdAt;
        private String updatedBy;
        private Instant updatedAt;
        private Integer version;

        public Builder id(String id) { this.id = id; return this; }
        public Builder tenantId(String tenantId) { this.tenantId = tenantId; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder domain(String domain) { this.domain = domain; return this; }
        public Builder settings(TenantSettings settings) { this.settings = settings; return this; }
        public Builder limits(TenantLimits limits) { this.limits = limits; return this; }
        public Builder features(TenantFeatures features) { this.features = features; return this; }
        public Builder active(boolean active) { this.active = active; return this; }
        public Builder environment(String environment) { this.environment = environment; return this; }
        public Builder createdBy(String createdBy) { this.createdBy = createdBy; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedBy(String updatedBy) { this.updatedBy = updatedBy; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }
        public Builder version(Integer version) { this.version = version; return this; }

        public TenantConfig build() {
            return new TenantConfig(this);
        }
    }
}
