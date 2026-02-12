package com.gogidix.rapidassist.ai.personalization.domain.repository;

import com.gogidix.rapidassist.ai.personalization.domain.model.UserPreference;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Port (Hexagonal Architecture - Outbound Port).
 * Defines the contract for persisting and retrieving UserPreference entities.
 */
public interface UserPreferenceRepositoryPort {

    /**
     * Save a user preference (create or update).
     */
    UserPreference save(String tenantId, UserPreference preference);

    /**
     * Find a preference by ID and tenant.
     */
    Optional<UserPreference> findById(String tenantId, UUID preferenceId);

    /**
     * Find all preferences for a tenant.
     */
    List<UserPreference> findByTenantId(String tenantId);

    /**
     * Find preferences by user ID and tenant.
     */
    List<UserPreference> findByUserId(String tenantId, String userId);

    /**
     * Find preferences by profile ID and tenant.
     */
    List<UserPreference> findByProfileId(String tenantId, UUID profileId);

    /**
     * Find preferences by key and tenant.
     */
    List<UserPreference> findByPreferenceKey(String tenantId, String preferenceKey);

    /**
     * Find preferences by category and tenant.
     */
    List<UserPreference> findByCategory(String tenantId, String category);

    /**
     * Find valid preferences at a given datetime.
     */
    List<UserPreference> findValidPreferences(String tenantId, LocalDateTime dateTime);

    /**
     * Delete a preference by ID and tenant.
     */
    void delete(String tenantId, UUID preferenceId);

    /**
     * Delete all preferences for a profile.
     */
    void deleteByProfileId(String tenantId, UUID profileId);

    /**
     * Check if a preference exists.
     */
    boolean exists(String tenantId, UUID preferenceId);

    /**
     * Count preferences by tenant.
     */
    long countByTenantId(String tenantId);
}
