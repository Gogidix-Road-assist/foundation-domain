package com.gogidix.rapidassist.ai.inference.application.port.in;

import com.gogidix.rapidassist.ai.inference.application.command.CreateInferenceRequestCommand;
import com.gogidix.rapidassist.ai.inference.application.dto.InferenceRequestDto;

import java.util.List;
import java.util.UUID;

/**
 * Input port for Inference use cases
 */
public interface InferenceUseCase {

    InferenceRequestDto createInferenceRequest(CreateInferenceRequestCommand command);

    InferenceRequestDto getInferenceRequestById(UUID id);

    InferenceRequestDto getInferenceRequestByRequestId(String requestId);

    List<InferenceRequestDto> getInferenceRequestsByTenant(String tenantId);

    List<InferenceRequestDto> getInferenceRequestsByModel(String modelId);

    void processInference(UUID inferenceRequestId);

    void completeInference(UUID inferenceRequestId, String outputData);

    void failInference(UUID inferenceRequestId, String errorMessage);

    List<InferenceRequestDto> getPendingInferences();
}
