package com.gogidix.rapidassist.user.profile.service.infrastructure.persistence.mongodb;

import com.gogidix.rapidassist.user.profile.service.domain.model.UserProfile;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Document(collection = "user_profiles")
public record UserProfileDocument(
    String id,
    String userId,
    String tenantId,
    String email,
    String firstName,
    String lastName,
    String phoneNumber,
    String avatarUrl,
    PreferencesData preferences,
    SettingsData settings,
    Set<String> roles,
    Set<String> permissions,
    String status,
    Map<String, Object> metadata,
    String createdBy,
    Instant createdAt,
    String updatedBy,
    Instant updatedAt,
    Instant lastLoginAt
) {

    public static UserProfileDocument fromDomain(UserProfile profile) {
        return new UserProfileDocument(
            profile.id(),
            profile.userId(),
            profile.tenantId(),
            profile.email(),
            profile.firstName(),
            profile.lastName(),
            profile.phoneNumber(),
            profile.avatarUrl(),
            profile.preferences() != null ? new PreferencesData(profile.preferences()) : null,
            profile.settings() != null ? new SettingsData(profile.settings()) : null,
            profile.roles(),
            profile.permissions(),
            profile.status().name(),
            profile.metadata(),
            profile.createdBy(),
            profile.createdAt(),
            profile.updatedBy(),
            profile.updatedAt(),
            profile.lastLoginAt()
        );
    }

    public UserProfile toDomain() {
        return new UserProfile(
            id,
            userId,
            tenantId,
            email,
            firstName,
            lastName,
            phoneNumber,
            avatarUrl,
            preferences != null ? preferences.toDomain() : new UserProfile.UserPreferences(true, "light", "en"),
            settings != null ? settings.toDomain() : new UserProfile.UserSettings(true, true, true, Map.of()),
            roles,
            permissions,
            UserProfile.UserStatus.valueOf(status),
            metadata,
            createdBy,
            createdAt,
            updatedBy,
            updatedAt,
            lastLoginAt
        );
    }

    public record PreferencesData(
        boolean notificationsEnabled,
        String theme,
        String language
    ) {
        public PreferencesData(UserProfile.UserPreferences prefs) {
            this(prefs.notificationsEnabled(), prefs.theme(), prefs.language());
        }

        public UserProfile.UserPreferences toDomain() {
            return new UserProfile.UserPreferences(notificationsEnabled, theme, language);
        }
    }

    public record SettingsData(
        boolean twoFactorEnabled,
        boolean emailVerified,
        boolean phoneVerified,
        Map<String, Object> customSettings
    ) {
        public SettingsData(UserProfile.UserSettings settings) {
            this(
                settings.twoFactorEnabled(),
                settings.emailVerified(),
                settings.phoneVerified(),
                settings.customSettings()
            );
        }

        public UserProfile.UserSettings toDomain() {
            return new UserProfile.UserSettings(twoFactorEnabled, emailVerified, phoneVerified, customSettings);
        }
    }
}
