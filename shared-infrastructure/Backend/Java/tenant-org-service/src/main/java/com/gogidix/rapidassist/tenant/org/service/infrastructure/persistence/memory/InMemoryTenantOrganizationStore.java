package com.gogidix.rapidassist.tenant.org.service.infrastructure.persistence.memory;

import com.gogidix.rapidassist.tenant.org.service.domain.model.TenantOrganization;
import com.gogidix.rapidassist.tenant.org.service.domain.port.out.TenantOrganizationStore;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Deprecated
public class InMemoryTenantOrganizationStore implements TenantOrganizationStore {

    private final ConcurrentHashMap<String, ConcurrentHashMap<String, TenantOrganization>> byTenant = new ConcurrentHashMap<>();

    @Override
    public CompletableFuture<TenantOrganization> save(TenantOrganization org) {
        return CompletableFuture.supplyAsync(() -> {
            byTenant.computeIfAbsent(org.tenantId(), k -> new ConcurrentHashMap<>()).put(org.orgId(), org);
            return org;
        });
    }

    @Override
    public CompletableFuture<Optional<TenantOrganization>> findById(String id) {
        return CompletableFuture.supplyAsync(() -> {
            for (var tenantMap : byTenant.values()) {
                for (var org : tenantMap.values()) {
                    if (org.id() != null && org.id().equals(id)) {
                        return Optional.of(org);
                    }
                }
            }
            return Optional.empty();
        });
    }

    @Override
    public CompletableFuture<Optional<TenantOrganization>> findByTenantAndOrg(String tenantId, String orgId) {
        return CompletableFuture.supplyAsync(() -> {
            var map = byTenant.get(tenantId);
            if (map == null) {
                return Optional.empty();
            }
            return Optional.ofNullable(map.get(orgId));
        });
    }

    @Override
    public CompletableFuture<List<TenantOrganization>> findByTenant(String tenantId) {
        return CompletableFuture.supplyAsync(() -> {
            var map = byTenant.get(tenantId);
            if (map == null) {
                return List.of();
            }
            return new ArrayList<>(map.values());
        });
    }

    @Override
    public CompletableFuture<List<TenantOrganization>> findByTenantAndStatus(String tenantId, TenantOrganization.OrganizationStatus status) {
        return findByTenant(tenantId).thenApply(orgs ->
                orgs.stream().filter(o -> o.status() == status).toList()
        );
    }

    @Override
    public CompletableFuture<List<TenantOrganization>> findByTenantAndType(String tenantId, TenantOrganization.OrganizationType orgType) {
        return findByTenant(tenantId).thenApply(orgs ->
                orgs.stream().filter(o -> o.orgType() == orgType).toList()
        );
    }

    @Override
    public CompletableFuture<List<TenantOrganization>> findByTenantAndPlanType(String tenantId, TenantOrganization.SubscriptionPlan.PlanType planType) {
        return findByTenant(tenantId).thenApply(orgs ->
                orgs.stream().filter(o -> o.plan() != null && o.plan().planType() == planType).toList()
        );
    }

    @Override
    public CompletableFuture<List<TenantOrganization>> searchByName(String tenantId, String keyword) {
        return findByTenant(tenantId).thenApply(orgs ->
                orgs.stream().filter(o -> o.orgName().toLowerCase().contains(keyword.toLowerCase())).toList()
        );
    }

    @Override
    public CompletableFuture<List<TenantOrganization>> findByTags(String tenantId, Set<String> tags) {
        return findByTenant(tenantId).thenApply(orgs ->
                orgs.stream().filter(o -> o.tags() != null && o.tags().stream().anyMatch(tags::contains)).toList()
        );
    }

    @Override
    public CompletableFuture<List<TenantOrganization>> findActiveByTenant(String tenantId) {
        return findByTenantAndStatus(tenantId, TenantOrganization.OrganizationStatus.ACTIVE);
    }

    @Override
    public CompletableFuture<List<TenantOrganization>> findByParentOrg(String tenantId, String parentOrgId) {
        return findByTenant(tenantId).thenApply(orgs ->
                orgs.stream().filter(o -> parentOrgId.equals(o.parentOrgId())).toList()
        );
    }

    @Override
    public CompletableFuture<List<TenantOrganization>> findByPlanExpiringBefore(String tenantId, Instant expiryDate) {
        return findByTenant(tenantId).thenApply(orgs ->
                orgs.stream().filter(o -> o.plan() != null && o.plan().endDate() != null && o.plan().endDate().isBefore(expiryDate)).toList()
        );
    }

    @Override
    public CompletableFuture<List<TenantOrganization>> findInactiveSince(String tenantId, Instant since) {
        return findByTenant(tenantId).thenApply(orgs ->
                orgs.stream().filter(o -> o.lastActiveAt() != null && o.lastActiveAt().isBefore(since)).toList()
        );
    }

    @Override
    public CompletableFuture<Boolean> deleteById(String id) {
        return CompletableFuture.supplyAsync(() -> {
            for (var tenantMap : byTenant.values()) {
                for (var entry : tenantMap.entrySet()) {
                    if (entry.getValue().id() != null && entry.getValue().id().equals(id)) {
                        tenantMap.remove(entry.getKey());
                        return true;
                    }
                }
            }
            return false;
        });
    }

    @Override
    public CompletableFuture<Boolean> deleteByTenantAndOrg(String tenantId, String orgId) {
        return CompletableFuture.supplyAsync(() -> {
            var map = byTenant.get(tenantId);
            if (map != null) {
                return map.remove(orgId) != null;
            }
            return false;
        });
    }

    @Override
    public CompletableFuture<Long> countByTenant(String tenantId) {
        return findByTenant(tenantId).thenApply(list -> (long) list.size());
    }

    @Override
    public CompletableFuture<Long> countByTenantAndStatus(String tenantId, TenantOrganization.OrganizationStatus status) {
        return findByTenantAndStatus(tenantId, status).thenApply(list -> (long) list.size());
    }
}
