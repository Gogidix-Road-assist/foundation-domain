package com.gogidix.rapidassist.geo.location.service.interfaces.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/**
 * REST Controller: HealthController
 *
 * Provides health check endpoints for the GeoLocation service.
 * This controller follows the hexagonal architecture pattern.
 *
 * @author Rapid Assist
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/v1")
public class HealthController {

    @GetMapping("/status")
    public Map<String, Object> status() {
        return Map.of(
                "service", "geo-location-service",
                "status", "UP",
                "timestamp", Instant.now().toString(),
                "version", "1.0.0"
        );
    }
}
