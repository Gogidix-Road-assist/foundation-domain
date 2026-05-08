package com.gogidix.rapidassist.service.registry.discovery.domain.port.out;

import com.gogidix.rapidassist.service.registry.discovery.domain.model.ServiceInstance;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface ServiceInstanceCacheStore {

    CompletableFuture<Void> put(String tenantId, String serviceName,
                                String instanceId, ServiceInstance instance);

    CompletableFuture<Optional<ServiceInstance>> get(
        String tenantId, String serviceName, String instanceId);

    CompletableFuture<List<ServiceInstance>> getByService(
        String tenantId, String serviceName);

    CompletableFuture<Void> evict(String tenantId, String serviceName,
                                 String instanceId);

    CompletableFuture<Void> evictByService(
        String tenantId, String serviceName);

    CompletableFuture<Void> evictByTenant(String tenantId);

    CompletableFuture<Void> evictAll();
}
