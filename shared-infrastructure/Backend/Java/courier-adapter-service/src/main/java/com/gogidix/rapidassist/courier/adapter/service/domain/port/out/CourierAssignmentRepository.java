package com.gogidix.rapidassist.courier.adapter.service.domain.port.out;

import com.gogidix.rapidassist.courier.adapter.service.domain.model.CourierAssignment;
import com.gogidix.rapidassist.courier.adapter.service.domain.model.CourierAssignment.AssignmentStatus;

import java.util.List;
import java.util.Optional;

/**
 * Output port for CourierAssignment persistence.
 * All methods are tenant-scoped to ensure proper multi-tenancy isolation.
 */
public interface CourierAssignmentRepository {

    /**
     * Save a courier assignment.
     * The tenantId must be set on the entity before saving.
     */
    CourierAssignment save(CourierAssignment assignment);

    /**
     * Find assignment by ID within the specified tenant.
     * This ensures tenant isolation by requiring tenantId parameter.
     */
    Optional<CourierAssignment> findByIdAndTenantId(String id, String tenantId);

    /**
     * Find all assignments for a specific tenant.
     */
    List<CourierAssignment> findByTenantId(String tenantId);

    /**
     * Find assignments by tenant and order ID.
     */
    Optional<CourierAssignment> findByTenantIdAndOrderId(String tenantId, String orderId);

    /**
     * Find assignments by tenant and courier ID.
     */
    List<CourierAssignment> findByTenantIdAndCourierId(String tenantId, String courierId);

    /**
     * Find assignments by tenant and status.
     */
    List<CourierAssignment> findByTenantIdAndStatus(String tenantId, AssignmentStatus status);

    /**
     * Find active assignments for a specific tenant.
     * Active means assigned, in transit, or at delivery location.
     */
    List<CourierAssignment> findActiveByTenantId(String tenantId);

    /**
     * Delete assignment by ID within the specified tenant.
     */
    void deleteByIdAndTenantId(String id, String tenantId);

    /**
     * Count assignments by tenant and status.
     */
    long countByTenantIdAndStatus(String tenantId, AssignmentStatus status);

    /**
     * Check if assignment exists by tenant and order ID.
     */
    boolean existsByTenantIdAndOrderId(String tenantId, String orderId);
}
