package com.gogidix.rapidassist.ai.inference.infrastructure.persistence;

import com.gogidix.rapidassist.ai.inference.domain.aggregate.InferenceRequest;
import com.gogidix.rapidassist.ai.inference.domain.model.InferenceResult;
import com.gogidix.rapidassist.ai.inference.domain.model.InferenceMetrics;
import com.gogidix.rapidassist.ai.inference.application.port.out.InferenceRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * MongoDB implementation of InferenceRepositoryPort
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class InferenceRequestRepository implements InferenceRepositoryPort {

    private final InferenceRequestMongoRepository mongoRepository;
    private final InferenceResultMongoRepository resultRepository;
    private final InferenceMetricsMongoRepository metricsRepository;

    @Override
    public InferenceRequest save(InferenceRequest inferenceRequest) {
        log.debug("Saving inference request: {}", inferenceRequest.getId());
        return mongoRepository.save(inferenceRequest);
    }

    @Override
    public Optional<InferenceRequest> findById(UUID id) {
        log.debug("Finding inference request by id: {}", id);
        return mongoRepository.findById(id);
    }

    @Override
    public Optional<InferenceRequest> findByRequestId(String requestId) {
        log.debug("Finding inference request by requestId: {}", requestId);
        return Optional.ofNullable(mongoRepository.findByRequestId(requestId));
    }

    @Override
    public List<InferenceRequest> findByTenantId(String tenantId) {
        log.debug("Finding inference requests for tenant: {}", tenantId);
        return mongoRepository.findByTenantId(tenantId);
    }

    @Override
    public List<InferenceRequest> findByModelId(String modelId) {
        log.debug("Finding inference requests for model: {}", modelId);
        return mongoRepository.findByModelId(modelId);
    }

    @Override
    public List<InferenceRequest> findByStatus(com.gogidix.rapidassist.ai.inference.domain.model.InferenceStatus status) {
        log.debug("Finding inference requests by status: {}", status);
        return mongoRepository.findByStatus(status);
    }

    @Override
    public List<InferenceRequest> findByTenantIdAndStatus(String tenantId, com.gogidix.rapidassist.ai.inference.domain.model.InferenceStatus status) {
        log.debug("Finding inference requests for tenant {} and status {}", tenantId, status);
        return mongoRepository.findByTenantIdAndStatus(tenantId, status);
    }

    @Override
    public List<InferenceRequest> findByRequestedBy(String requestedBy) {
        log.debug("Finding inference requests requested by: {}", requestedBy);
        return mongoRepository.findByRequestedBy(requestedBy);
    }

    @Override
    public void deleteById(UUID id) {
        log.debug("Deleting inference request: {}", id);
        mongoRepository.deleteById(id);
    }

    @Override
    public List<InferenceRequest> findAll() {
        log.debug("Finding all inference requests");
        return mongoRepository.findAll();
    }

    @Override
    public boolean existsByRequestId(String requestId) {
        return mongoRepository.existsByRequestId(requestId);
    }
}
