package com.gogidix.rapidassist.request.routing.service.application.usecase;

import com.gogidix.rapidassist.request.routing.service.domain.port.in.DeleteRoutingRuleCommand;
import com.gogidix.rapidassist.request.routing.service.domain.port.out.RoutingRuleStore;
import org.springframework.stereotype.Service;

@Service
public class DeleteRoutingRuleUseCase implements DeleteRoutingRuleCommand {

    private final RoutingRuleStore store;

    public DeleteRoutingRuleUseCase(RoutingRuleStore store) {
        this.store = store;
    }

    @Override
    public void delete(String tenantId, String routeKey) {
        store.delete(tenantId, routeKey);
    }
}
