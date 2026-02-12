package com.gogidix.rapidassist.service.registry.discovery.application.usecase;

import com.gogidix.rapidassist.service.registry.discovery.domain.port.in.DeregisterServiceInstanceCommand;
import com.gogidix.rapidassist.service.registry.discovery.domain.port.out.ServiceRegistryStore;
import org.springframework.stereotype.Service;

@Service
public class DeregisterServiceInstanceUseCase implements DeregisterServiceInstanceCommand {

    private final ServiceRegistryStore store;

    public DeregisterServiceInstanceUseCase(ServiceRegistryStore store) {
        this.store = store;
    }

    @Override
    public void deregister(String tenantId, String serviceName, String instanceId) {
        store.remove(tenantId, serviceName, instanceId);
    }
}
