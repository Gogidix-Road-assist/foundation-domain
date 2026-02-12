package com.gogidix.rapidassist.orchestration.monitoringservice.infrastructure.persistence;

import com.gogidix.rapidassist.orchestration.monitoringservice.domain.model.Entity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EntityRepository extends MongoRepository<Entity, String> {

    List<Entity> findByTenantId(String tenantId);

    Optional<Entity> findByEntityIdAndTenantId(String entityId, String tenantId);

    List<Entity> findByStatusAndTenantId(Entity.EntityStatus status, String tenantId);

    List<Entity> findByAssignedProviderIdAndStatusIn(String providerId, List<Entity.EntityStatus> statuses);

    List<Entity> findByTenantIdAndCurrentStatus(String tenantId, Entity.EntityStatus status);
}
