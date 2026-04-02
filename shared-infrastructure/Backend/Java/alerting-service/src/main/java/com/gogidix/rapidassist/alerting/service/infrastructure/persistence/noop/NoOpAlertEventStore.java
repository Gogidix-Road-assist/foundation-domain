package com.gogidix.rapidassist.alerting.service.infrastructure.persistence.noop;

import com.gogidix.rapidassist.alerting.service.domain.model.AlertEvent;
import com.gogidix.rapidassist.alerting.service.domain.port.out.AlertEventStore;

import java.time.Instant;
import java.util.List;

public class NoOpAlertEventStore implements AlertEventStore {

    @Override
    public void append(AlertEvent event, Instant expiresAt) {
    }

    @Override
    public List<AlertEvent> query(String tenantId, String ruleId, Instant from, Instant to, int limit) {
        return List.of();
    }
}
