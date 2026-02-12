package com.gogidix.rapidassist.ai.riskassessment.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.riskassessment.domain.aggregate.RiskAssessment;
import com.gogidix.rapidassist.ai.riskassessment.domain.model.AssessmentStatus;
import com.gogidix.rapidassist.ai.riskassessment.domain.model.RiskCategory;
import com.gogidix.rapidassist.ai.riskassessment.domain.model.RiskLevel;
import com.gogidix.rapidassist.ai.riskassessment.domain.repository.RiskAssessmentRepositoryPort;
import com.gogidix.rapidassist.ai.riskassessment.infrastructure.persistence.entity.RiskAssessmentEntity;
import com.gogidix.rapidassist.ai.riskassessment.infrastructure.persistence.mapper.RiskAssessmentPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of RiskAssessmentRepositoryPort
 * Bridges domain layer with infrastructure persistence layer
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RiskAssessmentRepositoryImpl implements RiskAssessmentRepositoryPort {

    private final SpringDataRiskAssessmentRepository springDataRepository;
    private final SpringDataRiskFactorRepository riskFactorRepository;
    private final SpringDataRiskAlertRepository riskAlertRepository;
    private final RiskAssessmentPersistenceMapper mapper;

    @Override
    public RiskAssessment save(RiskAssessment assessment) {
        log.debug("Saving risk assessment: {}", assessment.getId());
        RiskAssessmentEntity entity = mapper.toEntity(assessment);
        RiskAssessmentEntity saved = springDataRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<RiskAssessment> findByIdAndTenantId(UUID id, String tenantId) {
        log.debug("Finding risk assessment by id: {} for tenant: {}", id, tenantId);
        return springDataRepository.findByUuidAndTenantId(id, tenantId)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<RiskAssessment> findByCodeAndTenantId(String code, String tenantId) {
        log.debug("Finding risk assessment by code: {} for tenant: {}", code, tenantId);
        return springDataRepository.findByAssessmentCodeAndTenantId(code, tenantId)
                .map(mapper::toDomain);
    }

    @Override
    public List<RiskAssessment> findByTenantId(String tenantId) {
        log.debug("Finding all risk assessments for tenant: {}", tenantId);
        return springDataRepository.findByTenantId(tenantId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<RiskAssessment> findBySubjectIdAndTenantId(String subjectId, String tenantId) {
        log.debug("Finding risk assessments by subject: {} for tenant: {}", subjectId, tenantId);
        return springDataRepository.findBySubjectIdAndTenantId(subjectId, tenantId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<RiskAssessment> findByStatusAndTenantId(AssessmentStatus status, String tenantId) {
        log.debug("Finding risk assessments by status: {} for tenant: {}", status, tenantId);
        return springDataRepository.findByStatusAndTenantId(status, tenantId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<RiskAssessment> findByCategoryAndTenantId(RiskCategory category, String tenantId) {
        log.debug("Finding risk assessments by category: {} for tenant: {}", category, tenantId);
        return springDataRepository.findByCategoryAndTenantId(category, tenantId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<RiskAssessment> findByRiskLevelAndTenantId(RiskLevel riskLevel, String tenantId) {
        log.debug("Finding risk assessments by risk level: {} for tenant: {}", riskLevel, tenantId);
        return springDataRepository.findByRiskLevelAndTenantId(riskLevel, tenantId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<RiskAssessment> findByAssessedByAndTenantId(String assessedBy, String tenantId) {
        log.debug("Finding risk assessments by assessed by: {} for tenant: {}", assessedBy, tenantId);
        return springDataRepository.findByAssessedByAndTenantId(assessedBy, tenantId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<RiskAssessment> findActiveAssessments(String tenantId) {
        log.debug("Finding active risk assessments for tenant: {}", tenantId);
        List<AssessmentStatus> activeStatuses = List.of(
                AssessmentStatus.IN_PROGRESS,
                AssessmentStatus.COMPLETED,
                AssessmentStatus.REVIEWED
        );
        return springDataRepository.findByTenantIdAndStatusIn(tenantId, activeStatuses).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<RiskAssessment> findByCreatedAtBetweenAndTenantId(LocalDateTime startDate, LocalDateTime endDate, String tenantId) {
        log.debug("Finding risk assessments created between {} and {} for tenant: {}", startDate, endDate, tenantId);
        return springDataRepository.findByCreatedAtBetweenAndTenantId(startDate, endDate, tenantId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<RiskAssessment> findCriticalRiskAssessments(String tenantId) {
        log.debug("Finding critical risk assessments for tenant: {}", tenantId);
        return springDataRepository.findByTenantIdAndRiskLevel(tenantId, RiskLevel.CRITICAL).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByIdAndTenantId(UUID id, String tenantId) {
        return springDataRepository.existsByUuidAndTenantId(id, tenantId);
    }

    @Override
    public boolean existsByCodeAndTenantId(String code, String tenantId) {
        return springDataRepository.existsByAssessmentCodeAndTenantId(code, tenantId);
    }

    @Override
    public void deleteByIdAndTenantId(UUID id, String tenantId) {
        log.debug("Deleting risk assessment: {} for tenant: {}", id, tenantId);
        springDataRepository.deleteByUuidAndTenantId(id, tenantId);
    }

    @Override
    public long countByTenantId(String tenantId) {
        return springDataRepository.countByTenantId(tenantId);
    }

    @Override
    public long countByStatusAndTenantId(AssessmentStatus status, String tenantId) {
        return springDataRepository.countByStatusAndTenantId(status, tenantId);
    }
}
