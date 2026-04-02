package com.gogidix.rapidassist.alerting.service.application;

import com.gogidix.rapidassist.alerting.service.domain.model.AlertEvent;
import com.gogidix.rapidassist.alerting.service.domain.port.in.QueryAlertEventsQuery;
import com.gogidix.rapidassist.alerting.service.domain.port.out.AlertEventStore;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class QueryAlertEventsUseCase implements QueryAlertEventsQuery {

    private final AlertEventStore store;

    public QueryAlertEventsUseCase(AlertEventStore store) {
        this.store = store;
    }

    @Override
    public List<AlertEvent> query(String tenantId, String ruleId, Instant from, Instant to, int limit) {
        int safeLimit = Math.min(Math.max(limit, 1), 500);
        return store.query(tenantId, ruleId, from, to, safeLimit);
    }
}
