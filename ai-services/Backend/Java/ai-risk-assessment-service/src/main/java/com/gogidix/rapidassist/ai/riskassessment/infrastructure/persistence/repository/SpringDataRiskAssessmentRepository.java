package com.gogidix.rapidassist.ai.riskassessment.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.riskassessment.domain.model.AssessmentStatus;
import com.gogidix.rapidassist.ai.riskassessment.domain.model.RiskCategory;
import com.gogidix.rapidassist.ai.riskassessment.domain.model.RiskLevel;
import com.gogidix.rapidassist.ai.riskassessment.infrastructure.persistence.entity.RiskAssessmentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for RiskAssessmentEntity.
 */
@Repository
public interface SpringDataRiskAssessmentRepository extends MongoRepository<RiskAssessmentEntity, String> {

    /**
     * Find assessment by UUID and tenant.
     */
    Optional<RiskAssessmentEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find assessment by code and tenant.
     */
    Optional<RiskAssessmentEntity> findByAssessmentCodeAndTenantId(String assessmentCode, String tenantId);

    /**
     * Find all assessments by tenant.
     */
    List<RiskAssessmentEntity> findByTenantId(String tenantId);

    /**
     * Find assessments by subject and tenant.
     */
    List<RiskAssessmentEntity> findBySubjectIdAndTenantId(String subjectId, String tenantId);

    /**
     * Find assessments by status and tenant.
     */
    List<RiskAssessmentEntity> findByStatusAndTenantId(AssessmentStatus status, String tenantId);

    /**
     * Find assessments by category and tenant.
     */
    List<RiskAssessmentEntity> findByCategoryAndTenantId(RiskCategory category, String tenantId);

    /**
     * Find assessments by risk level and tenant.
     */
    List<RiskAssessmentEntity> findByRiskLevelAndTenantId(RiskLevel riskLevel, String tenantId);

    /**
     * Find assessments by assessed by and tenant.
     */
    List<RiskAssessmentEntity> findByAssessedByAndTenantId(String assessedBy, String tenantId);

    /**
     * Find active assessments (IN_PROGRESS, COMPLETED, REVIEWED) for tenant.
     */
    List<RiskAssessmentEntity> findByTenantIdAndStatusIn(String tenantId, List<AssessmentStatus> statuses);

    /**
     * Find assessments created within date range for tenant.
     */
    List<RiskAssessmentEntity> findByCreatedAtBetweenAndTenantId(LocalDateTime startDate, LocalDateTime endDate, String tenantId);

    /**
     * Find critical risk assessments (CRITICAL level) for tenant.
     */
    List<RiskAssessmentEntity> findByTenantIdAndRiskLevel(String tenantId, RiskLevel riskLevel);

    /**
     * Check if assessment exists.
     */
    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Check if assessment code exists.
     */
    boolean existsByAssessmentCodeAndTenantId(String assessmentCode, String tenantId);

    /**
     * Delete assessment by UUID and tenant.
     */
    void deleteByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Count assessments by tenant.
     */
    long countByTenantId(String tenantId);

    /**
     * Count assessments by status and tenant.
     */
    long countByStatusAndTenantId(AssessmentStatus status, String tenantId);
}
