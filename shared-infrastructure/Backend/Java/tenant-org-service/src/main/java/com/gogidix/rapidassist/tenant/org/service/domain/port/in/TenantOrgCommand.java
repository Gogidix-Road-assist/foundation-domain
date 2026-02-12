package com.gogidix.rapidassist.tenant.org.service.domain.port.in;

import com.gogidix.rapidassist.tenant.org.service.domain.model.TenantOrganization;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface TenantOrgCommand {

    CompletableFuture<TenantOrganization> createOrganization(CreateOrganizationCommand command);

    CompletableFuture<Optional<TenantOrganization>> updateOrganization(String tenantId, String orgId, UpdateOrganizationCommand command);

    CompletableFuture<Optional<TenantOrganization>> deleteOrganization(String tenantId, String orgId);

    CompletableFuture<Optional<TenantOrganization>> updateOrganizationStatus(
            String tenantId, String orgId, TenantOrganization.OrganizationStatus status);

    CompletableFuture<Optional<TenantOrganization>> updateOrganizationPlan(
            String tenantId, String orgId, TenantOrganization.SubscriptionPlan plan);

    CompletableFuture<Optional<TenantOrganization>> updateOrganizationSettings(
            String tenantId, String orgId, TenantOrganization.OrganizationSettings settings);

    CompletableFuture<Boolean> deactivateOrganization(String tenantId, String orgId);

    CompletableFuture<Boolean> reactivateOrganization(String tenantId, String orgId);

    CompletableFuture<List<TenantOrganization>> bulkCreateOrganizations(BulkCreateOrganizationsCommand command);

    record CreateOrganizationCommand(
            String tenantId,
            String orgId,
            String orgName,
            TenantOrganization.OrganizationType orgType,
            TenantOrganization.SubscriptionPlan plan,
            TenantOrganization.OrganizationSettings settings,
            Map<String, String> metadata,
            TenantOrganization.BillingInfo billingInfo,
            List<TenantOrganization.ContactInfo> contacts,
            String parentOrgId,
            Set<String> tags,
            Map<String, String> customAttributes,
            TenantOrganization.ComplianceInfo complianceInfo,
            String createdBy
    ) {}

    record UpdateOrganizationCommand(
            String orgName,
            TenantOrganization.OrganizationType orgType,
            TenantOrganization.SubscriptionPlan plan,
            TenantOrganization.OrganizationSettings settings,
            Map<String, String> metadata,
            TenantOrganization.BillingInfo billingInfo,
            List<TenantOrganization.ContactInfo> contacts,
            Set<String> tags,
            Map<String, String> customAttributes,
            TenantOrganization.ComplianceInfo complianceInfo,
            String updatedBy
    ) {}

    record BulkCreateOrganizationsCommand(
            String tenantId,
            List<CreateOrganizationCommand> organizations
    ) {}
}
