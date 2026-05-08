package com.gogidix.rapidassist.alerting.service.domain.port.in;

import com.gogidix.rapidassist.alerting.service.domain.model.AlertRule;

public interface UpsertAlertRuleCommand {

    void upsert(AlertRule rule);
}
