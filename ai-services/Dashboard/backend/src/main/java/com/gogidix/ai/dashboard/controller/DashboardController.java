package com.gogidix.ai.dashboard.controller;

import com.gogidix.ai.dashboard.model.ServiceHealth;
import com.gogidix.ai.dashboard.service.HealthCheckService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * REST Controller for Dashboard API
 */
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DashboardController {

    private final HealthCheckService healthCheckService;

    /**
     * Get health status of all 27 AI services
     */
    @GetMapping("/services/health")
    public CompletableFuture<List<ServiceHealth>> getAllServicesHealth() {
        log.debug("Fetching health status for all services");
        return healthCheckService.checkAllServices();
    }

    /**
     * Get health status of a specific service
     */
    @GetMapping("/services/{serviceId}/health")
    public CompletableFuture<ServiceHealth> getServiceHealth(@PathVariable String serviceId) {
        return CompletableFuture.supplyAsync(() ->
            healthCheckService.getLatestHealth(serviceId)
        );
    }

    /**
     * Get metrics for a specific service
     */
    @GetMapping("/services/{serviceId}/metrics")
    public CompletableFuture<ServiceHealth.ServiceMetrics> getServiceMetrics(@PathVariable String serviceId) {
        return CompletableFuture.supplyAsync(() -> {
            ServiceHealth health = healthCheckService.getLatestHealth(serviceId);
            return health != null ? health.getMetrics() : null;
        });
    }

    /**
     * Get overall dashboard statistics
     */
    @GetMapping("/dashboard/stats")
    public CompletableFuture<Map<String, Object>> getDashboardStats() {
        return healthCheckService.checkAllServices().thenApply(services -> {
            long healthy = services.stream()
                .filter(s -> s.getStatus() == ServiceHealth.ServiceStatus.HEALTHY)
                .count();
            long degraded = services.stream()
                .filter(s -> s.getStatus() == ServiceHealth.ServiceStatus.DEGRADED)
                .count();
            long down = services.stream()
                .filter(s -> s.getStatus() == ServiceHealth.ServiceStatus.DOWN)
                .count();

            long totalRequests = services.stream()
                .mapToLong(s -> s.getMetrics() != null ? s.getMetrics().getRequestCount() : 0)
                .sum();

            double avgResponseTime = services.stream()
                .mapToDouble(s -> s.getMetrics() != null ? s.getMetrics().getAvgResponseTime() : 0)
                .average()
                .orElse(0);

            return Map.of(
                "total", services.size(),
                "healthy", healthy,
                "degraded", degraded,
                "down", down,
                "totalRequests", totalRequests,
                "avgResponseTime", Math.round(avgResponseTime)
            );
        });
    }

    /**
     * Refresh all services (trigger immediate health check)
     */
    @PostMapping("/services/refresh")
    public CompletableFuture<Map<String, String>> refreshServices() {
        return healthCheckService.checkAllServices().thenApply(services -> {
            long healthy = services.stream()
                .filter(s -> s.getStatus() == ServiceHealth.ServiceStatus.HEALTHY)
                .count();

            return Map.of(
                "status", "refreshed",
                "timestamp", java.time.LocalDateTime.now().toString(),
                "servicesChecked", String.valueOf(services.size()),
                "healthyServices", String.valueOf(healthy)
            );
        });
    }

    /**
     * Get time series metrics for charts
     */
    @GetMapping("/metrics/timeseries")
    public Flux<Map<String, Object>> getTimeSeriesMetrics(
        @RequestParam(defaultValue = "requests") String metric,
        @RequestParam(defaultValue = "24") int hours
    ) {
        // Generate time series data for the last N hours
        return Flux.interval(Duration.ofSeconds(5))
            .map(sequence -> {
                long value = switch (metric) {
                    case "requests" -> (long) (30000 + Math.random() * 20000);
                    case "errors" -> (long) (100 + Math.random() * 400);
                    case "responseTime" -> (long) (100 + Math.random() * 200);
                    default -> 0L;
                };

                return Map.of(
                    "time", java.time.LocalDateTime.now()
                        .minusMinutes((24 - (sequence % 24)) * 60)
                        .toString(),
                    "value", value,
                    "metric", metric
                );
            })
            .take(hours);
    }

    /**
     * Server-Sent Events endpoint for real-time updates
     */
    @GetMapping(value = "/stream/health", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<List<ServiceHealth>>> streamHealthUpdates() {
        return Flux.interval(Duration.ofSeconds(10))
            .map(sequence -> {
                log.debug("Sending health update SSE #{}", sequence);
                return ServerSentEvent.<List<ServiceHealth>>builder()
                    .data(healthCheckService.getAllLatestHealth())
                    .id(String.valueOf(sequence))
                    .build();
            });
    }

    /**
     * Get services by category
     */
    @GetMapping("/services/category/{category}")
    public CompletableFuture<List<ServiceHealth>> getServicesByCategory(@PathVariable String category) {
        return healthCheckService.checkAllServices().thenApply(services ->
            services.stream()
                .filter(s -> s.getCategory().equals(category))
                .toList()
        );
    }

    /**
     * Get services by status
     */
    @GetMapping("/services/status/{status}")
    public CompletableFuture<List<ServiceHealth>> getServicesByStatus(@PathVariable String status) {
        return healthCheckService.checkAllServices().thenApply(services ->
            services.stream()
                .filter(s -> s.getStatus().toString().equalsIgnoreCase(status))
                .toList()
        );
    }
}
