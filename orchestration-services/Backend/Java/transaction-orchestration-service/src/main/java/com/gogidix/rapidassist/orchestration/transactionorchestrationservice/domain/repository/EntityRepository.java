package com.gogidix.rapidassist.orchestration.transactionorchestrationservice.domain.repository;

import com.gogidix.rapidassist.orchestration.transactionorchestrationservice.domain.model.Entity;
import com.gogidix.rapidassist.orchestration.transactionorchestrationservice.domain.port.out.EntityRepositoryPort;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository interface for Entity
 * Follows Hexagonal Architecture - this is a domain-specific interface
 */
public interface EntityRepository extends EntityRepositoryPort {

    /**
     * Save entity (create or update)
     */
    Entity save(Entity entity);

    /**
     * Find entity by ID
     */
    Optional<Entity> findById(String id);

    /**
     * Find all entityes for a tenant
     */
    List<Entity> findByTenantId(String tenantId);

    /**
     * Find entity by entityId
     */
    Optional<Entity> findByEntityId(String entityId);

    /**
     * Find entityes by request ID
     */
    List<Entity> findByRequestId(String requestId);

    /**
     * Find entityes by tenant and status
     */
    List<Entity> findByTenantIdAndStatusIn(String tenantId, List<Entity.EntityStatus> statuses);

    /**
     * Delete entity by ID
     */
    void deleteById(String id);

    /**
     * Check if entity exists
     */
    boolean existsByEntityId(String entityId);

    /**
     * Find entities by assigned provider ID and status list
     */
    List<Entity> findByAssignedProviderIdAndStatusIn(String providerId, List<Entity.EntityStatus> statuses);

    /**
     * Find entity by entityId and tenantId
     */
    Optional<Entity> findByEntityIdAndTenantId(String entityId, String tenantId);

    /**
     * Find entities by status and tenantId
     */
    List<Entity> findByStatusAndTenantId(Entity.EntityStatus status, String tenantId);

    /**
     * Find entities by tenantId and currentStatus
     */
    List<Entity> findByTenantIdAndCurrentStatus(String tenantId, Entity.EntityStatus status);
}
