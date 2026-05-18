package com.gogidix.rapidassist.api.gateway.interfaces.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
public class FallbackController {

    private static final Logger log = LoggerFactory.getLogger(FallbackController.class);

    @GetMapping("/api/gateway/fallback")
    public ResponseEntity<Map<String, Object>> fallback() {
        log.warn("Circuit breaker fallback triggered - downstream service unavailable");
        return ResponseEntity.ok(Map.of(
                "status", "SERVICE_UNAVAILABLE",
                "message", "Service temporarily unavailable. Please try again later.",
                "timestamp", Instant.now().toString()
        ));
    }
}
