package com.gogidix.rapidassist.orchestration.dispatching.infrastructure.persistence;

import com.gogidix.rapidassist.orchestration.dispatching.domain.model.Dispatch;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DispatchRepository extends MongoRepository<Dispatch, String> {

    Optional<Dispatch> findByDispatchId(String dispatchId);

    List<Dispatch> findByTenantId(String tenantId);

    List<Dispatch> findByRequestId(String requestId);

    List<Dispatch> findByTenantIdAndStatusIn(String tenantId, List<Dispatch.DispatchStatus> statuses);

    List<Dispatch> findByTenantIdAndStatus(String tenantId, Dispatch.DispatchStatus status);

    List<Dispatch> findByAssignedProviderIdAndStatusIn(
        String providerId,
        List<Dispatch.DispatchStatus> statuses
    );

    List<Dispatch> findByAssignedVehicleId(String vehicleId);

    @Query("{ 'tenantId': ?0, 'createdAt': { $gte: ?1, $lte: ?2 } }")
    List<Dispatch> findByTenantIdAndCreatedAtBetween(
        String tenantId,
        LocalDateTime start,
        LocalDateTime end
    );

    List<Dispatch> findByPriorityAndStatusIn(
        Dispatch.DispatchPriority priority,
        List<Dispatch.DispatchStatus> statuses
    );

    List<Dispatch> findByStatusAndAssignmentMethod(
        Dispatch.DispatchStatus status,
        Dispatch.AssignmentMethod assignmentMethod
    );

    long countByTenantIdAndStatus(String tenantId, Dispatch.DispatchStatus status);

    @Query("{ 'tenantId': ?0, 'deletedAt': null }")
    List<Dispatch> findAllActiveByTenantId(String tenantId);

    @Query("{ 'dispatchId': { $in: ?0 } }")
    List<Dispatch> findByDispatchIdIn(List<String> dispatchIds);
}
