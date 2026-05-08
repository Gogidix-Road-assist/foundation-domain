package com.gogidix.rapidassist.service.registry.discovery.domain.port.out;

import com.gogidix.rapidassist.service.registry.discovery.domain.model.ServiceInstance;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface ServiceInstanceStore {

    CompletableFuture<ServiceInstance> save(ServiceInstance instance);

    CompletableFuture<Optional<ServiceInstance>> findById(String id);

    CompletableFuture<Optional<ServiceInstance>> findByTenantServiceInstance(
        String tenantId, String serviceName, String instanceId);

    CompletableFuture<List<ServiceInstance>> findByTenantAndService(
        String tenantId, String serviceName);

    CompletableFuture<List<ServiceInstance>> findByTenant(String tenantId);

    CompletableFuture<List<ServiceInstance>> findByStatus(
        String tenantId, String serviceName,
        ServiceInstance.ServiceStatus status);

    CompletableFuture<List<String>> findServiceNamesByTenant(String tenantId);

    CompletableFuture<List<ServiceInstance>> findByTag(
        String tenantId, String tag);

    CompletableFuture<List<ServiceInstance>> findByTags(
        String tenantId, Set<String> tags);

    CompletableFuture<List<ServiceInstance>> findByVersion(
        String tenantId, String serviceName, String version);

    CompletableFuture<List<ServiceInstance>> findByEnvironment(
        String tenantId, String environment);

    CompletableFuture<List<ServiceInstance>> searchByKeyword(
        String tenantId, String keyword);

    CompletableFuture<List<ServiceInstance>> findExpiredInstances(
        long heartbeatTimeoutMs);

    CompletableFuture<Boolean> deleteById(String id);

    CompletableFuture<Boolean> deleteByTenantServiceInstance(
        String tenantId, String serviceName, String instanceId);

    CompletableFuture<Long> countByTenantAndService(
        String tenantId, String serviceName);
}
