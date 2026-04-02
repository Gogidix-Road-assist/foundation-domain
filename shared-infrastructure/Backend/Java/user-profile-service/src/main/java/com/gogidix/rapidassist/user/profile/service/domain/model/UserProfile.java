package com.gogidix.rapidassist.user.profile.service.domain.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Document(collection = "user_profiles")
public record UserProfile(

    @Id
    String id,

    @Field("userId")
    @NotBlank(message = "User ID is required")
    String userId,

    @Field("tenantId")
    @NotBlank(message = "Tenant ID is required")
    String tenantId,

    @Field("email")
    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is required")
    String email,

    @Field("firstName")
    String firstName,

    @Field("lastName")
    String lastName,

    @Field("phoneNumber")
    String phoneNumber,

    @Field("avatarUrl")
    String avatarUrl,

    @Field("preferences")
    UserPreferences preferences,

    @Field("settings")
    UserSettings settings,

    @Field("roles")
    Set<String> roles,

    @Field("permissions")
    Set<String> permissions,

    @Field("status")
    UserStatus status,

    @Field("metadata")
    Map<String, Object> metadata,

    @Field("createdBy")
    String createdBy,

    @Field("createdAt")
    Instant createdAt,

    @Field("updatedBy")
    String updatedBy,

    @Field("updatedAt")
    Instant updatedAt,

    @Field("lastLoginAt")
    Instant lastLoginAt

) {

    public static UserProfile create(
        String userId, String tenantId, String email,
        String firstName, String lastName, String createdBy
    ) {
        return new UserProfile(
            null, userId, tenantId, email, firstName, lastName,
            null, null,
            new UserPreferences(true, "light", "en"),
            new UserSettings(true, true, true, Map.of()),
            Set.of("USER"), Set.of(),
            UserStatus.ACTIVE, Map.of(),
            createdBy, Instant.now(),
            createdBy, Instant.now(),
            Instant.now()
        );
    }

    public UserProfile withStatus(UserStatus newStatus, String updatedBy) {
        return new UserProfile(
            id, userId, tenantId, email, firstName, lastName,
            phoneNumber, avatarUrl, preferences, settings,
            roles, permissions, newStatus, metadata,
            createdBy, createdAt, updatedBy, Instant.now(), lastLoginAt
        );
    }

    public UserProfile withRoles(Set<String> newRoles, String updatedBy) {
        return new UserProfile(
            id, userId, tenantId, email, firstName, lastName,
            phoneNumber, avatarUrl, preferences, settings,
            newRoles, permissions, status, metadata,
            createdBy, createdAt, updatedBy, Instant.now(), lastLoginAt
        );
    }

    public UserProfile withPermissions(Set<String> newPermissions, String updatedBy) {
        return new UserProfile(
            id, userId, tenantId, email, firstName, lastName,
            phoneNumber, avatarUrl, preferences, settings,
            roles, newPermissions, status, metadata,
            createdBy, createdAt, updatedBy, Instant.now(), lastLoginAt
        );
    }

    public UserProfile withLastLogin(Instant lastLogin) {
        return new UserProfile(
            id, userId, tenantId, email, firstName, lastName,
            phoneNumber, avatarUrl, preferences, settings,
            roles, permissions, status, metadata,
            createdBy, createdAt, updatedBy, updatedAt, lastLogin
        );
    }

    public enum UserStatus {
        ACTIVE, INACTIVE, SUSPENDED, PENDING_ACTIVATION, DELETED
    }

    public record UserPreferences(
        boolean notificationsEnabled,
        String theme,
        String language
    ) {}

    public record UserSettings(
        boolean twoFactorEnabled,
        boolean emailVerified,
        boolean phoneVerified,
        Map<String, Object> customSettings
    ) {}
}
