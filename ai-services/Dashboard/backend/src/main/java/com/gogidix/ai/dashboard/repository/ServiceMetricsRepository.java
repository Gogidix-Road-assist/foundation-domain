package com.gogidix.ai.dashboard.repository;

import com.gogidix.ai.dashboard.model.ServiceHealth;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory repository for service health data
 * In production, this would use Redis or a database
 */
@Repository
public class ServiceMetricsRepository {

    private final ConcurrentHashMap<String, ServiceHealth> healthStore = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, java.util.List<ServiceHealth>> historyStore = new ConcurrentHashMap<>();

    /**
     * Save service health data
     */
    public void save(String serviceId, ServiceHealth health) {
        healthStore.put(serviceId, health);

        // Keep history (last 100 records per service)
        historyStore.computeIfAbsent(serviceId, k -> new java.util.concurrent.CopyOnWriteArrayList<>())
            .add(health);

        if (historyStore.get(serviceId).size() > 100) {
            historyStore.get(serviceId).remove(0);
        }
    }

    /**
     * Get current health for a service
     */
    public ServiceHealth findById(String serviceId) {
        return healthStore.get(serviceId);
    }

    /**
     * Get all current health data
     */
    public java.util.Collection<ServiceHealth> findAll() {
        return healthStore.values();
    }

    /**
     * Get health history for a service
     */
    public java.util.List<ServiceHealth> findHistory(String serviceId, int limit) {
        java.util.List<ServiceHealth> history = historyStore.get(serviceId);
        if (history == null) {
            return java.util.Collections.emptyList();
        }
        int fromIndex = Math.max(0, history.size() - limit);
        return history.subList(fromIndex, history.size());
    }

    /**
     * Get health history for a service within a time range
     */
    public java.util.List<ServiceHealth> findHistoryBetween(
        String serviceId,
        LocalDateTime start,
        LocalDateTime end
    ) {
        java.util.List<ServiceHealth> history = historyStore.get(serviceId);
        if (history == null) {
            return java.util.Collections.emptyList();
        }
        return history.stream()
            .filter(h -> !h.getLastHealthCheck().isBefore(start) &&
                        !h.getLastHealthCheck().isAfter(end))
            .toList();
    }

    /**
     * Get all services by category
     */
    public java.util.List<ServiceHealth> findByCategory(String category) {
        return healthStore.values().stream()
            .filter(s -> s.getCategory().equals(category))
            .toList();
    }

    /**
     * Get services by status
     */
    public java.util.List<ServiceHealth> findByStatus(ServiceHealth.ServiceStatus status) {
        return healthStore.values().stream()
            .filter(s -> s.getStatus() == status)
            .toList();
    }

    /**
     * Get services by status and category
     */
    public java.util.List<ServiceHealth> findByStatusAndCategory(
        ServiceHealth.ServiceStatus status,
        String category
    ) {
        return healthStore.values().stream()
            .filter(s -> s.getStatus() == status && s.getCategory().equals(category))
            .toList();
    }

    /**
     * Count services by status
     */
    public long countByStatus(ServiceHealth.ServiceStatus status) {
        return healthStore.values().stream()
            .filter(s -> s.getStatus() == status)
            .count();
    }

    /**
     * Get average metrics across all services
     */
    public ServiceMetrics getAverageMetrics() {
        java.util.Collection<ServiceHealth> allHealth = healthStore.values();
        if (allHealth.isEmpty()) {
            return ServiceHealth.ServiceMetrics.builder().build();
        }

        return ServiceHealth.ServiceMetrics.builder()
            .uptime(allHealth.stream()
                .mapToDouble(s -> s.getMetrics() != null ? s.getMetrics().getUptime() : 0)
                .average()
                .orElse(0))
            .requestCount(allHealth.stream()
                .mapToLong(s -> s.getMetrics() != null ? s.getMetrics().getRequestCount() : 0)
                .sum())
            .errorRate(allHealth.stream()
                .mapToDouble(s -> s.getMetrics() != null ? s.getMetrics().getErrorRate() : 0)
                .average()
                .orElse(0))
            .avgResponseTime(allHealth.stream()
                .mapToDouble(s -> s.getMetrics() != null ? s.getMetrics().getAvgResponseTime() : 0)
                .average()
                .orElse(0))
            .cpuUsage(allHealth.stream()
                .mapToDouble(s -> s.getMetrics() != null ? s.getMetrics().getCpuUsage() : 0)
                .average()
                .orElse(0))
            .memoryUsage(allHealth.stream()
                .mapToDouble(s -> s.getMetrics() != null ? s.getMetrics().getMemoryUsage() : 0)
                .average()
                .orElse(0))
            .build();
    }
}
