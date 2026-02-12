package com.gogidix.rapidassist.anti.fraud.signals.service.domain.model;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Domain model representing an anti-fraud signal (indicator of potential fraud).
 * CRITICAL: tenantId is mandatory for tenant isolation - prevents data leakage between tenants.
 */
public class AntiFraudSignal {

    private final String id;
    private final String tenantId;
    private final String transactionId;
    private final String signalType;
    private final SignalSeverity severity;
    private final double riskScore;
    private final String description;
    private final Map<String, Object> metadata;
    private final boolean resolved;
    private final String resolvedBy;
    private final Instant resolvedAt;
    private final String resolutionNotes;
    private final Instant createdAt;
    private final String createdBy;

    private AntiFraudSignal(Builder builder) {
        this.id = builder.id;
        this.tenantId = builder.tenantId;
        this.transactionId = builder.transactionId;
        this.signalType = builder.signalType;
        this.severity = builder.severity;
        this.riskScore = builder.riskScore;
        this.description = builder.description;
        this.metadata = builder.metadata;
        this.resolved = builder.resolved;
        this.resolvedBy = builder.resolvedBy;
        this.resolvedAt = builder.resolvedAt;
        this.resolutionNotes = builder.resolutionNotes;
        this.createdAt = builder.createdAt;
        this.createdBy = builder.createdBy;
    }

    public String id() {
        return id;
    }

    public String tenantId() {
        return tenantId;
    }

    public String transactionId() {
        return transactionId;
    }

    public String signalType() {
        return signalType;
    }

    public SignalSeverity severity() {
        return severity;
    }

    public double riskScore() {
        return riskScore;
    }

    public String description() {
        return description;
    }

    public Map<String, Object> metadata() {
        return metadata;
    }

    public boolean resolved() {
        return resolved;
    }

    public String resolvedBy() {
        return resolvedBy;
    }

    public Instant resolvedAt() {
        return resolvedAt;
    }

    public String resolutionNotes() {
        return resolutionNotes;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public String createdBy() {
        return createdBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AntiFraudSignal that = (AntiFraudSignal) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AntiFraudSignal{" +
                "id='" + id + '\'' +
                ", tenantId='" + tenantId + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", signalType='" + signalType + '\'' +
                ", severity=" + severity +
                ", riskScore=" + riskScore +
                ", resolved=" + resolved +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String tenantId;
        private String transactionId;
        private String signalType;
        private SignalSeverity severity;
        private double riskScore;
        private String description;
        private Map<String, Object> metadata;
        private boolean resolved = false;
        private String resolvedBy;
        private Instant resolvedAt;
        private String resolutionNotes;
        private Instant createdAt;
        private String createdBy;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder tenantId(String tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        public Builder transactionId(String transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        public Builder signalType(String signalType) {
            this.signalType = signalType;
            return this;
        }

        public Builder severity(SignalSeverity severity) {
            this.severity = severity;
            return this;
        }

        public Builder riskScore(double riskScore) {
            this.riskScore = riskScore;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder metadata(Map<String, Object> metadata) {
            this.metadata = metadata;
            return this;
        }

        public Builder resolved(boolean resolved) {
            this.resolved = resolved;
            return this;
        }

        public Builder resolvedBy(String resolvedBy) {
            this.resolvedBy = resolvedBy;
            return this;
        }

        public Builder resolvedAt(Instant resolvedAt) {
            this.resolvedAt = resolvedAt;
            return this;
        }

        public Builder resolutionNotes(String resolutionNotes) {
            this.resolutionNotes = resolutionNotes;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public AntiFraudSignal build() {
            if (tenantId == null || tenantId.isBlank()) {
                throw new IllegalArgumentException("tenantId cannot be null or blank");
            }
            if (transactionId == null || transactionId.isBlank()) {
                throw new IllegalArgumentException("transactionId cannot be null or blank");
            }
            if (signalType == null || signalType.isBlank()) {
                throw new IllegalArgumentException("signalType cannot be null or blank");
            }
            if (severity == null) {
                throw new IllegalArgumentException("severity cannot be null");
            }
            if (riskScore < 0 || riskScore > 100) {
                throw new IllegalArgumentException("riskScore must be between 0 and 100");
            }
            if (createdAt == null) {
                createdAt = Instant.now();
            }
            return new AntiFraudSignal(this);
        }
    }

    public enum SignalSeverity {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }
}
