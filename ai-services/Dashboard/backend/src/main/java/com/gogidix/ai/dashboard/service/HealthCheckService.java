package com.gogidix.ai.dashboard.service;

import com.gogidix.ai.dashboard.configuration.ServicesConfiguration;
import com.gogidix.ai.dashboard.model.ServiceHealth;
import com.gogidix.ai.dashboard.model.ServiceMetrics;
import com.gogidix.ai.dashboard.repository.ServiceMetricsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for checking health of all 27 AI Services
 */
@Service
public class HealthCheckService {

    private static final Logger logger = LoggerFactory.getLogger(HealthCheckService.class);

    private final RestTemplate restTemplate;
    private final ServiceMetricsRepository metricsRepository;
    private final ConcurrentHashMap<String, ServiceHealth> latestHealthCache = new ConcurrentHashMap<>();

    public HealthCheckService(RestTemplate restTemplate, ServiceMetricsRepository metricsRepository) {
        this.restTemplate = restTemplate;
        this.metricsRepository = metricsRepository;
    }

    /**
     * Check health of all 27 AI services asynchronously
     */
    @Async
    public CompletableFuture<List<ServiceHealth>> checkAllServices() {
        List<CompletableFuture<ServiceHealth>> futures = ServicesConfiguration.ALL_SERVICES.stream()
            .map(this::checkServiceHealth)
            .toList();

        CompletableFuture<Void> allOf = CompletableFuture.allOf(
            futures.toArray(new CompletableFuture[0])
        );

        return allOf.thenApply(v -> futures.stream()
            .map(CompletableFuture::join)
            .toList());
    }

    /**
     * Check health of a single service
     */
    @Async
    public CompletableFuture<ServiceHealth> checkServiceHealth(ServicesConfiguration.ServiceDefinition service) {
        try {
            long startTime = System.currentTimeMillis();

            // Try to connect to the service's actuator health endpoint
            String healthUrl = service.getHealthUrl() + "?componentIndicator";

            ServiceHealth.ServiceStatus status = ServiceHealth.ServiceStatus.HEALTHY;

            try {
                // First, try basic connectivity check
                boolean isReachable = restTemplate.getForObject(
                    service.getHealthUrl(),
                    String.class
                ) != null;

                if (!isReachable) {
                    status = ServiceHealth.ServiceStatus.DOWN;
                }

                // Get metrics from the service
                ServiceMetrics metrics = getServiceMetrics(service);

                // Determine if service is degraded based on metrics
                if (metrics != null && (metrics.getErrorRate() > 1.0 || metrics.getCpuUsage() > 90)) {
                    status = ServiceHealth.ServiceStatus.DEGRADED;
                }

                long responseTime = System.currentTimeMillis() - startTime;

                // If response time is very high, mark as degraded
                if (responseTime > 5000 && status == ServiceHealth.ServiceStatus.HEALTHY) {
                    status = ServiceHealth.ServiceStatus.DEGRADED;
                }

            } catch (Exception e) {
                logger.warn("Service {} is unreachable: {}", service.id(), e.getMessage());
                status = ServiceHealth.ServiceStatus.DOWN;
            }

            ServiceHealth health = ServiceHealth.builder()
                .id(service.id())
                .name(service.name())
                .category(service.category())
                .description(service.description())
                .status(status)
                .port(service.port())
                .metrics(getServiceMetrics(service))
                .lastHealthCheck(LocalDateTime.now())
                .build();

            // Update cache
            latestHealthCache.put(service.id(), health);

            // Save to repository
            metricsRepository.save(service.id(), health);

            return CompletableFuture.completedFuture(health);

        } catch (Exception e) {
            logger.error("Error checking service health: {}", service.id(), e);
            return CompletableFuture.completedFuture(
                ServiceHealth.builder()
                    .id(service.id())
                    .name(service.name())
                    .category(service.category())
                    .description(service.description())
                    .status(ServiceHealth.ServiceStatus.DOWN)
                    .port(service.port())
                    .metrics(ServiceMetrics.builder()
                        .uptime(0.0)
                        .requestCount(0L)
                        .errorRate(100.0)
                        .avgResponseTime(0.0)
                        .cpuUsage(0.0)
                        .memoryUsage(0.0)
                        .build())
                    .lastHealthCheck(LocalDateTime.now())
                    .build()
            );
        }
    }

    /**
     * Get metrics from a service's actuator endpoint
     */
    private ServiceMetrics getServiceMetrics(ServicesConfiguration.ServiceDefinition service) {
        try {
            // In a real implementation, this would call the service's metrics endpoint
            // For now, return simulated metrics based on service status

            return ServiceMetrics.builder()
                .uptime(99.5 + Math.random() * 0.5)
                .requestCount((long) (Math.random() * 50000))
                .errorRate(Math.random() * 2)
                .avgResponseTime(100 + Math.random() * 400)
                .cpuUsage(30 + Math.random() * 50)
                .memoryUsage(512 + Math.random() * 1024)
                .diskUsage(40 + Math.random() * 30)
                .activeConnections((long) (Math.random() * 1000))
                .build();

        } catch (Exception e) {
            logger.debug("Could not get metrics for service: {}", service.id());
            return ServiceMetrics.builder()
                .uptime(0.0)
                .requestCount(0L)
                .errorRate(0.0)
                .avgResponseTime(0.0)
                .cpuUsage(0.0)
                .memoryUsage(0.0)
                .build();
        }
    }

    /**
     * Get latest health from cache
     */
    public ServiceHealth getLatestHealth(String serviceId) {
        return latestHealthCache.get(serviceId);
    }

    /**
     * Get all latest health from cache
     */
    public List<ServiceHealth> getAllLatestHealth() {
        return latestHealthCache.values().stream().toList();
    }

    /**
     * Scheduled health check - runs every 30 seconds
     */
    @Scheduled(fixedRate = 30000)
    public void scheduledHealthCheck() {
        logger.debug("Running scheduled health check for all services...");
        checkAllServices().thenAccept(healthList -> {
            long healthy = healthList.stream()
                .filter(s -> s.getStatus() == ServiceHealth.ServiceStatus.HEALTHY)
                .count();
            logger.debug("Health check complete: {}/{} services healthy",
                healthy, healthList.size());
        });
    }
}
