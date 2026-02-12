package com.gogidix.rapidassist.ai.riskassessment.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.riskassessment.domain.model.AlertPriority;
import com.gogidix.rapidassist.ai.riskassessment.domain.model.AlertStatus;
import com.gogidix.rapidassist.ai.riskassessment.domain.model.RiskAlert;
import com.gogidix.rapidassist.ai.riskassessment.domain.model.RiskCategory;
import com.gogidix.rapidassist.ai.riskassessment.domain.repository.RiskAlertRepositoryPort;
import com.gogidix.rapidassist.ai.riskassessment.infrastructure.persistence.entity.RiskAlertEntity;
import com.gogidix.rapidassist.ai.riskassessment.infrastructure.persistence.mapper.RiskAlertPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of RiskAlertRepositoryPort
 * Bridges domain layer with infrastructure persistence layer
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RiskAlertRepositoryImpl implements RiskAlertRepositoryPort {

    private final SpringDataRiskAlertRepository springDataRepository;
    private final RiskAlertPersistenceMapper mapper;

    @Override
    public RiskAlert save(RiskAlert alert) {
        log.debug("Saving risk alert: {}", alert.getId());
        RiskAlertEntity entity = mapper.toEntity(alert);
        RiskAlertEntity saved = springDataRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<RiskAlert> findByIdAndTenantId(UUID id, String tenantId) {
        log.debug("Finding risk alert by id: {} for tenant: {}", id, tenantId);
        return springDataRepository.findByUuidAndTenantId(id, tenantId)
                .map(mapper::toDomain);
    }

    @Override
    public List<RiskAlert> findByRiskAssessmentIdAndTenantId(UUID riskAssessmentId, String tenantId) {
        log.debug("Finding risk alerts by assessment: {} for tenant: {}", riskAssessmentId, tenantId);
        return springDataRepository.findByRiskAssessmentIdAndTenantId(riskAssessmentId, tenantId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<RiskAlert> findByStatusAndTenantId(AlertStatus status, String tenantId) {
        log.debug("Finding risk alerts by status: {} for tenant: {}", status, tenantId);
        return springDataRepository.findByStatusAndTenantId(status, tenantId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<RiskAlert> findByPriorityAndTenantId(AlertPriority priority, String tenantId) {
        log.debug("Finding risk alerts by priority: {} for tenant: {}", priority, tenantId);
        return springDataRepository.findByPriorityAndTenantId(priority, tenantId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<RiskAlert> findByCategoryAndTenantId(RiskCategory category, String tenantId) {
        log.debug("Finding risk alerts by category: {} for tenant: {}", category, tenantId);
        return springDataRepository.findByCategoryAndTenantId(category, tenantId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<RiskAlert> findByAssignedToAndTenantId(String assignedTo, String tenantId) {
        log.debug("Finding risk alerts assigned to: {} for tenant: {}", assignedTo, tenantId);
        return springDataRepository.findByAssignedToAndTenantId(assignedTo, tenantId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<RiskAlert> findActiveAlerts(String tenantId) {
        log.debug("Finding active risk alerts for tenant: {}", tenantId);
        List<AlertStatus> activeStatuses = List.of(AlertStatus.ACTIVE, AlertStatus.ACKNOWLEDGED, AlertStatus.IN_PROGRESS);
        return springDataRepository.findByTenantIdAndStatusIn(tenantId, activeStatuses).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<RiskAlert> findUnresolvedAlerts(String tenantId) {
        log.debug("Finding unresolved risk alerts for tenant: {}", tenantId);
        return springDataRepository.findUnresolvedAlerts(tenantId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<RiskAlert> findOverdueAlerts(LocalDateTime threshold, String tenantId) {
        log.debug("Finding overdue risk alerts for tenant: {}", tenantId);
        return springDataRepository.findOverdueAlerts(tenantId, threshold).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<RiskAlert> findByCreatedAtBetweenAndTenantId(LocalDateTime startDate, LocalDateTime endDate, String tenantId) {
        log.debug("Finding risk alerts created between {} and {} for tenant: {}", startDate, endDate, tenantId);
        return springDataRepository.findByCreatedAtBetweenAndTenantId(startDate, endDate, tenantId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<RiskAlert> findCriticalAlerts(String tenantId) {
        log.debug("Finding critical risk alerts for tenant: {}", tenantId);
        return springDataRepository.findByTenantIdAndPriority(tenantId, AlertPriority.CRITICAL).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByIdAndTenantId(UUID id, String tenantId) {
        return springDataRepository.existsByUuidAndTenantId(id, tenantId);
    }

    @Override
    public void deleteByIdAndTenantId(UUID id, String tenantId) {
        log.debug("Deleting risk alert: {} for tenant: {}", id, tenantId);
        springDataRepository.deleteByUuidAndTenantId(id, tenantId);
    }

    @Override
    public long countByTenantId(String tenantId) {
        return springDataRepository.countByTenantId(tenantId);
    }

    @Override
    public long countByStatusAndTenantId(AlertStatus status, String tenantId) {
        return springDataRepository.countByStatusAndTenantId(status, tenantId);
    }
}
