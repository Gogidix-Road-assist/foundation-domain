package com.gogidix.rapidassist.user.profile.service.domain.port.out;

import com.gogidix.rapidassist.user.profile.service.domain.model.UserProfile;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface UserProfileStore {

    CompletableFuture<UserProfile> save(UserProfile profile);

    CompletableFuture<Optional<UserProfile>> findById(String id);

    CompletableFuture<Optional<UserProfile>> findByUserId(String tenantId, String userId);

    CompletableFuture<Optional<UserProfile>> findByEmail(String tenantId, String email);

    CompletableFuture<List<UserProfile>> findByTenant(String tenantId);

    CompletableFuture<List<UserProfile>> findByRole(String tenantId, String role);

    CompletableFuture<List<UserProfile>> findByStatus(String tenantId, UserProfile.UserStatus status);

    CompletableFuture<List<UserProfile>> findByRoles(String tenantId, List<String> roles);

    CompletableFuture<List<UserProfile>> searchByEmailPattern(String tenantId, String emailPattern);

    CompletableFuture<List<UserProfile>> searchByNamePattern(String tenantId, String firstName, String lastName);

    CompletableFuture<List<UserProfile>> searchByKeyword(String tenantId, String keyword);

    CompletableFuture<List<UserProfile>> findActive(String tenantId);

    CompletableFuture<List<UserProfile>> findByPermission(String tenantId, String permission);

    CompletableFuture<Boolean> deleteById(String id);

    CompletableFuture<Boolean> deleteByUserId(String tenantId, String userId);

    CompletableFuture<Long> countByTenant(String tenantId);

    CompletableFuture<Long> countByStatus(String tenantId, UserProfile.UserStatus status);

    @Deprecated
    default Optional<UserProfile> find(String tenantId, String subject) {
        return findByUserId(tenantId, subject).join();
    }

    @Deprecated
    default UserProfile upsert(UserProfile profile) {
        return save(profile).join();
    }
}
