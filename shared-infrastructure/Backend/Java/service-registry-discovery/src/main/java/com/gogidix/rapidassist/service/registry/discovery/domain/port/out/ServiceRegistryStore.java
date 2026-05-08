package com.gogidix.rapidassist.service.registry.discovery.domain.port.out;

import com.gogidix.rapidassist.service.registry.discovery.domain.model.ServiceInstanceRegistration;

import java.util.List;

public interface ServiceRegistryStore {

    void upsert(ServiceInstanceRegistration registration);

    void remove(String tenantId, String serviceName, String instanceId);

    List<ServiceInstanceRegistration> list(String tenantId, String serviceName);
}
