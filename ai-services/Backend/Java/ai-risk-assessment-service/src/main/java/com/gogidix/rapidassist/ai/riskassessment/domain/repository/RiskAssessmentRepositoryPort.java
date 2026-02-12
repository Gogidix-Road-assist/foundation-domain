package com.gogidix.rapidassist.ai.riskassessment.domain.repository;

import com.gogidix.rapidassist.ai.riskassessment.domain.aggregate.RiskAssessment;
import com.gogidix.rapidassist.ai.riskassessment.domain.model.AssessmentStatus;
import com.gogidix.rapidassist.ai.riskassessment.domain.model.RiskCategory;
import com.gogidix.rapidassist.ai.riskassessment.domain.model.RiskLevel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Port for Risk Assessment Aggregate
 * Defines the contract for risk assessment persistence operations
 */
public interface RiskAssessmentRepositoryPort {

    /**
     * Save risk assessment
     */
    RiskAssessment save(RiskAssessment assessment);

    /**
     * Find risk assessment by ID and tenant
     */
    Optional<RiskAssessment> findByIdAndTenantId(UUID id, String tenantId);

    /**
     * Find risk assessment by code and tenant
     */
    Optional<RiskAssessment> findByCodeAndTenantId(String code, String tenantId);

    /**
     * Find all risk assessments by tenant
     */
    List<RiskAssessment> findByTenantId(String tenantId);

    /**
     * Find risk assessments by subject and tenant
     */
    List<RiskAssessment> findBySubjectIdAndTenantId(String subjectId, String tenantId);

    /**
     * Find risk assessments by status and tenant
     */
    List<RiskAssessment> findByStatusAndTenantId(AssessmentStatus status, String tenantId);

    /**
     * Find risk assessments by category and tenant
     */
    List<RiskAssessment> findByCategoryAndTenantId(RiskCategory category, String tenantId);

    /**
     * Find risk assessments by risk level and tenant
     */
    List<RiskAssessment> findByRiskLevelAndTenantId(RiskLevel riskLevel, String tenantId);

    /**
     * Find risk assessments by assessed by and tenant
     */
    List<RiskAssessment> findByAssessedByAndTenantId(String assessedBy, String tenantId);

    /**
     * Find active risk assessments for tenant
     */
    List<RiskAssessment> findActiveAssessments(String tenantId);

    /**
     * Find risk assessments created within date range
     */
    List<RiskAssessment> findByCreatedAtBetweenAndTenantId(LocalDateTime startDate, LocalDateTime endDate, String tenantId);

    /**
     * Find critical risk assessments for tenant
     */
    List<RiskAssessment> findCriticalRiskAssessments(String tenantId);

    /**
     * Check if risk assessment exists
     */
    boolean existsByIdAndTenantId(UUID id, String tenantId);

    /**
     * Check if assessment code exists
     */
    boolean existsByCodeAndTenantId(String code, String tenantId);

    /**
     * Delete risk assessment by ID and tenant
     */
    void deleteByIdAndTenantId(UUID id, String tenantId);

    /**
     * Count risk assessments by tenant
     */
    long countByTenantId(String tenantId);

    /**
     * Count risk assessments by status and tenant
     */
    long countByStatusAndTenantId(AssessmentStatus status, String tenantId);
}
