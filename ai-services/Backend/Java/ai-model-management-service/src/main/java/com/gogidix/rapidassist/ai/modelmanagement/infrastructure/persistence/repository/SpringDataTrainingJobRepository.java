package com.gogidix.rapidassist.ai.modelmanagement.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.modelmanagement.domain.model.TrainingJobStatus;
import com.gogidix.rapidassist.ai.modelmanagement.infrastructure.persistence.entity.TrainingJobEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for TrainingJobEntity.
 */
@Repository
public interface SpringDataTrainingJobRepository extends MongoRepository<TrainingJobEntity, String> {

    /**
     * Find training job by UUID and tenant.
     */
    Optional<TrainingJobEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find all jobs for a model.
     */
    List<TrainingJobEntity> findByModelIdAndTenantIdOrderByCreatedAtDesc(UUID modelId, String tenantId);

    /**
     * Find jobs by status.
     */
    List<TrainingJobEntity> findByTenantIdAndStatus(String tenantId, TrainingJobStatus status);

    /**
     * Find jobs by name.
     */
    Optional<TrainingJobEntity> findByTenantIdAndName(String tenantId, String name);

    /**
     * Find running jobs.
     */
    List<TrainingJobEntity> findByTenantIdAndStatusOrderByCreatedAtDesc(String tenantId, TrainingJobStatus status);

    /**
     * Find completed jobs for a model.
     */
    List<TrainingJobEntity> findByModelIdAndTenantIdAndStatusOrderByCreatedAtDesc(
            UUID modelId, String tenantId, TrainingJobStatus status);

    /**
     * Check if job exists.
     */
    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Delete job by UUID and tenant.
     */
    void deleteByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Delete all jobs for a model.
     */
    void deleteByModelIdAndTenantId(UUID modelId, String tenantId);
}
