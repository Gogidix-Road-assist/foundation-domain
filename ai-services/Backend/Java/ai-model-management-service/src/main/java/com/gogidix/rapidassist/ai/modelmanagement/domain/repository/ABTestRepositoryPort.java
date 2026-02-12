package com.gogidix.rapidassist.ai.modelmanagement.domain.repository;

import com.gogidix.rapidassist.ai.modelmanagement.domain.model.ABTest;
import com.gogidix.rapidassist.ai.modelmanagement.domain.model.ABTestStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for ABTest domain operations.
 */
public interface ABTestRepositoryPort {

    /**
     * Save an A/B test.
     */
    ABTest save(String tenantId, ABTest abTest);

    /**
     * Find A/B test by ID.
     */
    Optional<ABTest> findById(String tenantId, UUID id);

    /**
     * Find all tests for a tenant.
     */
    List<ABTest> findAll(String tenantId);

    /**
     * Find tests by status.
     */
    List<ABTest> findByStatus(String tenantId, ABTestStatus status);

    /**
     * Find tests by control model version.
     */
    List<ABTest> findByControlModelVersionId(String tenantId, UUID controlModelVersionId);

    /**
     * Find tests by treatment model version.
     */
    List<ABTest> findByTreatmentModelVersionId(String tenantId, UUID treatmentModelVersionId);

    /**
     * Find test by name.
     */
    Optional<ABTest> findByName(String tenantId, String name);

    /**
     * Find running tests.
     */
    List<ABTest> findRunningTests(String tenantId);

    /**
     * Check if test exists.
     */
    boolean exists(String tenantId, UUID id);

    /**
     * Delete an A/B test.
     */
    void delete(String tenantId, UUID id);
}
