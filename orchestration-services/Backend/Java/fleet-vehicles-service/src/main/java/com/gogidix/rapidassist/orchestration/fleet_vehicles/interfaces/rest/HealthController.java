package com.gogidix.rapidassist.orchestration.fleetorganization.interfaces.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Health check controller
 */
@RestController
@RequestMapping("/health")
public class HealthController {

    @GetMapping
    public Map<String, Object> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "fleet-organization-service");
        health.put("timestamp", LocalDateTime.now());
        health.put("port", 8086);
        return health;
    }

    @GetMapping("/readiness")
    public Map<String, String> readiness() {
        Map<String, String> readiness = new HashMap<>();
        readiness.put("status", "READY");
        return readiness;
    }

    @GetMapping("/liveness")
    public Map<String, String> liveness() {
        Map<String, String> liveness = new HashMap<>();
        liveness.put("status", "ALIVE");
        return liveness;
    }
}
