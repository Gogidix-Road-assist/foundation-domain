package com.gogidix.rapidassist.metrics.telemetry.service.application;

import com.gogidix.rapidassist.metrics.telemetry.service.domain.model.TelemetryEvent;
import com.gogidix.rapidassist.metrics.telemetry.service.domain.port.in.IngestTelemetryEventCommand;
import com.gogidix.rapidassist.metrics.telemetry.service.domain.port.out.TelemetryEventStore;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class IngestTelemetryEventUseCase implements IngestTelemetryEventCommand {

    private final TelemetryEventStore store;
    private final TelemetryRetentionProperties retentionProperties;

    public IngestTelemetryEventUseCase(TelemetryEventStore store, TelemetryRetentionProperties retentionProperties) {
        this.store = store;
        this.retentionProperties = retentionProperties;
    }

    @Override
    public void ingest(TelemetryEvent event) {
        Instant ts = event.timestamp() == null ? Instant.now() : event.timestamp();
        Instant expiresAt = ts.plus(retentionProperties.getRetention());

        TelemetryEvent normalized = new TelemetryEvent(
                event.tenantId(),
                event.country(),
                event.correlationId(),
                ts,
                event.type(),
                event.name(),
                event.value(),
                event.attributes()
        );

        store.append(normalized, expiresAt);
    }
}
