package com.gogidix.rapidassist.service.registry.discovery.application.service;

import com.gogidix.rapidassist.service.registry.discovery.domain.model.ServiceInstance;
import com.gogidix.rapidassist.service.registry.discovery.domain.port.in.ServiceRegistryCommand;
import com.gogidix.rapidassist.service.registry.discovery.domain.port.in.ServiceRegistryQuery;
import com.gogidix.rapidassist.service.registry.discovery.domain.port.out.ServiceInstanceCacheStore;
import com.gogidix.rapidassist.service.registry.discovery.domain.port.out.ServiceInstanceStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Service
public class ComprehensiveServiceRegistryService implements ServiceRegistryCommand, ServiceRegistryQuery {

    private static final Logger logger = LoggerFactory.getLogger(ComprehensiveServiceRegistryService.class);

    @Autowired
    private ServiceInstanceStore store;

    @Autowired
    private ServiceInstanceCacheStore cacheStore;

    @PostConstruct
    public void init() {
        logger.info("Comprehensive Service Registry Service initialized");
    }

    @Override
    public CompletableFuture<ServiceInstance> registerService(RegisterServiceCommand command) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ServiceInstance instance = ServiceInstance.create(
                    command.tenantId(),
                    command.serviceName(),
                    command.instanceId(),
                    command.baseUrl(),
                    command.host(),
                    command.port()
                );

                ServiceInstance enhanced = enhanceInstance(instance, command);

                ServiceInstance saved = store.save(enhanced).join();

                cacheStore.put(
                    command.tenantId(),
                    command.serviceName(),
                    command.instanceId(),
                    saved
                ).join();

                cacheStore.evictByService(command.tenantId(), command.serviceName()).join();

