package com.gogidix.rapidassist.courier.adapter.service.application;

import com.gogidix.rapidassist.courier.adapter.service.domain.model.CourierAssignment;
import com.gogidix.rapidassist.courier.adapter.service.domain.model.CourierAssignment.AssignmentStatus;
import com.gogidix.rapidassist.courier.adapter.service.domain.port.in.CourierAssignmentService;
import com.gogidix.rapidassist.courier.adapter.service.domain.port.out.CourierAssignmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Application service for CourierAssignment operations.
 * All operations are tenant-scoped to ensure proper multi-tenancy isolation.
 */
@Service
@Transactional
public class CourierAssignmentServiceImpl implements CourierAssignmentService {

    private static final Logger logger = LoggerFactory.getLogger(CourierAssignmentServiceImpl.class);

    private final CourierAssignmentRepository repository;

    public CourierAssignmentServiceImpl(CourierAssignmentRepository repository) {
        this.repository = repository;
    }

    @Override
    public CourierAssignment createAssignment(String tenantId, String orderId, String courierId,
                                              String pickupAddress, String deliveryAddress) {
        logger.info("Creating courier assignment for tenant: {}, order: {}, courier: {}",
                    tenantId, orderId, courierId);

        // Check if assignment already exists for this order
        if (repository.existsByTenantIdAndOrderId(tenantId, orderId)) {
            throw new IllegalArgumentException(
                "Courier assignment already exists for tenant '" + tenantId + "' and order '" + orderId + "'");
        }

        CourierAssignment assignment = new CourierAssignment(
            tenantId, orderId, courierId, pickupAddress, deliveryAddress
        );

        return repository.save(assignment);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CourierAssignment> getAssignmentById(String tenantId, String assignmentId) {
        return repository.findByIdAndTenantId(assignmentId, tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CourierAssignment> getAssignmentByOrderId(String tenantId, String orderId) {
        return repository.findByTenantIdAndOrderId(tenantId, orderId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourierAssignment> getAllAssignments(String tenantId) {
        return repository.findByTenantId(tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourierAssignment> getAssignmentsByCourier(String tenantId, String courierId) {
        return repository.findByTenantIdAndCourierId(tenantId, courierId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourierAssignment> getAssignmentsByStatus(String tenantId, AssignmentStatus status) {
        return repository.findByTenantIdAndStatus(tenantId, status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourierAssignment> getActiveAssignments(String tenantId) {
        return repository.findActiveByTenantId(tenantId);
    }

    @Override
    public CourierAssignment updateAssignmentStatus(String tenantId, String assignmentId, AssignmentStatus status) {
        CourierAssignment assignment = repository.findByIdAndTenantId(assignmentId, tenantId)
            .orElseThrow(() -> new IllegalArgumentException(
                "Assignment not found for tenant '" + tenantId + "' and id '" + assignmentId + "'"));

        assignment.setStatus(status);
        return repository.save(assignment);
    }

    @Override
    public CourierAssignment markAsPickedUp(String tenantId, String assignmentId) {
        CourierAssignment assignment = repository.findByIdAndTenantId(assignmentId, tenantId)
            .orElseThrow(() -> new IllegalArgumentException(
                "Assignment not found for tenant '" + tenantId + "' and id '" + assignmentId + "'"));

        assignment.markAsPickedUp();
        logger.info("Marked assignment {} as picked up for tenant: {}", assignmentId, tenantId);
        return repository.save(assignment);
    }

    @Override
    public CourierAssignment markAsDelivered(String tenantId, String assignmentId) {
        CourierAssignment assignment = repository.findByIdAndTenantId(assignmentId, tenantId)
            .orElseThrow(() -> new IllegalArgumentException(
                "Assignment not found for tenant '" + tenantId + "' and id '" + assignmentId + "'"));

        assignment.markAsDelivered();
        logger.info("Marked assignment {} as delivered for tenant: {}", assignmentId, tenantId);
        return repository.save(assignment);
    }

    @Override
    public CourierAssignment cancelAssignment(String tenantId, String assignmentId, String reason) {
        CourierAssignment assignment = repository.findByIdAndTenantId(assignmentId, tenantId)
            .orElseThrow(() -> new IllegalArgumentException(
                "Assignment not found for tenant '" + tenantId + "' and id '" + assignmentId + "'"));

        assignment.markAsCancelled(reason);
        logger.info("Cancelled assignment {} for tenant: {}, reason: {}", assignmentId, tenantId, reason);
        return repository.save(assignment);
    }

    @Override
    public void deleteAssignment(String tenantId, String assignmentId) {
        // Verify assignment exists for this tenant before deleting
        Optional<CourierAssignment> assignment = repository.findByIdAndTenantId(assignmentId, tenantId);
        if (assignment.isEmpty()) {
            throw new IllegalArgumentException(
                "Assignment not found for tenant '" + tenantId + "' and id '" + assignmentId + "'");
        }

        repository.deleteByIdAndTenantId(assignmentId, tenantId);
        logger.info("Deleted assignment {} for tenant: {}", assignmentId, tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public AssignmentStatistics getStatistics(String tenantId) {
        List<CourierAssignment> allAssignments = repository.findByTenantId(tenantId);
        List<CourierAssignment> activeAssignments = repository.findActiveByTenantId(tenantId);

        long completed = repository.countByTenantIdAndStatus(tenantId, AssignmentStatus.DELIVERED);
        long cancelled = repository.countByTenantIdAndStatus(tenantId, AssignmentStatus.CANCELLED);

        long pendingPickup = allAssignments.stream()
            .filter(a -> a.getStatus() == AssignmentStatus.ASSIGNED
                        || a.getStatus() == AssignmentStatus.EN_ROUTE_TO_PICKUP
                        || a.getStatus() == AssignmentStatus.AT_PICKUP_LOCATION)
            .count();

        return new AssignmentStatistics(
            allAssignments.size(),
            activeAssignments.size(),
            completed,
            cancelled,
            pendingPickup
        );
    }
}
