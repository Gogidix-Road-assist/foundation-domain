package com.gogidix.rapidassist.service.registry.discovery.application.usecase;

import com.gogidix.rapidassist.service.registry.discovery.domain.model.ServiceInstanceRegistration;
import com.gogidix.rapidassist.service.registry.discovery.domain.port.in.ListServiceInstancesQuery;
import com.gogidix.rapidassist.service.registry.discovery.domain.port.out.ServiceRegistryStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListServiceInstancesUseCase implements ListServiceInstancesQuery {

    private final ServiceRegistryStore store;

    public ListServiceInstancesUseCase(ServiceRegistryStore store) {
        this.store = store;
    }

    @Override
    public List<ServiceInstanceRegistration> list(String tenantId, String serviceName) {
        return store.list(tenantId, serviceName);
    }
}
