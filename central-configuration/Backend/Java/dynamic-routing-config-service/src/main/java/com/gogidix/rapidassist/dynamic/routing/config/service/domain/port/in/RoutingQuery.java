package com.gogidix.rapidassist.dynamic.routing.config.service.domain.port.in;

import com.gogidix.rapidassist.dynamic.routing.config.service.domain.model.RoutingRule;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Input port for routing query operations
 */
public interface RoutingQuery {

    CompletableFuture<Optional<RoutingRule>> getRuleById(String ruleId);
    CompletableFuture<List<RoutingRule>> getRulesByTenant(String tenantId);
    CompletableFuture<List<RoutingRule>> getRulesByEnvironment(String tenantId, String environment);
    CompletableFuture<List<RoutingRule>> getActiveRules(String tenantId, String environment);
    CompletableFuture<List<RoutingRule>> getRulesByPriorityRange(String tenantId, int min, int max);

    CompletableFuture<List<RoutingRule>> findMatchingRules(String tenantId, String path,
                                                           Map<String, String> headers,
                                                           Map<String, String> queryParams);

    CompletableFuture<Optional<RoutingRule>> findHighestPriorityMatch(String tenantId, String path,
                                                                      Map<String, String> headers,
                                                                      Map<String, String> queryParams);
}
