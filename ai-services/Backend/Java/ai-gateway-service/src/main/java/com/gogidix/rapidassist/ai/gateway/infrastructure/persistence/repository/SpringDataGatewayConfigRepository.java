package com.gogidix.rapidassist.ai.gateway.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.gateway.infrastructure.persistence.entity.GatewayConfigEntity;
import com.gogidix.rapidassist.ai.gateway.domain.model.GatewayStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for GatewayConfigEntity.
 */
@Repository
public interface SpringDataGatewayConfigRepository extends MongoRepository<GatewayConfigEntity, String> {

    /**
     * Find config by UUID and tenant.
     */
    Optional<GatewayConfigEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find all configs by tenant.
     */
    List<GatewayConfigEntity> findByTenantId(String tenantId);

    /**
     * Find configs by tenant and status.
     */
    List<GatewayConfigEntity> findByTenantIdAndStatus(String tenantId, GatewayStatus status);

    /**
     * Find config by tenant and config name.
     */
    Optional<GatewayConfigEntity> findByTenantIdAndConfigName(String tenantId, String configName);

    /**
     * Check if config exists.
     */
    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Delete config by UUID and tenant.
     */
    void deleteByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find active configs for tenant.
     */
    List<GatewayConfigEntity> findByTenantIdAndStatusOrderByPriorityDesc(
            String tenantId, GatewayStatus status);
}
