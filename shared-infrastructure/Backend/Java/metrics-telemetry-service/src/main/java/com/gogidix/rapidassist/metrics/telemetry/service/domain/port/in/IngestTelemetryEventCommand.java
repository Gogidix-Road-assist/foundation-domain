package com.gogidix.rapidassist.metrics.telemetry.service.domain.port.in;

import com.gogidix.rapidassist.metrics.telemetry.service.domain.model.TelemetryEvent;

public interface IngestTelemetryEventCommand {

    void ingest(TelemetryEvent event);
}
