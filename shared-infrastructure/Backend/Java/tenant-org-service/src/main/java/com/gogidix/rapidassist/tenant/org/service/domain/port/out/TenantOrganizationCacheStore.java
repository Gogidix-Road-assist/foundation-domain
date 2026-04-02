package com.gogidix.rapidassist.tenant.org.service.domain.port.out;

import com.gogidix.rapidassist.tenant.org.service.domain.model.TenantOrganization;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface TenantOrganizationCacheStore {

    CompletableFuture<Void> put(String tenantId, String orgId, TenantOrganization organization);

    CompletableFuture<Optional<TenantOrganization>> get(String tenantId, String orgId);

    CompletableFuture<List<TenantOrganization>> getByTenant(String tenantId);

    CompletableFuture<Void> evict(String tenantId, String orgId);

    CompletableFuture<Void> evictByTenant(String tenantId);

    CompletableFuture<Void> evictAll();
}
