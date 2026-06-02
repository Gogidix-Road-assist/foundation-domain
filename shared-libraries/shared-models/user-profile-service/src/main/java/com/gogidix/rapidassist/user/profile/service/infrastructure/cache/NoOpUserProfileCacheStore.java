package com.gogidix.rapidassist.user.profile.service.infrastructure.cache;

import com.gogidix.rapidassist.user.profile.service.domain.model.UserProfile;
import com.gogidix.rapidassist.user.profile.service.domain.port.out.UserProfileCacheStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Component
@ConditionalOnMissingBean(RedisTemplate.class)
public class NoOpUserProfileCacheStore implements UserProfileCacheStore {

    @Override
    public CompletableFuture<Void> put(String tenantId, String userId, UserProfile profile) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Optional<UserProfile>> get(String tenantId, String userId) {
        return CompletableFuture.completedFuture(Optional.empty());
    }

    @Override
    public CompletableFuture<List<UserProfile>> getByTenant(String tenantId) {
        return CompletableFuture.completedFuture(List.of());
    }

    @Override
    public CompletableFuture<Void> evict(String tenantId, String userId) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> evictByTenant(String tenantId) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> evictByRole(String tenantId, String role) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> evictAll() {
        return CompletableFuture.completedFuture(null);
    }
}
