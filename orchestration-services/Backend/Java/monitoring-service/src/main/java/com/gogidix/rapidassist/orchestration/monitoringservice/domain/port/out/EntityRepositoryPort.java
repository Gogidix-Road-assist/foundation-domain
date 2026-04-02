package com.gogidix.rapidassist.orchestration.monitoringservice.domain.port.out;

import com.gogidix.rapidassist.orchestration.monitoringservice.domain.model.Entity;

import java.util.List;
import java.util.Optional;

/**
 * Output port for Entity persistence operations
 */
public interface EntityRepositoryPort {

    Entity save(Entity entity);

    Optional<Entity> findById(String id);

    List<Entity> findAll();

    void deleteById(String id);

    Optional<Entity> findByEntityId(String entityId);

    List<Entity> findByRequestId(String requestId);

    List<Entity> findByTenantId(String tenantId);

    List<Entity> findByTenantIdAndStatusIn(String tenantId, List<Entity.EntityStatus> statuses);

    List<Entity> findByAssignedProviderIdAndStatusIn(String providerId, List<Entity.EntityStatus> statuses);

    boolean existsByEntityId(String entityId);
}
