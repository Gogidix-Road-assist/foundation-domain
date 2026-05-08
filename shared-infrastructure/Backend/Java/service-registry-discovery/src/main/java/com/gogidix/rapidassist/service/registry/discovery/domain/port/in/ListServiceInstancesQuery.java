package com.gogidix.rapidassist.service.registry.discovery.domain.port.in;

import com.gogidix.rapidassist.service.registry.discovery.domain.model.ServiceInstanceRegistration;

import java.util.List;

public interface ListServiceInstancesQuery {

    List<ServiceInstanceRegistration> list(String tenantId, String serviceName);
}
