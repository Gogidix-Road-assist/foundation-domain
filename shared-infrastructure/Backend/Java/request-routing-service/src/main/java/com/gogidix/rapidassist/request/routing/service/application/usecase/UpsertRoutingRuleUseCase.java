package com.gogidix.rapidassist.request.routing.service.application.usecase;

import com.gogidix.rapidassist.request.routing.service.domain.model.RoutingRule;
import com.gogidix.rapidassist.request.routing.service.domain.port.in.UpsertRoutingRuleCommand;
import com.gogidix.rapidassist.request.routing.service.domain.port.out.RoutingRuleStore;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UpsertRoutingRuleUseCase implements UpsertRoutingRuleCommand {

    private final RoutingRuleStore store;

    public UpsertRoutingRuleUseCase(RoutingRuleStore store) {
        this.store = store;
    }

    @Override
    public void upsert(String tenantId, String routeKey, String destinationBaseUrl) {
        store.upsert(new RoutingRule(tenantId, routeKey, destinationBaseUrl, Instant.now()));
    }
}
