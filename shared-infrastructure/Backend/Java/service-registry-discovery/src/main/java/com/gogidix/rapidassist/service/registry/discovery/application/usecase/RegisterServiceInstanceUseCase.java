package com.gogidix.rapidassist.service.registry.discovery.application.usecase;

import com.gogidix.rapidassist.service.registry.discovery.domain.model.ServiceInstanceRegistration;
import com.gogidix.rapidassist.service.registry.discovery.domain.port.in.RegisterServiceInstanceCommand;
import com.gogidix.rapidassist.service.registry.discovery.domain.port.out.ServiceRegistryStore;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
public class RegisterServiceInstanceUseCase implements RegisterServiceInstanceCommand {

    private final ServiceRegistryStore store;

    public RegisterServiceInstanceUseCase(ServiceRegistryStore store) {
        this.store = store;
    }

    @Override
    public void register(String tenantId, String serviceName, String instanceId, String baseUrl) {
        store.upsert(new ServiceInstanceRegistration(
                tenantId,
                serviceName,
                instanceId,
                baseUrl,
                Instant.now(),
                Map.of()
        ));
    }
}
