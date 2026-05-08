package com.gogidix.rapidassist.alerting.service.domain.port.out;

import com.gogidix.rapidassist.alerting.service.domain.model.AlertEvent;

import java.time.Instant;
import java.util.List;

public interface AlertEventStore {

    void append(AlertEvent event, Instant expiresAt);

    List<AlertEvent> query(String tenantId, String ruleId, Instant from, Instant to, int limit);
}
