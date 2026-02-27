package com.gogidix.rapidassist.request.routing.service.domain.port.out;

import com.gogidix.rapidassist.request.routing.service.domain.model.RoutingRule;

import java.util.Optional;

public interface RoutingRuleStore {

    void upsert(RoutingRule rule);

    void delete(String tenantId, String routeKey);

    Optional<RoutingRule> find(String tenantId, String routeKey);
}
