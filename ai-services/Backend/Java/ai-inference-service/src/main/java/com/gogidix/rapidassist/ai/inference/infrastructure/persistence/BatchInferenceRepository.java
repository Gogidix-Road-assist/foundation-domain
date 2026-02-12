package com.gogidix.rapidassist.ai.inference.infrastructure.persistence;

import com.gogidix.rapidassist.ai.inference.domain.model.BatchInferenceRequest;
import com.gogidix.rapidassist.ai.inference.application.port.out.BatchInferenceRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * MongoDB implementation of BatchInferenceRepositoryPort
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class BatchInferenceRepository implements BatchInferenceRepositoryPort {

    private final BatchInferenceMongoRepository mongoRepository;

    @Override
    public BatchInferenceRequest save(BatchInferenceRequest batchRequest) {
        log.debug("Saving batch inference request: {}", batchRequest.getBatchId());
        return mongoRepository.save(batchRequest);
    }

    @Override
    public Optional<BatchInferenceRequest> findById(UUID id) {
        log.debug("Finding batch request by id: {}", id);
        return mongoRepository.findById(id);
    }

    @Override
    public Optional<BatchInferenceRequest> findByBatchId(String batchId) {
        log.debug("Finding batch request by batchId: {}", batchId);
        return mongoRepository.findByBatchId(batchId);
    }

    @Override
    public List<BatchInferenceRequest> findByTenantId(String tenantId) {
        log.debug("Finding batch requests for tenant: {}", tenantId);
        return mongoRepository.findByTenantId(tenantId);
    }

    @Override
    public List<BatchInferenceRequest> findByStatus(com.gogidix.rapidassist.ai.inference.domain.model.BatchStatus status) {
        log.debug("Finding batch requests by status: {}", status);
        return mongoRepository.findByStatus(status);
    }

    @Override
    public List<BatchInferenceRequest> findByCreatedBy(String createdBy) {
        log.debug("Finding batch requests created by: {}", createdBy);
        return mongoRepository.findByCreatedBy(createdBy);
    }

    @Override
    public void deleteById(UUID id) {
        log.debug("Deleting batch request: {}", id);
        mongoRepository.deleteById(id);
    }

    @Override
    public List<BatchInferenceRequest> findAll() {
        log.debug("Finding all batch requests");
        return mongoRepository.findAll();
    }

    @Override
    public boolean existsByBatchId(String batchId) {
        return mongoRepository.existsByBatchId(batchId);
    }
}
