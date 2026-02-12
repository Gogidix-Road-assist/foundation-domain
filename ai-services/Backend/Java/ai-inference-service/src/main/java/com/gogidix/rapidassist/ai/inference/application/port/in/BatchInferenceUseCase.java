package com.gogidix.rapidassist.ai.inference.application.port.in;

import com.gogidix.rapidassist.ai.inference.application.command.CreateBatchInferenceCommand;
import com.gogidix.rapidassist.ai.inference.application.dto.BatchInferenceRequestDto;

import java.util.List;
import java.util.UUID;

/**
 * Input port for Batch Inference use cases
 */
public interface BatchInferenceUseCase {

    BatchInferenceRequestDto createBatchInference(CreateBatchInferenceCommand command);

    BatchInferenceRequestDto getBatchInferenceById(UUID id);

    BatchInferenceRequestDto getBatchInferenceByBatchId(String batchId);

    List<BatchInferenceRequestDto> getBatchInferencesByTenant(String tenantId);

    void processBatchInference(UUID batchRequestId);

    void completeBatchInference(UUID batchRequestId);

    void failBatchInference(UUID batchRequestId, String errorMessage);

    List<BatchInferenceRequestDto> getPendingBatches();
}
