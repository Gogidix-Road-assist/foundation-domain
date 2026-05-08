package com.gogidix.rapidassist.access.control.service.domain.repository;

import com.gogidix.rapidassist.access.control.service.domain.model.Subject;

import java.util.List;
import java.util.Optional;

/**
 * Domain Repository: SubjectRepository
 *
 * Repository interface for Subject aggregates.
 * This is a PORT in the hexagonal architecture.
 *
 * Implementations must ensure tenant isolation at all times.
 */
public interface SubjectRepository {

    /**
     * Save a subject (create or update).
     * Must preserve tenant isolation.
     */
    Subject save(Subject subject);

    /**
     * Find a subject by ID.
     * MUST filter by tenantId to ensure tenant isolation.
     */
    Optional<Subject> findById(String id, String tenantId);

    /**
     * Find a subject by subject key within a tenant.
     */
    Optional<Subject> findBySubjectKey(String subjectKey, String tenantId);

    /**
     * Find all subjects for a tenant.
     */
    List<Subject> findByTenantId(String tenantId);

    /**
     * Find all active subjects for a tenant.
     */
    List<Subject> findActiveByTenantId(String tenantId);

    /**
     * Find subjects by type within a tenant.
     */
    List<Subject> findBySubjectType(String subjectType, String tenantId);

    /**
     * Delete a subject by ID.
     * MUST filter by tenantId to prevent cross-tenant deletion.
     */
    boolean deleteById(String id, String tenantId);

    /**
     * Check if a subject exists by key within a tenant.
     */
    boolean existsBySubjectKey(String subjectKey, String tenantId);
}
