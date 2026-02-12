package com.gogidix.rapidassist.orchestration.alerting_service.infrastructure.messaging.kafka;

import com.gogidix.rapidassist.orchestration.alerting_service.domain.model.Alert;
import com.gogidix.rapidassist.orchestration.alerting_service.domain.port.output.AlertEventPublisherPort;
import com.gogidix.rapidassist.orchestration.alerting_service.infrastructure.messaging.events.AlertEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Kafka adapter for Alert event publishing
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AlertEventPublisherAdapter implements AlertEventPublisherPort {

    private final KafkaTemplate<String, AlertEvent> kafkaTemplate;

    @Value("${alerting.kafka.topic.alert-events:alert-events}")
    private String alertEventsTopic;

    @Override
    public void publishAlertCreated(Alert alert) {
        AlertEvent event = AlertEvent.builder()
            .eventType("AlertCreated")
            .alertId(alert.getAlertId())
            .requestId(alert.getRequestId())
            .tenantId(alert.getTenantId())
            .type(alert.getType().name())
            .severity(alert.getSeverity().name())
            .status(alert.getStatus().name())
            .timestamp(java.time.Instant.now())
            .build();

        publish(event, "AlertCreated");
    }

    @Override
    public void publishAlertAcknowledged(Alert alert) {
        AlertEvent event = AlertEvent.builder()
            .eventType("AlertAcknowledged")
            .alertId(alert.getAlertId())
            .requestId(alert.getRequestId())
            .tenantId(alert.getTenantId())
            .assignedTo(alert.getAssignedTo())
            .acknowledgedBy(alert.getAcknowledgedBy())
            .status(alert.getStatus().name())
            .timestamp(java.time.Instant.now())
            .build();

        publish(event, "AlertAcknowledged");
    }

    @Override
    public void publishAlertEscalated(Alert alert) {
        AlertEvent event = AlertEvent.builder()
            .eventType("AlertEscalated")
            .alertId(alert.getAlertId())
            .requestId(alert.getRequestId())
            .tenantId(alert.getTenantId())
            .assignedTo(alert.getAssignedTo())
            .escalationLevel(alert.getEscalationLevel())
            .status(alert.getStatus().name())
            .timestamp(java.time.Instant.now())
            .build();

        publish(event, "AlertEscalated");
    }

    @Override
    public void publishAlertResolved(Alert alert) {
        AlertEvent event = AlertEvent.builder()
            .eventType("AlertResolved")
            .alertId(alert.getAlertId())
            .requestId(alert.getRequestId())
            .tenantId(alert.getTenantId())
            .resolvedBy(alert.getResolvedBy())
            .status(alert.getStatus().name())
            .timestamp(java.time.Instant.now())
            .build();

        publish(event, "AlertResolved");
    }

    @Override
    public void publishAlertClosed(Alert alert) {
        AlertEvent event = AlertEvent.builder()
            .eventType("AlertClosed")
            .alertId(alert.getAlertId())
            .requestId(alert.getRequestId())
            .tenantId(alert.getTenantId())
            .status(alert.getStatus().name())
            .timestamp(java.time.Instant.now())
            .build();

        publish(event, "AlertClosed");
    }

    @Override
    public void publishCriticalAlertDetected(Alert alert) {
        AlertEvent event = AlertEvent.builder()
            .eventType("CriticalAlertDetected")
            .alertId(alert.getAlertId())
            .requestId(alert.getRequestId())
            .tenantId(alert.getTenantId())
            .type(alert.getType().name())
            .severity(alert.getSeverity().name())
            .status(alert.getStatus().name())
            .timestamp(java.time.Instant.now())
            .build();

        publish(event, "CriticalAlertDetected");
    }

    @Override
    public void publishEscalationRequired(Alert alert) {
        AlertEvent event = AlertEvent.builder()
            .eventType("EscalationRequired")
            .alertId(alert.getAlertId())
            .requestId(alert.getRequestId())
            .tenantId(alert.getTenantId())
            .escalationLevel(alert.getEscalationLevel())
            .severity(alert.getSeverity().name())
            .status(alert.getStatus().name())
            .timestamp(java.time.Instant.now())
            .build();

        publish(event, "EscalationRequired");
    }

    private void publish(AlertEvent event, String eventType) {
        try {
            CompletableFuture<SendResult<String, AlertEvent>> future =
                kafkaTemplate.send(alertEventsTopic, event.getAlertId(), event);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.debug("Successfully published {} event for alert: {}", eventType, event.getAlertId());
                } else {
                    log.error("Failed to publish {} event for alert: {}", eventType, event.getAlertId(), ex);
                }
            });
        } catch (Exception e) {
            log.error("Error publishing {} event for alert: {}", eventType, event.getAlertId(), e);
        }
    }
}
