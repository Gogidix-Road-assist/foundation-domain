package com.gogidix.rapidassist.service.registry.discovery.domain.model;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

@Document(collection = "service_instances")
public record ServiceInstance(

    @Id
    String id,

    @Indexed
    @NotBlank(message = "Tenant ID is required")
    String tenantId,

    @Indexed
    @NotBlank(message = "Service name is required")
    String serviceName,

    @Indexed
    @NotBlank(message = "Instance ID is required")
    String instanceId,

    @NotBlank(message = "Base URL is required")
    String baseUrl,

    String host,

    Integer port,

    Boolean secure,

    String healthCheckUrl,

    String statusUrl,

    HealthCheckConfig healthCheckConfig,

    ServiceStatus status,

    Set<String> tags,

    Map<String, String> metadata,

    String version,

    String environment,

    Integer weight,

    @Indexed
    Instant lastHeartbeat,

    Instant registeredAt,

    Instant updatedAt

) {

    public static ServiceInstance create(
        String tenantId, String serviceName, String instanceId,
        String baseUrl, String host, Integer port
    ) {
        Instant now = Instant.now();
        return new ServiceInstance(
            null, tenantId, serviceName, instanceId, baseUrl,
            host, port, false, null, null, null,
            ServiceStatus.UP, Set.of(), Map.of(),
            null, null, 100, now, now, now
        );
    }

    public ServiceInstance withHeartbeat() {
        return new ServiceInstance(
            id, tenantId, serviceName, instanceId, baseUrl,
            host, port, secure, healthCheckUrl, statusUrl,
            healthCheckConfig, ServiceStatus.UP, tags, metadata,
            version, environment, weight, Instant.now(),
            registeredAt, Instant.now()
        );
    }

    public ServiceInstance withStatus(ServiceStatus newStatus) {
        return new ServiceInstance(
            id, tenantId, serviceName, instanceId, baseUrl,
            host, port, secure, healthCheckUrl, statusUrl,
            healthCheckConfig, newStatus, tags, metadata,
            version, environment, weight, lastHeartbeat,
            registeredAt, Instant.now()
        );
    }

    public boolean isExpired(long heartbeatTimeoutMs) {
        return lastHeartbeat == null ||
               Instant.now().toEpochMilli() - lastHeartbeat.toEpochMilli() > heartbeatTimeoutMs;
    }

    public enum ServiceStatus {
        UP, DOWN, STARTING, OUT_OF_SERVICE, UNKNOWN
    }

    public record HealthCheckConfig(
        String healthCheckPath,
        long intervalMs,
        long timeoutMs,
        int failureThreshold,
        String expectedStatus
    ) {}
}
