package com.gogidix.rapidassist.service.registry.discovery.domain.port.in;

public interface DeregisterServiceInstanceCommand {

    void deregister(String tenantId, String serviceName, String instanceId);
}
