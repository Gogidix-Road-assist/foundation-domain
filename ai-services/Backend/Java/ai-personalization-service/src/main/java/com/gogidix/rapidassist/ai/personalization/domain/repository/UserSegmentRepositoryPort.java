package com.gogidix.rapidassist.ai.personalization.domain.repository;

import com.gogidix.rapidassist.ai.personalization.domain.model.UserSegment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Port (Hexagonal Architecture - Outbound Port).
 * Defines the contract for persisting and retrieving UserSegment entities.
 */
public interface UserSegmentRepositoryPort {

    /**
     * Save a user segment (create or update).
     */
    UserSegment save(String tenantId, UserSegment segment);

    /**
     * Find a segment by ID and tenant.
     */
    Optional<UserSegment> findById(String tenantId, UUID segmentId);

    /**
     * Find a segment by code and tenant.
     */
    Optional<UserSegment> findBySegmentCode(String tenantId, String segmentCode);

    /**
     * Find all segments for a tenant.
     */
    List<UserSegment> findByTenantId(String tenantId);

    /**
     * Find segments by type and tenant.
     */
    List<UserSegment> findBySegmentType(String tenantId, String segmentType);

    /**
     * Find segments by status and tenant.
     */
    List<UserSegment> findByStatus(String tenantId, String status);

    /**
     * Find segments by category and tenant.
     */
    List<UserSegment> findByCategory(String tenantId, String category);

    /**
     * Find segments that contain a specific user.
     */
    List<UserSegment> findByUserId(String tenantId, String userId);

    /**
     * Find auto-update segments.
     */
    List<UserSegment> findAutoUpdateSegments(String tenantId);

    /**
     * Delete a segment by ID and tenant.
     */
    void delete(String tenantId, UUID segmentId);

    /**
     * Check if a segment exists.
     */
    boolean exists(String tenantId, UUID segmentId);

    /**
     * Check if a segment exists by code.
     */
    boolean existsBySegmentCode(String tenantId, String segmentCode);

    /**
     * Count segments by tenant.
     */
    long countByTenantId(String tenantId);
}
