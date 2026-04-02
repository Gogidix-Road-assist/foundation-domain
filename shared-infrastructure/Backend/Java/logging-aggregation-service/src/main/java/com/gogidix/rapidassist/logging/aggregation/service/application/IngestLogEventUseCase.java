package com.gogidix.rapidassist.logging.aggregation.service.application;

import com.gogidix.rapidassist.logging.aggregation.service.domain.model.LogEvent;
import com.gogidix.rapidassist.logging.aggregation.service.domain.port.in.IngestLogEventCommand;
import com.gogidix.rapidassist.logging.aggregation.service.domain.port.out.LogEventStore;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class IngestLogEventUseCase implements IngestLogEventCommand {

    private final LogEventStore store;
    private final LoggingRetentionProperties retentionProperties;

    public IngestLogEventUseCase(LogEventStore store, LoggingRetentionProperties retentionProperties) {
        this.store = store;
        this.retentionProperties = retentionProperties;
    }

    @Override
    public void ingest(LogEvent event) {
        Instant ts = event.timestamp() == null ? Instant.now() : event.timestamp();
        Instant expiresAt = ts.plus(retentionProperties.getRetention());

        LogEvent normalized = new LogEvent(
                event.tenantId(),
                event.country(),
                event.correlationId(),
                ts,
                event.level(),
                event.logger(),
                event.message(),
                event.attributes()
        );

        store.append(normalized, expiresAt);
    }
}
