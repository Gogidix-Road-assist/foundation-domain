package com.gogidix.rapidassist.release.rollout.config.service.application;

import com.gogidix.rapidassist.release.rollout.config.service.domain.model.ReleaseRollout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class ReleaseRolloutService {

    private static final Logger logger = LoggerFactory.getLogger(ReleaseRolloutService.class);

    public CompletableFuture<ReleaseRollout> createRollout(String tenantId, String releaseId, String version,
                                                            ReleaseRollout.RolloutStrategy strategy, String createdBy) {
        return CompletableFuture.completedFuture(
            ReleaseRollout.builder()
                .tenantId(tenantId)
                .releaseId(releaseId)
                .version(version)
                .strategy(strategy)
                .config(ReleaseRollout.RolloutConfig.standard())
                .createdBy(createdBy)
                .build()
        );
    }

    public CompletableFuture<List<ReleaseRollout>> getRollouts(String tenantId) {
        return CompletableFuture.completedFuture(List.of());
    }

    public CompletableFuture<Optional<ReleaseRollout>> getRollout(String rolloutId) {
        return CompletableFuture.completedFuture(Optional.empty());
    }

    public CompletableFuture<Optional<ReleaseRollout>> startRollout(String rolloutId, String startedBy) {
        return CompletableFuture.completedFuture(Optional.empty());
    }

    public CompletableFuture<Optional<ReleaseRollout>> pauseRollout(String rolloutId, String pausedBy) {
        return CompletableFuture.completedFuture(Optional.empty());
    }

    public CompletableFuture<Optional<ReleaseRollout>> promoteRollout(String rolloutId, String promotedBy) {
        return CompletableFuture.completedFuture(Optional.empty());
    }

    public CompletableFuture<Optional<ReleaseRollout>> rollbackRollout(String rolloutId, String rolledBackBy) {
        return CompletableFuture.completedFuture(Optional.empty());
    }
}
