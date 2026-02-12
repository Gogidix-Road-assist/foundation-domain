package com.gogidix.rapidassist.dynamic.routing.config.service.application;

import com.gogidix.rapidassist.dynamic.routing.config.service.domain.model.RoutingRule;
import com.gogidix.rapidassist.dynamic.routing.config.service.domain.port.in.RoutingCommand;
import com.gogidix.rapidassist.dynamic.routing.config.service.domain.port.in.RoutingQuery;
import com.gogidix.rapidassist.dynamic.routing.config.service.domain.port.out.RoutingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
public class RoutingService implements RoutingCommand, RoutingQuery {

    private static final Logger logger = LoggerFactory.getLogger(RoutingService.class);

    @Autowired
    private RoutingRepository repository;

    // Command implementation

    @Override
    public CompletableFuture<RoutingRule> createRule(CreateRuleCommand command) {
        return CompletableFuture.supplyAsync(() -> {
            RoutingRule rule = RoutingRule.builder()
                .tenantId(command.tenantId())
                .ruleName(command.ruleName())
                .pattern(command.pattern())
                .target(command.target())
                .strategy(command.strategy())
                .conditions(command.conditions())
                .config(command.config())
                .priority(command.priority())
                .environment(command.environment())
                .active(true)
                .createdBy(command.createdBy())
                .build();

            RoutingRule saved = repository.save(rule).join();
            repository.cacheRule(saved).join();

            logger.info("Created routing rule: {} for tenant: {}", command.ruleName(), command.tenantId());
            return saved;
        });
    }

    @Override
    public CompletableFuture<Optional<RoutingRule>> updateRule(String ruleId, UpdateRuleCommand command) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<RoutingRule> existing = repository.findById(ruleId).join();
            if (existing.isEmpty()) {
                return Optional.empty();
            }

            RoutingRule current = existing.get();

            RoutingRule updated = RoutingRule.builder()
                .id(current.id())
                .tenantId(current.tenantId())
                .ruleName(command.ruleName())
                .pattern(command.pattern())
                .target(command.target())
                .strategy(command.strategy())
                .conditions(command.conditions())
                .config(command.config())
                .priority(command.priority())
                .environment(current.environment())
                .active(current.active())
                .createdBy(current.createdBy())
                .createdAt(current.createdAt())
                .updatedBy(command.updatedBy())
                .version(current.version() + 1)
                .build();

            RoutingRule saved = repository.save(updated).join();
            repository.cacheRule(saved).join();

