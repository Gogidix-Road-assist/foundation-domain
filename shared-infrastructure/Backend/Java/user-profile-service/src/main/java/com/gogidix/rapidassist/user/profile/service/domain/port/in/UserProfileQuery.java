package com.gogidix.rapidassist.user.profile.service.domain.port.in;

import com.gogidix.rapidassist.user.profile.service.domain.model.UserProfile;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface UserProfileQuery {

    CompletableFuture<Optional<UserProfile>> getProfileByUserId(String tenantId, String userId);

    CompletableFuture<Optional<UserProfile>> getProfileByEmail(String tenantId, String email);

    CompletableFuture<List<UserProfile>> getProfilesByTenant(String tenantId);

    CompletableFuture<List<UserProfile>> getProfilesByRole(String tenantId, String role);

    CompletableFuture<List<UserProfile>> getProfilesByStatus(String tenantId, UserProfile.UserStatus status);

    CompletableFuture<List<UserProfile>> getProfilesByRoles(String tenantId, List<String> roles);

    CompletableFuture<List<UserProfile>> searchByEmail(String tenantId, String emailPattern);

    CompletableFuture<List<UserProfile>> searchByName(String tenantId, String namePattern);

    CompletableFuture<List<UserProfile>> searchProfiles(String tenantId, String keyword);

    CompletableFuture<List<UserProfile>> getActiveProfiles(String tenantId);

    CompletableFuture<List<UserProfile>> getProfilesWithPermission(String tenantId, String permission);

    CompletableFuture<Long> countProfilesByTenant(String tenantId);

    CompletableFuture<Long> countProfilesByStatus(String tenantId, UserProfile.UserStatus status);
}
