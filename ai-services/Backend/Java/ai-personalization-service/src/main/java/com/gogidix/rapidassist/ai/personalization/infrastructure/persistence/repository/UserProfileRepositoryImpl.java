package com.gogidix.rapidassist.ai.personalization.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.personalization.domain.model.UserProfile;
import com.gogidix.rapidassist.ai.personalization.domain.model.UserProfileStatus;
import com.gogidix.rapidassist.ai.personalization.domain.repository.UserProfileRepositoryPort;
import com.gogidix.rapidassist.ai.personalization.infrastructure.persistence.entity.UserProfileEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of UserProfileRepositoryPort.
 * Adapters domain repository port to Spring Data MongoDB infrastructure.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class UserProfileRepositoryImpl implements UserProfileRepositoryPort {

    private final SpringDataUserProfileRepository springDataRepository;

    @Override
    public UserProfile save(String tenantId, UserProfile profile) {
        log.debug("Saving profile: {} for tenant: {}", profile.getId(), tenantId);

        // Convert domain to entity
        UserProfileEntity entity = toEntity(profile);

        // Save entity
        UserProfileEntity savedEntity = springDataRepository.save(entity);

        // Convert back to domain
        return toDomain(savedEntity);
    }

    @Override
    public Optional<UserProfile> findById(String tenantId, UUID profileId) {
        log.debug("Finding profile by ID: {} for tenant: {}", profileId, tenantId);

        return springDataRepository.findByUuidAndTenantId(profileId, tenantId)
                .map(this::toDomain);
    }

    @Override
    public Optional<UserProfile> findByUserId(String tenantId, String userId) {
        log.debug("Finding profile by user ID: {} for tenant: {}", userId, tenantId);

        return springDataRepository.findByUserIdAndTenantId(userId, tenantId)
                .stream()
                .findFirst()
                .map(this::toDomain);
    }

    @Override
    public List<UserProfile> findByTenantId(String tenantId) {
        log.debug("Finding all profiles for tenant: {}", tenantId);

        return springDataRepository.findByTenantId(tenantId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserProfile> findBySegmentId(String tenantId, String segmentId) {
        log.debug("Finding profiles by segment ID: {} for tenant: {}", segmentId, tenantId);

        return springDataRepository.findBySegmentIdAndTenantId(segmentId, tenantId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserProfile> findByStatus(String tenantId, String status) {
        log.debug("Finding profiles by status: {} for tenant: {}", status, tenantId);

        return springDataRepository.findByStatusAndTenantId(status, tenantId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserProfile> findActiveUsers(String tenantId, LocalDateTime since) {
        log.debug("Finding active users since: {} for tenant: {}", since, tenantId);

        return springDataRepository.findActiveUsers(tenantId, since).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserProfile> findInactiveUsers(String tenantId, LocalDateTime before) {
        log.debug("Finding inactive users before: {} for tenant: {}", before, tenantId);

        return springDataRepository.findInactiveUsers(tenantId, before).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String tenantId, UUID profileId) {
        log.debug("Deleting profile: {} for tenant: {}", profileId, tenantId);

        springDataRepository.deleteByUuidAndTenantId(profileId, tenantId);
    }

    @Override
    public boolean exists(String tenantId, UUID profileId) {
        return springDataRepository.existsByUuidAndTenantId(profileId, tenantId);
    }

    @Override
    public boolean existsByUserId(String tenantId, String userId) {
        return springDataRepository.existsByUserIdAndTenantId(userId, tenantId);
    }

    @Override
    public long countByTenantId(String tenantId) {
        return springDataRepository.countByTenantId(tenantId);
    }

    /**
     * Convert domain model to entity
     */
    private UserProfileEntity toEntity(UserProfile domain) {
        return UserProfileEntity.builder()
                .uuid(domain.getId())
                .tenantId(domain.getTenantId())
                .userId(domain.getUserId())
                .segmentId(domain.getSegmentId())
                .age(domain.getAge())
                .gender(domain.getGender())
                .location(domain.getLocation())
                .language(domain.getLanguage())
                .timezone(domain.getTimezone())
                .totalSessions(domain.getTotalSessions())
                .totalInteractions(domain.getTotalInteractions())
                .lastActivityAt(domain.getLastActivityAt())
                .firstSeenAt(domain.getFirstSeenAt())
                .interests(domain.getInterests())
                .preferences(domain.getPreferences())
                .engagementScore(domain.getEngagementScore())
                .loyaltyScore(domain.getLoyaltyScore())
                .satisfactionScore(domain.getSatisfactionScore())
                .status(domain.getStatus() != null ? domain.getStatus().name() : null)
                .attributes(domain.getAttributes())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .createdBy(domain.getCreatedBy())
                .updatedBy(domain.getUpdatedBy())
                .version(domain.getVersion())
                .build();
    }

    /**
     * Convert entity to domain model
     */
    private UserProfile toDomain(UserProfileEntity entity) {
        return UserProfile.builder()
                .id(entity.getUuid())
                .tenantId(entity.getTenantId())
                .userId(entity.getUserId())
                .segmentId(entity.getSegmentId())
                .age(entity.getAge())
                .gender(entity.getGender())
                .location(entity.getLocation())
                .language(entity.getLanguage())
                .timezone(entity.getTimezone())
                .totalSessions(entity.getTotalSessions())
                .totalInteractions(entity.getTotalInteractions())
                .lastActivityAt(entity.getLastActivityAt())
                .firstSeenAt(entity.getFirstSeenAt())
                .interests(entity.getInterests())
                .preferences(entity.getPreferences())
                .engagementScore(entity.getEngagementScore())
                .loyaltyScore(entity.getLoyaltyScore())
                .satisfactionScore(entity.getSatisfactionScore())
                .status(entity.getStatus() != null ?
                    UserProfileStatus.valueOf(entity.getStatus()) : null)
                .attributes(entity.getAttributes())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .version(entity.getVersion())
                .build();
    }
}
