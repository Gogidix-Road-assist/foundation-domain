package com.gogidix.rapidassist.ai.inference.application.service;

import com.gogidix.rapidassist.ai.inference.application.command.CreateBatchInferenceCommand;
import com.gogidix.rapidassist.ai.inference.application.dto.BatchInferenceRequestDto;
import com.gogidix.rapidassist.ai.inference.application.mapper.BatchInferenceMapper;
import com.gogidix.rapidassist.ai.inference.application.port.in.BatchInferenceUseCase;
import com.gogidix.rapidassist.ai.inference.application.port.out.BatchInferenceRepositoryPort;
import com.gogidix.rapidassist.ai.inference.domain.event.BatchInferenceCompletedEvent;
import com.gogidix.rapidassist.ai.inference.domain.model.BatchInferenceRequest;
import com.gogidix.rapidassist.ai.inference.domain.model.BatchStatus;
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
 * Application service for Batch Inference use cases
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BatchInferenceApplicationService implements BatchInferenceUseCase {

    private final BatchInferenceRepositoryPort batchInferenceRepository;
    private final BatchInferenceMapper batchInferenceMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public BatchInferenceRequestDto createBatchInference(CreateBatchInferenceCommand command) {
        log.info("Creating batch inference request for model: {}", command.getModelId());

        BatchInferenceRequest batchRequest = BatchInferenceRequest.create(
                command.getTenantId(),
                command.getModelId(),
                command.getModelVersion(),
                command.getInputItems(),
                command.getCreatedBy()
        );

        if (command.getParameters() != null) {
            batchRequest.setParameters(command.getParameters());
        }

        BatchInferenceRequest savedRequest = batchInferenceRepository.save(batchRequest);
        log.info("Batch inference request created with ID: {}", savedRequest.getId());
        return batchInferenceMapper.toDto(savedRequest);
    }

    @Override
    public BatchInferenceRequestDto getBatchInferenceById(UUID id) {
        log.debug("Getting batch inference by ID: {}", id);
        return batchInferenceRepository.findById(id)
                .map(batchInferenceMapper::toDto)
                .orElse(null);
    }

    @Override
    public BatchInferenceRequestDto getBatchInferenceByBatchId(String batchId) {
        log.debug("Getting batch inference by batchId: {}", batchId);
        return batchInferenceRepository.findByBatchId(batchId)
                .map(batchInferenceMapper::toDto)
                .orElse(null);
    }

    @Override
    public List<BatchInferenceRequestDto> getBatchInferencesByTenant(String tenantId) {
        log.debug("Getting batch inferences for tenant: {}", tenantId);
        return batchInferenceRepository.findByTenantId(tenantId).stream()
                .map(batchInferenceMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void processBatchInference(UUID batchRequestId) {
        log.info("Processing batch inference: {}", batchRequestId);

        BatchInferenceRequest batchRequest = batchInferenceRepository.findById(batchRequestId)
                .orElseThrow(() -> new IllegalArgumentException("Batch request not found"));

        batchRequest.startProcessing();
        batchInferenceRepository.save(batchRequest);

        log.info("Batch inference processing started: {}", batchRequestId);
    }

    @Override
    @Transactional
    public void completeBatchInference(UUID batchRequestId) {
        log.info("Completing batch inference: {}", batchRequestId);

        BatchInferenceRequest batchRequest = batchInferenceRepository.findById(batchRequestId)
                .orElseThrow(() -> new IllegalArgumentException("Batch request not found"));

        batchRequest.complete();
        BatchInferenceRequest savedRequest = batchInferenceRepository.save(batchRequest);

        // Publish domain event
        BatchInferenceCompletedEvent event = BatchInferenceCompletedEvent.builder()
                .eventId(UUID.randomUUID())
                .batchRequestId(savedRequest.getId())
                .tenantId(savedRequest.getTenantId())
                .modelId(savedRequest.getModelId())
                .modelVersion(savedRequest.getModelVersion())
                .totalItems(savedRequest.getTotalItems())
                .completedItems(savedRequest.getCompletedItems())
                .failedItems(savedRequest.getFailedItems())
                .timestamp(LocalDateTime.now())
                .build();
        eventPublisher.publishEvent(event);

        log.info("Batch inference completed: {}", batchRequestId);
    }

    @Override
    @Transactional
    public void failBatchInference(UUID batchRequestId, String errorMessage) {
        log.info("Failing batch inference: {}", batchRequestId);

        BatchInferenceRequest batchRequest = batchInferenceRepository.findById(batchRequestId)
                .orElseThrow(() -> new IllegalArgumentException("Batch request not found"));

        batchRequest.fail(errorMessage);
        batchInferenceRepository.save(batchRequest);

        log.info("Batch inference failed: {}", batchRequestId);
    }

    @Override
    public List<BatchInferenceRequestDto> getPendingBatches() {
        log.debug("Getting pending batch inferences");
        return batchInferenceRepository.findByStatus(BatchStatus.PENDING).stream()
                .map(batchInferenceMapper::toDto)
                .collect(Collectors.toList());
    }
}
