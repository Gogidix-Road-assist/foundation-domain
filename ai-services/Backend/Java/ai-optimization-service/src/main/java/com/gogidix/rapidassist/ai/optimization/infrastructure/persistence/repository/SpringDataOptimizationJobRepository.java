package com.gogidix.rapidassist.ai.optimization.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.optimization.domain.model.OptimizationStatus;
import com.gogidix.rapidassist.ai.optimization.domain.model.OptimizationType;
import com.gogidix.rapidassist.ai.optimization.infrastructure.persistence.entity.OptimizationJobEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for OptimizationJobEntity.
 */
@Repository
public interface SpringDataOptimizationJobRepository extends MongoRepository<OptimizationJobEntity, String> {

    /**
     * Find job by UUID and tenant.
     */
    Optional<OptimizationJobEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find all jobs by tenant.
     */
    List<OptimizationJobEntity> findByTenantId(String tenantId);

    /**
     * Find jobs by type and tenant.
     */
    List<OptimizationJobEntity> findByTypeAndTenantId(OptimizationType type, String tenantId);

    /**
     * Find jobs by status and tenant.
     */
    List<OptimizationJobEntity> findByStatusAndTenantId(OptimizationStatus status, String tenantId);

    /**
     * Find jobs by model ID and tenant.
     */
    List<OptimizationJobEntity> findByModelIdAndTenantId(String modelId, String tenantId);

    /**
     * Find jobs by dataset ID and tenant.
     */
    List<OptimizationJobEntity> findByDatasetIdAndTenantId(String datasetId, String tenantId);

    /**
     * Find jobs by status and type.
     */
    List<OptimizationJobEntity> findByStatusAndTypeAndTenantId(OptimizationStatus status, OptimizationType type, String tenantId);

    /**
     * Find jobs by creator and tenant.
     */
    List<OptimizationJobEntity> findByCreatedByAndTenantId(String createdBy, String tenantId);

    /**
     * Find jobs by name pattern (case-insensitive).
     */
    List<OptimizationJobEntity> findByTenantIdAndNameContainingIgnoreCase(String tenantId, String namePattern);

    /**
     * Find jobs created within date range.
     */
    List<OptimizationJobEntity> findByTenantIdAndCreatedAtBetween(String tenantId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Check if job exists.
     */
    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Count jobs by tenant.
     */
    long countByTenantId(String tenantId);

    /**
     * Count jobs by status and tenant.
     */
    long countByStatusAndTenantId(OptimizationStatus status, String tenantId);

    /**
     * Delete job by UUID and tenant.
     */
    void deleteByUuidAndTenantId(UUID uuid, String tenantId);
}
