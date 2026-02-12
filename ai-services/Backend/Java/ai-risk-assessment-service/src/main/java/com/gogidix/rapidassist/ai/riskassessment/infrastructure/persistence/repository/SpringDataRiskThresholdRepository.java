package com.gogidix.rapidassist.ai.riskassessment.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.riskassessment.domain.model.RiskCategory;
import com.gogidix.rapidassist.ai.riskassessment.infrastructure.persistence.entity.RiskThresholdEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for RiskThresholdEntity.
 */
@Repository
public interface SpringDataRiskThresholdRepository extends MongoRepository<RiskThresholdEntity, String> {

    /**
     * Find threshold by UUID and tenant.
     */
    Optional<RiskThresholdEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find threshold by name and tenant.
     */
    Optional<RiskThresholdEntity> findByNameAndTenantId(String name, String tenantId);

    /**
     * Find all thresholds by tenant.
     */
    List<RiskThresholdEntity> findByTenantId(String tenantId);

    /**
     * Find active thresholds by tenant.
     */
    List<RiskThresholdEntity> findByTenantIdAndActive(String tenantId, boolean active);

    /**
     * Find threshold by category and tenant.
     */
    Optional<RiskThresholdEntity> findByCategoryAndTenantId(RiskCategory category, String tenantId);

    /**
     * Find default threshold (marked as default) for tenant.
     */
    Optional<RiskThresholdEntity> findFirstByTenantIdOrderByCreatedAtAsc(String tenantId);

    /**
     * Check if threshold exists.
     */
    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Check if threshold name exists.
     */
    boolean existsByNameAndTenantId(String name, String tenantId);

    /**
     * Delete threshold by UUID and tenant.
     */
    void deleteByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Count thresholds by tenant.
     */
    long countByTenantId(String tenantId);
}
