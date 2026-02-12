package com.gogidix.rapidassist.orchestration.alerting_service.infrastructure.persistence.mongo;

import com.gogidix.rapidassist.orchestration.alerting_service.domain.model.Alert;
import com.gogidix.rapidassist.orchestration.alerting_service.domain.port.output.AlertRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * MongoDB adapter for Alert repository port
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AlertRepositoryAdapter implements AlertRepositoryPort {

    private final AlertMongoRepository mongoRepository;

    @Override
    public Alert save(Alert alert) {
        AlertDocument document = AlertDocument.fromDomain(alert);
        AlertDocument saved = mongoRepository.save(document);
        log.debug("Saved alert with ID: {}", saved.getAlertId());
        return saved.toDomain();
    }

    @Override
    public Optional<Alert> findById(String id) {
        return mongoRepository.findById(id)
            .map(AlertDocument::toDomain);
    }

    @Override
    public Optional<Alert> findByAlertId(String alertId) {
        return mongoRepository.findByAlertId(alertId).stream()
            .findFirst()
            .map(AlertDocument::toDomain);
    }

    @Override
    public List<Alert> findByRequestId(String requestId) {
        return mongoRepository.findByRequestId(requestId).stream()
            .map(AlertDocument::toDomain)
            .toList();
    }

    @Override
    public List<Alert> findByTenantId(String tenantId) {
        return mongoRepository.findByTenantId(tenantId).stream()
            .map(AlertDocument::toDomain)
            .toList();
    }

    @Override
    public List<Alert> findByTenantIdAndStatusIn(String tenantId, List<Alert.AlertStatus> statuses) {
        List<String> statusStrings = statuses.stream().map(Enum::name).toList();
        return mongoRepository.findByTenantIdAndStatusIn(tenantId, statusStrings).stream()
            .map(AlertDocument::toDomain)
            .toList();
    }

    @Override
    public List<Alert> findByTenantIdAndTypeAndStatusIn(
        String tenantId,
        Alert.AlertType type,
        List<Alert.AlertStatus> statuses
    ) {
        List<String> statusStrings = statuses.stream().map(Enum::name).toList();
        return mongoRepository.findByTenantIdAndTypeAndStatusIn(
            tenantId,
            type.name(),
            statusStrings
        ).stream()
            .map(AlertDocument::toDomain)
            .toList();
    }

    @Override
    public List<Alert> findBySeverityAndStatusIn(
        Alert.AlertSeverity severity,
        List<Alert.AlertStatus> statuses
    ) {
        List<String> statusStrings = statuses.stream().map(Enum::name).toList();
        return mongoRepository.findBySeverityAndStatusIn(severity.name(), statusStrings).stream()
            .map(AlertDocument::toDomain)
            .toList();
    }

    @Override
    public List<Alert> findByTenantIdAndCreatedAtBetween(
        String tenantId,
        LocalDateTime start,
        LocalDateTime end
    ) {
        return mongoRepository.findByTenantIdAndCreatedAtBetween(tenantId, start, end).stream()
            .map(AlertDocument::toDomain)
            .toList();
    }

    @Override
    public List<Alert> findByAssignedToAndStatusIn(String assignedTo, List<Alert.AlertStatus> statuses) {
        List<String> statusStrings = statuses.stream().map(Enum::name).toList();
        return mongoRepository.findByAssignedToAndStatusIn(assignedTo, statusStrings).stream()
            .map(AlertDocument::toDomain)
            .toList();
    }

    @Override
    public List<Alert> findEscalationRequiredAlerts() {
        List<String> statuses = List.of(
            Alert.AlertStatus.PENDING.name(),
            Alert.AlertStatus.ACKNOWLEDGED.name(),
            Alert.AlertStatus.IN_PROGRESS.name()
        );
        return mongoRepository.findEscalationRequiredByStatusIn(statuses).stream()
            .map(AlertDocument::toDomain)
            .toList();
    }

    @Override
    public List<Alert> findCriticalAndUnattendedAlerts() {
        List<String> statuses = List.of(
            Alert.AlertStatus.PENDING.name(),
            Alert.AlertStatus.ACKNOWLEDGED.name()
        );
        return mongoRepository.findCriticalByStatusIn(statuses).stream()
            .map(AlertDocument::toDomain)
            .toList();
    }

    @Override
    public long countByStatus(Alert.AlertStatus status) {
        return mongoRepository.countByStatus(status.name());
    }

    @Override
    public long countBySeverityAndStatusIn(
        Alert.AlertSeverity severity,
        List<Alert.AlertStatus> statuses
    ) {
        List<String> statusStrings = statuses.stream().map(Enum::name).toList();
        return mongoRepository.countBySeverityAndStatusIn(severity.name(), statusStrings);
    }

    @Override
    public void deleteByAlertId(String alertId) {
        mongoRepository.deleteByAlertId(alertId);
    }

    @Override
    public boolean existsByAlertId(String alertId) {
        return mongoRepository.existsByAlertId(alertId);
    }
}
