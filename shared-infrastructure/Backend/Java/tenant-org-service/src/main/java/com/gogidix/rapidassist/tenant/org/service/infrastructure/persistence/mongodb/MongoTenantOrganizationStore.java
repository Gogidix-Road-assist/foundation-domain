package com.gogidix.rapidassist.tenant.org.service.infrastructure.persistence.mongodb;

import com.gogidix.rapidassist.tenant.org.service.domain.model.TenantOrganization;
import com.gogidix.rapidassist.tenant.org.service.domain.port.out.TenantOrganizationStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Component
@Primary
public class MongoTenantOrganizationStore implements TenantOrganizationStore {

    private static final Logger logger = LoggerFactory.getLogger(MongoTenantOrganizationStore.class);

    @Autowired
    private TenantOrganizationRepository repository;

    @Override
    public CompletableFuture<TenantOrganization> save(TenantOrganization organization) {
        return CompletableFuture.supplyAsync(() -> {
            TenantOrganizationDocument document = TenantOrganizationDocument.fromDomain(organization);
            TenantOrganizationDocument saved = repository.save(document);
            logger.debug("Saved organization: {} for tenant: {}", saved.orgId(), saved.tenantId());
            return saved.toDomain();
        });
    }

    @Override
    public CompletableFuture<Optional<TenantOrganization>> findById(String id) {
        return CompletableFuture.supplyAsync(() -> {
            return repository.findById(id)
                    .map(TenantOrganizationDocument::toDomain);
        });
    }

    @Override
    public CompletableFuture<Optional<TenantOrganization>> findByTenantAndOrg(String tenantId, String orgId) {
        return CompletableFuture.supplyAsync(() -> {
            return repository.findByTenantIdAndOrgId(tenantId, orgId)
                    .map(TenantOrganizationDocument::toDomain);
        });
    }

    @Override
    public CompletableFuture<List<TenantOrganization>> findByTenant(String tenantId) {
        return CompletableFuture.supplyAsync(() -> {
            return repository.findByTenantId(tenantId).stream()
                    .map(TenantOrganizationDocument::toDomain)
                    .toList();
        });
    }

    @Override
    public CompletableFuture<List<TenantOrganization>> findByTenantAndStatus(
            String tenantId, TenantOrganization.OrganizationStatus status) {
        return CompletableFuture.supplyAsync(() -> {
            return repository.findByTenantIdAndStatus(tenantId, status.name()).stream()
                    .map(TenantOrganizationDocument::toDomain)
                    .toList();
        });
    }

    @Override
    public CompletableFuture<List<TenantOrganization>> findByTenantAndType(
            String tenantId, TenantOrganization.OrganizationType orgType) {
        return CompletableFuture.supplyAsync(() -> {
            return repository.findByTenantIdAndOrgType(tenantId, orgType.name()).stream()
                    .map(TenantOrganizationDocument::toDomain)
                    .toList();
        });
    }

    @Override
    public CompletableFuture<List<TenantOrganization>> findByTenantAndPlanType(
            String tenantId, TenantOrganization.SubscriptionPlan.PlanType planType) {
        return CompletableFuture.supplyAsync(() -> {
            return repository.findByTenantIdAndPlanPlanType(tenantId, planType.name()).stream()
                    .map(TenantOrganizationDocument::toDomain)
                    .toList();
        });
    }

    @Override
    public CompletableFuture<List<TenantOrganization>> searchByName(String tenantId, String keyword) {
        return CompletableFuture.supplyAsync(() -> {
            return repository.findByTenantIdAndOrgNameRegex(tenantId, keyword).stream()
                    .map(TenantOrganizationDocument::toDomain)
                    .toList();
        });
    }

    @Override
    public CompletableFuture<List<TenantOrganization>> findByTags(String tenantId, Set<String> tags) {
        return CompletableFuture.supplyAsync(() -> {
            return repository.findByTenantIdAndTagsIn(tenantId, tags).stream()
                    .map(TenantOrganizationDocument::toDomain)
                    .toList();
        });
    }

    @Override
    public CompletableFuture<List<TenantOrganization>> findActiveByTenant(String tenantId) {
        return CompletableFuture.supplyAsync(() -> {
            return repository.findActiveByTenantId(tenantId).stream()
                    .map(TenantOrganizationDocument::toDomain)
                    .toList();
        });
    }

    @Override
    public CompletableFuture<List<TenantOrganization>> findByParentOrg(String tenantId, String parentOrgId) {
        return CompletableFuture.supplyAsync(() -> {
            return repository.findByTenantIdAndParentOrgId(tenantId, parentOrgId).stream()
                    .map(TenantOrganizationDocument::toDomain)
                    .toList();
        });
    }

    @Override
    public CompletableFuture<List<TenantOrganization>> findByPlanExpiringBefore(String tenantId, Instant expiryDate) {
        return CompletableFuture.supplyAsync(() -> {
            return repository.findByTenantIdAndPlanEndDateBefore(tenantId, expiryDate).stream()
                    .map(TenantOrganizationDocument::toDomain)
                    .toList();
        });
    }

    @Override
    public CompletableFuture<List<TenantOrganization>> findInactiveSince(String tenantId, Instant since) {
        return CompletableFuture.supplyAsync(() -> {
            return repository.findInactiveByTenantIdSince(tenantId, since).stream()
                    .map(TenantOrganizationDocument::toDomain)
                    .toList();
        });
    }

    @Override
    public CompletableFuture<Boolean> deleteById(String id) {
        return CompletableFuture.supplyAsync(() -> {
            repository.deleteById(id);
            return true;
        });
    }

    @Override
    public CompletableFuture<Boolean> deleteByTenantAndOrg(String tenantId, String orgId) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<TenantOrganizationDocument> document =
                    repository.findByTenantIdAndOrgId(tenantId, orgId);
            if (document.isPresent()) {
                repository.delete(document.get());
                return true;
            }
            return false;
        });
    }

    @Override
    public CompletableFuture<Long> countByTenant(String tenantId) {
        return CompletableFuture.supplyAsync(() -> {
            return repository.countByTenantId(tenantId);
        });
    }

    @Override
    public CompletableFuture<Long> countByTenantAndStatus(
            String tenantId, TenantOrganization.OrganizationStatus status) {
        return CompletableFuture.supplyAsync(() -> {
            return repository.countByTenantIdAndStatus(tenantId, status.name());
        });
    }
}
