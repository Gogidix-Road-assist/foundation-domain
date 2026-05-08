package com.gogidix.rapidassist.alerting.service.application;

import com.gogidix.rapidassist.alerting.service.domain.model.AlertEvent;
import com.gogidix.rapidassist.alerting.service.domain.port.in.IngestAlertEventCommand;
import com.gogidix.rapidassist.alerting.service.domain.port.out.AlertEventStore;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class IngestAlertEventUseCase implements IngestAlertEventCommand {

    private final AlertEventStore store;
    private final AlertingRetentionProperties properties;

    public IngestAlertEventUseCase(AlertEventStore store, AlertingRetentionProperties properties) {
        this.store = store;
        this.properties = properties;
    }

    @Override
    public void ingest(AlertEvent event) {
        Instant occurredAt = event.occurredAt() == null ? Instant.now() : event.occurredAt();
        Instant expiresAt = occurredAt.plus(properties.getEventsRetention());

        AlertEvent normalized = new AlertEvent(
                event.tenantId(),
                event.country(),
                event.correlationId(),
                occurredAt,
                event.ruleId(),
                event.severity(),
                event.message(),
                event.attributes()
        );

        store.append(normalized, expiresAt);
    }
}
