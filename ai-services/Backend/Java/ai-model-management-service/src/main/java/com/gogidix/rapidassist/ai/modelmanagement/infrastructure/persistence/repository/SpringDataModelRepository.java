package com.gogidix.rapidassist.ai.modelmanagement.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.modelmanagement.domain.model.ModelStatus;
import com.gogidix.rapidassist.ai.modelmanagement.domain.model.ModelType;
import com.gogidix.rapidassist.ai.modelmanagement.infrastructure.persistence.entity.ModelEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for ModelEntity.
 */
@Repository
public interface SpringDataModelRepository extends MongoRepository<ModelEntity, String> {

    /**
     * Find model by UUID and tenant.
     */
    Optional<ModelEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find all models for a tenant.
     */
    List<ModelEntity> findByTenantId(String tenantId);

    /**
     * Find models by status.
     */
    List<ModelEntity> findByTenantIdAndStatus(String tenantId, ModelStatus status);

    /**
     * Find models by type.
     */
    List<ModelEntity> findByTenantIdAndModelType(String tenantId, ModelType modelType);

    /**
     * Find model by name.
     */
    Optional<ModelEntity> findByTenantIdAndName(String tenantId, String name);

    /**
     * Check if model exists.
     */
    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Delete model by UUID and tenant.
     */
    void deleteByUuidAndTenantId(UUID uuid, String tenantId);
}
