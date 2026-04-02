package com.gogidix.rapidassist.tenant.org.service.domain.port.in;

import com.gogidix.rapidassist.tenant.org.service.domain.model.TenantOrganization;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface TenantOrgQuery {

    CompletableFuture<Optional<TenantOrganization>> getOrganization(String tenantId, String orgId);

    CompletableFuture<Optional<TenantOrganization>> getOrganizationById(String id);

    CompletableFuture<List<TenantOrganization>> listAllOrganizations(String tenantId);

    CompletableFuture<List<TenantOrganization>> listByStatus(
            String tenantId, TenantOrganization.OrganizationStatus status);

    CompletableFuture<List<TenantOrganization>> listByType(
            String tenantId, TenantOrganization.OrganizationType orgType);

    CompletableFuture<List<TenantOrganization>> listByPlan(
            String tenantId, TenantOrganization.SubscriptionPlan.PlanType planType);

    CompletableFuture<List<TenantOrganization>> searchByName(String tenantId, String keyword);

    CompletableFuture<List<TenantOrganization>> searchByTags(String tenantId, Set<String> tags);

    CompletableFuture<List<TenantOrganization>> listActiveOrganizations(String tenantId);

    CompletableFuture<List<TenantOrganization>> listChildOrganizations(String tenantId, String parentOrgId);

    CompletableFuture<List<TenantOrganization>> listByPlanExpiringBefore(
            String tenantId, java.time.Instant expiryDate);

    CompletableFuture<List<TenantOrganization>> listInactiveOrganizations(
            String tenantId, java.time.Instant since);
}
