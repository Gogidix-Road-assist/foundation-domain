package com.gogidix.rapidassist.ai.personalization.domain.repository;

import com.gogidix.rapidassist.ai.personalization.domain.model.ABTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Port (Hexagonal Architecture - Outbound Port).
 * Defines the contract for persisting and retrieving ABTest entities.
 */
public interface ABTestRepositoryPort {

    /**
     * Save an A/B test (create or update).
     */
    ABTest save(String tenantId, ABTest test);

    /**
     * Find a test by ID and tenant.
     */
    Optional<ABTest> findById(String tenantId, UUID testId);

    /**
     * Find a test by code and tenant.
     */
    Optional<ABTest> findByTestCode(String tenantId, String testCode);

    /**
     * Find all tests for a tenant.
     */
    List<ABTest> findByTenantId(String tenantId);

    /**
     * Find tests by type and tenant.
     */
    List<ABTest> findByTestType(String tenantId, String testType);

    /**
     * Find tests by status and tenant.
     */
    List<ABTest> findByStatus(String tenantId, String status);

    /**
     * Find tests by category and tenant.
     */
    List<ABTest> findByCategory(String tenantId, String category);

    /**
     * Find tests by multiple statuses and tenant.
     */
    List<ABTest> findByStatusIn(String tenantId, List<String> statuses);

    /**
     * Find running tests at a given datetime.
     */
    List<ABTest> findRunningTests(String tenantId, LocalDateTime dateTime);

    /**
     * Delete a test by ID and tenant.
     */
    void delete(String tenantId, UUID testId);

    /**
     * Check if a test exists.
     */
    boolean exists(String tenantId, UUID testId);

    /**
     * Check if a test exists by code.
     */
    boolean existsByTestCode(String tenantId, String testCode);

    /**
     * Count tests by tenant.
     */
    long countByTenantId(String tenantId);
}
