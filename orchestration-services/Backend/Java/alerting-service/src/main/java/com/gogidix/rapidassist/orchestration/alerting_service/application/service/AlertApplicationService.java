package com.gogidix.rapidassist.orchestration.alerting_service.application.service;

import com.gogidix.rapidassist.orchestration.alerting_service.application.command.AcknowledgeAlertCommand;
import com.gogidix.rapidassist.orchestration.alerting_service.application.command.CreateAlertCommand;
import com.gogidix.rapidassist.orchestration.alerting_service.application.command.EscalateAlertCommand;
import com.gogidix.rapidassist.orchestration.alerting_service.application.command.ResolveAlertCommand;
import com.gogidix.rapidassist.orchestration.alerting_service.application.dto.AlertDTO;
import com.gogidix.rapidassist.orchestration.alerting_service.application.dto.AlertSummaryDTO;
import com.gogidix.rapidassist.orchestration.alerting_service.application.mapper.AlertMapper;
import com.gogidix.rapidassist.orchestration.alerting_service.application.query.GetAlertQuery;
import com.gogidix.rapidassist.orchestration.alerting_service.application.query.ListAlertsQuery;
import com.gogidix.rapidassist.orchestration.alerting_service.domain.model.Alert;
import com.gogidix.rapidassist.orchestration.alerting_service.domain.port.input.AlertServicePort;
import com.gogidix.rapidassist.orchestration.alerting_service.domain.port.output.AlertEventPublisherPort;
import com.gogidix.rapidassist.orchestration.alerting_service.domain.port.output.AlertRepositoryPort;
import com.gogidix.rapidassist.orchestration.alerting_service.shared.exception.NotFoundException;
import com.gogidix.rapidassist.orchestration.alerting_service.shared.requestcontext.RequestContext;
import com.gogidix.rapidassist.orchestration.alerting_service.shared.requestcontext.RequestContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Application service implementing Alert use cases.
 * This service orchestrates business logic and coordinates between domain and infrastructure.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlertApplicationService implements AlertServicePort {

    private final AlertRepositoryPort alertRepository;
    private final AlertEventPublisherPort eventPublisher;
    private final AlertMapper alertMapper;

    @Override
    @Transactional
    public AlertDTO createAlert(CreateAlertCommand command) {
        RequestContext context = RequestContextHolder.get();
        log.info("Creating alert for request: {} in tenant: {}", command.getRequestId(), context.getTenantId());

        Alert alert = buildAlert(command);
        alert.setCreatedAt(LocalDateTime.now());
        alert.setUpdatedAt(LocalDateTime.now());

        alert = alertRepository.save(alert);
        eventPublisher.publishAlertCreated(alert);

        if (alert.isCriticalAndUnattended()) {
            eventPublisher.publishCriticalAlertDetected(alert);
        }

        log.info("Alert created successfully with ID: {}", alert.getAlertId());
        return alertMapper.toDTO(alert);
    }

    @Override
    @Transactional
    public AlertDTO acknowledgeAlert(String alertId, AcknowledgeAlertCommand command) {
        RequestContext context = RequestContextHolder.get();
        log.info("Acknowledging alert: {} for tenant: {}", alertId, context.getTenantId());

        Alert alert = getAlertForTenant(alertId, context.getTenantId());
        alert.acknowledge(command.getAcknowledgedBy(), command.getAssignedTo());

        alert = alertRepository.save(alert);
        eventPublisher.publishAlertAcknowledged(alert);

        log.info("Alert acknowledged successfully: {}", alertId);
        return alertMapper.toDTO(alert);
    }

    @Override
    @Transactional
    public AlertDTO escalateAlert(String alertId, EscalateAlertCommand command) {
        RequestContext context = RequestContextHolder.get();
        log.info("Escalating alert: {} to level: {} for tenant: {}", alertId, command.getEscalationLevel(), context.getTenantId());

        Alert alert = getAlertForTenant(alertId, context.getTenantId());
        alert.escalate(command.getEscalationLevel(), command.getAssignedTo());

        alert = alertRepository.save(alert);
        eventPublisher.publishAlertEscalated(alert);

        log.info("Alert escalated successfully: {}", alertId);
        return alertMapper.toDTO(alert);
    }

    @Override
    @Transactional
    public AlertDTO resolveAlert(String alertId, ResolveAlertCommand command) {
        RequestContext context = RequestContextHolder.get();
        log.info("Resolving alert: {} for tenant: {}", alertId, context.getTenantId());

        Alert alert = getAlertForTenant(alertId, context.getTenantId());
        alert.resolve(command.getResolvedBy(), command.getResolutionNotes());

        alert = alertRepository.save(alert);
        eventPublisher.publishAlertResolved(alert);

        log.info("Alert resolved successfully: {}", alertId);
        return alertMapper.toDTO(alert);
    }

    @Override
    @Transactional
    public AlertDTO closeAlert(String alertId) {
        RequestContext context = RequestContextHolder.get();
        log.info("Closing alert: {} for tenant: {}", alertId, context.getTenantId());

        Alert alert = getAlertForTenant(alertId, context.getTenantId());
        alert.close();

        alert = alertRepository.save(alert);
        eventPublisher.publishAlertClosed(alert);

        log.info("Alert closed successfully: {}", alertId);
        return alertMapper.toDTO(alert);
    }

    @Override
    @Transactional
    public void deleteAlert(String alertId) {
        RequestContext context = RequestContextHolder.get();
        log.info("Deleting alert: {} for tenant: {}", alertId, context.getTenantId());

        Alert alert = getAlertForTenant(alertId, context.getTenantId());
        alertRepository.deleteByAlertId(alertId);

        log.info("Alert deleted successfully: {}", alertId);
    }

    @Override
    public AlertDTO getAlert(GetAlertQuery query) {
        log.debug("Getting alert: {} for tenant: {}", query.getAlertId(), query.getTenantId());

        Alert alert = alertRepository.findByAlertId(query.getAlertId())
            .filter(a -> a.getTenantId().equals(query.getTenantId()))
            .orElseThrow(() -> new NotFoundException("Alert not found: " + query.getAlertId()));

        return alertMapper.toDTO(alert);
    }

    @Override
    public List<AlertDTO> listAlerts(ListAlertsQuery query) {
        log.debug("Listing alerts with filters: {}", query);

        List<Alert> alerts = fetchAlerts(query);
        return alertMapper.toDTOList(alerts);
    }

    @Override
    public List<AlertDTO> getAlertsByRequestId(String requestId) {
        RequestContext context = RequestContextHolder.get();
        log.debug("Getting alerts for request: {} in tenant: {}", requestId, context.getTenantId());

        List<Alert> alerts = alertRepository.findByRequestId(requestId);
        return alertMapper.toDTOList(filterByTenant(alerts, context.getTenantId()));
    }

    @Override
    public List<AlertDTO> getActiveAlertsByTenant(String tenantId) {
        log.debug("Getting active alerts for tenant: {}", tenantId);

        List<Alert> alerts = alertRepository.findByTenantIdAndStatusIn(
            tenantId,
            List.of(Alert.AlertStatus.PENDING, Alert.AlertStatus.ACKNOWLEDGED, Alert.AlertStatus.IN_PROGRESS)
        );

        return alertMapper.toDTOList(alerts);
    }

    @Override
    public List<AlertDTO> getCriticalAlerts() {
        RequestContext context = RequestContextHolder.get();
        log.debug("Getting critical alerts for tenant: {}", context.getTenantId());

        List<Alert> alerts = alertRepository.findBySeverityAndStatusIn(
            Alert.AlertSeverity.CRITICAL,
            List.of(Alert.AlertStatus.PENDING, Alert.AlertStatus.ACKNOWLEDGED)
        );

        return alertMapper.toDTOList(filterByTenant(alerts, context.getTenantId()));
    }

    @Override
    public List<AlertDTO> getEscalationRequiredAlerts() {
        RequestContext context = RequestContextHolder.get();
        log.debug("Getting alerts requiring escalation for tenant: {}", context.getTenantId());

        List<Alert> alerts = alertRepository.findEscalationRequiredAlerts();
        return alertMapper.toDTOList(filterByTenant(alerts, context.getTenantId()));
    }

    // Private helper methods

    private Alert buildAlert(CreateAlertCommand command) {
        return Alert.builder()
            .alertId(generateAlertId())
            .requestId(command.getRequestId())
            .tenantId(command.getTenantId())
            .type(command.getType())
            .severity(command.getSeverity())
            .title(command.getTitle())
            .description(command.getDescription())
            .source(command.getSource())
            .location(command.getLocation())
            .vehicleInfo(command.getVehicleInfo())
            .customerInfo(command.getCustomerInfo())
            .status(Alert.AlertStatus.PENDING)
            .escalationLevel(0)
            .escalationRequired(false)
            .build();
    }

    private Alert getAlertForTenant(String alertId, String tenantId) {
        return alertRepository.findByAlertId(alertId)
            .filter(a -> a.getTenantId().equals(tenantId))
            .orElseThrow(() -> new NotFoundException("Alert not found: " + alertId));
    }

    private List<Alert> fetchAlerts(ListAlertsQuery query) {
        // Apply filters based on query parameters
        if (query.getTenantId() != null) {
            if (query.getType() != null && query.getStatuses() != null) {
                return alertRepository.findByTenantIdAndTypeAndStatusIn(
                    query.getTenantId(), query.getType(), query.getStatuses()
                );
            } else if (query.getStatuses() != null) {
                return alertRepository.findByTenantIdAndStatusIn(query.getTenantId(), query.getStatuses());
            } else {
                return alertRepository.findByTenantId(query.getTenantId());
            }
        } else if (query.getAssignedTo() != null) {
            return alertRepository.findByAssignedToAndStatusIn(
                query.getAssignedTo(),
                query.getStatuses() != null ? query.getStatuses() : List.of(Alert.AlertStatus.values())
            );
        } else {
            return List.of();
        }
    }

    private List<Alert> filterByTenant(List<Alert> alerts, String tenantId) {
        return alerts.stream()
            .filter(a -> a.getTenantId().equals(tenantId))
            .toList();
    }

    private String generateAlertId() {
        return "ALT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
