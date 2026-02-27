package com.gogidix.rapidassist.tenant.org.service.domain.port.out;

import com.gogidix.rapidassist.tenant.org.service.domain.model.TenantOrganization;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface TenantOrganizationStore {

    CompletableFuture<TenantOrganization> save(TenantOrganization organization);

    CompletableFuture<Optional<TenantOrganization>> findById(String id);

    CompletableFuture<Optional<TenantOrganization>> findByTenantAndOrg(String tenantId, String orgId);

    CompletableFuture<List<TenantOrganization>> findByTenant(String tenantId);

    CompletableFuture<List<TenantOrganization>> findByTenantAndStatus(
            String tenantId, TenantOrganization.OrganizationStatus status);

    CompletableFuture<List<TenantOrganization>> findByTenantAndType(
            String tenantId, TenantOrganization.OrganizationType orgType);

    CompletableFuture<List<TenantOrganization>> findByTenantAndPlanType(
            String tenantId, TenantOrganization.SubscriptionPlan.PlanType planType);

    CompletableFuture<List<TenantOrganization>> searchByName(
            String tenantId, String keyword);

    CompletableFuture<List<TenantOrganization>> findByTags(
            String tenantId, Set<String> tags);

    CompletableFuture<List<TenantOrganization>> findActiveByTenant(String tenantId);

    CompletableFuture<List<TenantOrganization>> findByParentOrg(
            String tenantId, String parentOrgId);

    CompletableFuture<List<TenantOrganization>> findByPlanExpiringBefore(
            String tenantId, Instant expiryDate);

    CompletableFuture<List<TenantOrganization>> findInactiveSince(
            String tenantId, Instant since);

    CompletableFuture<Boolean> deleteById(String id);

    CompletableFuture<Boolean> deleteByTenantAndOrg(String tenantId, String orgId);

    CompletableFuture<Long> countByTenant(String tenantId);

    CompletableFuture<Long> countByTenantAndStatus(
            String tenantId, TenantOrganization.OrganizationStatus status);
}
