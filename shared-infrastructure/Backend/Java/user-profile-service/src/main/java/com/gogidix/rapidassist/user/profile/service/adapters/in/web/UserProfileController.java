package com.gogidix.rapidassist.user.profile.service.adapters.in.web;

import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import com.gogidix.rapidassist.user.profile.service.application.service.ComprehensiveUserProfileService;
import com.gogidix.rapidassist.user.profile.service.domain.model.UserProfile;
import com.gogidix.rapidassist.user.profile.service.domain.port.in.UserProfileCommand;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/user-profile")
public class UserProfileController {

    private static final Logger logger = LoggerFactory.getLogger(UserProfileController.class);

    @Autowired
    private ComprehensiveUserProfileService userProfileService;

    @PostMapping
    public CompletableFuture<ResponseEntity<UserProfile>> createProfile(
        @Valid @RequestBody CreateProfileRequest request
    ) {
        logger.info("Creating user profile: {} for tenant: {}", request.userId(), request.tenantId());

        UserProfileCommand.CreateProfileCommand command = new UserProfileCommand.CreateProfileCommand(
            request.userId(),
            request.tenantId(),
            request.email(),
            request.firstName(),
            request.lastName(),
            request.phoneNumber(),
            request.avatarUrl(),
            request.preferences(),
            request.settings(),
            request.roles(),
            request.permissions(),
            request.metadata(),
            request.createdBy()
        );

        return userProfileService.createProfile(command)
            .thenApply(profile -> ResponseEntity.status(HttpStatus.CREATED).body(profile));
    }

