package com.gogidix.rapidassist.policy.configuration.service.application;

import com.gogidix.rapidassist.policy.configuration.service.domain.model.Policy;
import com.gogidix.rapidassist.policy.configuration.service.domain.port.in.PolicyCommand;
import com.gogidix.rapidassist.policy.configuration.service.domain.port.in.PolicyQuery;
import com.gogidix.rapidassist.policy.configuration.service.domain.port.out.PolicyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
public class PolicyService implements PolicyCommand, PolicyQuery {

    private static final Logger logger = LoggerFactory.getLogger(PolicyService.class);

    @Autowired
    private PolicyRepository repository;

    @Override
    public CompletableFuture<Policy> createPolicy(CreatePolicyCommand command) {
        return CompletableFuture.supplyAsync(() -> {
            Policy policy = Policy.builder()
                .tenantId(command.tenantId())
                .policyKey(command.policyKey())
                .name(command.name())
                .description(command.description())
                .type(command.type())
                .scope(command.scope())
                .rules(command.rules())
                .constraints(command.constraints())
                .priority(command.priority())
                .environment(command.environment())
                .tags(command.tags())
                .enforced(true)
                .status(Policy.PolicyStatus.DRAFT)
                .createdBy(command.createdBy())
                .build();

            Policy saved = repository.save(policy).join();
            repository.cachePolicy(saved).join();

            logger.info("Created policy: {} for tenant: {}", command.policyKey(), command.tenantId());
            return saved;
        });
    }

    @Override
    public CompletableFuture<Optional<Policy>> updatePolicy(String policyId, UpdatePolicyCommand command) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Policy> existing = repository.findById(policyId).join();
            if (existing.isEmpty()) return Optional.empty();

            Policy current = existing.get();
            Policy updated = Policy.builder()
                .id(current.id())
                .tenantId(current.tenantId())
                .policyKey(current.policyKey())
                .name(command.name())
                .description(command.description())
                .type(current.type())
                .scope(command.scope())
                .rules(command.rules())
                .constraints(command.constraints())
                .priority(command.priority())
                .environment(current.environment())
                .status(current.status())
                .enforced(current.enforced())
                .tags(current.tags())
                .createdBy(current.createdBy())
                .createdAt(current.createdAt())
                .updatedBy(command.updatedBy())
                .version(current.version() + 1)
                .build();

            Policy saved = repository.save(updated).join();
            repository.cachePolicy(saved).join();

            logger.info("Updated policy: {}", policyId);
            return Optional.of(saved);
        });
    }

    @Override
    public CompletableFuture<Optional<Policy>> activatePolicy(String policyId, String updatedBy) {
        return updateStatus(policyId, Policy.PolicyStatus.ACTIVE, updatedBy);
    }

    @Override
    public CompletableFuture<Optional<Policy>> deactivatePolicy(String policyId, String updatedBy) {
        return updateStatus(policyId, Policy.PolicyStatus.INACTIVE, updatedBy);
    }

    @Override
    public CompletableFuture<Optional<Policy>> enforcePolicy(String policyId, boolean enforced, String updatedBy) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Policy> existing = repository.findById(policyId).join();
            if (existing.isEmpty()) return Optional.empty();

            Policy current = existing.get();
            Policy updated = Policy.builder()
                .id(current.id())
                .tenantId(current.tenantId())
                .policyKey(current.policyKey())
                .name(current.name())
                .description(current.description())
                .type(current.type())
                .scope(current.scope())
                .rules(current.rules())
                .constraints(current.constraints())
                .priority(current.priority())
                .environment(current.environment())
                .status(current.status())
                .enforced(enforced)
                .tags(current.tags())
                .createdBy(current.createdBy())
                .createdAt(current.createdAt())
                .updatedBy(updatedBy)
                .version(current.version() + 1)
                .build();

            Policy saved = repository.save(updated).join();
            repository.cachePolicy(saved).join();

            logger.info("{} policy: {}", enforced ? "Enforced" : "Unenforced", policyId);
            return Optional.of(saved);
        });
    }

    @Override
    public CompletableFuture<Boolean> deletePolicy(String policyId) {
        return CompletableFuture.supplyAsync(() -> {
            boolean deleted = repository.deleteById(policyId).join();
            repository.evictPolicy(policyId).join();
            logger.info("Deleted policy: {}", policyId);
            return deleted;
        });
    }

    @Override
    public CompletableFuture<List<Policy>> bulkUpdatePolicies(BulkUpdateCommand command) {
        return CompletableFuture.supplyAsync(() -> {
            List<Policy> results = command.policyIds().stream()
                .map(id -> enforcePolicy(id, command.enforced(), command.updatedBy()).join())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
            logger.info("Bulk updated {} policies", results.size());
            return results;
        });
    }

    private CompletableFuture<Optional<Policy>> updateStatus(String policyId, Policy.PolicyStatus status, String updatedBy) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Policy> existing = repository.findById(policyId).join();
            if (existing.isEmpty()) return Optional.empty();

            Policy current = existing.get();
            Policy updated = Policy.builder()
                .id(current.id())
                .tenantId(current.tenantId())
                .policyKey(current.policyKey())
                .name(current.name())
                .description(current.description())
                .type(current.type())
                .scope(current.scope())
                .rules(current.rules())
                .constraints(current.constraints())
                .priority(current.priority())
                .environment(current.environment())
                .status(status)
                .enforced(current.enforced())
                .tags(current.tags())
                .createdBy(current.createdBy())
                .createdAt(current.createdAt())
                .updatedBy(updatedBy)
                .version(current.version() + 1)
                .build();

            Policy saved = repository.save(updated).join();
            repository.cachePolicy(saved).join();

            logger.info("{} policy: {}", status, policyId);
            return Optional.of(saved);
        });
    }

    @Override
    public CompletableFuture<Optional<Policy>> getPolicyById(String policyId) {
        return repository.findById(policyId);
    }

    @Override
    public CompletableFuture<Optional<Policy>> getPolicyByKey(String tenantId, String policyKey) {
        return repository.findByKey(tenantId, policyKey);
    }

    @Override
    public CompletableFuture<List<Policy>> getPoliciesByTenant(String tenantId) {
        return repository.findByTenant(tenantId);
    }

    @Override
    public CompletableFuture<List<Policy>> getPoliciesByType(String tenantId, Policy.PolicyType type) {
        return repository.findByType(tenantId, type);
    }

    @Override
    public CompletableFuture<List<Policy>> getActivePolicies(String tenantId, String environment) {
        return repository.findActive(tenantId, environment);
    }

    @Override
    public CompletableFuture<List<Policy>> getEnforcedPolicies(String tenantId) {
        return repository.findEnforced(tenantId);
    }

    @Override
    public CompletableFuture<List<Policy>> getPoliciesByTags(String tenantId, Set<String> tags) {
        return repository.findByTags(tenantId, tags);
    }

    @Override
    public CompletableFuture<List<Policy>> findApplicablePolicies(String tenantId, String entityType,
                                                                   String entityId, String environment) {
        return getActivePolicies(tenantId, environment)
            .thenApply(policies -> policies.stream()
                .filter(p -> p.appliesTo(entityType, entityId))
                .sorted(Comparator.comparingInt(Policy::priority).reversed())
                .toList());
    }

    @Override
    public CompletableFuture<Optional<Policy>> findHighestPriorityPolicy(String tenantId, String entityType,
                                                                        String entityId, String environment) {
        return findApplicablePolicies(tenantId, entityType, entityId, environment)
            .thenApply(policies -> policies.stream().findFirst());
    }
}
