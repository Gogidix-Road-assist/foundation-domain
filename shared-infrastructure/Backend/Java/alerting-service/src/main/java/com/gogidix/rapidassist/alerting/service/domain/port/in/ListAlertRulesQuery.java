package com.gogidix.rapidassist.alerting.service.domain.port.in;

import com.gogidix.rapidassist.alerting.service.domain.model.AlertRule;

import java.util.List;

public interface ListAlertRulesQuery {

    List<AlertRule> list(String tenantId, int limit);
}
