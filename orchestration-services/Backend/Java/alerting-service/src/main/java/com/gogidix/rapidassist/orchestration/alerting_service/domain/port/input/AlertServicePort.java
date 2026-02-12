package com.gogidix.rapidassist.orchestration.alerting_service.domain.port.input;

import com.gogidix.rapidassist.orchestration.alerting_service.application.command.CreateAlertCommand;
import com.gogidix.rapidassist.orchestration.alerting_service.application.command.AcknowledgeAlertCommand;
import com.gogidix.rapidassist.orchestration.alerting_service.application.command.EscalateAlertCommand;
import com.gogidix.rapidassist.orchestration.alerting_service.application.command.ResolveAlertCommand;
import com.gogidix.rapidassist.orchestration.alerting_service.application.dto.AlertDTO;
import com.gogidix.rapidassist.orchestration.alerting_service.application.query.GetAlertQuery;
import com.gogidix.rapidassist.orchestration.alerting_service.application.query.ListAlertsQuery;

import java.util.List;

/**
 * Input port for Alert operations.
 * This interface defines the use cases that the application layer provides.
 */
public interface AlertServicePort {

    // Commands (state-changing operations)
    AlertDTO createAlert(CreateAlertCommand command);

    AlertDTO acknowledgeAlert(String alertId, AcknowledgeAlertCommand command);

    AlertDTO escalateAlert(String alertId, EscalateAlertCommand command);

    AlertDTO resolveAlert(String alertId, ResolveAlertCommand command);

    AlertDTO closeAlert(String alertId);

    void deleteAlert(String alertId);

    // Queries (read-only operations)
    AlertDTO getAlert(GetAlertQuery query);

    List<AlertDTO> listAlerts(ListAlertsQuery query);

    List<AlertDTO> getAlertsByRequestId(String requestId);

    List<AlertDTO> getActiveAlertsByTenant(String tenantId);

    List<AlertDTO> getCriticalAlerts();

    List<AlertDTO> getEscalationRequiredAlerts();
}
