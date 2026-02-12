package com.gogidix.rapidassist.courier.adapter.service.adapters.persistence;

import com.gogidix.rapidassist.courier.adapter.service.domain.model.CourierAssignment;
import com.gogidix.rapidassist.courier.adapter.service.domain.model.CourierAssignment.AssignmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for CourierAssignment entity.
 * All queries are tenant-scoped to ensure proper multi-tenancy isolation.
 */
public interface CourierAssignmentJpaRepository extends JpaRepository<CourierAssignment, String> {

    /**
     * Find assignment by ID and tenantId to ensure tenant isolation.
     */
    @Query("SELECT ca FROM CourierAssignment ca WHERE ca.id = :id AND ca.tenantId = :tenantId")
    Optional<CourierAssignment> findByIdAndTenantId(@Param("id") String id, @Param("tenantId") String tenantId);

    /**
     * Find all assignments for a specific tenant.
     */
    @Query("SELECT ca FROM CourierAssignment ca WHERE ca.tenantId = :tenantId ORDER BY ca.assignedAt DESC")
    List<CourierAssignment> findByTenantId(@Param("tenantId") String tenantId);

    /**
     * Find assignment by tenant and order ID.
     */
    @Query("SELECT ca FROM CourierAssignment ca WHERE ca.tenantId = :tenantId AND ca.orderId = :orderId")
    Optional<CourierAssignment> findByTenantIdAndOrderId(@Param("tenantId") String tenantId, @Param("orderId") String orderId);

    /**
     * Find assignments by tenant and courier ID.
     */
    @Query("SELECT ca FROM CourierAssignment ca WHERE ca.tenantId = :tenantId AND ca.courierId = :courierId ORDER BY ca.assignedAt DESC")
    List<CourierAssignment> findByTenantIdAndCourierId(@Param("tenantId") String tenantId, @Param("courierId") String courierId);

    /**
     * Find assignments by tenant and status.
     */
    @Query("SELECT ca FROM CourierAssignment ca WHERE ca.tenantId = :tenantId AND ca.status = :status ORDER BY ca.assignedAt DESC")
    List<CourierAssignment> findByTenantIdAndStatus(@Param("tenantId") String tenantId, @Param("status") AssignmentStatus status);

    /**
     * Find active assignments for a specific tenant.
     * Active statuses: ASSIGNED, EN_ROUTE_TO_PICKUP, AT_PICKUP_LOCATION, PACKAGE_PICKED_UP, IN_TRANSIT, AT_DELIVERY_LOCATION
     */
    @Query("SELECT ca FROM CourierAssignment ca WHERE ca.tenantId = :tenantId AND ca.status IN :statuses ORDER BY ca.assignedAt DESC")
    List<CourierAssignment> findActiveByTenantId(@Param("tenantId") String tenantId, @Param("statuses") List<AssignmentStatus> statuses);

    /**
     * Delete assignment by ID and tenantId.
     */
    @Query("DELETE FROM CourierAssignment ca WHERE ca.id = :id AND ca.tenantId = :tenantId")
    void deleteByIdAndTenantId(@Param("id") String id, @Param("tenantId") String tenantId);

    /**
     * Count assignments by tenant and status.
     */
    @Query("SELECT COUNT(ca) FROM CourierAssignment ca WHERE ca.tenantId = :tenantId AND ca.status = :status")
    long countByTenantIdAndStatus(@Param("tenantId") String tenantId, @Param("status") AssignmentStatus status);

    /**
     * Check if assignment exists by tenant and order ID.
     */
    @Query("SELECT CASE WHEN COUNT(ca) > 0 THEN true ELSE false END FROM CourierAssignment ca WHERE ca.tenantId = :tenantId AND ca.orderId = :orderId")
    boolean existsByTenantIdAndOrderId(@Param("tenantId") String tenantId, @Param("orderId") String orderId);

    /**
     * Prevent usage of unsafe findAll() method - must use tenant-scoped version.
     */
    @Override
    @Query("SELECT ca FROM CourierAssignment ca WHERE 1=0")
    List<CourierAssignment> findAll();

    /**
     * Prevent usage of unsafe findById() method - must use tenant-scoped version.
     */
    @Override
    @Query("SELECT ca FROM CourierAssignment ca WHERE 1=0")
    Optional<CourierAssignment> findById(String id);

    /**
     * Prevent usage of unsafe deleteById() method - must use tenant-scoped version.
     */
    @Override
    @Query("SELECT ca FROM CourierAssignment ca WHERE 1=0")
    void deleteById(String id);
}
