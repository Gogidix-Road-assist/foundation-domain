package com.gogidix.rapidassist.service.registry.discovery.domain.port.in;

import com.gogidix.rapidassist.service.registry.discovery.domain.model.ServiceInstance;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface ServiceRegistryQuery {

    CompletableFuture<Optional<ServiceInstance>> getServiceInstance(
        String tenantId, String serviceName, String instanceId);

    CompletableFuture<List<ServiceInstance>> getAllServiceInstances(
        String tenantId, String serviceName);

    CompletableFuture<List<ServiceInstance>> getServiceInstancesByStatus(
        String tenantId, String serviceName,
        ServiceInstance.ServiceStatus status);

    CompletableFuture<List<ServiceInstance>> getAllServices(String tenantId);

    CompletableFuture<List<String>> getAllServiceNames(String tenantId);

    CompletableFuture<List<ServiceInstance>> getServicesByTag(
        String tenantId, String tag);

    CompletableFuture<List<ServiceInstance>> getServicesByTags(
        String tenantId, Set<String> tags);

    CompletableFuture<List<ServiceInstance>> getServicesByVersion(
        String tenantId, String serviceName, String version);

    CompletableFuture<List<ServiceInstance>> getServicesByEnvironment(
        String tenantId, String environment);

    CompletableFuture<List<ServiceInstance>> searchServices(
        String tenantId, String keyword);
}
