package com.gogidix.rapidassist.tenant.org.service.application.service;

import com.gogidix.rapidassist.tenant.org.service.domain.model.TenantOrganization;
import com.gogidix.rapidassist.tenant.org.service.domain.port.in.TenantOrgCommand;
import com.gogidix.rapidassist.tenant.org.service.domain.port.in.TenantOrgQuery;
import com.gogidix.rapidassist.tenant.org.service.domain.port.out.TenantOrganizationCacheStore;
import com.gogidix.rapidassist.tenant.org.service.domain.port.out.TenantOrganizationStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class ComprehensiveTenantOrgService implements TenantOrgCommand, TenantOrgQuery {

    private static final Logger logger = LoggerFactory.getLogger(ComprehensiveTenantOrgService.class);

    @Autowired
    private TenantOrganizationStore store;

    @Autowired
    private TenantOrganizationCacheStore cacheStore;

    @PostConstruct
    public void init() {
        logger.info("Comprehensive Tenant Organization Service initialized");
    }

    // ==================== Command Methods ====================

    @Override
    public CompletableFuture<TenantOrganization> createOrganization(CreateOrganizationCommand command) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                TenantOrganization organization = TenantOrganization.create(
                        command.tenantId(),
                        command.orgId(),
                        command.orgName(),
                        command.orgType() != null ? command.orgType() : TenantOrganization.OrganizationType.ENTERPRISE
                );

                TenantOrganization enhanced = enhanceOrganization(organization, command);

                TenantOrganization saved = store.save(enhanced).join();

                cacheStore.put(command.tenantId(), command.orgId(), saved).join();

                cacheStore.evictByTenant(command.tenantId()).join();

                logger.info("Created organization: {} for tenant: {}", command.orgId(), command.tenantId());
                return saved;

            } catch (Exception e) {
                logger.error("Error creating organization", e);
                throw new RuntimeException("Failed to create organization", e);
            }
        });
    }

    @Override
    public CompletableFuture<Optional<TenantOrganization>> updateOrganization(
            String tenantId, String orgId, UpdateOrganizationCommand command) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<TenantOrganization> existing = store.findByTenantAndOrg(tenantId, orgId).join();

                if (existing.isEmpty()) {
                    return Optional.empty();
                }

                TenantOrganization org = existing.get();
                TenantOrganization updated = mergeOrganization(org, command);

                TenantOrganization saved = store.save(updated).join();

                cacheStore.put(tenantId, orgId, saved).join();
                cacheStore.evictByTenant(tenantId).join();

                logger.info("Updated organization: {} for tenant: {}", orgId, tenantId);
                return Optional.of(saved);

            } catch (Exception e) {
                logger.error("Error updating organization", e);
                throw new RuntimeException("Failed to update organization", e);
            }
        });
    }

    @Override
    public CompletableFuture<Optional<TenantOrganization>> deleteOrganization(String tenantId, String orgId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<TenantOrganization> existing = store.findByTenantAndOrg(tenantId, orgId).join();

                if (existing.isEmpty()) {
                    return Optional.empty();
                }

                store.deleteByTenantAndOrg(tenantId, orgId).join();

                cacheStore.evict(tenantId, orgId).join();
                cacheStore.evictByTenant(tenantId).join();

                logger.info("Deleted organization: {} for tenant: {}", orgId, tenantId);
                return existing;

            } catch (Exception e) {
                logger.error("Error deleting organization", e);
                throw new RuntimeException("Failed to delete organization", e);
            }
        });
    }

    @Override
    public CompletableFuture<Optional<TenantOrganization>> updateOrganizationStatus(
            String tenantId, String orgId, TenantOrganization.OrganizationStatus status) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<TenantOrganization> existing = store.findByTenantAndOrg(tenantId, orgId).join();

                if (existing.isEmpty()) {
                    return Optional.empty();
                }

                TenantOrganization org = existing.get();
                TenantOrganization updated = org.withStatus(status);

                TenantOrganization saved = store.save(updated).join();

                cacheStore.put(tenantId, orgId, saved).join();
                cacheStore.evictByTenant(tenantId).join();

                logger.info("Updated status to {} for organization: {} of tenant: {}", status, orgId, tenantId);
                return Optional.of(saved);

            } catch (Exception e) {
                logger.error("Error updating organization status", e);
                throw new RuntimeException("Failed to update organization status", e);
            }
        });
    }

    @Override
    public CompletableFuture<Optional<TenantOrganization>> updateOrganizationPlan(
            String tenantId, String orgId, TenantOrganization.SubscriptionPlan plan) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<TenantOrganization> existing = store.findByTenantAndOrg(tenantId, orgId).join();

                if (existing.isEmpty()) {
                    return Optional.empty();
                }

                TenantOrganization org = existing.get();
                TenantOrganization updated = org.withPlan(plan);

                TenantOrganization saved = store.save(updated).join();

                cacheStore.put(tenantId, orgId, saved).join();
                cacheStore.evictByTenant(tenantId).join();

                logger.info("Updated plan for organization: {} of tenant: {}", orgId, tenantId);
                return Optional.of(saved);

            } catch (Exception e) {
                logger.error("Error updating organization plan", e);
                throw new RuntimeException("Failed to update organization plan", e);
            }
        });
    }

    @Override
    public CompletableFuture<Optional<TenantOrganization>> updateOrganizationSettings(
            String tenantId, String orgId, TenantOrganization.OrganizationSettings settings) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<TenantOrganization> existing = store.findByTenantAndOrg(tenantId, orgId).join();

                if (existing.isEmpty()) {
                    return Optional.empty();
                }

                TenantOrganization org = existing.get();
                TenantOrganization updated = org.withSettings(settings);

                TenantOrganization saved = store.save(updated).join();

                cacheStore.put(tenantId, orgId, saved).join();

                logger.info("Updated settings for organization: {} of tenant: {}", orgId, tenantId);
                return Optional.of(saved);

            } catch (Exception e) {
                logger.error("Error updating organization settings", e);
                throw new RuntimeException("Failed to update organization settings", e);
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> deactivateOrganization(String tenantId, String orgId) {
        return updateOrganizationStatus(tenantId, orgId, TenantOrganization.OrganizationStatus.INACTIVE)
                .thenApply(result -> result.isPresent());
    }

    @Override
    public CompletableFuture<Boolean> reactivateOrganization(String tenantId, String orgId) {
        return updateOrganizationStatus(tenantId, orgId, TenantOrganization.OrganizationStatus.ACTIVE)
                .thenApply(result -> result.isPresent());
    }

    @Override
    public CompletableFuture<List<TenantOrganization>> bulkCreateOrganizations(BulkCreateOrganizationsCommand command) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<TenantOrganization> results = command.organizations().stream()
                        .map(this::createOrganization)
                        .map(CompletableFuture::join)
                        .toList();

                cacheStore.evictByTenant(command.tenantId()).join();

                logger.info("Bulk created {} organizations for tenant: {}", results.size(), command.tenantId());
                return results;

            } catch (Exception e) {
                logger.error("Error bulk creating organizations", e);
                throw new RuntimeException("Failed to bulk create organizations", e);
            }
        });
    }

    // ==================== Query Methods ====================

    @Override
    public CompletableFuture<Optional<TenantOrganization>> getOrganization(String tenantId, String orgId) {
        return cacheStore.get(tenantId, orgId)
                .thenCompose(cached -> cached.isPresent()
                        ? CompletableFuture.completedFuture(cached)
                        : store.findByTenantAndOrg(tenantId, orgId)
                                .thenApply(orgOpt -> {
                                        orgOpt.ifPresent(org -> {
                                                cacheStore.put(tenantId, orgId, org);
                                        });
                                        return orgOpt;
                                }));
    }

    @Override
    public CompletableFuture<Optional<TenantOrganization>> getOrganizationById(String id) {
        return store.findById(id);
    }

    @Override
    public CompletableFuture<List<TenantOrganization>> listAllOrganizations(String tenantId) {
        return store.findByTenant(tenantId);
    }

    @Override
    public CompletableFuture<List<TenantOrganization>> listByStatus(
            String tenantId, TenantOrganization.OrganizationStatus status) {
        return store.findByTenantAndStatus(tenantId, status);
    }

    @Override
    public CompletableFuture<List<TenantOrganization>> listByType(
            String tenantId, TenantOrganization.OrganizationType orgType) {
        return store.findByTenantAndType(tenantId, orgType);
    }

    @Override
    public CompletableFuture<List<TenantOrganization>> listByPlan(
            String tenantId, TenantOrganization.SubscriptionPlan.PlanType planType) {
        return store.findByTenantAndPlanType(tenantId, planType);
    }

    @Override
    public CompletableFuture<List<TenantOrganization>> searchByName(String tenantId, String keyword) {
        return store.searchByName(tenantId, keyword);
    }

    @Override
    public CompletableFuture<List<TenantOrganization>> searchByTags(String tenantId, java.util.Set<String> tags) {
        return store.findByTags(tenantId, tags);
    }

    @Override
    public CompletableFuture<List<TenantOrganization>> listActiveOrganizations(String tenantId) {
        return store.findActiveByTenant(tenantId);
    }

    @Override
    public CompletableFuture<List<TenantOrganization>> listChildOrganizations(String tenantId, String parentOrgId) {
        return store.findByParentOrg(tenantId, parentOrgId);
    }

    @Override
    public CompletableFuture<List<TenantOrganization>> listByPlanExpiringBefore(String tenantId, Instant expiryDate) {
        return store.findByPlanExpiringBefore(tenantId, expiryDate);
    }

    @Override
    public CompletableFuture<List<TenantOrganization>> listInactiveOrganizations(String tenantId, Instant since) {
        return store.findInactiveSince(tenantId, since);
    }

    // ==================== Helper Methods ====================

    private TenantOrganization enhanceOrganization(
            TenantOrganization organization, CreateOrganizationCommand command) {
        return new TenantOrganization(
                organization.id(),
                organization.tenantId(),
                organization.orgId(),
                organization.orgName(),
                organization.orgType(),
                organization.status(),
                command.plan() != null ? command.plan() : organization.plan(),
                command.settings() != null ? command.settings() : organization.settings(),
                command.metadata() != null ? command.metadata() : organization.metadata(),
                command.billingInfo(),
                command.contacts() != null ? command.contacts() : organization.contacts(),
                organization.createdAt(),
                organization.updatedAt(),
                command.createdBy() != null ? command.createdBy() : organization.createdBy(),
                organization.updatedBy(),
                organization.lastActiveAt(),
                organization.isActive(),
                command.parentOrgId() != null ? command.parentOrgId() : organization.parentOrgId(),
                command.tags() != null ? command.tags() : organization.tags(),
                command.customAttributes() != null ? command.customAttributes() : organization.customAttributes(),
                command.complianceInfo()
        );
    }

    private TenantOrganization mergeOrganization(
            TenantOrganization organization, UpdateOrganizationCommand command) {
        return new TenantOrganization(
                organization.id(),
                organization.tenantId(),
                organization.orgId(),
                command.orgName() != null ? command.orgName() : organization.orgName(),
                command.orgType() != null ? command.orgType() : organization.orgType(),
                organization.status(),
                command.plan() != null ? command.plan() : organization.plan(),
                command.settings() != null ? command.settings() : organization.settings(),
                command.metadata() != null ? command.metadata() : organization.metadata(),
                command.billingInfo() != null ? command.billingInfo() : organization.billingInfo(),
                command.contacts() != null ? command.contacts() : organization.contacts(),
                organization.createdAt(),
                Instant.now(),
                organization.createdBy(),
                command.updatedBy() != null ? command.updatedBy() : organization.updatedBy(),
                Instant.now(),
                organization.isActive(),
                organization.parentOrgId(),
                command.tags() != null ? command.tags() : organization.tags(),
                command.customAttributes() != null ? command.customAttributes() : organization.customAttributes(),
                command.complianceInfo() != null ? command.complianceInfo() : organization.complianceInfo()
        );
    }
}
