package com.gogidix.rapidassist.user.profile.service.domain.port.out;

import com.gogidix.rapidassist.user.profile.service.domain.model.UserProfile;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface UserProfileCacheStore {

    CompletableFuture<Void> put(String tenantId, String userId, UserProfile profile);

    CompletableFuture<Optional<UserProfile>> get(String tenantId, String userId);

    CompletableFuture<List<UserProfile>> getByTenant(String tenantId);

    CompletableFuture<Void> evict(String tenantId, String userId);

    CompletableFuture<Void> evictByTenant(String tenantId);

    CompletableFuture<Void> evictByRole(String tenantId, String role);

    CompletableFuture<Void> evictAll();
}
