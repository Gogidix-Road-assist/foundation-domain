package com.gogidix.rapidassist.user.profile.service.domain.port.in;

import com.gogidix.rapidassist.user.profile.service.domain.model.UserProfile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface UserProfileCommand {

    CompletableFuture<UserProfile> createProfile(CreateProfileCommand command);

    CompletableFuture<Optional<UserProfile>> updateProfile(UpdateProfileCommand command);

    CompletableFuture<Boolean> deleteProfile(String tenantId, String userId, String deletedBy);

    CompletableFuture<Optional<UserProfile>> updateProfileStatus(
        String tenantId, String userId, UserProfile.UserStatus newStatus, String updatedBy);

    CompletableFuture<Optional<UserProfile>> assignRoles(
        String tenantId, String userId, Set<String> roles, String updatedBy);

    CompletableFuture<Optional<UserProfile>> assignPermissions(
        String tenantId, String userId, Set<String> permissions, String updatedBy);

    CompletableFuture<Optional<UserProfile>> removeRoles(
        String tenantId, String userId, Set<String> roles, String updatedBy);

    CompletableFuture<Optional<UserProfile>> removePermissions(
        String tenantId, String userId, Set<String> permissions, String updatedBy);

    CompletableFuture<Optional<UserProfile>> updatePreferences(
        String tenantId, String userId, UserProfile.UserPreferences preferences, String updatedBy);

    CompletableFuture<Optional<UserProfile>> updateSettings(
        String tenantId, String userId, UserProfile.UserSettings settings, String updatedBy);

    CompletableFuture<Optional<UserProfile>> updateLastLogin(
        String tenantId, String userId);

    CompletableFuture<List<UserProfile>> bulkCreateProfiles(BulkCreateProfilesCommand command);

    record CreateProfileCommand(
        String userId,
        String tenantId,
        String email,
        String firstName,
        String lastName,
        String phoneNumber,
        String avatarUrl,
        UserProfile.UserPreferences preferences,
        UserProfile.UserSettings settings,
        Set<String> roles,
        Set<String> permissions,
        Map<String, Object> metadata,
        String createdBy
    ) {}

    record UpdateProfileCommand(
        String tenantId,
        String userId,
        String email,
        String firstName,
        String lastName,
        String phoneNumber,
        String avatarUrl,
        UserProfile.UserPreferences preferences,
        UserProfile.UserSettings settings,
        Map<String, Object> metadata,
        String updatedBy
    ) {}

    record BulkCreateProfilesCommand(
        String tenantId,
        List<CreateProfileCommand> profiles,
        String createdBy
    ) {}
}
