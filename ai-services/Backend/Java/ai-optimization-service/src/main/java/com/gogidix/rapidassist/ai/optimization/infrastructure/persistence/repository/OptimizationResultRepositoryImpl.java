package com.gogidix.rapidassist.ai.optimization.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.optimization.domain.model.OptimizationResult;
import com.gogidix.rapidassist.ai.optimization.domain.repository.OptimizationResultRepositoryPort;
import com.gogidix.rapidassist.ai.optimization.infrastructure.persistence.mapper.OptimizationResultPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of OptimizationResultRepositoryPort.
 * Adapters domain repository port to Spring Data MongoDB infrastructure.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class OptimizationResultRepositoryImpl implements OptimizationResultRepositoryPort {

    private final SpringDataOptimizationResultRepository springDataRepository;
    private final OptimizationResultPersistenceMapper persistenceMapper;

    @Override
    @Transactional
    public OptimizationResult save(String tenantId, OptimizationResult result) {
        log.info("Saving optimization result: {} for tenant: {}", result.getId(), tenantId);

        var entity = persistenceMapper.toEntity(result);
        var savedEntity = springDataRepository.save(entity);
        return persistenceMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OptimizationResult> findById(String tenantId, UUID resultId) {
        log.info("Finding optimization result by ID: {} for tenant: {}", resultId, tenantId);

        return springDataRepository.findByUuidAndTenantId(resultId, tenantId)
                .map(persistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OptimizationResult> findByJobId(String tenantId, UUID jobId) {
        log.info("Finding optimization results for job: {} for tenant: {}", jobId, tenantId);

        return springDataRepository.findByOptimizationJobIdAndTenantId(jobId, tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OptimizationResult> findByJobIdOrderByIteration(String tenantId, UUID jobId) {
        log.info("Finding optimization results for job: {} ordered by iteration for tenant: {}", jobId, tenantId);

        return springDataRepository.findByOptimizationJobIdAndTenantIdOrderByIterationAsc(jobId, tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OptimizationResult> findByJobIdAndIteration(String tenantId, UUID jobId, Integer iteration) {
        log.info("Finding optimization result for job: {} and iteration: {} for tenant: {}", jobId, iteration, tenantId);

        return springDataRepository.findByOptimizationJobIdAndTenantIdAndIteration(jobId, tenantId, iteration)
                .map(persistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OptimizationResult> findBestResultByJobId(String tenantId, UUID jobId) {
        log.info("Finding best optimization result for job: {} for tenant: {}", jobId, tenantId);

        return springDataRepository.findFirstByOptimizationJobIdAndTenantIdOrderByObjectiveValueAsc(jobId, tenantId)
                .map(persistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OptimizationResult> findByStatus(String tenantId, String status) {
        log.info("Finding optimization results by status: {} for tenant: {}", status, tenantId);

        return springDataRepository.findByStatusAndTenantId(status, tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void deleteByJobId(String tenantId, UUID jobId) {
        log.info("Deleting optimization results for job: {} for tenant: {}", jobId, tenantId);

        springDataRepository.deleteByOptimizationJobIdAndTenantId(jobId, tenantId);
    }

    @Override
    @Transactional
    public void delete(String tenantId, UUID resultId) {
        log.info("Deleting optimization result: {} for tenant: {}", resultId, tenantId);

        springDataRepository.deleteByUuidAndTenantId(resultId, tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByJobId(String tenantId, UUID jobId) {
        return springDataRepository.countByOptimizationJobIdAndTenantId(jobId, tenantId);
    }
}
