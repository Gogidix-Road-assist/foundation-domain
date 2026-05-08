package com.gogidix.rapidassist.logging.aggregation.service.application;

import com.gogidix.rapidassist.logging.aggregation.service.domain.model.LogEvent;
import com.gogidix.rapidassist.logging.aggregation.service.domain.port.in.QueryLogEventsQuery;
import com.gogidix.rapidassist.logging.aggregation.service.domain.port.out.LogEventStore;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class QueryLogEventsUseCase implements QueryLogEventsQuery {

    private final LogEventStore store;

    public QueryLogEventsUseCase(LogEventStore store) {
        this.store = store;
    }

    @Override
    public List<LogEvent> query(String tenantId, String correlationId, Instant from, Instant to, String level, int limit) {
        int safeLimit = Math.min(Math.max(limit, 1), 500);
        return store.query(tenantId, correlationId, from, to, level, safeLimit);
    }
}
