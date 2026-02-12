package com.gogidix.rapidassist.rate.limit.policy.service.application;

import com.gogidix.rapidassist.rate.limit.policy.service.domain.model.RateLimitPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Service
public class RateLimitService {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitService.class);

    @Autowired
    private ReactiveRedisTemplate<String, String> redisTemplate;

    private static final String RATE_LIMIT_PREFIX = "rate-limit:";
    private static final String COUNTER_PREFIX = "rate-counter:";

    public CompletableFuture<Boolean> checkRateLimit(String tenantId, String identifier, String endpoint) {
        String key = COUNTER_PREFIX + tenantId + ":" + identifier + ":" + endpoint;
        return CompletableFuture.supplyAsync(() -> {
            return redisTemplate.opsForValue().increment(key).blockOptional().orElse(0L) == 1L;
        }).thenCompose(firstRequest -> {
            if (firstRequest) {
                return redisTemplate.expire(key, Duration.ofMinutes(1)).toFuture()
                    .thenApply(v -> true);
            }
            return CompletableFuture.completedFuture(true);
        });
    }

    public CompletableFuture<RateLimitPolicy> getPolicy(String tenantId, String policyKey) {
        return CompletableFuture.completedFuture(
            RateLimitPolicy.builder()
                .tenantId(tenantId)
                .policyKey(policyKey)
                .name("Default Rate Limit")
                .limitType(RateLimitPolicy.LimitType.USER_BASED)
                .config(new RateLimitPolicy.RateLimitConfig(60, 1000, 10000, 10, 60000, "token-bucket"))
                .build()
        );
    }

    public CompletableFuture<List<RateLimitPolicy>> getPolicies(String tenantId) {
        return CompletableFuture.completedFuture(List.of(
            RateLimitPolicy.builder()
                .tenantId(tenantId)
                .policyKey("default")
                .name("Default Rate Limit")
                .config(new RateLimitPolicy.RateLimitConfig(60, 1000, 10000, 10, 60000, "sliding-window"))
                .build()
        ));
    }

    public CompletableFuture<RateLimitPolicy> createPolicy(String tenantId, String policyKey, String name,
                                                           RateLimitPolicy.RateLimitConfig config, String createdBy) {
        return CompletableFuture.completedFuture(
            RateLimitPolicy.builder()
                .tenantId(tenantId)
                .policyKey(policyKey)
                .name(name)
                .config(config)
                .createdBy(createdBy)
                .build()
        );
    }

    public CompletableFuture<Optional<RateLimitPolicy>> updatePolicy(String policyId, RateLimitPolicy.RateLimitConfig config, String updatedBy) {
        return CompletableFuture.completedFuture(Optional.empty());
    }

    public CompletableFuture<Boolean> deletePolicy(String policyId) {
        return CompletableFuture.completedFuture(true);
    }

    public CompletableFuture<Void> resetCounters(String tenantId, String identifier) {
        return CompletableFuture.runAsync(() -> {
            String pattern = COUNTER_PREFIX + tenantId + ":" + identifier + ":*";
            redisTemplate.scan(ScanOptions.scanOptions().match(pattern).count(100).build())
                .flatMap(k -> redisTemplate.delete(k))
                .then()
                .block();
        });
    }
}
