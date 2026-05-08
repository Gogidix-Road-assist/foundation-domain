package com.gogidix.rapidassist.alerting.service.infrastructure.persistence.noop;

import com.gogidix.rapidassist.alerting.service.domain.model.AlertRule;
import com.gogidix.rapidassist.alerting.service.domain.port.out.AlertRuleStore;

import java.time.Instant;
import java.util.List;

public class NoOpAlertRuleStore implements AlertRuleStore {

    @Override
    public void upsert(AlertRule rule, Instant expiresAt) {
    }

    @Override
    public List<AlertRule> list(String tenantId, int limit) {
        return List.of();
    }
}
