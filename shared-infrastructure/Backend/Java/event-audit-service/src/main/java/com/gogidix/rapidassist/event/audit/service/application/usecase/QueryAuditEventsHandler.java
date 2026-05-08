package com.gogidix.rapidassist.event.audit.service.application.usecase;

import com.gogidix.rapidassist.event.audit.service.domain.model.AuditEvent;
import com.gogidix.rapidassist.event.audit.service.domain.port.in.QueryAuditEventsQuery;
import com.gogidix.rapidassist.event.audit.service.domain.port.out.AuditEventStore;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class QueryAuditEventsHandler implements QueryAuditEventsQuery {

    private final AuditEventStore store;

    public QueryAuditEventsHandler(AuditEventStore store) {
        this.store = store;
    }

    @Override
    public List<AuditEvent> query(String tenantId, String country, String entityType, String entityId, Instant from, Instant to, int limit) {
        int safeLimit = limit <= 0 ? 100 : Math.min(limit, 1000);
        return store.query(tenantId, country, entityType, entityId, from, to, safeLimit);
    }
}
