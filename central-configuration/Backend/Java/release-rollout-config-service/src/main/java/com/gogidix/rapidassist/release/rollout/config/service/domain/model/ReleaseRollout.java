package com.gogidix.rapidassist.release.rollout.config.service.domain.model;

import java.time.Instant;
import java.util.*;

/**
 * Domain model for release rollout configuration
 */
public class ReleaseRollout {

    private final String id;
    private final String tenantId;
    private final String releaseId;
    private final String version;
    private final RolloutStrategy strategy;
    private final RolloutConfig config;
    private final RolloutStatus status;
    private final String environment;
    private final String createdBy;
    private final Instant createdAt;
    private final String updatedBy;
    private final Instant updatedAt;
    private final Integer recordVersion;

    public enum RolloutStrategy {
        BLUE_GREEN, CANARY, GRADUAL, BIG_BANG, AB_TESTING
    }

    public enum RolloutStatus {
        PLANNED, IN_PROGRESS, PAUSED, COMPLETED, ROLLED_BACK, FAILED
    }

    public record RolloutConfig(
        int batchSize,
        int batchIntervalMinutes,
        int initialPercentage,
        int maxPercentage,
        List<String> targetSegments,
        String criteria,
        boolean autoPromote,
        boolean requireApproval
    ) {
        public static RolloutConfig standard() {
            return new RolloutConfig(10, 30, 10, 100, List.of("all-users"), null, false, false);
        }
    }

    private ReleaseRollout(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID().toString();
        this.tenantId = builder.tenantId;
        this.releaseId = builder.releaseId;
        this.version = builder.version;
        this.strategy = builder.strategy;
        this.config = builder.config;
        this.status = builder.status;
        this.environment = builder.environment;
        this.createdBy = builder.createdBy;
        this.createdAt = builder.createdAt != null ? builder.createdAt : Instant.now();
        this.updatedBy = builder.updatedBy;
        this.updatedAt = builder.updatedAt != null ? builder.updatedAt : Instant.now();
        this.recordVersion = builder.recordVersion != null ? builder.recordVersion : 1;
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean canProceed() {
        return status == RolloutStatus.PLANNED || status == RolloutStatus.IN_PROGRESS;
    }

    // Getters
    public String id() { return id; }
    public String tenantId() { return tenantId; }
    public String releaseId() { return releaseId; }
    public String version() { return version; }
    public RolloutStrategy strategy() { return strategy; }
    public RolloutConfig config() { return config; }
    public RolloutStatus status() { return status; }
    public String environment() { return environment; }
    public String createdBy() { return createdBy; }
    public Instant createdAt() { return createdAt; }
    public String updatedBy() { return updatedBy; }
    public Instant updatedAt() { return updatedAt; }
    public Integer recordVersion() { return recordVersion; }

    public static class Builder {
        private String id;
        private String tenantId;
        private String releaseId;
        private String version;
        private RolloutStrategy strategy;
        private RolloutConfig config = RolloutConfig.standard();
        private RolloutStatus status = RolloutStatus.PLANNED;
        private String environment = "production";
        private String createdBy;
        private Instant createdAt;
        private String updatedBy;
        private Instant updatedAt;
        private Integer recordVersion;

        public Builder id(String id) { this.id = id; return this; }
        public Builder tenantId(String tenantId) { this.tenantId = tenantId; return this; }
        public Builder releaseId(String releaseId) { this.releaseId = releaseId; return this; }
        public Builder version(String version) { this.version = version; return this; }
        public Builder strategy(RolloutStrategy strategy) { this.strategy = strategy; return this; }
        public Builder config(RolloutConfig config) { this.config = config; return this; }
        public Builder status(RolloutStatus status) { this.status = status; return this; }
        public Builder environment(String environment) { this.environment = environment; return this; }
        public Builder createdBy(String createdBy) { this.createdBy = createdBy; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedBy(String updatedBy) { this.updatedBy = updatedBy; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }
        public Builder recordVersion(Integer recordVersion) { this.recordVersion = recordVersion; return this; }

        public ReleaseRollout build() {
            return new ReleaseRollout(this);
        }
    }
}
