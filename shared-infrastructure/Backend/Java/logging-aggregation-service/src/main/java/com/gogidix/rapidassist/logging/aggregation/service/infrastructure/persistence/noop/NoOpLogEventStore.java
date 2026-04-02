package com.gogidix.rapidassist.logging.aggregation.service.infrastructure.persistence.noop;

import com.gogidix.rapidassist.logging.aggregation.service.domain.model.LogEvent;
import com.gogidix.rapidassist.logging.aggregation.service.domain.port.out.LogEventStore;

import java.time.Instant;
import java.util.List;

public class NoOpLogEventStore implements LogEventStore {

    @Override
    public void append(LogEvent event, Instant expiresAt) {
    }

    @Override
    public List<LogEvent> query(String tenantId, String correlationId, Instant from, Instant to, String level, int limit) {
        return List.of();
    }
}
