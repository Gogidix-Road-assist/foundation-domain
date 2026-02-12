package com.gogidix.ai.dashboard.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Metrics for an AI service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceMetrics {

    private Double uptime;
    private Long requestCount;
    private Double errorRate;
    private Double avgResponseTime;
    private Double cpuUsage;
    private Double memoryUsage;
    private Double diskUsage;
    private Long activeConnections;
}
