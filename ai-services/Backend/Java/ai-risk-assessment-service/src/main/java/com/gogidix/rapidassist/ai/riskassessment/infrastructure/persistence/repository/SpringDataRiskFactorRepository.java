package com.gogidix.rapidassist.ai.riskassessment.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.riskassessment.infrastructure.persistence.entity.RiskFactorEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for RiskFactorEntity.
 */
@Repository
public interface SpringDataRiskFactorRepository extends MongoRepository<RiskFactorEntity, String> {

    /**
     * Find factor by UUID and tenant.
     */
    Optional<RiskFactorEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find all factors by risk assessment and tenant.
     */
    List<RiskFactorEntity> findByRiskAssessmentIdAndTenantId(UUID riskAssessmentId, String tenantId);

    /**
     * Find all factors by tenant.
     */
    List<RiskFactorEntity> findByTenantId(String tenantId);

    /**
     * Check if factor exists.
     */
    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Delete factors by risk assessment and tenant.
     */
    void deleteByRiskAssessmentIdAndTenantId(UUID riskAssessmentId, String tenantId);

    /**
     * Count factors by risk assessment and tenant.
     */
    long countByRiskAssessmentIdAndTenantId(UUID riskAssessmentId, String tenantId);
}
