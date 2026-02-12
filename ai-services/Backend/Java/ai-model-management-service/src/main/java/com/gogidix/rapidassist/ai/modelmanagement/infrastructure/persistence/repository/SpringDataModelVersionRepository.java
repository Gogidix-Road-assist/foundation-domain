package com.gogidix.rapidassist.ai.modelmanagement.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.modelmanagement.infrastructure.persistence.entity.ModelVersionEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for ModelVersionEntity.
 */
@Repository
public interface SpringDataModelVersionRepository extends MongoRepository<ModelVersionEntity, String> {

    /**
     * Find model version by UUID and tenant.
     */
    Optional<ModelVersionEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find all versions for a model.
     */
    List<ModelVersionEntity> findByModelIdAndTenantIdOrderByCreatedAtDesc(UUID modelId, String tenantId);

    /**
     * Find model version by version number.
     */
    Optional<ModelVersionEntity> findByModelIdAndVersionNumberAndTenantId(UUID modelId, String versionNumber, String tenantId);

    /**
     * Find production-ready versions.
     */
    List<ModelVersionEntity> findByModelIdAndTenantIdAndIsProductionReadyTrue(UUID modelId, String tenantId);

    /**
     * Find latest version.
     */
    Optional<ModelVersionEntity> findFirstByModelIdAndTenantIdOrderByCreatedAtDesc(UUID modelId, String tenantId);

    /**
     * Check if version exists.
     */
    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Delete version by UUID and tenant.
     */
    void deleteByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Delete all versions for a model.
     */
    void deleteByModelIdAndTenantId(UUID modelId, String tenantId);
}
