package com.gogidix.rapidassist.alerting.service.application;

import com.gogidix.rapidassist.alerting.service.domain.model.AlertRule;
import com.gogidix.rapidassist.alerting.service.domain.port.in.ListAlertRulesQuery;
import com.gogidix.rapidassist.alerting.service.domain.port.out.AlertRuleStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListAlertRulesUseCase implements ListAlertRulesQuery {

    private final AlertRuleStore store;

    public ListAlertRulesUseCase(AlertRuleStore store) {
        this.store = store;
    }

    @Override
    public List<AlertRule> list(String tenantId, int limit) {
        int safeLimit = Math.min(Math.max(limit, 1), 500);
        return store.list(tenantId, safeLimit);
    }
}
