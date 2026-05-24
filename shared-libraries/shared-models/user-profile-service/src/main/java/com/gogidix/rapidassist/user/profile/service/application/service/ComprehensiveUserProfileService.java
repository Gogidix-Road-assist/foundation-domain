package com.gogidix.rapidassist.user.profile.service.application.service;

import com.gogidix.rapidassist.user.profile.service.domain.model.UserProfile;
import com.gogidix.rapidassist.user.profile.service.domain.port.in.UserProfileCommand;
import com.gogidix.rapidassist.user.profile.service.domain.port.in.UserProfileQuery;
import com.gogidix.rapidassist.user.profile.service.domain.port.out.UserProfileCacheStore;
import com.gogidix.rapidassist.user.profile.service.domain.port.out.UserProfileStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class ComprehensiveUserProfileService implements UserProfileCommand, UserProfileQuery {

    private static final Logger logger = LoggerFactory.getLogger(ComprehensiveUserProfileService.class);

    @Autowired
    private UserProfileStore userProfileStore;

    @Autowired
    private UserProfileCacheStore cacheStore;

    @PostConstruct
    public void init() {
        logger.info("Comprehensive User Profile Service initialized");
    }

    @Override
    public CompletableFuture<UserProfile> createProfile(CreateProfileCommand command) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<UserProfile> existing = userProfileStore.findByUserId(
                    command.tenantId(), command.userId()).join();

                if (existing.isPresent()) {
                    throw new IllegalStateException("User profile already exists: " + command.userId());
                }

                UserProfile profile = UserProfile.create(
                    command.userId(),
                    command.tenantId(),
                    command.email(),
                    command.firstName(),
                    command.lastName(),
                    command.createdBy()
                );

                UserProfile enhancedProfile = enhanceProfile(profile, command);

                UserProfile saved = userProfileStore.save(enhancedProfile).join();

                cacheStore.put(command.tenantId(), command.userId(), saved).join();

                logger.info("Created user profile: {} for tenant: {}",
                    command.userId(), command.tenantId());
                return saved;

            } catch (Exception e) {
                logger.error("Error creating user profile", e);
                throw new RuntimeException("Failed to create user profile", e);
            }
        });
    }

    @Override
    public CompletableFuture<Optional<UserProfile>> updateProfile(UpdateProfileCommand command) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<UserProfile> existingOpt = userProfileStore.findByUserId(
                    command.tenantId(), command.userId()).join();

                if (existingOpt.isEmpty()) {
                    return Optional.empty();
                }

                UserProfile existing = existingOpt.get();

                UserProfile updated = new UserProfile(
                    existing.id(),
                    existing.userId(),
                    existing.tenantId(),
                    command.email() != null ? command.email() : existing.email(),
                    command.firstName() != null ? command.firstName() : existing.firstName(),
                    command.lastName() != null ? command.lastName() : existing.lastName(),
                    command.phoneNumber() != null ? command.phoneNumber() : existing.phoneNumber(),
                    command.avatarUrl() != null ? command.avatarUrl() : existing.avatarUrl(),
                    command.preferences() != null ? command.preferences() : existing.preferences(),
                    command.settings() != null ? command.settings() : existing.settings(),
                    existing.roles(),
                    existing.permissions(),
                    existing.status(),
                    command.metadata() != null ? command.metadata() : existing.metadata(),
                    existing.createdBy(),
                    existing.createdAt(),
                    command.updatedBy(),
                    Instant.now(),
                    existing.lastLoginAt()
                );

                UserProfile saved = userProfileStore.save(updated).join();

                cacheStore.evict(command.tenantId(), command.userId()).join();
                cacheStore.put(command.tenantId(), command.userId(), saved).join();

                logger.info("Updated user profile: {} for tenant: {}",
                    command.userId(), command.tenantId());
                return Optional.of(saved);

            } catch (Exception e) {
                logger.error("Error updating user profile", e);
                throw new RuntimeException("Failed to update user profile", e);
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> deleteProfile(String tenantId, String userId, String deletedBy) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<UserProfile> existingOpt = userProfileStore.findByUserId(tenantId, userId).join();

                if (existingOpt.isEmpty()) {
                    return false;
                }

                userProfileStore.deleteByUserId(tenantId, userId).join();
                cacheStore.evict(tenantId, userId).join();

                logger.info("Deleted user profile: {} for tenant: {}", userId, tenantId);
                return true;

            } catch (Exception e) {
                logger.error("Error deleting user profile", e);
                return false;
            }
        });
    }

    @Override
    public CompletableFuture<Optional<UserProfile>> updateProfileStatus(
        String tenantId, String userId, UserProfile.UserStatus newStatus, String updatedBy) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<UserProfile> existingOpt = userProfileStore.findByUserId(tenantId, userId).join();

                if (existingOpt.isEmpty()) {
                    return Optional.empty();
                }

                UserProfile existing = existingOpt.get();
                UserProfile updated = existing.withStatus(newStatus, updatedBy);

                UserProfile saved = userProfileStore.save(updated).join();

                cacheStore.evict(tenantId, userId).join();
                cacheStore.put(tenantId, userId, saved).join();

                logger.info("Updated status for user profile: {} to {}", userId, newStatus);
                return Optional.of(saved);

            } catch (Exception e) {
                logger.error("Error updating user profile status", e);
                throw new RuntimeException("Failed to update user profile status", e);
            }
        });
    }

    @Override
    public CompletableFuture<Optional<UserProfile>> assignRoles(
        String tenantId, String userId, java.util.Set<String> roles, String updatedBy) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<UserProfile> existingOpt = userProfileStore.findByUserId(tenantId, userId).join();

                if (existingOpt.isEmpty()) {
                    return Optional.empty();
                }

                UserProfile existing = existingOpt.get();
                java.util.Set<String> mergedRoles = new java.util.HashSet<>(existing.roles());
                mergedRoles.addAll(roles);

                UserProfile updated = existing.withRoles(mergedRoles, updatedBy);

                UserProfile saved = userProfileStore.save(updated).join();

                cacheStore.evict(tenantId, userId).join();
                cacheStore.put(tenantId, userId, saved).join();

                logger.info("Assigned roles {} to user profile: {}", roles, userId);
                return Optional.of(saved);

            } catch (Exception e) {
                logger.error("Error assigning roles to user profile", e);
                throw new RuntimeException("Failed to assign roles to user profile", e);
            }
        });
    }

    @Override
    public CompletableFuture<Optional<UserProfile>> assignPermissions(
        String tenantId, String userId, java.util.Set<String> permissions, String updatedBy) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<UserProfile> existingOpt = userProfileStore.findByUserId(tenantId, userId).join();

                if (existingOpt.isEmpty()) {
                    return Optional.empty();
                }

                UserProfile existing = existingOpt.get();
                java.util.Set<String> mergedPermissions = new java.util.HashSet<>(existing.permissions());
                mergedPermissions.addAll(permissions);

                UserProfile updated = existing.withPermissions(mergedPermissions, updatedBy);

                UserProfile saved = userProfileStore.save(updated).join();

                cacheStore.evict(tenantId, userId).join();
                cacheStore.put(tenantId, userId, saved).join();

                logger.info("Assigned permissions {} to user profile: {}", permissions, userId);
                return Optional.of(saved);

            } catch (Exception e) {
                logger.error("Error assigning permissions to user profile", e);
                throw new RuntimeException("Failed to assign permissions to user profile", e);
            }
        });
    }

    @Override
    public CompletableFuture<Optional<UserProfile>> removeRoles(
        String tenantId, String userId, java.util.Set<String> roles, String updatedBy) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<UserProfile> existingOpt = userProfileStore.findByUserId(tenantId, userId).join();

                if (existingOpt.isEmpty()) {
                    return Optional.empty();
                }

                UserProfile existing = existingOpt.get();
                java.util.Set<String> remainingRoles = new java.util.HashSet<>(existing.roles());
                remainingRoles.removeAll(roles);

                UserProfile updated = existing.withRoles(remainingRoles, updatedBy);

                UserProfile saved = userProfileStore.save(updated).join();

                cacheStore.evict(tenantId, userId).join();
                cacheStore.put(tenantId, userId, saved).join();

                logger.info("Removed roles {} from user profile: {}", roles, userId);
                return Optional.of(saved);

            } catch (Exception e) {
                logger.error("Error removing roles from user profile", e);
                throw new RuntimeException("Failed to remove roles from user profile", e);
            }
        });
    }

    @Override
    public CompletableFuture<Optional<UserProfile>> removePermissions(
        String tenantId, String userId, java.util.Set<String> permissions, String updatedBy) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<UserProfile> existingOpt = userProfileStore.findByUserId(tenantId, userId).join();

                if (existingOpt.isEmpty()) {
                    return Optional.empty();
                }

                UserProfile existing = existingOpt.get();
                java.util.Set<String> remainingPermissions = new java.util.HashSet<>(existing.permissions());
                remainingPermissions.removeAll(permissions);

                UserProfile updated = existing.withPermissions(remainingPermissions, updatedBy);

                UserProfile saved = userProfileStore.save(updated).join();

                cacheStore.evict(tenantId, userId).join();
                cacheStore.put(tenantId, userId, saved).join();

                logger.info("Removed permissions {} from user profile: {}", permissions, userId);
                return Optional.of(saved);

            } catch (Exception e) {
                logger.error("Error removing permissions from user profile", e);
                throw new RuntimeException("Failed to remove permissions from user profile", e);
            }
        });
    }

    @Override
    public CompletableFuture<Optional<UserProfile>> updatePreferences(
        String tenantId, String userId, UserProfile.UserPreferences preferences, String updatedBy) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<UserProfile> existingOpt = userProfileStore.findByUserId(tenantId, userId).join();

                if (existingOpt.isEmpty()) {
                    return Optional.empty();
                }

                UserProfile existing = existingOpt.get();
                UserProfile updated = new UserProfile(
                    existing.id(), existing.userId(), existing.tenantId(), existing.email(),
                    existing.firstName(), existing.lastName(), existing.phoneNumber(), existing.avatarUrl(),
                    preferences, existing.settings(), existing.roles(), existing.permissions(),
                    existing.status(), existing.metadata(), existing.createdBy(), existing.createdAt(),
                    updatedBy, Instant.now(), existing.lastLoginAt()
                );

                UserProfile saved = userProfileStore.save(updated).join();

                cacheStore.evict(tenantId, userId).join();
                cacheStore.put(tenantId, userId, saved).join();

                logger.info("Updated preferences for user profile: {}", userId);
                return Optional.of(saved);

            } catch (Exception e) {
                logger.error("Error updating user profile preferences", e);
                throw new RuntimeException("Failed to update user profile preferences", e);
            }
        });
    }

    @Override
    public CompletableFuture<Optional<UserProfile>> updateSettings(
        String tenantId, String userId, UserProfile.UserSettings settings, String updatedBy) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<UserProfile> existingOpt = userProfileStore.findByUserId(tenantId, userId).join();

                if (existingOpt.isEmpty()) {
                    return Optional.empty();
                }

                UserProfile existing = existingOpt.get();
                UserProfile updated = new UserProfile(
                    existing.id(), existing.userId(), existing.tenantId(), existing.email(),
                    existing.firstName(), existing.lastName(), existing.phoneNumber(), existing.avatarUrl(),
                    existing.preferences(), settings, existing.roles(), existing.permissions(),
                    existing.status(), existing.metadata(), existing.createdBy(), existing.createdAt(),
                    updatedBy, Instant.now(), existing.lastLoginAt()
                );

                UserProfile saved = userProfileStore.save(updated).join();

                cacheStore.evict(tenantId, userId).join();
                cacheStore.put(tenantId, userId, saved).join();

                logger.info("Updated settings for user profile: {}", userId);
                return Optional.of(saved);

            } catch (Exception e) {
                logger.error("Error updating user profile settings", e);
                throw new RuntimeException("Failed to update user profile settings", e);
            }
        });
    }

    @Override
    public CompletableFuture<Optional<UserProfile>> updateLastLogin(String tenantId, String userId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<UserProfile> existingOpt = userProfileStore.findByUserId(tenantId, userId).join();

                if (existingOpt.isEmpty()) {
                    return Optional.empty();
                }

                UserProfile existing = existingOpt.get();
                UserProfile updated = existing.withLastLogin(Instant.now());

                UserProfile saved = userProfileStore.save(updated).join();

                cacheStore.evict(tenantId, userId).join();
                cacheStore.put(tenantId, userId, saved).join();

                logger.debug("Updated last login for user profile: {}", userId);
                return Optional.of(saved);

            } catch (Exception e) {
                logger.error("Error updating last login for user profile", e);
                throw new RuntimeException("Failed to update last login for user profile", e);
            }
        });
    }

    @Override
    public CompletableFuture<List<UserProfile>> bulkCreateProfiles(BulkCreateProfilesCommand command) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<UserProfile> results = command.profiles().stream()
                    .map(createProfileCommand -> createProfile(createProfileCommand).join())
                    .toList();

                logger.info("Bulk created {} user profiles for tenant: {}",
                    results.size(), command.tenantId());
                return results;

            } catch (Exception e) {
                logger.error("Error bulk creating user profiles", e);
                throw new RuntimeException("Failed to bulk create user profiles", e);
            }
        });
    }

    @Override
    public CompletableFuture<Optional<UserProfile>> getProfileByUserId(String tenantId, String userId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return cacheStore.get(tenantId, userId).join()
                    .or(() -> userProfileStore.findByUserId(tenantId, userId).join());
            } catch (Exception e) {
                logger.error("Error getting user profile by userId", e);
                return Optional.empty();
            }
        });
    }

    @Override
    public CompletableFuture<Optional<UserProfile>> getProfileByEmail(String tenantId, String email) {
        return userProfileStore.findByEmail(tenantId, email);
    }

    @Override
    public CompletableFuture<List<UserProfile>> getProfilesByTenant(String tenantId) {
        return userProfileStore.findByTenant(tenantId);
    }

    @Override
    public CompletableFuture<List<UserProfile>> getProfilesByRole(String tenantId, String role) {
        return userProfileStore.findByRole(tenantId, role);
    }

    @Override
    public CompletableFuture<List<UserProfile>> getProfilesByStatus(
        String tenantId, UserProfile.UserStatus status) {
        return userProfileStore.findByStatus(tenantId, status);
    }

    @Override
    public CompletableFuture<List<UserProfile>> getProfilesByRoles(String tenantId, List<String> roles) {
        return userProfileStore.findByRoles(tenantId, roles);
    }

    @Override
    public CompletableFuture<List<UserProfile>> searchByEmail(String tenantId, String emailPattern) {
        return userProfileStore.searchByEmailPattern(tenantId, emailPattern);
    }

    @Override
    public CompletableFuture<List<UserProfile>> searchByName(String tenantId, String namePattern) {
        return userProfileStore.searchByNamePattern(tenantId, namePattern, namePattern);
    }

    @Override
    public CompletableFuture<List<UserProfile>> searchProfiles(String tenantId, String keyword) {
        return userProfileStore.searchByKeyword(tenantId, keyword);
    }

    @Override
    public CompletableFuture<List<UserProfile>> getActiveProfiles(String tenantId) {
        return userProfileStore.findActive(tenantId);
    }

    @Override
    public CompletableFuture<List<UserProfile>> getProfilesWithPermission(String tenantId, String permission) {
        return userProfileStore.findByPermission(tenantId, permission);
    }

    @Override
    public CompletableFuture<Long> countProfilesByTenant(String tenantId) {
        return userProfileStore.countByTenant(tenantId);
    }

    @Override
    public CompletableFuture<Long> countProfilesByStatus(
        String tenantId, UserProfile.UserStatus status) {
        return userProfileStore.countByStatus(tenantId, status);
    }

    private UserProfile enhanceProfile(UserProfile profile, CreateProfileCommand command) {
        return new UserProfile(
            profile.id(),
            profile.userId(),
            profile.tenantId(),
            profile.email(),
            command.firstName(),
            command.lastName(),
            command.phoneNumber(),
            command.avatarUrl(),
            command.preferences() != null ? command.preferences() : profile.preferences(),
            command.settings() != null ? command.settings() : profile.settings(),
            command.roles() != null ? command.roles() : profile.roles(),
            command.permissions() != null ? command.permissions() : profile.permissions(),
            profile.status(),
            command.metadata() != null ? command.metadata() : profile.metadata(),
            profile.createdBy(),
            profile.createdAt(),
            profile.updatedBy(),
            profile.updatedAt(),
            profile.lastLoginAt()
        );
    }
}
