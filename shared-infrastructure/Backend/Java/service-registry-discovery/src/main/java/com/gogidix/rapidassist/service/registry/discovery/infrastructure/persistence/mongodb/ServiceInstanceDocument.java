package com.gogidix.rapidassist.service.registry.discovery.infrastructure.persistence.mongodb;

import com.gogidix.rapidassist.service.registry.discovery.domain.model.ServiceInstance;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Document(collection = "service_instances")
public record ServiceInstanceDocument(
    @Id
    String id,
    @Indexed
    String tenantId,
    @Indexed
    String serviceName,
    @Indexed
    String instanceId,
    String baseUrl,
    String host,
    Integer port,
    Boolean secure,
    String healthCheckUrl,
    String statusUrl,
    HealthCheckConfigData healthCheckConfig,
    String status,
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

    public static ServiceInstanceDocument fromDomain(ServiceInstance instance) {
        return new ServiceInstanceDocument(
            instance.id(),
            instance.tenantId(),
            instance.serviceName(),
            instance.instanceId(),
            instance.baseUrl(),
            instance.host(),
            instance.port(),
            instance.secure(),
            instance.healthCheckUrl(),
            instance.statusUrl(),
            instance.healthCheckConfig() != null
                ? new HealthCheckConfigData(instance.healthCheckConfig())
                : null,
            instance.status().name(),
            instance.tags(),
            instance.metadata(),
            instance.version(),
            instance.environment(),
            instance.weight(),
            instance.lastHeartbeat(),
            instance.registeredAt(),
            instance.updatedAt()
        );
    }

    public ServiceInstance toDomain() {
        return new ServiceInstance(
            id,
            tenantId,
            serviceName,
            instanceId,
            baseUrl,
            host,
            port,
            secure,
            healthCheckUrl,
            statusUrl,
            healthCheckConfig != null ? healthCheckConfig.toDomain() : null,
            ServiceInstance.ServiceStatus.valueOf(status),
            tags,
            metadata,
            version,
            environment,
            weight,
            lastHeartbeat,
            registeredAt,
            updatedAt
        );
    }

    public record HealthCheckConfigData(
        String healthCheckPath,
        long intervalMs,
        long timeoutMs,
        int failureThreshold,
        String expectedStatus
    ) {
        public HealthCheckConfigData(ServiceInstance.HealthCheckConfig config) {
            this(
                config.healthCheckPath(),
                config.intervalMs(),
                config.timeoutMs(),
                config.failureThreshold(),
                config.expectedStatus()
            );
        }

        public ServiceInstance.HealthCheckConfig toDomain() {
            return new ServiceInstance.HealthCheckConfig(
                healthCheckPath,
                intervalMs,
                timeoutMs,
                failureThreshold,
                expectedStatus
            );
        }
    }
}