                logger.info("Registered service instance: {} for service: {}, tenant: {}",
                    command.instanceId(), command.serviceName(), command.tenantId());
                return saved;

            } catch (Exception e) {
                logger.error("Error registering service instance", e);
                throw new RuntimeException("Failed to register service instance", e);
            }
        });
    }

    @Override
    public CompletableFuture<Optional<ServiceInstance>> deregisterService(
        String tenantId, String serviceName, String instanceId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<ServiceInstance> existing = store.findByTenantServiceInstance(
                    tenantId, serviceName, instanceId).join();

                if (existing.isEmpty()) {
                    return Optional.empty();
                }

                store.deleteByTenantServiceInstance(tenantId, serviceName, instanceId).join();

                cacheStore.evict(tenantId, serviceName, instanceId).join();
                cacheStore.evictByService(tenantId, serviceName).join();

                logger.info("Deregistered service instance: {} for service: {}, tenant: {}",
                    instanceId, serviceName, tenantId);
                return existing;

            } catch (Exception e) {
                logger.error("Error deregistering service instance", e);
                throw new RuntimeException("Failed to deregister service instance", e);
            }
        });
    }

    @Override
    public CompletableFuture<ServiceInstance> sendHeartbeat(
        String tenantId, String serviceName, String instanceId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<ServiceInstance> existing = store.findByTenantServiceInstance(
                    tenantId, serviceName, instanceId).join();

                if (existing.isEmpty()) {
                    throw new IllegalStateException("Service instance not found: " + instanceId);
                }

                ServiceInstance instance = existing.get();
                ServiceInstance updated = instance.withHeartbeat();

                ServiceInstance saved = store.save(updated).join();

                cacheStore.put(tenantId, serviceName, instanceId, saved).join();

                logger.debug("Heartbeat received for instance: {} of service: {}",
                    instanceId, serviceName);
                return saved;

            } catch (Exception e) {
                logger.error("Error processing heartbeat", e);
                throw new RuntimeException("Failed to process heartbeat", e);
            }
        });
    }

    @Override
    public CompletableFuture<Optional<ServiceInstance>> updateServiceStatus(
        String tenantId, String serviceName, String instanceId,
        ServiceInstance.ServiceStatus status) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<ServiceInstance> existing = store.findByTenantServiceInstance(
                    tenantId, serviceName, instanceId).join();

                if (existing.isEmpty()) {
                    return Optional.empty();
                }

                ServiceInstance instance = existing.get();
                ServiceInstance updated = instance.withStatus(status);

                ServiceInstance saved = store.save(updated).join();

                cacheStore.put(tenantId, serviceName, instanceId, saved).join();
                cacheStore.evictByService(tenantId, serviceName).join();

                logger.info("Updated status to {} for instance: {} of service: {}",
                    status, instanceId, serviceName);
                return Optional.of(saved);

            } catch (Exception e) {
                logger.error("Error updating service status", e);
                throw new RuntimeException("Failed to update service status", e);
            }
        });
    }

    @Override
    public CompletableFuture<List<ServiceInstance>> bulkRegisterServices(
        BulkRegisterServicesCommand command) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<ServiceInstance> results = command.services().stream()
                    .map(this::registerService)
                    .map(CompletableFuture::join)
                    .toList();

                cacheStore.evictByTenant(command.tenantId()).join();

                logger.info("Bulk registered {} service instances for tenant: {}",
                    results.size(), command.tenantId());
                return results;

            } catch (Exception e) {
                logger.error("Error bulk registering services", e);
                throw new RuntimeException("Failed to bulk register services", e);
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> removeExpiredServices(long heartbeatTimeoutMs) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<ServiceInstance> expired = store.findExpiredInstances(heartbeatTimeoutMs).join();

                for (ServiceInstance instance : expired) {
                    store.deleteByTenantServiceInstance(
                        instance.tenantId(),
                        instance.serviceName(),
                        instance.instanceId()
                    ).join();

                    cacheStore.evict(
                        instance.tenantId(),
                        instance.serviceName(),
                        instance.instanceId()
                    ).join();
                }

                if (!expired.isEmpty()) {
                    logger.info("Removed {} expired service instances", expired.size());
                }

                return true;

            } catch (Exception e) {
                logger.error("Error removing expired services", e);
                return false;
            }
        });
    }

    @Scheduled(fixedRate = 60000)
    public void cleanupExpiredServices() {
        logger.debug("Running cleanup of expired services");
        long heartbeatTimeoutMs = 90000; // 90 seconds
        removeExpiredServices(heartbeatTimeoutMs).join();
    }

    @Override
    public CompletableFuture<Optional<ServiceInstance>> getServiceInstance(
        String tenantId, String serviceName, String instanceId) {
        return cacheStore.get(tenantId, serviceName, instanceId)
            .thenCompose(cached -> cached.isPresent()
                ? CompletableFuture.completedFuture(cached)
                : store.findByTenantServiceInstance(tenantId, serviceName, instanceId)
                    .thenApply(instanceOpt -> {
                        instanceOpt.ifPresent(instance -> {
                            cacheStore.put(tenantId, serviceName, instanceId, instance);
                        });
                        return instanceOpt;
                    }));
    }

    @Override
    public CompletableFuture<List<ServiceInstance>> getAllServiceInstances(
        String tenantId, String serviceName) {
        return cacheStore.getByService(tenantId, serviceName)
            .thenCompose(cached -> {
                if (!cached.isEmpty()) {
                    return CompletableFuture.completedFuture(cached);
                }
                return store.findByTenantAndService(tenantId, serviceName)
                    .thenApply(instances -> {
                        instances.forEach(instance -> {
                            cacheStore.put(tenantId, serviceName,
                                instance.instanceId(), instance);
                        });
                        return instances;
                    });
            });
    }

    @Override
    public CompletableFuture<List<ServiceInstance>> getServiceInstancesByStatus(
        String tenantId, String serviceName,
        ServiceInstance.ServiceStatus status) {
        return store.findByStatus(tenantId, serviceName, status);
    }

    @Override
    public CompletableFuture<List<ServiceInstance>> getAllServices(String tenantId) {
        return store.findByTenant(tenantId);
    }

    @Override
    public CompletableFuture<List<String>> getAllServiceNames(String tenantId) {
        return store.findServiceNamesByTenant(tenantId);
    }

    @Override
    public CompletableFuture<List<ServiceInstance>> getServicesByTag(
        String tenantId, String tag) {
        return store.findByTag(tenantId, tag);
    }

    @Override
    public CompletableFuture<List<ServiceInstance>> getServicesByTags(
        String tenantId, Set<String> tags) {
        return store.findByTags(tenantId, tags);
    }

    @Override
    public CompletableFuture<List<ServiceInstance>> getServicesByVersion(
        String tenantId, String serviceName, String version) {
        return store.findByVersion(tenantId, serviceName, version);
    }

    @Override
    public CompletableFuture<List<ServiceInstance>> getServicesByEnvironment(
        String tenantId, String environment) {
        return store.findByEnvironment(tenantId, environment);
    }

    @Override
    public CompletableFuture<List<ServiceInstance>> searchServices(
        String tenantId, String keyword) {
        return store.searchByKeyword(tenantId, keyword);
    }

    private ServiceInstance enhanceInstance(
        ServiceInstance instance, RegisterServiceCommand command) {
        return new ServiceInstance(
            instance.id(),
            instance.tenantId(),
            instance.serviceName(),
            instance.instanceId(),
            instance.baseUrl(),
            command.host() != null ? command.host() : instance.host(),
            command.port() != null ? command.port() : instance.port(),
            command.secure() != null ? command.secure() : instance.secure(),
            command.healthCheckUrl(),
            command.statusUrl(),
            command.healthCheckConfig(),
            instance.status(),
            command.tags() != null ? command.tags() : instance.tags(),
            command.metadata() != null ? command.metadata() : instance.metadata(),
            command.version(),
            command.environment(),
            command.weight() != null ? command.weight() : instance.weight(),
            instance.lastHeartbeat(),
            instance.registeredAt(),
            instance.updatedAt()
        );
    }
}
