package com.gogidix.rapidassist.ai.personalization.application.service;

import com.gogidix.rapidassist.ai.personalization.application.dto.UserProfileDto;
import com.gogidix.rapidassist.ai.personalization.domain.model.UserProfile;
import com.gogidix.rapidassist.ai.personalization.domain.model.UserProfileStatus;
import com.gogidix.rapidassist.ai.personalization.domain.repository.UserProfileRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Application Service for UserProfile operations.
 * Implements business logic and orchestrates domain operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileApplicationService {

    private final UserProfileRepositoryPort profileRepository;

    /**
     * Create a new user profile.
     */
    public UserProfileDto createProfile(String tenantId, String userId,
                                        String segmentId,
                                        Integer age, String gender, String location,
                                        java.util.Map<String, Object> interests,
                                        java.util.Map<String, Object> preferences,
                                        java.util.Map<String, Object> attributes) {
        log.info("Creating user profile for tenant: {}, user: {}", tenantId, userId);

        // Check if profile already exists
        if (profileRepository.existsByUserId(tenantId, userId)) {
            throw new IllegalArgumentException("User profile already exists for user: " + userId);
        }

        // Create profile
        var profile = UserProfile.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .userId(userId)
                .segmentId(segmentId)
                .age(age)
                .gender(gender)
                .location(location)
                .totalSessions(0)
                .totalInteractions(0)
                .firstSeenAt(LocalDateTime.now())
                .lastActivityAt(LocalDateTime.now())
                .interests(interests)
                .preferences(preferences)
                .status(UserProfileStatus.ACTIVE)
                .attributes(attributes)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .version(0L)
                .build();

        // Calculate initial scores
        profile.calculateEngagementScore();
        profile.calculateLoyaltyScore();

        // Save profile
        var savedProfile = profileRepository.save(tenantId, profile);

        log.info("User profile created: {}", savedProfile.getId());
        return toDto(savedProfile);
    }

    /**
     * Get profile by ID.
     */
    public UserProfileDto getProfile(String tenantId, UUID profileId) {
        log.info("Getting user profile: {} for tenant: {}", profileId, tenantId);

        var profile = profileRepository.findById(tenantId, profileId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User profile not found: " + profileId));

        return toDto(profile);
    }

    /**
     * Get profile by user ID.
     */
    public UserProfileDto getProfileByUserId(String tenantId, String userId) {
        log.info("Getting user profile by user ID: {} for tenant: {}", userId, tenantId);

        var profile = profileRepository.findByUserId(tenantId, userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User profile not found for user: " + userId));

        return toDto(profile);
    }

    /**
     * Get all profiles for a tenant.
     */
    public List<UserProfileDto> getProfilesByTenant(String tenantId) {
        log.info("Getting profiles for tenant: {}", tenantId);

        return profileRepository.findByTenantId(tenantId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Update profile demographics.
     */
    public UserProfileDto updateDemographics(String tenantId, UUID profileId,
                                            Integer age, String gender, String location,
                                            String language, String timezone) {
        log.info("Updating demographics for profile: {} for tenant: {}", profileId, tenantId);

        var profile = profileRepository.findById(tenantId, profileId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User profile not found: " + profileId));

        if (age != null) profile.setAge(age);
        if (gender != null) profile.setGender(gender);
        if (location != null) profile.setLocation(location);
        if (language != null) profile.setLanguage(language);
        if (timezone != null) profile.setTimezone(timezone);

        profile.setUpdatedAt(LocalDateTime.now());

        var savedProfile = profileRepository.save(tenantId, profile);

        log.info("Profile demographics updated: {}", savedProfile.getId());
        return toDto(savedProfile);
    }

    /**
     * Record user activity.
     */
    public UserProfileDto recordActivity(String tenantId, UUID profileId, int interactions) {
        log.info("Recording activity for profile: {} for tenant: {}", profileId, tenantId);

        var profile = profileRepository.findById(tenantId, profileId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User profile not found: " + profileId));

        profile.recordActivity();
        if (interactions > 0) {
            profile.addInteraction(interactions);
        }

        var savedProfile = profileRepository.save(tenantId, profile);

        log.info("Activity recorded for profile: {}", savedProfile.getId());
        return toDto(savedProfile);
    }

    /**
     * Update user preference.
     */
    public UserProfileDto updatePreference(String tenantId, UUID profileId,
                                          String key, Object value) {
        log.info("Updating preference for profile: {} for tenant: {}", profileId, tenantId);

        var profile = profileRepository.findById(tenantId, profileId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User profile not found: " + profileId));

        profile.setPreference(key, value);

        var savedProfile = profileRepository.save(tenantId, profile);

        log.info("Preference updated for profile: {}", savedProfile.getId());
        return toDto(savedProfile);
    }

    /**
     * Update user attribute.
     */
    public UserProfileDto updateAttribute(String tenantId, UUID profileId,
                                         String key, Object value) {
        log.info("Updating attribute for profile: {} for tenant: {}", profileId, tenantId);

        var profile = profileRepository.findById(tenantId, profileId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User profile not found: " + profileId));

        profile.setAttribute(key, value);

        var savedProfile = profileRepository.save(tenantId, profile);

        log.info("Attribute updated for profile: {}", savedProfile.getId());
        return toDto(savedProfile);
    }

    /**
     * Assign segment to profile.
     */
    public UserProfileDto assignSegment(String tenantId, UUID profileId, String segmentId) {
        log.info("Assigning segment {} to profile: {} for tenant: {}", segmentId, profileId, tenantId);

        var profile = profileRepository.findById(tenantId, profileId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User profile not found: " + profileId));

        profile.setSegmentId(segmentId);
        profile.setUpdatedAt(LocalDateTime.now());

        var savedProfile = profileRepository.save(tenantId, profile);

        log.info("Segment assigned to profile: {}", savedProfile.getId());
        return toDto(savedProfile);
    }

    /**
     * Deactivate profile.
     */
    public UserProfileDto deactivateProfile(String tenantId, UUID profileId) {
        log.info("Deactivating profile: {} for tenant: {}", profileId, tenantId);

        var profile = profileRepository.findById(tenantId, profileId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User profile not found: " + profileId));

        profile.deactivate();

        var savedProfile = profileRepository.save(tenantId, profile);

        log.info("Profile deactivated: {}", savedProfile.getId());
        return toDto(savedProfile);
    }

    /**
     * Reactivate profile.
     */
    public UserProfileDto reactivateProfile(String tenantId, UUID profileId) {
        log.info("Reactivating profile: {} for tenant: {}", profileId, tenantId);

        var profile = profileRepository.findById(tenantId, profileId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User profile not found: " + profileId));

        profile.activate();

        var savedProfile = profileRepository.save(tenantId, profile);

        log.info("Profile reactivated: {}", savedProfile.getId());
        return toDto(savedProfile);
    }

    /**
     * Delete profile.
     */
    public void deleteProfile(String tenantId, UUID profileId) {
        log.info("Deleting profile: {} for tenant: {}", profileId, tenantId);

        if (!profileRepository.exists(tenantId, profileId)) {
            throw new IllegalArgumentException("User profile not found: " + profileId);
        }

        profileRepository.delete(tenantId, profileId);

        log.info("Profile deleted: {}", profileId);
    }

    /**
     * Convert domain model to DTO.
     */
    private UserProfileDto toDto(UserProfile profile) {
        return UserProfileDto.builder()
                .id(profile.getId())
                .tenantId(profile.getTenantId())
                .userId(profile.getUserId())
                .segmentId(profile.getSegmentId())
                .age(profile.getAge())
                .gender(profile.getGender())
                .location(profile.getLocation())
                .language(profile.getLanguage())
                .timezone(profile.getTimezone())
                .totalSessions(profile.getTotalSessions())
                .totalInteractions(profile.getTotalInteractions())
                .lastActivityAt(profile.getLastActivityAt())
                .firstSeenAt(profile.getFirstSeenAt())
                .interests(profile.getInterests())
                .preferences(profile.getPreferences())
                .engagementScore(profile.getEngagementScore())
                .loyaltyScore(profile.getLoyaltyScore())
                .satisfactionScore(profile.getSatisfactionScore())
                .status(profile.getStatus() != null ? profile.getStatus().name() : null)
                .attributes(profile.getAttributes())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .createdBy(profile.getCreatedBy())
                .updatedBy(profile.getUpdatedBy())
                .version(profile.getVersion())
                .build();
    }
}
