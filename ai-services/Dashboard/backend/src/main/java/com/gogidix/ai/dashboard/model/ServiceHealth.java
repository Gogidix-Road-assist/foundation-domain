package com.gogidix.ai.dashboard.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Represents the health status of an AI service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceHealth {

    private String id;
    private String name;
    private String category;
    private String description;
    private ServiceStatus status;
    private Integer port;
    private ServiceMetrics metrics;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastHealthCheck;

    public enum ServiceStatus {
        HEALTHY, DEGRADED, DOWN
    }
}
