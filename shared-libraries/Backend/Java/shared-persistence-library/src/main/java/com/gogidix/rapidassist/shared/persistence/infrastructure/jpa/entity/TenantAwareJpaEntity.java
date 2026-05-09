package com.gogidix.rapidassist.shared.persistence.infrastructure.jpa.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Abstract base class for tenant-aware JPA entities in a multi-tenant system.
 * <p>
 * This class provides automatic tenant isolation by including a tenant_id field
 * in all extending entities. It follows the Domain-Driven Design (DDD) pattern
 * for persistence-aware domain models using JPA.
 * </p>
 * <p>
 * Entities extending this class will automatically have:
 * <ul>
 *   <li>tenant_id field for data isolation</li>
 *   <li>created_at timestamp</li>
 *   <li>updated_at timestamp</li>
 *   <li>created_by and updated_by audit fields</li>
 * </ul>
 * </p>
 * <p>
 * Usage example:
 * <pre>{@code
 * @Entity
 * @Table(name = "policies")
 * public class Policy extends TenantAwareJpaEntity {
 *     @Id
 *     @GeneratedValue(strategy = GenerationType.IDENTITY)
 *     private Long id;
 *
 *     private String policyNumber;
 *     // other fields...
 * }
 * }</pre>
 * </p>
 *
 * @author Gogidix Platform Team
 * @version 1.0
 * @since 2026-03-10
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class TenantAwareJpaEntity {

    /**
     * The unique identifier of the tenant that owns this entity.
     * <p>
     * This field is automatically populated from the request context
     * before persistence operations and is used for query filtering
     * to ensure tenant data isolation.
     * </p>
     */
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId = 1L;

    /**
     * The timestamp when this entity was created.
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * The timestamp when this entity was last updated.
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * The user who created this entity.
     */
    @Column(name = "created_by")
    private String createdBy;

    /**
     * The user who last updated this entity.
     */
    @Column(name = "updated_by")
    private String updatedBy;

    /**
     * Lifecycle callback before persist - sets creation timestamp.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (tenantId == null) {
            tenantId = 1L;
        }
    }

    /**
     * Lifecycle callback before update - sets update timestamp.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Gets the tenant ID for this entity.
     *
     * @return the tenant ID
     */
    public Long getTenantId() {
        return tenantId;
    }

    /**
     * Sets the tenant identifier for this entity.
     * <p>
     * This method validates that the tenant ID is not null before assignment.
     * In normal operation, this is automatically populated from the request context.
     * </p>
     *
     * @param tenantId the unique identifier of the tenant
     * @throws IllegalArgumentException if tenantId is null
     */
    public void setTenantId(Long tenantId) {
        if (tenantId == null) {
            throw new IllegalArgumentException("Tenant ID cannot be null");
        }
        this.tenantId = tenantId;
    }

    /**
     * Gets the creation timestamp.
     *
     * @return the creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation timestamp.
     *
     * @param createdAt the creation timestamp
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the last update timestamp.
     *
     * @return the last update timestamp
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the last update timestamp.
     *
     * @param updatedAt the last update timestamp
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Gets the user who created this entity.
     *
     * @return the creator user ID
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets the user who created this entity.
     *
     * @param createdBy the creator user ID
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Gets the user who last updated this entity.
     *
     * @return the last updater user ID
     */
    public String getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Sets the user who last updated this entity.
     *
     * @param updatedBy the last updater user ID
     */
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Checks if this entity belongs to the specified tenant.
     *
     * @param tenantId the tenant ID to check against
     * @return true if this entity belongs to the specified tenant
     */
    public boolean belongsToTenant(Long tenantId) {
        return Objects.equals(this.tenantId, tenantId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        TenantAwareJpaEntity that = (TenantAwareJpaEntity) o;
        return Objects.equals(tenantId, that.tenantId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tenantId);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "tenantId=" + tenantId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
