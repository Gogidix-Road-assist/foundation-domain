package com.gogidix.rapidassist.ai.inference.application.port.out;

import com.gogidix.rapidassist.ai.inference.domain.aggregate.InferenceRequest;
import com.gogidix.rapidassist.ai.inference.domain.model.InferenceStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Output port for Inference Request repository operations
 */
public interface InferenceRepositoryPort {

    InferenceRequest save(InferenceRequest inferenceRequest);

    Optional<InferenceRequest> findById(UUID id);

    Optional<InferenceRequest> findByRequestId(String requestId);

    List<InferenceRequest> findByTenantId(String tenantId);

    List<InferenceRequest> findByModelId(String modelId);

    List<InferenceRequest> findByStatus(InferenceStatus status);

    List<InferenceRequest> findByTenantIdAndStatus(String tenantId, InferenceStatus status);

    List<InferenceRequest> findByRequestedBy(String requestedBy);

    void deleteById(UUID id);

    List<InferenceRequest> findAll();

    boolean existsByRequestId(String requestId);
}
