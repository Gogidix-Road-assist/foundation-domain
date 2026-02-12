package com.gogidix.rapidassist.orchestration.alerting_service.domain.port.output;

import com.gogidix.rapidassist.orchestration.alerting_service.domain.model.Alert;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Output port for Alert persistence.
 * This interface defines the contract for storing and retrieving alerts.
 * The infrastructure layer will provide the MongoDB implementation.
 */
public interface AlertRepositoryPort {

    Alert save(Alert alert);

    Optional<Alert> findById(String id);

    Optional<Alert> findByAlertId(String alertId);

    List<Alert> findByRequestId(String requestId);

    List<Alert> findByTenantId(String tenantId);

    List<Alert> findByTenantIdAndStatusIn(String tenantId, List<Alert.AlertStatus> statuses);

    List<Alert> findByTenantIdAndTypeAndStatusIn(
        String tenantId,
        Alert.AlertType type,
        List<Alert.AlertStatus> statuses
    );

    List<Alert> findBySeverityAndStatusIn(
        Alert.AlertSeverity severity,
        List<Alert.AlertStatus> statuses
    );

    List<Alert> findByTenantIdAndCreatedAtBetween(
        String tenantId,
        LocalDateTime start,
        LocalDateTime end
    );

    List<Alert> findByAssignedToAndStatusIn(String assignedTo, List<Alert.AlertStatus> statuses);

    List<Alert> findEscalationRequiredAlerts();

    List<Alert> findCriticalAndUnattendedAlerts();

    long countByStatus(Alert.AlertStatus status);

    long countBySeverityAndStatusIn(
        Alert.AlertSeverity severity,
        List<Alert.AlertStatus> statuses
    );

    void deleteByAlertId(String alertId);

    boolean existsByAlertId(String alertId);
}
