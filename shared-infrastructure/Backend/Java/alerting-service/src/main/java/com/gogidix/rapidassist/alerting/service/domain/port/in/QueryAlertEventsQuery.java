package com.gogidix.rapidassist.alerting.service.domain.port.in;

import com.gogidix.rapidassist.alerting.service.domain.model.AlertEvent;

import java.time.Instant;
import java.util.List;

public interface QueryAlertEventsQuery {

    List<AlertEvent> query(String tenantId, String ruleId, Instant from, Instant to, int limit);
}
