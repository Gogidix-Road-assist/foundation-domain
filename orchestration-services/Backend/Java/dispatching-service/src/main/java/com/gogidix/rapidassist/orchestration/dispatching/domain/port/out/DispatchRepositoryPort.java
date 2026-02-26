package com.gogidix.rapidassist.orchestration.dispatching.domain.port.out;

import com.gogidix.rapidassist.orchestration.dispatching.domain.model.Dispatch;

import java.util.List;
import java.util.Optional;

/**
 * Output port for dispatch repository
 * This is the interface that the infrastructure layer implements
 */
public interface DispatchRepositoryPort {

    Dispatch save(Dispatch dispatch);

    Optional<Dispatch> findById(String id);

    List<Dispatch> findByTenantId(String tenantId);

    Optional<Dispatch> findByDispatchId(String dispatchId);

    List<Dispatch> findByRequestId(String requestId);

    List<Dispatch> findByTenantIdAndStatusIn(String tenantId, List<Dispatch.DispatchStatus> statuses);

    void deleteById(String id);

    boolean existsByDispatchId(String dispatchId);
}