            logger.info("Updated routing rule: {}", ruleId);
            return Optional.of(saved);
        });
    }

    @Override
    public CompletableFuture<Optional<RoutingRule>> activateRule(String ruleId, String updatedBy) {
        return updateActiveStatus(ruleId, true, updatedBy);
    }

    @Override
    public CompletableFuture<Optional<RoutingRule>> deactivateRule(String ruleId, String updatedBy) {
        return updateActiveStatus(ruleId, false, updatedBy);
    }

    private CompletableFuture<Optional<RoutingRule>> updateActiveStatus(String ruleId, boolean active, String updatedBy) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<RoutingRule> existing = repository.findById(ruleId).join();
            if (existing.isEmpty()) {
                return Optional.empty();
            }

            RoutingRule current = existing.get();

            RoutingRule updated = RoutingRule.builder()
                .id(current.id())
                .tenantId(current.tenantId())
                .ruleName(current.ruleName())
                .pattern(current.pattern())
                .target(current.target())
                .strategy(current.strategy())
                .conditions(current.conditions())
                .config(current.config())
                .priority(current.priority())
                .environment(current.environment())
                .active(active)
                .createdBy(current.createdBy())
                .createdAt(current.createdAt())
                .updatedBy(updatedBy)
                .version(current.version() + 1)
                .build();

            RoutingRule saved = repository.save(updated).join();

            if (!active) {
                repository.evictRule(ruleId).join();
            } else {
                repository.cacheRule(saved).join();
            }

            logger.info("{} routing rule: {}", active ? "Activated" : "Deactivated", ruleId);
            return Optional.of(saved);
        });
    }

    @Override
    public CompletableFuture<Boolean> deleteRule(String ruleId) {
        return CompletableFuture.supplyAsync(() -> {
            boolean deleted = repository.deleteById(ruleId).join();
            repository.evictRule(ruleId).join();
            logger.info("Deleted routing rule: {}", ruleId);
            return deleted;
        });
    }

    @Override
    public CompletableFuture<List<RoutingRule>> bulkUpdatePriority(BulkPriorityCommand command) {
        return CompletableFuture.supplyAsync(() -> {
            List<RoutingRule> results = new java.util.ArrayList<>();
            for (int i = 0; i < command.ruleIds().size() && i < command.priorities().size(); i++) {
                String ruleId = command.ruleIds().get(i);
                int priority = command.priorities().get(i);

                Optional<RoutingRule> existing = repository.findById(ruleId).join();
                if (existing.isPresent()) {
                    RoutingRule current = existing.get();
                    RoutingRule updated = RoutingRule.builder()
                        .id(current.id())
                        .tenantId(current.tenantId())
                        .ruleName(current.ruleName())
                        .pattern(current.pattern())
                        .target(current.target())
                        .strategy(current.strategy())
                        .conditions(current.conditions())
                        .config(current.config())
                        .priority(priority)
                        .environment(current.environment())
                        .active(current.active())
                        .createdBy(current.createdBy())
                        .createdAt(current.createdAt())
                        .updatedBy(command.updatedBy())
                        .version(current.version() + 1)
                        .build();
                    results.add(repository.save(updated).join());
                }
            }

            // Evict all rules for the tenant
            if (!results.isEmpty()) {
                repository.evictAll(results.get(0).tenantId()).join();
            }

            logger.info("Bulk updated priorities for {} rules", results.size());
            return results;
        });
    }

    @Override
    public CompletableFuture<List<RoutingRule>> bulkActivate(List<String> ruleIds, String updatedBy) {
        return CompletableFuture.supplyAsync(() -> {
            List<RoutingRule> results = ruleIds.stream()
                .map(id -> activateRule(id, updatedBy).join())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
            logger.info("Bulk activated {} routing rules", results.size());
            return results;
        });
    }

    @Override
    public CompletableFuture<List<RoutingRule>> bulkDeactivate(List<String> ruleIds, String updatedBy) {
        return CompletableFuture.supplyAsync(() -> {
            List<RoutingRule> results = ruleIds.stream()
                .map(id -> deactivateRule(id, updatedBy).join())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
            logger.info("Bulk deactivated {} routing rules", results.size());
            return results;
        });
    }

    // Query implementation

    @Override
    public CompletableFuture<Optional<RoutingRule>> getRuleById(String ruleId) {
        return repository.findById(ruleId);
    }

    @Override
    public CompletableFuture<List<RoutingRule>> getRulesByTenant(String tenantId) {
        return repository.findByTenant(tenantId);
    }

    @Override
    public CompletableFuture<List<RoutingRule>> getRulesByEnvironment(String tenantId, String environment) {
        return repository.findByTenantAndEnvironment(tenantId, environment);
    }

    @Override
    public CompletableFuture<List<RoutingRule>> getActiveRules(String tenantId, String environment) {
        return repository.findActiveRules(tenantId, environment);
    }

    @Override
    public CompletableFuture<List<RoutingRule>> getRulesByPriorityRange(String tenantId, int min, int max) {
        return repository.findByPriorityRange(tenantId, min, max);
    }

    @Override
    public CompletableFuture<List<RoutingRule>> findMatchingRules(String tenantId, String path,
                                                                  Map<String, String> headers,
                                                                  Map<String, String> queryParams) {
        return getActiveRules(tenantId, "production")
            .thenApply(rules -> rules.stream()
                .filter(rule -> rule.matches(path, headers, queryParams))
                .sorted(Comparator.comparingInt(RoutingRule::priority).reversed())
                .toList());
    }

    @Override
    public CompletableFuture<Optional<RoutingRule>> findHighestPriorityMatch(String tenantId, String path,
                                                                             Map<String, String> headers,
                                                                             Map<String, String> queryParams) {
        return findMatchingRules(tenantId, path, headers, queryParams)
            .thenApply(rules -> rules.stream().findFirst());
    }
}
