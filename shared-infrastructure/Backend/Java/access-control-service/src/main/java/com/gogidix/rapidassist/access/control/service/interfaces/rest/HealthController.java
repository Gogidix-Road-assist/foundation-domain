package com.gogidix.rapidassist.access.control.service.interfaces.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/**
 * REST Controller: HealthController
 *
 * Provides health check endpoints for the service.
 */
@RestController
@RequestMapping("/api/v1")
public class HealthController {

    @GetMapping("/status")
    public Map<String, Object> status() {
        return Map.of(
                "service", "access-control-service",
                "status", "UP",
                "timestamp", Instant.now().toString(),
                "version", "1.0.0"
        );
    }
}
