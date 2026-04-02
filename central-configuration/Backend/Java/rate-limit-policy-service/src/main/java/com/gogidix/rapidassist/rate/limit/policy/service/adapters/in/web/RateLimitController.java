package com.gogidix.rapidassist.rate.limit.policy.service.adapters.in.web;

import com.gogidix.rapidassist.rate.limit.policy.service.application.RateLimitService;
import com.gogidix.rapidassist.rate.limit.policy.service.domain.model.RateLimitPolicy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rate-limits")
@CrossOrigin(origins = "*")
public class RateLimitController {

    private final RateLimitService rateLimitService;

    public RateLimitController(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "rate-limit-policy-service"));
    }

    @GetMapping("/policies")
    public ResponseEntity<List<RateLimitPolicy>> getPolicies(@RequestParam String tenantId) {
        return ResponseEntity.ok(rateLimitService.getPolicies(tenantId).join());
    }

    @GetMapping("/policies/{policyKey}")
    public ResponseEntity<RateLimitPolicy> getPolicy(@RequestParam String tenantId, @PathVariable String policyKey) {
        return ResponseEntity.ok(rateLimitService.getPolicy(tenantId, policyKey).join());
    }

    @PostMapping("/policies")
    public ResponseEntity<RateLimitPolicy> createPolicy(@RequestBody CreatePolicyRequest request) {
        RateLimitPolicy policy = rateLimitService.createPolicy(
            request.tenantId(), request.policyKey(), request.name(), request.config(), request.createdBy()
        ).join();
        return ResponseEntity.created(URI.create("/api/rate-limits/policies/" + policy.id())).body(policy);
    }

    @DeleteMapping("/policies/{policyId}")
    public ResponseEntity<Void> deletePolicy(@PathVariable String policyId) {
        rateLimitService.deletePolicy(policyId).join();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/check")
    public ResponseEntity<Map<String, Object>> checkRateLimit(@RequestBody CheckRequest request) {
        boolean allowed = rateLimitService.checkRateLimit(request.tenantId(), request.identifier(), request.endpoint()).join();
        return ResponseEntity.ok(Map.of("allowed", allowed));
    }

    @PostMapping("/reset")
    public ResponseEntity<Void> resetCounters(@RequestBody ResetRequest request) {
        rateLimitService.resetCounters(request.tenantId(), request.identifier()).join();
        return ResponseEntity.ok().build();
    }

    record CreatePolicyRequest(
        String tenantId, String policyKey, String name,
        RateLimitPolicy.RateLimitConfig config, String createdBy
    ) {}

    record CheckRequest(String tenantId, String identifier, String endpoint) {}
    record ResetRequest(String tenantId, String identifier) {}
}
