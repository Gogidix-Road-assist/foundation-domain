package com.gogidix.rapidassist.ai.modelmanagement.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.modelmanagement.domain.model.DeploymentStatus;
import com.gogidix.rapidassist.ai.modelmanagement.infrastructure.persistence.entity.ModelDeploymentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for ModelDeploymentEntity.
 */
@Repository
public interface SpringDataModelDeploymentRepository extends MongoRepository<ModelDeploymentEntity, String> {

    /**
     * Find deployment by UUID and tenant.
     */
    Optional<ModelDeploymentEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find all deployments for a model.
     */
    List<ModelDeploymentEntity> findByModelIdAndTenantId(UUID modelId, String tenantId);

    /**
     * Find deployments by environment.
     */
    List<ModelDeploymentEntity> findByTenantIdAndEnvironment(String tenantId, String environment);

    /**
     * Find deployments by status.
     */
    List<ModelDeploymentEntity> findByTenantIdAndStatus(String tenantId, DeploymentStatus status);

    /**
     * Find active deployments by environment.
     */
    List<ModelDeploymentEntity> findByTenantIdAndEnvironmentAndStatus(String tenantId, String environment, DeploymentStatus status);

    /**
     * Find deployment by model version.
     */
    Optional<ModelDeploymentEntity> findByModelVersionIdAndTenantId(UUID modelVersionId, String tenantId);

    /**
     * Check if deployment exists.
     */
    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Delete deployment by UUID and tenant.
     */
    void deleteByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Delete all deployments for a model.
     */
    void deleteByModelIdAndTenantId(UUID modelId, String tenantId);
}
