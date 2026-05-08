package com.gogidix.rapidassist.alerting.service.domain.port.out;

import com.gogidix.rapidassist.alerting.service.domain.model.AlertRule;

import java.time.Instant;
import java.util.List;

public interface AlertRuleStore {

    void upsert(AlertRule rule, Instant expiresAt);

    List<AlertRule> list(String tenantId, int limit);
}
