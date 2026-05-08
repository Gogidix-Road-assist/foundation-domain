package com.gogidix.rapidassist.logging.aggregation.service.domain.port.in;

import com.gogidix.rapidassist.logging.aggregation.service.domain.model.LogEvent;

import java.time.Instant;
import java.util.List;

public interface QueryLogEventsQuery {

    List<LogEvent> query(
            String tenantId,
            String correlationId,
            Instant from,
            Instant to,
            String level,
            int limit
    );
}
