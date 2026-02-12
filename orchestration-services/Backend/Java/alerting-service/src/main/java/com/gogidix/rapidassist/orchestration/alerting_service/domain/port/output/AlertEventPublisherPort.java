package com.gogidix.rapidassist.orchestration.alerting_service.domain.port.output;

import com.gogidix.rapidassist.orchestration.alerting_service.domain.model.Alert;

/**
 * Output port for publishing alert events.
 * This interface defines the contract for event publishing.
 * The infrastructure layer will provide the Kafka implementation.
 */
public interface AlertEventPublisherPort {

    void publishAlertCreated(Alert alert);

    void publishAlertAcknowledged(Alert alert);

    void publishAlertEscalated(Alert alert);

    void publishAlertResolved(Alert alert);

    void publishAlertClosed(Alert alert);

    void publishCriticalAlertDetected(Alert alert);

    void publishEscalationRequired(Alert alert);
}
