package com.gogidix.rapidassist.service.registry.discovery.domain.port.in;

public interface RegisterServiceInstanceCommand {

    void register(String tenantId, String serviceName, String instanceId, String baseUrl);
}
