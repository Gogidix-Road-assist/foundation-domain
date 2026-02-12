package com.gogidix.rapidassist.orchestration.dispatching.domain.repository;

import com.gogidix.rapidassist.orchestration.dispatching.domain.model.Dispatch;
import com.gogidix.rapidassist.orchestration.dispatching.domain.port.out.DispatchRepositoryPort;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository interface for Dispatch
 * Follows Hexagonal Architecture - this is a domain-specific interface
 */
public interface DispatchRepository extends DispatchRepositoryPort {

    /**
     * Save dispatch (create or update)
     */
    Dispatch save(Dispatch dispatch);

    /**
     * Find dispatch by ID
     */
    Optional<Dispatch> findById(String id);

    /**
     * Find all dispatches for a tenant
     */
    List<Dispatch> findByTenantId(String tenantId);

    /**
     * Find dispatch by dispatchId
     */
    Optional<Dispatch> findByDispatchId(String dispatchId);

    /**
     * Find dispatches by request ID
     */
    List<Dispatch> findByRequestId(String requestId);

    /**
     * Find dispatches by tenant and status
     */
    List<Dispatch> findByTenantIdAndStatusIn(String tenantId, List<Dispatch.DispatchStatus> statuses);

    /**
     * Delete dispatch by ID
     */
    void deleteById(String id);

    /**
     * Check if dispatch exists
     */
    boolean existsByDispatchId(String dispatchId);
}
