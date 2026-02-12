package com.gogidix.rapidassist.ai.anomaly.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.anomaly.domain.model.AnomalyAlert;
import com.gogidix.rapidassist.ai.anomaly.domain.model.AlertSeverity;
import com.gogidix.rapidassist.ai.anomaly.domain.model.AlertStatus;
import com.gogidix.rapidassist.ai.anomaly.domain.repository.AnomalyAlertRepositoryPort;
import com.gogidix.rapidassist.ai.anomaly.infrastructure.persistence.entity.AnomalyAlertEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * MongoDB implementation of AnomalyAlertRepositoryPort.
 */
@Repository
@RequiredArgsConstructor
public class AnomalyAlertRepositoryImpl implements AnomalyAlertRepositoryPort {

    private final AnomalyAlertMongoRepository mongoRepository;

    @Override
    public AnomalyAlert save(String tenantId, AnomalyAlert alert) {
        AnomalyAlertEntity entity = toEntity(alert);
        entity.setTenantId(tenantId);
        AnomalyAlertEntity savedEntity = mongoRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<AnomalyAlert> findById(String tenantId, UUID alertId) {
        return mongoRepository.findByTenantId(tenantId).stream()
                .filter(entity -> entity.getUuid().equals(alertId))
                .findFirst()
                .map(this::toDomain);
    }

    @Override
    public Optional<AnomalyAlert> findByAlertId(String tenantId, String alertId) {
        return Optional.ofNullable(mongoRepository.findByTenantIdAndAlertId(tenantId, alertId))
                .map(this::toDomain);
    }

    @Override
    public List<AnomalyAlert> findByTenantId(String tenantId) {
        return mongoRepository.findByTenantId(tenantId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AnomalyAlert> findByDetectionId(String tenantId, UUID detectionId) {
        return mongoRepository.findByTenantIdAndDetectionId(tenantId, detectionId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AnomalyAlert> findBySeverity(String tenantId, String severity) {
        AlertSeverity severityEnum = AlertSeverity.valueOf(severity.toUpperCase());
        return mongoRepository.findByTenantIdAndSeverity(tenantId, severityEnum).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AnomalyAlert> findByStatus(String tenantId, String status) {
        AlertStatus statusEnum = AlertStatus.valueOf(status.toUpperCase());
        return mongoRepository.findByTenantIdAndStatus(tenantId, statusEnum).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AnomalyAlert> findByAssignedTo(String tenantId, String assignedTo) {
        return mongoRepository.findByTenantIdAndAssignedTo(tenantId, assignedTo).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AnomalyAlert> findOpenAlerts(String tenantId) {
        return mongoRepository.findOpenAlerts(tenantId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AnomalyAlert> findCriticalAlerts(String tenantId) {
        return mongoRepository.findCriticalAlerts(tenantId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AnomalyAlert> findByTriggeredAtBetween(String tenantId, LocalDateTime startDate, LocalDateTime endDate) {
        return mongoRepository.findByTenantIdAndTriggeredAtBetween(tenantId, startDate, endDate).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String tenantId, UUID alertId) {
        mongoRepository.findByTenantId(tenantId).stream()
                .filter(entity -> entity.getUuid().equals(alertId))
                .findFirst()
                .ifPresent(mongoRepository::delete);
    }

    @Override
    public boolean exists(String tenantId, UUID alertId) {
        return mongoRepository.existsByTenantIdAndUuid(tenantId, alertId);
    }

    @Override
    public long countByTenantId(String tenantId) {
        return mongoRepository.countByTenantId(tenantId);
    }

    @Override
    public long countByStatus(String tenantId, String status) {
        AlertStatus statusEnum = AlertStatus.valueOf(status.toUpperCase());
        return mongoRepository.countByTenantIdAndStatus(tenantId, statusEnum);
    }

    private AnomalyAlertEntity toEntity(AnomalyAlert domain) {
        return AnomalyAlertEntity.builder()
                .uuid(domain.getId())
                .tenantId(domain.getTenantId())
                .alertId(domain.getAlertId())
                .detectionId(domain.getDetectionId())
                .ruleId(domain.getRuleId())
                .title(domain.getTitle())
                .message(domain.getMessage())
                .severity(domain.getSeverity())
                .status(domain.getStatus())
                .notificationChannels(domain.getNotificationChannels())
                .context(domain.getContext())
                .assignedTo(domain.getAssignedTo())
                .triggeredAt(domain.getTriggeredAt())
                .acknowledgedAt(domain.getAcknowledgedAt())
                .acknowledgedBy(domain.getAcknowledgedBy())
                .resolvedAt(domain.getResolvedAt())
                .resolvedBy(domain.getResolvedBy())
                .resolutionNotes(domain.getResolutionNotes())
                .escalationLevel(domain.getEscalationLevel())
                .lastEscalatedAt(domain.getLastEscalatedAt())
                .metadata(domain.getMetadata())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .version(domain.getVersion())
                .build();
    }

    private AnomalyAlert toDomain(AnomalyAlertEntity entity) {
        return AnomalyAlert.builder()
                .id(entity.getUuid())
                .tenantId(entity.getTenantId())
                .alertId(entity.getAlertId())
                .detectionId(entity.getDetectionId())
                .ruleId(entity.getRuleId())
                .title(entity.getTitle())
                .message(entity.getMessage())
                .severity(entity.getSeverity())
                .status(entity.getStatus())
                .notificationChannels(entity.getNotificationChannels())
                .context(entity.getContext())
                .assignedTo(entity.getAssignedTo())
                .triggeredAt(entity.getTriggeredAt())
                .acknowledgedAt(entity.getAcknowledgedAt())
                .acknowledgedBy(entity.getAcknowledgedBy())
                .resolvedAt(entity.getResolvedAt())
                .resolvedBy(entity.getResolvedBy())
                .resolutionNotes(entity.getResolutionNotes())
                .escalationLevel(entity.getEscalationLevel())
                .lastEscalatedAt(entity.getLastEscalatedAt())
                .metadata(entity.getMetadata())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .version(entity.getVersion())
                .build();
    }
}
