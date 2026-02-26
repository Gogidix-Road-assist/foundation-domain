package com.gogidix.rapidassist.orchestration.transactionorchestrationservice.domain.port.out;

import com.gogidix.rapidassist.orchestration.transactionorchestrationservice.domain.model.Entity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EntityRepositoryPort extends MongoRepository<Entity, String> {

    Optional<Entity> findByEntityId(String entityId);

    List<Entity> findByRequestId(String requestId);

    List<Entity> findByTenantId(String tenantId);

    List<Entity> findByTenantIdAndStatusIn(String tenantId, List<Entity.EntityStatus> statuses);

    boolean existsByEntityId(String entityId);
}
