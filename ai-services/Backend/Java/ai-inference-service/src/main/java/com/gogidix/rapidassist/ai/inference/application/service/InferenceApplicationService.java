package com.gogidix.rapidassist.ai.inference.application.service;

import com.gogidix.rapidassist.ai.inference.application.command.CreateInferenceRequestCommand;
import com.gogidix.rapidassist.ai.inference.application.dto.InferenceRequestDto;
import com.gogidix.rapidassist.ai.inference.application.mapper.InferenceRequestMapper;
import com.gogidix.rapidassist.ai.inference.application.port.in.InferenceUseCase;
import com.gogidix.rapidassist.ai.inference.application.port.out.InferenceRepositoryPort;
import com.gogidix.rapidassist.ai.inference.domain.aggregate.InferenceRequest;
import com.gogidix.rapidassist.ai.inference.domain.event.InferenceCompletedEvent;
import com.gogidix.rapidassist.ai.inference.domain.event.InferenceFailedEvent;
import com.gogidix.rapidassist.ai.inference.domain.event.InferenceRequestedEvent;
import com.gogidix.rapidassist.ai.inference.domain.model.InferenceResult;
import com.gogidix.rapidassist.ai.inference.domain.model.InferenceStatus;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Application service for Inference use cases
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InferenceApplicationService implements InferenceUseCase {

    private final InferenceRepositoryPort inferenceRepository;
    private final InferenceRequestMapper inferenceRequestMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public InferenceRequestDto createInferenceRequest(CreateInferenceRequestCommand command) {
        log.info("Creating inference request for model: {}", command.getModelId());

        InferenceRequest inferenceRequest = InferenceRequest.initialize(
                command.getTenantId(),
                command.getModelId(),
                command.getModelVersion(),
                command.getInferenceType(),
                command.getInputData(),
                command.getRequestedBy()
        );

        if (command.getParameters() != null) {
            command.getParameters().forEach(inferenceRequest::addParameter);
        }

        if (command.getPriority() != null) {
            inferenceRequest.setPriority(command.getPriority());
        }

        InferenceRequest savedRequest = inferenceRepository.save(inferenceRequest);

        // Publish domain event
        InferenceRequestedEvent event = InferenceRequestedEvent.create(
                savedRequest.getId(),
                savedRequest.getTenantId(),
                savedRequest.getModelId(),
                savedRequest.getModelVersion(),
                savedRequest.getInferenceType(),
                savedRequest.getRequestedBy()
        );
        eventPublisher.publishEvent(event);

        log.info("Inference request created with ID: {}", savedRequest.getId());
        return inferenceRequestMapper.toDto(savedRequest);
    }

    @Override
    public InferenceRequestDto getInferenceRequestById(UUID id) {
        log.debug("Getting inference request by ID: {}", id);
        return inferenceRepository.findById(id)
                .map(inferenceRequestMapper::toDto)
                .orElse(null);
    }

    @Override
    public InferenceRequestDto getInferenceRequestByRequestId(String requestId) {
        log.debug("Getting inference request by requestId: {}", requestId);
        return inferenceRepository.findByRequestId(requestId)
                .map(inferenceRequestMapper::toDto)
                .orElse(null);
    }

    @Override
    public List<InferenceRequestDto> getInferenceRequestsByTenant(String tenantId) {
        log.debug("Getting inference requests for tenant: {}", tenantId);
        return inferenceRepository.findByTenantId(tenantId).stream()
                .map(inferenceRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<InferenceRequestDto> getInferenceRequestsByModel(String modelId) {
        log.debug("Getting inference requests for model: {}", modelId);
        return inferenceRepository.findByModelId(modelId).stream()
                .map(inferenceRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void processInference(UUID inferenceRequestId) {
        log.info("Processing inference: {}", inferenceRequestId);

        InferenceRequest inferenceRequest = inferenceRepository.findById(inferenceRequestId)
                .orElseThrow(() -> new IllegalArgumentException("Inference request not found"));

        inferenceRequest.startProcessing();
        inferenceRepository.save(inferenceRequest);
    }

    @Override
    @Transactional
    public void completeInference(UUID inferenceRequestId, String outputData) {
        log.info("Completing inference: {}", inferenceRequestId);

        InferenceRequest inferenceRequest = inferenceRepository.findById(inferenceRequestId)
                .orElseThrow(() -> new IllegalArgumentException("Inference request not found"));

        // Create result
        long duration = inferenceRequest.getProcessingDurationMillis();
        InferenceResult result = InferenceResult.create(
                inferenceRequest.getTenantId(),
                outputData,
                inferenceRequest.getModelVersion(),
                0.95, // default confidence
                (int) duration,
                "inference"
        );

        inferenceRequest.completeSuccessfully(result);
        InferenceRequest savedRequest = inferenceRepository.save(inferenceRequest);

        // Publish domain event
        InferenceCompletedEvent event = InferenceCompletedEvent.builder()
                .eventId(UUID.randomUUID())
                .inferenceRequestId(savedRequest.getId())
                .tenantId(savedRequest.getTenantId())
                .modelId(savedRequest.getModelId())
                .modelVersion(savedRequest.getModelVersion())
                .outputData(outputData)
                .processingDurationMillis(savedRequest.getProcessingDurationMillis())
                .timestamp(LocalDateTime.now())
                .build();
        eventPublisher.publishEvent(event);

        log.info("Inference completed: {}", inferenceRequestId);
    }

    @Override
    @Transactional
    public void failInference(UUID inferenceRequestId, String errorMessage) {
        log.info("Failing inference: {}", inferenceRequestId);

        InferenceRequest inferenceRequest = inferenceRepository.findById(inferenceRequestId)
                .orElseThrow(() -> new IllegalArgumentException("Inference request not found"));

        inferenceRequest.fail(errorMessage);
        InferenceRequest savedRequest = inferenceRepository.save(inferenceRequest);

        // Publish domain event
        InferenceFailedEvent event = InferenceFailedEvent.builder()
                .eventId(UUID.randomUUID())
                .inferenceRequestId(savedRequest.getId())
                .tenantId(savedRequest.getTenantId())
                .modelId(savedRequest.getModelId())
                .errorMessage(errorMessage)
                .timestamp(LocalDateTime.now())
                .build();
        eventPublisher.publishEvent(event);

        log.info("Inference failed: {}", inferenceRequestId);
    }

    @Override
    public List<InferenceRequestDto> getPendingInferences() {
        log.debug("Getting pending inferences");
        return inferenceRepository.findByStatus(InferenceStatus.PENDING).stream()
                .map(inferenceRequestMapper::toDto)
                .collect(Collectors.toList());
    }
}
