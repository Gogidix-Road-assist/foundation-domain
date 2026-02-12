package com.gogidix.rapidassist.courier.adapter.service.domain.port.in;

import com.gogidix.rapidassist.courier.adapter.service.domain.model.CourierAssignment;
import com.gogidix.rapidassist.courier.adapter.service.domain.model.CourierAssignment.AssignmentStatus;

import java.util.List;
import java.util.Optional;

/**
 * Input port for CourierAssignment operations.
 * All operations are tenant-scoped to ensure proper multi-tenancy isolation.
 */
public interface CourierAssignmentService {

    /**
     * Create a new courier assignment for the specified tenant.
     *
     * @param tenantId the tenant ID (will be enforced)
     * @param orderId the order ID
     * @param courierId the courier ID
     * @param pickupAddress the pickup address
     * @param deliveryAddress the delivery address
     * @return the created assignment
     */
    CourierAssignment createAssignment(String tenantId, String orderId, String courierId,
                                       String pickupAddress, String deliveryAddress);

    /**
     * Get assignment by ID for the specified tenant.
     *
     * @param tenantId the tenant ID
     * @param assignmentId the assignment ID
     * @return the assignment if found
     */
    Optional<CourierAssignment> getAssignmentById(String tenantId, String assignmentId);

    /**
     * Get assignment by order ID for the specified tenant.
     *
     * @param tenantId the tenant ID
     * @param orderId the order ID
     * @return the assignment if found
     */
    Optional<CourierAssignment> getAssignmentByOrderId(String tenantId, String orderId);

    /**
     * Get all assignments for the specified tenant.
     *
     * @param tenantId the tenant ID
     * @return list of assignments
     */
    List<CourierAssignment> getAllAssignments(String tenantId);

    /**
     * Get assignments by courier for the specified tenant.
     *
     * @param tenantId the tenant ID
     * @param courierId the courier ID
     * @return list of assignments
     */
    List<CourierAssignment> getAssignmentsByCourier(String tenantId, String courierId);

    /**
     * Get assignments by status for the specified tenant.
     *
     * @param tenantId the tenant ID
     * @param status the assignment status
     * @return list of assignments
     */
    List<CourierAssignment> getAssignmentsByStatus(String tenantId, AssignmentStatus status);

    /**
     * Get active assignments for the specified tenant.
     *
     * @param tenantId the tenant ID
     * @return list of active assignments
     */
    List<CourierAssignment> getActiveAssignments(String tenantId);

    /**
     * Update assignment status for the specified tenant.
     *
     * @param tenantId the tenant ID
     * @param assignmentId the assignment ID
     * @param status the new status
     * @return the updated assignment
     */
    CourierAssignment updateAssignmentStatus(String tenantId, String assignmentId, AssignmentStatus status);

    /**
     * Mark assignment as picked up.
     *
     * @param tenantId the tenant ID
     * @param assignmentId the assignment ID
     * @return the updated assignment
     */
    CourierAssignment markAsPickedUp(String tenantId, String assignmentId);

    /**
     * Mark assignment as delivered.
     *
     * @param tenantId the tenant ID
     * @param assignmentId the assignment ID
     * @return the updated assignment
     */
    CourierAssignment markAsDelivered(String tenantId, String assignmentId);

    /**
     * Cancel assignment.
     *
     * @param tenantId the tenant ID
     * @param assignmentId the assignment ID
     * @param reason the cancellation reason
     * @return the updated assignment
     */
    CourierAssignment cancelAssignment(String tenantId, String assignmentId, String reason);

    /**
     * Delete assignment for the specified tenant.
     *
     * @param tenantId the tenant ID
     * @param assignmentId the assignment ID
     */
    void deleteAssignment(String tenantId, String assignmentId);

    /**
     * Get assignment statistics for the specified tenant.
     *
     * @param tenantId the tenant ID
     * @return statistics
     */
    AssignmentStatistics getStatistics(String tenantId);

    /**
     * DTO for assignment statistics.
     */
    record AssignmentStatistics(
        long totalAssignments,
        long activeAssignments,
        long completedAssignments,
        long cancelledAssignments,
        long pendingPickup
    ) {}
}
