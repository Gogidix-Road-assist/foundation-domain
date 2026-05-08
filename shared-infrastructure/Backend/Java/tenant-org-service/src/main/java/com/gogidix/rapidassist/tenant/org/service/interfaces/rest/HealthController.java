package com.gogidix.rapidassist.tenant.org.service.interfaces.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/**
 * REST Controller: HealthController
 *
 * Provides health check endpoints for the tenant organization service.
 * This controller follows the hexagonal architecture pattern.
 *
 * @author Rapid Assist
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/v1")
public class HealthController {

    /**
     * Health check endpoint.
     *
     * @return a map containing service health information
     */
    @GetMapping("/status")
    public Map<String, Object> status() {
        return Map.of(
                "service", "tenant-org-service",
                "status", "UP",
                "timestamp", Instant.now().toString(),
                "version", "1.0.0"
        );
    }
}
