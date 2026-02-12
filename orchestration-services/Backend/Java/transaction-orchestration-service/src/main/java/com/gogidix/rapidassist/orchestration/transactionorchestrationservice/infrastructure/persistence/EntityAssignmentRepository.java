package com.gogidix.rapidassist.orchestration.transactionorchestrationservice.infrastructure.persistence;

import com.gogidix.rapidassist.orchestration.transactionorchestrationservice.domain.model.EntityAssignment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EntityAssignmentRepository extends MongoRepository<EntityAssignment, String> {

    List<EntityAssignment> findByEntityId(String entityId);

    List<EntityAssignment> findByProviderId(String providerId);

    Optional<EntityAssignment> findByEntityIdAndProviderId(String entityId, String providerId);

    List<EntityAssignment> findByProviderIdAndStatusIn(String providerId, List<EntityAssignment.AssignmentStatus> statuses);

    List<EntityAssignment> findByAssignedProviderIdAndStatusIn(String providerId, List<EntityAssignment.AssignmentStatus> statuses);

    List<EntityAssignment> findByTenantIdAndCurrentStatus(String tenantId, EntityAssignment.AssignmentStatus status);
}
