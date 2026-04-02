package com.gogidix.rapidassist.alerting.service.application;

import com.gogidix.rapidassist.alerting.service.domain.model.AlertRule;
import com.gogidix.rapidassist.alerting.service.domain.port.in.UpsertAlertRuleCommand;
import com.gogidix.rapidassist.alerting.service.domain.port.out.AlertRuleStore;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UpsertAlertRuleUseCase implements UpsertAlertRuleCommand {

    private final AlertRuleStore store;
    private final AlertingRetentionProperties properties;

    public UpsertAlertRuleUseCase(AlertRuleStore store, AlertingRetentionProperties properties) {
        this.store = store;
        this.properties = properties;
    }

    @Override
    public void upsert(AlertRule rule) {
        Instant createdAt = rule.createdAt() == null ? Instant.now() : rule.createdAt();
        Instant expiresAt = createdAt.plus(properties.getRulesRetention());

        AlertRule normalized = new AlertRule(
                rule.tenantId(),
                rule.ruleId(),
                rule.name(),
                rule.severity(),
                rule.enabled(),
                rule.conditions(),
                createdAt
        );

        store.upsert(normalized, expiresAt);
    }
}
