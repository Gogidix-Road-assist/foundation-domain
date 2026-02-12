package com.gogidix.rapidassist.ai.personalization.interfaces.rest.controller;

import com.gogidix.rapidassist.ai.personalization.application.dto.UserProfileDto;
import com.gogidix.rapidassist.ai.personalization.application.service.UserProfileApplicationService;
import com.gogidix.rapidassist.ai.personalization.interfaces.rest.request.CreateUserProfileRequest;
import com.gogidix.rapidassist.ai.personalization.interfaces.rest.response.UserProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST Controller for User Profile management.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/personalization/profiles")
@RequiredArgsConstructor
@Tag(name = "User Profile", description = "User profile management APIs")
public class UserProfileController {

    private final UserProfileApplicationService profileService;

    @PostMapping
    @Operation(summary = "Create a new user profile")
    public ResponseEntity<UserProfileResponse> create(@Valid @RequestBody CreateUserProfileRequest request) {
        log.info("REST request to create user profile for user: {}", request.getUserId());

        UserProfileDto dto = profileService.createProfile(
                request.getTenantId(),
                request.getUserId(),
                request.getSegmentId(),
                request.getAge(),
                request.getGender(),
                request.getLocation(),
                request.getInterests(),
                request.getPreferences(),
                request.getAttributes()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user profile by ID")
    public ResponseEntity<UserProfileResponse> getById(
            @PathVariable String tenantId,
            @PathVariable UUID id) {
        log.info("REST request to get user profile: {}", id);

        UserProfileDto dto = profileService.getProfile(tenantId, id);
        return ResponseEntity.ok(toResponse(dto));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user profile by user ID")
    public ResponseEntity<UserProfileResponse> getByUserId(
            @RequestParam String tenantId,
            @PathVariable String userId) {
        log.info("REST request to get user profile for user: {}", userId);

        UserProfileDto dto = profileService.getProfileByUserId(tenantId, userId);
        return ResponseEntity.ok(toResponse(dto));
    }

    @GetMapping
    @Operation(summary = "Get all user profiles for tenant")
    public ResponseEntity<List<UserProfileResponse>> getByTenant(
            @RequestParam String tenantId) {
        log.info("REST request to get profiles for tenant: {}", tenantId);

        List<UserProfileResponse> responses = profileService.getProfilesByTenant(tenantId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}/demographics")
    @Operation(summary = "Update user profile demographics")
    public ResponseEntity<UserProfileResponse> updateDemographics(
            @RequestParam String tenantId,
            @PathVariable UUID id,
            @RequestBody Map<String, Object> demographics) {
        log.info("REST request to update demographics for profile: {}", id);

        UserProfileDto dto = profileService.updateDemographics(
                tenantId,
                id,
                (Integer) demographics.get("age"),
                (String) demographics.get("gender"),
                (String) demographics.get("location"),
                (String) demographics.get("language"),
                (String) demographics.get("timezone")
        );

        return ResponseEntity.ok(toResponse(dto));
    }

    @PostMapping("/{id}/activity")
    @Operation(summary = "Record user activity")
    public ResponseEntity<UserProfileResponse> recordActivity(
            @RequestParam String tenantId,
            @PathVariable UUID id,
            @RequestBody Map<String, Object> activityData) {
        log.info("REST request to record activity for profile: {}", id);

        Integer interactions = activityData.get("interactions") != null ?
                (Integer) activityData.get("interactions") : 1;

        UserProfileDto dto = profileService.recordActivity(tenantId, id, interactions);
        return ResponseEntity.ok(toResponse(dto));
    }

    @PutMapping("/{id}/preferences")
    @Operation(summary = "Update user preference")
    public ResponseEntity<UserProfileResponse> updatePreference(
            @RequestParam String tenantId,
            @PathVariable UUID id,
            @RequestBody Map<String, Object> preferenceData) {
        log.info("REST request to update preference for profile: {}", id);

        String key = (String) preferenceData.get("key");
        Object value = preferenceData.get("value");

        UserProfileDto dto = profileService.updatePreference(tenantId, id, key, value);
        return ResponseEntity.ok(toResponse(dto));
    }

    @PutMapping("/{id}/attributes")
    @Operation(summary = "Update user attribute")
    public ResponseEntity<UserProfileResponse> updateAttribute(
            @RequestParam String tenantId,
            @PathVariable UUID id,
            @RequestBody Map<String, Object> attributeData) {
        log.info("REST request to update attribute for profile: {}", id);

        String key = (String) attributeData.get("key");
        Object value = attributeData.get("value");

        UserProfileDto dto = profileService.updateAttribute(tenantId, id, key, value);
        return ResponseEntity.ok(toResponse(dto));
    }

    @PutMapping("/{id}/segment")
    @Operation(summary = "Assign segment to profile")
    public ResponseEntity<UserProfileResponse> assignSegment(
            @RequestParam String tenantId,
            @PathVariable UUID id,
            @RequestBody Map<String, String> segmentData) {
        log.info("REST request to assign segment to profile: {}", id);

        String segmentId = segmentData.get("segmentId");

        UserProfileDto dto = profileService.assignSegment(tenantId, id, segmentId);
        return ResponseEntity.ok(toResponse(dto));
    }

    @PostMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate user profile")
    public ResponseEntity<UserProfileResponse> deactivate(
            @RequestParam String tenantId,
            @PathVariable UUID id) {
        log.info("REST request to deactivate profile: {}", id);

        UserProfileDto dto = profileService.deactivateProfile(tenantId, id);
        return ResponseEntity.ok(toResponse(dto));
    }

    @PostMapping("/{id}/reactivate")
    @Operation(summary = "Reactivate user profile")
    public ResponseEntity<UserProfileResponse> reactivate(
            @RequestParam String tenantId,
            @PathVariable UUID id) {
        log.info("REST request to reactivate profile: {}", id);

        UserProfileDto dto = profileService.reactivateProfile(tenantId, id);
        return ResponseEntity.ok(toResponse(dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user profile")
    public ResponseEntity<Void> delete(
            @RequestParam String tenantId,
            @PathVariable UUID id) {
        log.info("REST request to delete profile: {}", id);

        profileService.deleteProfile(tenantId, id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Convert DTO to Response.
     */
    private UserProfileResponse toResponse(UserProfileDto dto) {
        return UserProfileResponse.builder()
                .id(dto.getId())
                .tenantId(dto.getTenantId())
                .userId(dto.getUserId())
                .segmentId(dto.getSegmentId())
                .age(dto.getAge())
                .gender(dto.getGender())
                .location(dto.getLocation())
                .language(dto.getLanguage())
                .timezone(dto.getTimezone())
                .totalSessions(dto.getTotalSessions())
                .totalInteractions(dto.getTotalInteractions())
                .lastActivityAt(dto.getLastActivityAt())
                .firstSeenAt(dto.getFirstSeenAt())
                .interests(dto.getInterests())
                .preferences(dto.getPreferences())
                .engagementScore(dto.getEngagementScore())
                .loyaltyScore(dto.getLoyaltyScore())
                .satisfactionScore(dto.getSatisfactionScore())
                .status(dto.getStatus())
                .attributes(dto.getAttributes())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .createdBy(dto.getCreatedBy())
                .updatedBy(dto.getUpdatedBy())
                .version(dto.getVersion())
                .build();
    }
}
