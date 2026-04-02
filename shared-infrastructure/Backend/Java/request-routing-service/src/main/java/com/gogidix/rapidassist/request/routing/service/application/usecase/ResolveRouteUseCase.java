package com.gogidix.rapidassist.request.routing.service.application.usecase;

import com.gogidix.rapidassist.request.routing.service.domain.port.in.ResolveRouteQuery;
import com.gogidix.rapidassist.request.routing.service.domain.port.out.RoutingRuleStore;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ResolveRouteUseCase implements ResolveRouteQuery {

    private final RoutingRuleStore store;

    public ResolveRouteUseCase(RoutingRuleStore store) {
        this.store = store;
    }

    @Override
    public Optional<String> resolveDestinationBaseUrl(String tenantId, String routeKey) {
        return store.find(tenantId, routeKey).map(r -> r.destinationBaseUrl());
    }
}
