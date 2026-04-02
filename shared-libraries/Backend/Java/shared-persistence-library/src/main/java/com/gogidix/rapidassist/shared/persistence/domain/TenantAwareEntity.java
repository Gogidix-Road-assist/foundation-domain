package com.gogidix.rapidassist.shared.persistence.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import java.util.Objects;
import java.util.UUID;

/**
 * Abstract base class for tenant-aware entities in a multi-tenant system.
 * <p>
 * This class provides automatic tenant isolation by including a tenant_id field
 * in all extending entities. It follows the Domain-Driven Design (DDD) pattern
 * for persistence-aware domain models.
 * </p>
 * <p>
 * Entities extending this class will automatically have tenant_id field
 * in their MongoDB document, enabling row-level security and data isolation
 * between tenants in the SaaS platform.
 * </p>
 * <p>
 * Usage example:
 * <pre>{@code
 * @Document(collection = "service_requests")
 * public class ServiceRequest extends TenantAwareEntity {
 *     @Id
 *     private UUID id = UUID.randomUUID();
 *
 *     private String description;
 *     // other fields...
 * }
 * }</pre>
 * </p>
 *
 * @author Gogidix Platform Team
 * @version 1.0
 * @since 2026-01-13
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public abstract class TenantAwareEntity {

    /**
     * The unique identifier of the tenant that owns this entity.
     * <p>
     * This field is automatically populated from the request context
     * before persistence operations and is used for query filtering
     * to ensure tenant data isolation.
     * </p>
     */
    @Field("tenant_id")
    private UUID tenantId;

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
    public void setTenantId(UUID tenantId) {
        if (tenantId == null) {
            throw new IllegalArgumentException("Tenant ID cannot be null");
        }
        this.tenantId = tenantId;
    }

    /**
     * Checks if this entity belongs to the specified tenant.
     *
     * @param tenantId the tenant ID to check against
     * @return true if this entity belongs to the specified tenant
     */
    public boolean belongsToTenant(UUID tenantId) {
        return Objects.equals(this.tenantId, tenantId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TenantAwareEntity that = (TenantAwareEntity) o;
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
                '}';
    }
}
