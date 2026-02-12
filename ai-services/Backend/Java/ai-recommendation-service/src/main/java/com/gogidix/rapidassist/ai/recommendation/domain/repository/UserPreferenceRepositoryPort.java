package com.gogidix.rapidassist.ai.recommendation.domain.repository;

import com.gogidix.rapidassist.ai.recommendation.domain.model.UserPreference;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for UserPreference aggregate.
 */
public interface UserPreferenceRepositoryPort {

    UserPreference save(String tenantId, UserPreference preference);

    Optional<UserPreference> findById(String tenantId, UUID id);

    List<UserPreference> findByUserId(String tenantId, String userId);

    List<UserPreference> findByUserIdAndItemType(String tenantId, String userId, String itemType);

    Optional<UserPreference> findByUserAndItem(String tenantId, String userId, String itemType, String itemId);

    List<UserPreference> findByItemType(String tenantId, String itemType);

    void delete(String tenantId, UUID id);

    void deleteByUserId(String tenantId, String userId);

    boolean exists(String tenantId, UUID id);

    List<UserPreference> findStalePreferences(String tenantId, int staleThresholdDays);
}
