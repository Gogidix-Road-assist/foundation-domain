package com.gogidix.rapidassist.payments.adapter.service.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * Payment entity representing a financial transaction.
 * <p>
 * CRITICAL: All payments MUST be tenant-isolated for compliance and security.
 * The tenantId field is mandatory and indexed to prevent cross-tenant data access.
 *
 * @author Gogidix
 * @since 1.0.0
 */
@Entity
@Table(
    name = "payments",
    indexes = {
        @Index(name = "idx_payments_tenant_id", columnList = "tenant_id"),
        @Index(name = "idx_payments_provider_reference", columnList = "provider_reference"),
        @Index(name = "idx_payments_status", columnList = "status"),
        @Index(name = "idx_payments_created_at", columnList = "created_at")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_payments_tenant_provider_ref", columnNames = {"tenant_id", "provider_reference"})
    }
)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    /**
     * CRITICAL: Tenant ID for multi-tenancy isolation.
     * All queries MUST filter by this field to prevent cross-tenant data access.
     */
    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "provider", nullable = false)
    private String provider;

    @Column(name = "provider_reference", nullable = false)
    private String providerReference;

    @Column(name = "amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    @Column(name = "external_payment_id")
    private String externalPaymentId;

    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Version
    @Column(name = "version")
    private Long version;

    /**
     * Payment status enum.
     */
    public enum PaymentStatus {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED,
        CANCELLED,
        REFUNDED
    }

    // Default constructor required by JPA
    protected Payment() {
    }

    // Builder pattern for construction
    private Payment(Builder builder) {
        this.id = builder.id;
        this.tenantId = builder.tenantId;
        this.provider = builder.provider;
        this.providerReference = builder.providerReference;
        this.amount = builder.amount;
        this.currency = builder.currency;
        this.status = builder.status;
        this.externalPaymentId = builder.externalPaymentId;
        this.metadata = builder.metadata;
        this.createdAt = builder.createdAt != null ? builder.createdAt : Instant.now();
        this.updatedAt = builder.updatedAt;
        this.completedAt = builder.completedAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getProvider() {
        return provider;
    }

    public String getProviderReference() {
        return providerReference;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public String getExternalPaymentId() {
        return externalPaymentId;
    }

    public String getMetadata() {
        return metadata;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public Long getVersion() {
        return version;
    }

    public void markAsCompleted(String externalPaymentId) {
        this.status = PaymentStatus.COMPLETED;
        this.externalPaymentId = externalPaymentId;
        this.completedAt = Instant.now();
    }

    public void markAsFailed() {
        this.status = PaymentStatus.FAILED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Objects.equals(id, payment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id='" + id + '\'' +
                ", tenantId='" + tenantId + '\'' +
                ", provider='" + provider + '\'' +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", status=" + status +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String tenantId;
        private String provider;
        private String providerReference;
        private BigDecimal amount;
        private String currency;
        private PaymentStatus status;
        private String externalPaymentId;
        private String metadata;
        private Instant createdAt;
        private Instant updatedAt;
        private Instant completedAt;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder tenantId(String tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        public Builder provider(String provider) {
            this.provider = provider;
            return this;
        }

        public Builder providerReference(String providerReference) {
            this.providerReference = providerReference;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public Builder status(PaymentStatus status) {
            this.status = status;
            return this;
        }

        public Builder externalPaymentId(String externalPaymentId) {
            this.externalPaymentId = externalPaymentId;
            return this;
        }

        public Builder metadata(String metadata) {
            this.metadata = metadata;
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

        public Builder completedAt(Instant completedAt) {
            this.completedAt = completedAt;
            return this;
        }

        public Payment build() {
            if (tenantId == null) {
                throw new IllegalStateException("tenantId is required");
            }
            if (provider == null) {
                throw new IllegalStateException("provider is required");
            }
            if (providerReference == null) {
                throw new IllegalStateException("providerReference is required");
            }
            if (amount == null) {
                throw new IllegalStateException("amount is required");
            }
            if (currency == null) {
                throw new IllegalStateException("currency is required");
            }
            if (status == null) {
                this.status = PaymentStatus.PENDING;
            }
            return new Payment(this);
        }
    }
}
