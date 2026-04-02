package com.gogidix.rapidassist.release.rollout.config.service.adapters.in.web;

import com.gogidix.rapidassist.release.rollout.config.service.application.ReleaseRolloutService;
import com.gogidix.rapidassist.release.rollout.config.service.domain.model.ReleaseRollout;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rollouts")
@CrossOrigin(origins = "*")
public class ReleaseRolloutController {

    private final ReleaseRolloutService rolloutService;

    public ReleaseRolloutController(ReleaseRolloutService rolloutService) {
        this.rolloutService = rolloutService;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "release-rollout-config-service"));
    }

    @GetMapping
    public ResponseEntity<List<ReleaseRollout>> getRollouts(@RequestParam String tenantId) {
        return ResponseEntity.ok(rolloutService.getRollouts(tenantId).join());
    }

    @GetMapping("/{rolloutId}")
    public ResponseEntity<ReleaseRollout> getRollout(@PathVariable String rolloutId) {
        return rolloutService.getRollout(rolloutId).join()
            .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ReleaseRollout> createRollout(@RequestBody CreateRolloutRequest request) {
        ReleaseRollout rollout = rolloutService.createRollout(
            request.tenantId(), request.releaseId(), request.version(), request.strategy(), request.createdBy()
        ).join();
        return ResponseEntity.created(URI.create("/api/rollouts/" + rollout.id())).body(rollout);
    }

    @PostMapping("/{rolloutId}/start")
    public ResponseEntity<ReleaseRollout> startRollout(@PathVariable String rolloutId, @RequestBody Map<String, String> request) {
        return rolloutService.startRollout(rolloutId, request.get("startedBy")).join()
            .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{rolloutId}/pause")
    public ResponseEntity<ReleaseRollout> pauseRollout(@PathVariable String rolloutId, @RequestBody Map<String, String> request) {
        return rolloutService.pauseRollout(rolloutId, request.get("pausedBy")).join()
            .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{rolloutId}/promote")
    public ResponseEntity<ReleaseRollout> promoteRollout(@PathVariable String rolloutId, @RequestBody Map<String, String> request) {
        return rolloutService.promoteRollout(rolloutId, request.get("promotedBy")).join()
            .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{rolloutId}/rollback")
    public ResponseEntity<ReleaseRollout> rollbackRollout(@PathVariable String rolloutId, @RequestBody Map<String, String> request) {
        return rolloutService.rollbackRollout(rolloutId, request.get("rolledBackBy")).join()
            .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    record CreateRolloutRequest(
        String tenantId, String releaseId, String version,
        ReleaseRollout.RolloutStrategy strategy, String createdBy
    ) {}
}
