package com.gogidix.rapidassist.logging.aggregation.service.domain.port.out;

import com.gogidix.rapidassist.logging.aggregation.service.domain.model.LogEvent;

import java.time.Instant;
import java.util.List;

public interface LogEventStore {

    void append(LogEvent event, Instant expiresAt);

    List<LogEvent> query(
            String tenantId,
            String correlationId,
            Instant from,
            Instant to,
            String level,
            int limit
    );
}
