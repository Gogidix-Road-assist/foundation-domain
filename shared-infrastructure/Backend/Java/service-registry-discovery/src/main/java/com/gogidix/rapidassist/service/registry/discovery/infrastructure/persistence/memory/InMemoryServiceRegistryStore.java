package com.gogidix.rapidassist.service.registry.discovery.infrastructure.persistence.memory;

import com.gogidix.rapidassist.service.registry.discovery.domain.model.ServiceInstanceRegistration;
import com.gogidix.rapidassist.service.registry.discovery.domain.port.out.ServiceRegistryStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryServiceRegistryStore implements ServiceRegistryStore {

    private final Map<String, Map<String, Map<String, ServiceInstanceRegistration>>> data = new ConcurrentHashMap<>();

    @Override
    public void upsert(ServiceInstanceRegistration registration) {
        data.computeIfAbsent(registration.tenantId(), t -> new ConcurrentHashMap<>())
                .computeIfAbsent(registration.serviceName(), s -> new ConcurrentHashMap<>())
                .put(registration.instanceId(), registration);
    }

    @Override
    public void remove(String tenantId, String serviceName, String instanceId) {
        Map<String, Map<String, ServiceInstanceRegistration>> byTenant = data.get(tenantId);
        if (byTenant == null) {
            return;
        }
        Map<String, ServiceInstanceRegistration> byService = byTenant.get(serviceName);
        if (byService == null) {
            return;
        }
        byService.remove(instanceId);
    }

    @Override
    public List<ServiceInstanceRegistration> list(String tenantId, String serviceName) {
        Map<String, Map<String, ServiceInstanceRegistration>> byTenant = data.get(tenantId);
        if (byTenant == null) {
            return List.of();
        }
        Map<String, ServiceInstanceRegistration> byService = byTenant.get(serviceName);
        if (byService == null) {
            return List.of();
        }
        return new ArrayList<>(byService.values());
    }
}