    @PutMapping("/{userId}")
    public CompletableFuture<ResponseEntity<UserProfile>> updateProfile(
        @PathVariable String userId,
        @Valid @RequestBody UpdateProfileRequest request
    ) {
        logger.info("Updating user profile: {} for tenant: {}", userId, request.tenantId());

        UserProfileCommand.UpdateProfileCommand command = new UserProfileCommand.UpdateProfileCommand(
            request.tenantId(),
            userId,
            request.email(),
            request.firstName(),
            request.lastName(),
            request.phoneNumber(),
            request.avatarUrl(),
            request.preferences(),
            request.settings(),
            request.metadata(),
            request.updatedBy()
        );

        return userProfileService.updateProfile(command)
            .thenApply(profileOpt -> profileOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()));
    }

    @DeleteMapping("/{userId}")
    public CompletableFuture<ResponseEntity<Void>> deleteProfile(
        @PathVariable String userId,
        @RequestParam String tenantId,
        @RequestParam String deletedBy
    ) {
        logger.info("Deleting user profile: {} for tenant: {}", userId, tenantId);

        return userProfileService.deleteProfile(tenantId, userId, deletedBy)
            .thenApply(deleted -> deleted ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build());
    }

    @PostMapping("/{userId}/status")
    public CompletableFuture<ResponseEntity<UserProfile>> updateStatus(
        @PathVariable String userId,
        @RequestParam String tenantId,
        @RequestParam String status,
        @RequestParam String updatedBy
    ) {
        logger.info("Updating status for user profile: {} to {}", userId, status);

        return userProfileService.updateProfileStatus(
            tenantId, userId, UserProfile.UserStatus.valueOf(status), updatedBy)
            .thenApply(profileOpt -> profileOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()));
    }

    @PostMapping("/{userId}/roles")
    public CompletableFuture<ResponseEntity<UserProfile>> assignRoles(
        @PathVariable String userId,
        @RequestParam String tenantId,
        @RequestBody List<String> roles,
        @RequestParam String updatedBy
    ) {
        logger.info("Assigning roles {} to user profile: {}", roles, userId);

        return userProfileService.assignRoles(tenantId, userId, java.util.Set.copyOf(roles), updatedBy)
            .thenApply(profileOpt -> profileOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()));
    }

    @DeleteMapping("/{userId}/roles")
    public CompletableFuture<ResponseEntity<UserProfile>> removeRoles(
        @PathVariable String userId,
        @RequestParam String tenantId,
        @RequestBody List<String> roles,
        @RequestParam String updatedBy
    ) {
        logger.info("Removing roles {} from user profile: {}", roles, userId);

        return userProfileService.removeRoles(tenantId, userId, java.util.Set.copyOf(roles), updatedBy)
            .thenApply(profileOpt -> profileOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()));
    }

    @PostMapping("/{userId}/permissions")
    public CompletableFuture<ResponseEntity<UserProfile>> assignPermissions(
        @PathVariable String userId,
        @RequestParam String tenantId,
        @RequestBody List<String> permissions,
        @RequestParam String updatedBy
    ) {
        logger.info("Assigning permissions {} to user profile: {}", permissions, userId);

        return userProfileService.assignPermissions(tenantId, userId, java.util.Set.copyOf(permissions), updatedBy)
            .thenApply(profileOpt -> profileOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()));
    }

    @DeleteMapping("/{userId}/permissions")
    public CompletableFuture<ResponseEntity<UserProfile>> removePermissions(
        @PathVariable String userId,
        @RequestParam String tenantId,
        @RequestBody List<String> permissions,
        @RequestParam String updatedBy
    ) {
        logger.info("Removing permissions {} from user profile: {}", permissions, userId);

        return userProfileService.removePermissions(tenantId, userId, java.util.Set.copyOf(permissions), updatedBy)
            .thenApply(profileOpt -> profileOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()));
    }

    @PostMapping("/{userId}/preferences")
    public CompletableFuture<ResponseEntity<UserProfile>> updatePreferences(
        @PathVariable String userId,
        @RequestParam String tenantId,
        @RequestBody UserProfile.UserPreferences preferences,
        @RequestParam String updatedBy
    ) {
        logger.info("Updating preferences for user profile: {}", userId);

        return userProfileService.updatePreferences(tenantId, userId, preferences, updatedBy)
            .thenApply(profileOpt -> profileOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()));
    }

    @PostMapping("/{userId}/settings")
    public CompletableFuture<ResponseEntity<UserProfile>> updateSettings(
        @PathVariable String userId,
        @RequestParam String tenantId,
        @RequestBody UserProfile.UserSettings settings,
        @RequestParam String updatedBy
    ) {
        logger.info("Updating settings for user profile: {}", userId);

        return userProfileService.updateSettings(tenantId, userId, settings, updatedBy)
            .thenApply(profileOpt -> profileOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()));
    }

    @PostMapping("/{userId}/login")
    public CompletableFuture<ResponseEntity<UserProfile>> updateLastLogin(
        @PathVariable String userId,
        @RequestParam String tenantId
    ) {
        logger.info("Updating last login for user profile: {}", userId);

        return userProfileService.updateLastLogin(tenantId, userId)
            .thenApply(profileOpt -> profileOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()));
    }

    @PostMapping("/bulk")
    public CompletableFuture<ResponseEntity<List<UserProfile>>> bulkCreateProfiles(
        @Valid @RequestBody BulkCreateProfilesRequest request
    ) {
        logger.info("Bulk creating {} user profiles for tenant: {}",
            request.profiles().size(), request.tenantId());

        List<UserProfileCommand.CreateProfileCommand> commands = request.profiles().stream()
            .map(req -> new UserProfileCommand.CreateProfileCommand(
                req.userId(),
                request.tenantId(),
                req.email(),
                req.firstName(),
                req.lastName(),
                req.phoneNumber(),
                req.avatarUrl(),
                req.preferences(),
                req.settings(),
                req.roles(),
                req.permissions(),
                req.metadata(),
                request.createdBy()
            ))
            .toList();

        UserProfileCommand.BulkCreateProfilesCommand command =
            new UserProfileCommand.BulkCreateProfilesCommand(
                request.tenantId(),
                commands,
                request.createdBy()
            );

        return userProfileService.bulkCreateProfiles(command)
            .thenApply(profiles -> ResponseEntity.status(HttpStatus.CREATED).body(profiles));
    }

    @GetMapping("/{userId}")
    public CompletableFuture<ResponseEntity<UserProfile>> getProfile(
        @PathVariable String userId,
        @RequestParam String tenantId
    ) {
        return userProfileService.getProfileByUserId(tenantId, userId)
            .thenApply(profileOpt -> profileOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()));
    }

    @GetMapping
    public CompletableFuture<ResponseEntity<List<UserProfile>>> getProfilesByTenant(
        @RequestParam String tenantId
    ) {
        return userProfileService.getProfilesByTenant(tenantId)
            .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/email")
    public CompletableFuture<ResponseEntity<UserProfile>> getProfileByEmail(
        @RequestParam String tenantId,
        @RequestParam String email
    ) {
        return userProfileService.getProfileByEmail(tenantId, email)
            .thenApply(profileOpt -> profileOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()));
    }

    @GetMapping("/role/{role}")
    public CompletableFuture<ResponseEntity<List<UserProfile>>> getProfilesByRole(
        @PathVariable String role,
        @RequestParam String tenantId
    ) {
        return userProfileService.getProfilesByRole(tenantId, role)
            .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/status/{status}")
    public CompletableFuture<ResponseEntity<List<UserProfile>>> getProfilesByStatus(
        @PathVariable String status,
        @RequestParam String tenantId
    ) {
        return userProfileService.getProfilesByStatus(tenantId, UserProfile.UserStatus.valueOf(status))
            .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/active")
    public CompletableFuture<ResponseEntity<List<UserProfile>>> getActiveProfiles(
        @RequestParam String tenantId
    ) {
        return userProfileService.getActiveProfiles(tenantId)
            .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/search")
    public CompletableFuture<ResponseEntity<List<UserProfile>>> searchProfiles(
        @RequestParam String tenantId,
        @RequestParam String keyword
    ) {
        return userProfileService.searchProfiles(tenantId, keyword)
            .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/permission/{permission}")
    public CompletableFuture<ResponseEntity<List<UserProfile>>> getProfilesWithPermission(
        @PathVariable String permission,
        @RequestParam String tenantId
    ) {
        return userProfileService.getProfilesWithPermission(tenantId, permission)
            .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/count")
    public CompletableFuture<ResponseEntity<Map<String, Long>>> countProfiles(
        @RequestParam String tenantId,
        @RequestParam(required = false) String status
    ) {
        if (status != null && !status.isBlank()) {
            return userProfileService.countProfilesByStatus(tenantId, UserProfile.UserStatus.valueOf(status))
                .thenApply(count -> ResponseEntity.ok(Map.of("count", count)));
        }
        return userProfileService.countProfilesByTenant(tenantId)
            .thenApply(count -> ResponseEntity.ok(Map.of("count", count)));
    }

    @GetMapping("/me")
    public CompletableFuture<ResponseEntity<UserProfile>> getMyProfile(Authentication authentication) {
        String tenantId = RequestContextHolder.get().map(c -> c.tenantId()).orElse(null);
        String userId = authentication != null ? authentication.getName() : null;

        if (tenantId == null || tenantId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing tenantId in JWT context");
        }
        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing userId in JWT context");
        }

        return userProfileService.getProfileByUserId(tenantId, userId)
            .thenApply(profileOpt -> profileOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()));
    }

    // Request records
    record CreateProfileRequest(
        String tenantId,
        String userId,
        String email,
        String firstName,
        String lastName,
        String phoneNumber,
        String avatarUrl,
        UserProfile.UserPreferences preferences,
        UserProfile.UserSettings settings,
        java.util.Set<String> roles,
        java.util.Set<String> permissions,
        Map<String, Object> metadata,
        String createdBy
    ) {}

    record UpdateProfileRequest(
        String tenantId,
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

    record BulkCreateProfilesRequest(
        String tenantId,
        List<CreateProfileRequest> profiles,
        String createdBy
    ) {}
}
