package com.gogidix.rapidassist.service.registry.discovery.domain.port.in;

import com.gogidix.rapidassist.service.registry.discovery.domain.model.ServiceInstance;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface ServiceRegistryCommand {

    CompletableFuture<ServiceInstance> registerService(RegisterServiceCommand command);

    CompletableFuture<Optional<ServiceInstance>> deregisterService(
        String tenantId, String serviceName, String instanceId);

    CompletableFuture<ServiceInstance> sendHeartbeat(
        String tenantId, String serviceName, String instanceId);

    CompletableFuture<Optional<ServiceInstance>> updateServiceStatus(
        String tenantId, String serviceName, String instanceId,
        ServiceInstance.ServiceStatus status);

    CompletableFuture<List<ServiceInstance>> bulkRegisterServices(
        BulkRegisterServicesCommand command);

    CompletableFuture<Boolean> removeExpiredServices(long heartbeatTimeoutMs);

    record RegisterServiceCommand(
        String tenantId,
        String serviceName,
        String instanceId,
        String baseUrl,
        String host,
        Integer port,
        Boolean secure,
        String healthCheckUrl,
        String statusUrl,
        ServiceInstance.HealthCheckConfig healthCheckConfig,
        Set<String> tags,
        java.util.Map<String, String> metadata,
        String version,
        String environment,
        Integer weight
    ) {}

    record BulkRegisterServicesCommand(
        String tenantId,
        List<RegisterServiceCommand> services
    ) {}
}
