package com.gogidix.rapidassist.courier.adapter.service.application;

import com.gogidix.rapidassist.courier.adapter.service.domain.model.CourierAssignment;
import com.gogidix.rapidassist.courier.adapter.service.domain.model.CourierAssignment.AssignmentStatus;
import com.gogidix.rapidassist.courier.adapter.service.domain.port.out.CourierAssignmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tenant Isolation Tests for CourierAssignmentRepository.
 * Verifies that tenant data is properly isolated.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CourierAssignmentTenantIsolationTest {

    @Autowired
    private CourierAssignmentRepository repository;

    private CourierAssignment assignment1;
    private CourierAssignment assignment2;
    private CourierAssignment assignment3;

    @BeforeEach
    void setUp() {
        // Clear any existing data
        repository.findByTenantId("tenant-1").forEach(a ->
            repository.deleteByIdAndTenantId(a.getId(), "tenant-1"));
        repository.findByTenantId("tenant-2").forEach(a ->
            repository.deleteByIdAndTenantId(a.getId(), "tenant-2"));

        // Create test assignments for different tenants
        assignment1 = new CourierAssignment(
            "tenant-1", "order-1", "courier-1",
            "123 Main St", "456 Oak Ave"
        );

        assignment2 = new CourierAssignment(
            "tenant-2", "order-2", "courier-2",
            "789 Pine Rd", "321 Elm St"
        );

        assignment3 = new CourierAssignment(
            "tenant-1", "order-3", "courier-3",
            "555 Walnut Blvd", "777 Maple Dr"
        );

        assignment1 = repository.save(assignment1);
        assignment2 = repository.save(assignment2);
        assignment3 = repository.save(assignment3);
    }

    @Test
    void testTenantIsolation_FindByTenantId() {
        // When finding assignments by tenant-1
        List<CourierAssignment> tenant1Assignments = repository.findByTenantId("tenant-1");

        // Then should only return tenant-1 assignments
        assertThat(tenant1Assignments).hasSize(2);
        assertThat(tenant1Assignments)
            .allMatch(a -> "tenant-1".equals(a.getTenantId()));

        // When finding assignments by tenant-2
        List<CourierAssignment> tenant2Assignments = repository.findByTenantId("tenant-2");

        // Then should only return tenant-2 assignments
        assertThat(tenant2Assignments).hasSize(1);
        assertThat(tenant2Assignments.get(0).getTenantId()).isEqualTo("tenant-2");
    }

    @Test
    void testTenantIsolation_FindByIdAndTenantId() {
        // When finding assignment1 by tenant-1
        Optional<CourierAssignment> found1 = repository.findByIdAndTenantId(
            assignment1.getId(), "tenant-1"
        );

        // Then should find it
        assertThat(found1).isPresent();
        assertThat(found1.get().getId()).isEqualTo(assignment1.getId());
        assertThat(found1.get().getTenantId()).isEqualTo("tenant-1");

        // When finding assignment1 by tenant-2 (wrong tenant)
        Optional<CourierAssignment> notFound = repository.findByIdAndTenantId(
            assignment1.getId(), "tenant-2"
        );

        // Then should NOT find it
        assertThat(notFound).isEmpty();
    }

    @Test
    void testTenantIsolation_FindByTenantIdAndOrderId() {
        // When finding by tenant-1 and order-1
        Optional<CourierAssignment> found1 = repository.findByTenantIdAndOrderId(
            "tenant-1", "order-1"
        );

        // Then should find it
        assertThat(found1).isPresent();
        assertThat(found1.get().getOrderId()).isEqualTo("order-1");

        // When finding by tenant-2 and order-1 (wrong tenant)
        Optional<CourierAssignment> notFound = repository.findByTenantIdAndOrderId(
            "tenant-2", "order-1"
        );

        // Then should NOT find it
        assertThat(notFound).isEmpty();
    }

    @Test
    void testTenantIsolation_FindByTenantIdAndCourierId() {
        // Add another assignment for tenant-1 with same courier
        CourierAssignment assignment1b = new CourierAssignment(
            "tenant-1", "order-1b", "courier-1",
            "111 A St", "222 B St"
        );
        repository.save(assignment1b);

        // When finding by tenant-1 and courier-1
        List<CourierAssignment> tenant1Results = repository.findByTenantIdAndCourierId(
            "tenant-1", "courier-1"
        );

        // Then should find 2 assignments
        assertThat(tenant1Results).hasSize(2);
        assertThat(tenant1Results)
            .allMatch(a -> "tenant-1".equals(a.getTenantId()));
        assertThat(tenant1Results)
            .allMatch(a -> "courier-1".equals(a.getCourierId()));

        // When finding by tenant-2 and courier-1 (different tenant)
        List<CourierAssignment> tenant2Results = repository.findByTenantIdAndCourierId(
            "tenant-2", "courier-1"
        );

        // Then should find 0 assignments
        assertThat(tenant2Results).isEmpty();
    }

    @Test
    void testTenantIsolation_FindByTenantIdAndStatus() {
        // Update statuses
        assignment1.setStatus(AssignmentStatus.DELIVERED);
        assignment2.setStatus(AssignmentStatus.DELIVERED);
        assignment3.setStatus(AssignmentStatus.ASSIGNED);
        repository.save(assignment1);
        repository.save(assignment2);
        repository.save(assignment3);

        // When finding delivered assignments for tenant-1
        List<CourierAssignment> tenant1Delivered = repository.findByTenantIdAndStatus(
            "tenant-1", AssignmentStatus.DELIVERED
        );

        // Then should find 1 (assignment1)
        assertThat(tenant1Delivered).hasSize(1);
        assertThat(tenant1Delivered.get(0).getId()).isEqualTo(assignment1.getId());

        // When finding delivered assignments for tenant-2
        List<CourierAssignment> tenant2Delivered = repository.findByTenantIdAndStatus(
            "tenant-2", AssignmentStatus.DELIVERED
        );

        // Then should find 1 (assignment2)
        assertThat(tenant2Delivered).hasSize(1);
        assertThat(tenant2Delivered.get(0).getId()).isEqualTo(assignment2.getId());
    }

    @Test
    void testTenantIsolation_FindActiveByTenantId() {
        // Set statuses
        assignment1.setStatus(AssignmentStatus.IN_TRANSIT);
        assignment2.setStatus(AssignmentStatus.IN_TRANSIT);
        assignment3.setStatus(AssignmentStatus.DELIVERED);
        repository.save(assignment1);
        repository.save(assignment2);
        repository.save(assignment3);

        // When finding active assignments for tenant-1
        List<CourierAssignment> tenant1Active = repository.findActiveByTenantId("tenant-1");

        // Then should find 1 (assignment1 - IN_TRANSIT is active)
        assertThat(tenant1Active).hasSize(1);
        assertThat(tenant1Active.get(0).getId()).isEqualTo(assignment1.getId());

        // When finding active assignments for tenant-2
        List<CourierAssignment> tenant2Active = repository.findActiveByTenantId("tenant-2");

        // Then should find 1 (assignment2 - IN_TRANSIT is active)
        assertThat(tenant2Active).hasSize(1);
        assertThat(tenant2Active.get(0).getId()).isEqualTo(assignment2.getId());
    }

    @Test
    void testTenantIsolation_DeleteByIdAndTenantId() {
        // When deleting assignment1 with tenant-1
        repository.deleteByIdAndTenantId(assignment1.getId(), "tenant-1");

        // Then assignment1 should be gone for tenant-1
        Optional<CourierAssignment> deleted = repository.findByIdAndTenantId(
            assignment1.getId(), "tenant-1"
        );
        assertThat(deleted).isEmpty();

        // Other assignments should still exist
        assertThat(repository.findByTenantId("tenant-1")).hasSize(1); // assignment3
        assertThat(repository.findByTenantId("tenant-2")).hasSize(1); // assignment2
    }

    @Test
    void testTenantIsolation_CountByTenantIdAndStatus() {
        // Set statuses
        assignment1.setStatus(AssignmentStatus.DELIVERED);
        assignment2.setStatus(AssignmentStatus.DELIVERED);
        assignment3.setStatus(AssignmentStatus.CANCELLED);
        repository.save(assignment1);
        repository.save(assignment2);
        repository.save(assignment3);

        // When counting delivered for tenant-1
        long tenant1DeliveredCount = repository.countByTenantIdAndStatus(
            "tenant-1", AssignmentStatus.DELIVERED
        );

        // Then should be 1
        assertThat(tenant1DeliveredCount).isEqualTo(1);

        // When counting delivered for tenant-2
        long tenant2DeliveredCount = repository.countByTenantIdAndStatus(
            "tenant-2", AssignmentStatus.DELIVERED
        );

        // Then should be 1
        assertThat(tenant2DeliveredCount).isEqualTo(1);
    }

    @Test
    void testTenantIsolation_ExistsByTenantIdAndOrderId() {
        // When checking if order-1 exists for tenant-1
        boolean existsForTenant1 = repository.existsByTenantIdAndOrderId("tenant-1", "order-1");

        // Then should be true
        assertThat(existsForTenant1).isTrue();

        // When checking if order-1 exists for tenant-2 (wrong tenant)
        boolean existsForTenant2 = repository.existsByTenantIdAndOrderId("tenant-2", "order-1");

        // Then should be false
        assertThat(existsForTenant2).isFalse();

        // When checking if order-2 exists for tenant-2
        boolean existsForTenant2Order2 = repository.existsByTenantIdAndOrderId("tenant-2", "order-2");

        // Then should be true
        assertThat(existsForTenant2Order2).isTrue();
    }

    @Test
    void testTenantIsolation_CrossTenantDataLeakPrevention() {
        // This test verifies that tenants cannot access each other's data
        // even when using the same IDs

        // Create assignment with same orderId for different tenants
        CourierAssignment tenant1Order = new CourierAssignment(
            "tenant-1", "shared-order-id", "courier-1",
            "Address 1", "Address 2"
        );
        CourierAssignment tenant2Order = new CourierAssignment(
            "tenant-2", "shared-order-id", "courier-2",
            "Address 3", "Address 4"
        );

        repository.save(tenant1Order);
        repository.save(tenant2Order);

        // Verify they have different IDs
        assertThat(tenant1Order.getId()).isNotEqualTo(tenant2Order.getId());

        // Verify each tenant only sees their own
        Optional<CourierAssignment> tenant1View = repository.findByTenantIdAndOrderId(
            "tenant-1", "shared-order-id"
        );
        Optional<CourierAssignment> tenant2View = repository.findByTenantIdAndOrderId(
            "tenant-2", "shared-order-id"
        );

        assertThat(tenant1View).isPresent();
        assertThat(tenant2View).isPresent();
        assertThat(tenant1View.get().getId()).isNotEqualTo(tenant2View.get().getId());
        assertThat(tenant1View.get().getCourierId()).isEqualTo("courier-1");
        assertThat(tenant2View.get().getCourierId()).isEqualTo("courier-2");
    }
}
