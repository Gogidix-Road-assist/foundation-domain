package com.gogidix.rapidassist.request.routing.service.infrastructure.persistence.memory;

import com.gogidix.rapidassist.request.routing.service.domain.model.RoutingRule;
import com.gogidix.rapidassist.request.routing.service.domain.port.out.RoutingRuleStore;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryRoutingRuleStore implements RoutingRuleStore {

    private final Map<String, Map<String, RoutingRule>> data = new ConcurrentHashMap<>();

    @Override
    public void upsert(RoutingRule rule) {
        data.computeIfAbsent(rule.tenantId(), t -> new ConcurrentHashMap<>())
                .put(rule.routeKey(), rule);
    }

    @Override
    public void delete(String tenantId, String routeKey) {
        Map<String, RoutingRule> byTenant = data.get(tenantId);
        if (byTenant == null) {
            return;
        }
        byTenant.remove(routeKey);
    }

    @Override
    public Optional<RoutingRule> find(String tenantId, String routeKey) {
        Map<String, RoutingRule> byTenant = data.get(tenantId);
        if (byTenant == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(byTenant.get(routeKey));
    }
}
