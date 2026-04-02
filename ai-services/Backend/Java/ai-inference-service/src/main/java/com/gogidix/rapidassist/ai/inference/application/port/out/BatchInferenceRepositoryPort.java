package com.gogidix.rapidassist.ai.inference.application.port.out;

import com.gogidix.rapidassist.ai.inference.domain.model.BatchInferenceRequest;
import com.gogidix.rapidassist.ai.inference.domain.model.BatchStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Output port for Batch Inference Request repository operations
 */
public interface BatchInferenceRepositoryPort {

    BatchInferenceRequest save(BatchInferenceRequest batchRequest);

    Optional<BatchInferenceRequest> findById(UUID id);

    Optional<BatchInferenceRequest> findByBatchId(String batchId);

    List<BatchInferenceRequest> findByTenantId(String tenantId);

    List<BatchInferenceRequest> findByStatus(BatchStatus status);

    List<BatchInferenceRequest> findByCreatedBy(String createdBy);

    void deleteById(UUID id);

    List<BatchInferenceRequest> findAll();

    boolean existsByBatchId(String batchId);
}
