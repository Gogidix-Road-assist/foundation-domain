package com.gogidix.rapidassist.courier.adapter.service.adapters.persistence;

import com.gogidix.rapidassist.courier.adapter.service.domain.model.CourierAssignment;
import com.gogidix.rapidassist.courier.adapter.service.domain.model.CourierAssignment.AssignmentStatus;
import com.gogidix.rapidassist.courier.adapter.service.domain.port.out.CourierAssignmentRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA-based implementation of CourierAssignmentRepository port.
 * Ensures all operations are tenant-scoped for proper multi-tenancy isolation.
 */
@Repository
public class CourierAssignmentRepositoryImpl implements CourierAssignmentRepository {

    private final CourierAssignmentJpaRepository jpaRepository;

    public CourierAssignmentRepositoryImpl(CourierAssignmentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public CourierAssignment save(CourierAssignment assignment) {
        return jpaRepository.save(assignment);
    }

    @Override
    public Optional<CourierAssignment> findByIdAndTenantId(String id, String tenantId) {
        return jpaRepository.findByIdAndTenantId(id, tenantId);
    }

    @Override
    public List<CourierAssignment> findByTenantId(String tenantId) {
        return jpaRepository.findByTenantId(tenantId);
    }

    @Override
    public Optional<CourierAssignment> findByTenantIdAndOrderId(String tenantId, String orderId) {
        return jpaRepository.findByTenantIdAndOrderId(tenantId, orderId);
    }

    @Override
    public List<CourierAssignment> findByTenantIdAndCourierId(String tenantId, String courierId) {
        return jpaRepository.findByTenantIdAndCourierId(tenantId, courierId);
    }

    @Override
    public List<CourierAssignment> findByTenantIdAndStatus(String tenantId, AssignmentStatus status) {
        return jpaRepository.findByTenantIdAndStatus(tenantId, status);
    }

    @Override
    public List<CourierAssignment> findActiveByTenantId(String tenantId) {
        List<AssignmentStatus> activeStatuses = List.of(
            AssignmentStatus.ASSIGNED,
            AssignmentStatus.EN_ROUTE_TO_PICKUP,
            AssignmentStatus.AT_PICKUP_LOCATION,
            AssignmentStatus.PACKAGE_PICKED_UP,
            AssignmentStatus.IN_TRANSIT,
            AssignmentStatus.AT_DELIVERY_LOCATION
        );
        return jpaRepository.findActiveByTenantId(tenantId, activeStatuses);
    }

    @Override
    public void deleteByIdAndTenantId(String id, String tenantId) {
        jpaRepository.deleteByIdAndTenantId(id, tenantId);
    }

    @Override
    public long countByTenantIdAndStatus(String tenantId, AssignmentStatus status) {
        return jpaRepository.countByTenantIdAndStatus(tenantId, status);
    }

    @Override
    public boolean existsByTenantIdAndOrderId(String tenantId, String orderId) {
        return jpaRepository.existsByTenantIdAndOrderId(tenantId, orderId);
    }
}
