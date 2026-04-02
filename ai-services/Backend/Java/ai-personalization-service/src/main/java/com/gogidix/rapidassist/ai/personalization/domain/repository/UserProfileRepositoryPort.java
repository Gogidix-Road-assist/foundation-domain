package com.gogidix.rapidassist.ai.personalization.domain.repository;

import com.gogidix.rapidassist.ai.personalization.domain.model.UserProfile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Port (Hexagonal Architecture - Outbound Port).
 * Defines the contract for persisting and retrieving UserProfile aggregates.
 */
public interface UserProfileRepositoryPort {

    /**
     * Save a user profile (create or update).
     */
    UserProfile save(String tenantId, UserProfile profile);

    /**
     * Find a profile by ID and tenant.
     */
    Optional<UserProfile> findById(String tenantId, UUID profileId);

    /**
     * Find a profile by user ID and tenant.
     */
    Optional<UserProfile> findByUserId(String tenantId, String userId);

    /**
     * Find all profiles for a tenant.
     */
    List<UserProfile> findByTenantId(String tenantId);

    /**
     * Find profiles by segment ID and tenant.
     */
    List<UserProfile> findBySegmentId(String tenantId, String segmentId);

    /**
     * Find profiles by status and tenant.
     */
    List<UserProfile> findByStatus(String tenantId, String status);

    /**
     * Find active users since a given date.
     */
    List<UserProfile> findActiveUsers(String tenantId, LocalDateTime since);

    /**
     * Find inactive users before a given date.
     */
    List<UserProfile> findInactiveUsers(String tenantId, LocalDateTime before);

    /**
     * Delete a profile by ID and tenant.
     */
    void delete(String tenantId, UUID profileId);

    /**
     * Check if a profile exists.
     */
    boolean exists(String tenantId, UUID profileId);

    /**
     * Check if a profile exists for user.
     */
    boolean existsByUserId(String tenantId, String userId);

    /**
     * Count profiles by tenant.
     */
    long countByTenantId(String tenantId);
}
