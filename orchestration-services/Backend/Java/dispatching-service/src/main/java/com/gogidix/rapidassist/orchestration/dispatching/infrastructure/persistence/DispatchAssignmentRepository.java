package com.gogidix.rapidassist.orchestration.dispatching.infrastructure.persistence;

import com.gogidix.rapidassist.orchestration.dispatching.domain.model.DispatchAssignment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DispatchAssignmentRepository extends MongoRepository<DispatchAssignment, String> {

    List<DispatchAssignment> findByDispatchId(String dispatchId);

    Optional<DispatchAssignment> findByDispatchIdAndProviderId(String dispatchId, String providerId);

    List<DispatchAssignment> findByProviderIdAndStatusIn(
        String providerId,
        List<DispatchAssignment.AssignmentStatus> statuses
    );

    List<DispatchAssignment> findByStatusAndAssignedAtBefore(
        DispatchAssignment.AssignmentStatus status,
        LocalDateTime cutoffTime
    );

    List<DispatchAssignment> findByDriverId(String driverId);

    List<DispatchAssignment> findByVehicleId(String vehicleId);

    List<DispatchAssignment> findByTenantId(String tenantId);

    long countByProviderIdAndStatus(String providerId, DispatchAssignment.AssignmentStatus status);

    List<DispatchAssignment> findTop10ByProviderIdOrderByAssignedAtDesc(String providerId);
}
