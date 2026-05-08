package com.gogidix.rapidassist.feature.flags.service.domain.model;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain model representing a change to a feature flag
 */
public class FeatureFlagChange {

    private final String id;
    private final String flagId;
    private final String flagKey;
    private final String tenantId;
    private final ChangeType changeType;
    private final Integer fromVersion;
    private final Integer toVersion;
    private final Boolean previousEnabled;
    private final Boolean newEnabled;
    private final String changedBy;
    private final String reason;
    private final Instant changedAt;
    private final ApprovalStatus approvalStatus;
    private final String approvedBy;
    private final Instant approvedAt;

    private FeatureFlagChange(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID().toString();
        this.flagId = builder.flagId;
        this.flagKey = builder.flagKey;
        this.tenantId = builder.tenantId;
        this.changeType = builder.changeType;
        this.fromVersion = builder.fromVersion;
        this.toVersion = builder.toVersion;
        this.previousEnabled = builder.previousEnabled;
        this.newEnabled = builder.newEnabled;
        this.changedBy = builder.changedBy;
        this.reason = builder.reason;
        this.changedAt = builder.changedAt != null ? builder.changedAt : Instant.now();
        this.approvalStatus = builder.approvalStatus != null ? builder.approvalStatus : ApprovalStatus.PENDING;
        this.approvedBy = builder.approvedBy;
        this.approvedAt = builder.approvedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static FeatureFlagChange create(String flagId, String flagKey, String tenantId,
                                          ChangeType changeType, Boolean previousEnabled,
                                          Boolean newEnabled, String changedBy, String reason) {
        return builder()
            .flagId(flagId)
            .flagKey(flagKey)
            .tenantId(tenantId)
            .changeType(changeType)
            .previousEnabled(previousEnabled)
            .newEnabled(newEnabled)
            .changedBy(changedBy)
            .reason(reason)
            .build();
    }

    public FeatureFlagChange approve(String approvedBy) {
        return toBuilder()
            .approvalStatus(ApprovalStatus.APPROVED)
            .approvedBy(approvedBy)
            .approvedAt(Instant.now())
            .build();
    }

    public FeatureFlagChange reject(String rejectedBy) {
        return toBuilder()
            .approvalStatus(ApprovalStatus.REJECTED)
            .approvedBy(rejectedBy)
            .approvedAt(Instant.now())
            .build();
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    // Getters
    public String id() { return id; }
    public String flagId() { return flagId; }
    public String flagKey() { return flagKey; }
    public String tenantId() { return tenantId; }
    public ChangeType changeType() { return changeType; }
    public Integer fromVersion() { return fromVersion; }
    public Integer toVersion() { return toVersion; }
    public Boolean previousEnabled() { return previousEnabled; }
    public Boolean newEnabled() { return newEnabled; }
    public String changedBy() { return changedBy; }
    public String reason() { return reason; }
    public Instant changedAt() { return changedAt; }
    public ApprovalStatus approvalStatus() { return approvalStatus; }
    public String approvedBy() { return approvedBy; }
    public Instant approvedAt() { return approvedAt; }

    public enum ChangeType {
        CREATE, ENABLE, DISABLE, UPDATE, ARCHIVE, RESTORE
    }

    public enum ApprovalStatus {
        PENDING, APPROVED, REJECTED
    }

    public static class Builder {
        private String id;
        private String flagId;
        private String flagKey;
        private String tenantId;
        private ChangeType changeType;
        private Integer fromVersion;
        private Integer toVersion;
        private Boolean previousEnabled;
        private Boolean newEnabled;
        private String changedBy;
        private String reason;
        private Instant changedAt;
        private ApprovalStatus approvalStatus;
        private String approvedBy;
        private Instant approvedAt;

        private Builder() {}

        private Builder(FeatureFlagChange change) {
            this.id = change.id;
            this.flagId = change.flagId;
            this.flagKey = change.flagKey;
            this.tenantId = change.tenantId;
            this.changeType = change.changeType;
            this.fromVersion = change.fromVersion;
            this.toVersion = change.toVersion;
            this.previousEnabled = change.previousEnabled;
            this.newEnabled = change.newEnabled;
            this.changedBy = change.changedBy;
            this.reason = change.reason;
            this.changedAt = change.changedAt;
            this.approvalStatus = change.approvalStatus;
            this.approvedBy = change.approvedBy;
            this.approvedAt = change.approvedAt;
        }

        public Builder id(String id) { this.id = id; return this; }
        public Builder flagId(String flagId) { this.flagId = flagId; return this; }
        public Builder flagKey(String flagKey) { this.flagKey = flagKey; return this; }
        public Builder tenantId(String tenantId) { this.tenantId = tenantId; return this; }
        public Builder changeType(ChangeType changeType) { this.changeType = changeType; return this; }
        public Builder fromVersion(Integer fromVersion) { this.fromVersion = fromVersion; return this; }
        public Builder toVersion(Integer toVersion) { this.toVersion = toVersion; return this; }
        public Builder previousEnabled(Boolean previousEnabled) { this.previousEnabled = previousEnabled; return this; }
        public Builder newEnabled(Boolean newEnabled) { this.newEnabled = newEnabled; return this; }
        public Builder changedBy(String changedBy) { this.changedBy = changedBy; return this; }
        public Builder reason(String reason) { this.reason = reason; return this; }
        public Builder changedAt(Instant changedAt) { this.changedAt = changedAt; return this; }
        public Builder approvalStatus(ApprovalStatus approvalStatus) { this.approvalStatus = approvalStatus; return this; }
        public Builder approvedBy(String approvedBy) { this.approvedBy = approvedBy; return this; }
        public Builder approvedAt(Instant approvedAt) { this.approvedAt = approvedAt; return this; }

        public FeatureFlagChange build() {
            return new FeatureFlagChange(this);
        }
    }
}
