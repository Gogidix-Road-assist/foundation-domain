package com.gogidix.rapidassist.service.registry.discovery.infrastructure.persistence.mongodb;

import com.gogidix.rapidassist.service.registry.discovery.domain.model.ServiceInstance;
import com.gogidix.rapidassist.service.registry.discovery.domain.port.out.ServiceInstanceStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Component
public class MongoServiceInstanceStore implements ServiceInstanceStore {

    private static final Logger logger = LoggerFactory.getLogger(MongoServiceInstanceStore.class);

    @Autowired
    private ServiceInstanceRepository repository;

    @Override
    public CompletableFuture<ServiceInstance> save(ServiceInstance instance) {
        return CompletableFuture.supplyAsync(() -> {
            ServiceInstanceDocument document = ServiceInstanceDocument.fromDomain(instance);
            ServiceInstanceDocument saved = repository.save(document);
            logger.debug("Saved service instance: {}", saved.instanceId());
            return saved.toDomain();
        });
    }

    @Override
    public CompletableFuture<Optional<ServiceInstance>> findById(String id) {
        return CompletableFuture.supplyAsync(() -> {
            return repository.findById(id)
                .map(ServiceInstanceDocument::toDomain);
        });
    }

    @Override
    public CompletableFuture<Optional<ServiceInstance>> findByTenantServiceInstance(
        String tenantId, String serviceName, String instanceId) {
        return CompletableFuture.supplyAsync(() -> {
            return repository.findByTenantIdAndServiceNameAndInstanceId(
                tenantId, serviceName, instanceId)
                .map(ServiceInstanceDocument::toDomain);
        });
    }

    @Override
    public CompletableFuture<List<ServiceInstance>> findByTenantAndService(
        String tenantId, String serviceName) {
        return CompletableFuture.supplyAsync(() -> {
            return repository.findByTenantIdAndServiceName(tenantId, serviceName).stream()
                .map(ServiceInstanceDocument::toDomain)
                .toList();
        });
    }

    @Override
    public CompletableFuture<List<ServiceInstance>> findByTenant(String tenantId) {
        return CompletableFuture.supplyAsync(() -> {
            return repository.findByTenantId(tenantId).stream()
                .map(ServiceInstanceDocument::toDomain)
                .toList();
        });
    }

    @Override
    public CompletableFuture<List<ServiceInstance>> findByStatus(
        String tenantId, String serviceName,
        ServiceInstance.ServiceStatus status) {
        return CompletableFuture.supplyAsync(() -> {
            return repository.findByTenantIdAndServiceNameAndStatus(
                tenantId, serviceName, status.name()).stream()
                .map(ServiceInstanceDocument::toDomain)
                .toList();
        });
    }

    @Override
    public CompletableFuture<List<String>> findServiceNamesByTenant(String tenantId) {
        return CompletableFuture.supplyAsync(() -> {
            return repository.findDistinctServiceNameByTenantId(tenantId);
        });
    }

    @Override
    public CompletableFuture<List<ServiceInstance>> findByTag(
        String tenantId, String tag) {
        return CompletableFuture.supplyAsync(() -> {
            return repository.findByTenantIdAndTagsContaining(tenantId, tag).stream()
                .map(ServiceInstanceDocument::toDomain)
                .toList();
        });
    }

    @Override
    public CompletableFuture<List<ServiceInstance>> findByTags(
        String tenantId, Set<String> tags) {
        return CompletableFuture.supplyAsync(() -> {
            return repository.findByTenantIdAndTagsIn(
                tenantId, tags.stream().toList()).stream()
                .map(ServiceInstanceDocument::toDomain)
                .toList();
        });
    }

    @Override
    public CompletableFuture<List<ServiceInstance>> findByVersion(
        String tenantId, String serviceName, String version) {
        return CompletableFuture.supplyAsync(() -> {
            return repository.findByTenantIdAndServiceNameAndVersion(
                tenantId, serviceName, version).stream()
                .map(ServiceInstanceDocument::toDomain)
                .toList();
        });
    }

    @Override
    public CompletableFuture<List<ServiceInstance>> findByEnvironment(
        String tenantId, String environment) {
        return CompletableFuture.supplyAsync(() -> {
            return repository.findByTenantIdAndEnvironment(
                tenantId, environment).stream()
                .map(ServiceInstanceDocument::toDomain)
                .toList();
        });
    }

    @Override
    public CompletableFuture<List<ServiceInstance>> searchByKeyword(
        String tenantId, String keyword) {
        return CompletableFuture.supplyAsync(() -> {
            return repository.searchByTenantIdAndKeyword(tenantId, keyword).stream()
                .map(ServiceInstanceDocument::toDomain)
                .toList();
        });
    }

    @Override
    public CompletableFuture<List<ServiceInstance>> findExpiredInstances(
        long heartbeatTimeoutMs) {
        return CompletableFuture.supplyAsync(() -> {
            Instant threshold = Instant.now().minusMillis(heartbeatTimeoutMs);
            return repository.findByLastHeartbeatBefore(threshold).stream()
                .map(ServiceInstanceDocument::toDomain)
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
    public CompletableFuture<Boolean> deleteByTenantServiceInstance(
        String tenantId, String serviceName, String instanceId) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<ServiceInstanceDocument> document =
                repository.findByTenantIdAndServiceNameAndInstanceId(
                    tenantId, serviceName, instanceId);
            if (document.isPresent()) {
                repository.delete(document.get());
                return true;
            }
            return false;
        });
    }

    @Override
    public CompletableFuture<Long> countByTenantAndService(
        String tenantId, String serviceName) {
        return CompletableFuture.supplyAsync(() -> {
            return repository.countByTenantIdAndServiceName(tenantId, serviceName);
        });
    }
}
