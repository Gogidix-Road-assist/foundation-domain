package com.gogidix.rapidassist.ai.recommendation.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.recommendation.domain.model.UserPreference;
import com.gogidix.rapidassist.ai.recommendation.domain.repository.UserPreferenceRepositoryPort;
import com.gogidix.rapidassist.ai.recommendation.infrastructure.persistence.entity.UserPreferenceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of UserPreferenceRepositoryPort using MongoDB.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserPreferenceRepositoryImpl implements UserPreferenceRepositoryPort {

    private final SpringDataUserPreferenceRepository springDataRepository;

    @Override
    public UserPreference save(String tenantId, UserPreference preference) {
        log.debug("Saving user preference: {} for tenant: {}", preference.getId(), tenantId);
        UserPreferenceEntity entity = toEntity(preference);
        UserPreferenceEntity saved = springDataRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<UserPreference> findById(String tenantId, UUID id) {
        log.debug("Finding user preference by id: {} for tenant: {}", id, tenantId);
        return springDataRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public List<UserPreference> findByUserId(String tenantId, String userId) {
        log.debug("Finding preferences for user: {} in tenant: {}", userId, tenantId);
        return springDataRepository.findByTenantIdAndUserId(tenantId, userId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserPreference> findByUserIdAndItemType(String tenantId, String userId, String itemType) {
        log.debug("Finding preferences for user: {} and item type: {} in tenant: {}", userId, itemType, tenantId);
        return springDataRepository.findByTenantIdAndUserIdAndItemType(tenantId, userId, itemType).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserPreference> findByUserAndItem(String tenantId, String userId, String itemType, String itemId) {
        log.debug("Finding preference for user: {}, item type: {}, item: {} in tenant: {}", userId, itemType, itemId, tenantId);
        return Optional.ofNullable(springDataRepository
                .findByTenantIdAndUserIdAndItemTypeAndItemId(tenantId, userId, itemType, itemId))
                .map(this::toDomain);
    }

    @Override
    public List<UserPreference> findByItemType(String tenantId, String itemType) {
        log.debug("Finding preferences for item type: {} in tenant: {}", itemType, tenantId);
        return springDataRepository.findByTenantIdAndItemType(tenantId, itemType).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String tenantId, UUID id) {
        log.debug("Deleting preference: {} for tenant: {}", id, tenantId);
        springDataRepository.deleteByTenantIdAndUuid(tenantId, id);
    }

    @Override
    public void deleteByUserId(String tenantId, String userId) {
        log.debug("Deleting preferences for user: {} in tenant: {}", userId, tenantId);
        springDataRepository.deleteByTenantIdAndUserId(tenantId, userId);
    }

    @Override
    public boolean exists(String tenantId, UUID id) {
        return springDataRepository.existsByTenantIdAndUuid(tenantId, id);
    }

    @Override
    public List<UserPreference> findStalePreferences(String tenantId, int staleThresholdDays) {
        log.debug("Finding stale preferences in tenant: {} with threshold: {} days", tenantId, staleThresholdDays);
        LocalDateTime threshold = LocalDateTime.now().minusDays(staleThresholdDays);
        return springDataRepository.findByTenantIdAndLastInteractionAtBefore(tenantId, threshold).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private UserPreferenceEntity toEntity(UserPreference domain) {
        return UserPreferenceEntity.builder()
                .uuid(domain.getId())
                .tenantId(domain.getTenantId())
                .userId(domain.getUserId())
                .itemType(domain.getItemType())
                .itemId(domain.getItemId())
                .preferenceKey(domain.getPreferenceKey())
                .preferenceValue(domain.getPreferenceValue())
                .preferenceScore(domain.getPreferenceScore())
                .interactionCount(domain.getInteractionCount())
                .lastInteractionAt(domain.getLastInteractionAt())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .metadata(domain.getMetadata())
                .build();
    }

    private UserPreference toDomain(UserPreferenceEntity entity) {
        return UserPreference.builder()
                .id(entity.getUuid())
                .tenantId(entity.getTenantId())
                .userId(entity.getUserId())
                .itemType(entity.getItemType())
                .itemId(entity.getItemId())
                .preferenceKey(entity.getPreferenceKey())
                .preferenceValue(entity.getPreferenceValue())
                .preferenceScore(entity.getPreferenceScore())
                .interactionCount(entity.getInteractionCount())
                .lastInteractionAt(entity.getLastInteractionAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .metadata(entity.getMetadata())
                .build();
    }
}
