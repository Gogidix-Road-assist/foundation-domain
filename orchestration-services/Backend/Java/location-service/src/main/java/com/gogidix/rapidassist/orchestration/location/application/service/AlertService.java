package com.gogidix.rapidassist.orchestration.location.application.service;

import com.gogidix.rapidassist.orchestration.location.domain.model.LocationAlert;
import com.gogidix.rapidassist.orchestration.location.domain.port.in.AlertServicePort;
import com.gogidix.rapidassist.orchestration.location.domain.port.out.LocationAlertRepositoryPort;
import com.gogidix.rapidassist.orchestration.location.infrastructure.messaging.kafka.AlertEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Application service for Alert operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlertService implements AlertServicePort {

    private final LocationAlertRepositoryPort alertRepository;
    private final AlertEventPublisher eventPublisher;

    @Override
    @Transactional
    public LocationAlert createAlert(String tenantId, LocationAlert alert) {
        log.info("Creating alert for tenant: {}, entity: {} {}", tenantId, alert.getEntityType(), alert.getEntityId());

        alert.setTenantId(tenantId);
        alert.setId(UUID.randomUUID().toString());
        alert.setTimestamp(LocalDateTime.now());

        if (alert.getStatus() == null) {
            alert.setStatus(LocationAlert.AlertStatus.PENDING);
        }

        LocationAlert saved = alertRepository.save(alert);
        eventPublisher.publishAlertCreated(saved);

        return saved;
    }

    @Override
    public LocationAlert getAlert(String tenantId, String alertId) {
        return alertRepository.findById(alertId)
                .filter(a -> a.getTenantId().equals(tenantId))
                .orElseThrow(() -> new IllegalArgumentException("Alert not found: " + alertId));
    }

    @Override
    public List<LocationAlert> getPendingAlerts(String tenantId) {
        return alertRepository.findPendingAlerts(tenantId);
    }

    @Override
    public List<LocationAlert> getAlertsForEntity(
            String tenantId,
            com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType,
            String entityId
    ) {
        return alertRepository.findByTenantIdAndEntityTypeAndEntityId(tenantId, entityType, entityId);
    }

    @Override
    public List<LocationAlert> getAlertsWithinTimeRange(
            String tenantId,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        return alertRepository.findByTenantIdAndTimestampBetween(tenantId, startTime, endTime);
    }

    @Override
    public List<LocationAlert> getRecentAlerts(String tenantId, Integer limit) {
        return alertRepository.findRecentAlerts(tenantId, limit);
    }

    @Override
    @Transactional
    public LocationAlert acknowledgeAlert(String tenantId, String alertId, String acknowledgedBy) {
        log.info("Acknowledging alert: {} for tenant: {} by: {}", alertId, tenantId, acknowledgedBy);

        LocationAlert alert = getAlert(tenantId, alertId);
        alert.acknowledge(acknowledgedBy);

        LocationAlert updated = alertRepository.save(alert);
        eventPublisher.publishAlertAcknowledged(updated);

        return updated;
    }

    @Override
    @Transactional
    public LocationAlert resolveAlert(String tenantId, String alertId) {
        log.info("Resolving alert: {} for tenant: {}", alertId, tenantId);

        LocationAlert alert = getAlert(tenantId, alertId);
        alert.resolve();

        LocationAlert updated = alertRepository.save(alert);
        eventPublisher.publishAlertResolved(updated);

        return updated;
    }

    @Override
    @Transactional
    public void sendPendingAlerts(String tenantId) {
        log.info("Sending pending alerts for tenant: {}", tenantId);

        List<LocationAlert> pendingAlerts = getPendingAlerts(tenantId);

        for (LocationAlert alert : pendingAlerts) {
            try {
                // TODO: Implement actual notification sending (email, SMS, push, etc.)
                alert.markAsSent();
                alertRepository.save(alert);
                eventPublisher.publishAlertSent(alert);
            } catch (Exception e) {
                log.error("Failed to send alert: {}", alert.getId(), e);
                alert.markAsFailed();
                alertRepository.save(alert);
            }
        }
    }
}
