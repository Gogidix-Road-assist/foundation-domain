package com.gogidix.rapidassist.ai.optimization.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.optimization.domain.model.OptimizationJob;
import com.gogidix.rapidassist.ai.optimization.domain.model.OptimizationStatus;
import com.gogidix.rapidassist.ai.optimization.domain.model.OptimizationType;
import com.gogidix.rapidassist.ai.optimization.domain.repository.OptimizationJobRepositoryPort;
import com.gogidix.rapidassist.ai.optimization.infrastructure.persistence.entity.OptimizationJobEntity;
import com.gogidix.rapidassist.ai.optimization.infrastructure.persistence.mapper.OptimizationJobPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of OptimizationJobRepositoryPort.
 * Adapters domain repository port to Spring Data MongoDB infrastructure.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class OptimizationJobRepositoryImpl implements OptimizationJobRepositoryPort {

    private final SpringDataOptimizationJobRepository springDataRepository;
    private final OptimizationJobPersistenceMapper persistenceMapper;

    @Override
    @Transactional
    public OptimizationJob save(String tenantId, OptimizationJob job) {
        log.info("Saving optimization job: {} for tenant: {}", job.getId(), tenantId);

        OptimizationJobEntity entity = persistenceMapper.toEntity(job);
        entity.setUpdatedAt(LocalDateTime.now());

        OptimizationJobEntity savedEntity = springDataRepository.save(entity);
        return persistenceMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OptimizationJob> findById(String tenantId, UUID jobId) {
        log.info("Finding optimization job by ID: {} for tenant: {}", jobId, tenantId);

        return springDataRepository.findByUuidAndTenantId(jobId, tenantId)
                .map(persistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OptimizationJob> findByTenantId(String tenantId) {
        log.info("Finding all optimization jobs for tenant: {}", tenantId);

        return springDataRepository.findByTenantId(tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OptimizationJob> findByType(String tenantId, OptimizationType type) {
        log.info("Finding optimization jobs by type: {} for tenant: {}", type, tenantId);

        return springDataRepository.findByTypeAndTenantId(type, tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OptimizationJob> findByStatus(String tenantId, OptimizationStatus status) {
        log.info("Finding optimization jobs by status: {} for tenant: {}", status, tenantId);

        return springDataRepository.findByStatusAndTenantId(status, tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OptimizationJob> findByModelId(String tenantId, String modelId) {
        log.info("Finding optimization jobs by model ID: {} for tenant: {}", modelId, tenantId);

        return springDataRepository.findByModelIdAndTenantId(modelId, tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OptimizationJob> findByDatasetId(String tenantId, String datasetId) {
        log.info("Finding optimization jobs by dataset ID: {} for tenant: {}", datasetId, tenantId);

        return springDataRepository.findByDatasetIdAndTenantId(datasetId, tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OptimizationJob> findRunningJobs(String tenantId) {
        log.info("Finding running optimization jobs for tenant: {}", tenantId);

        return springDataRepository.findByStatusAndTenantId(OptimizationStatus.RUNNING, tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OptimizationJob> findByStatusAndType(String tenantId, OptimizationStatus status, OptimizationType type) {
        log.info("Finding optimization jobs by status: {} and type: {} for tenant: {}", status, type, tenantId);

        return springDataRepository.findByStatusAndTypeAndTenantId(status, type, tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OptimizationJob> findByCreatedAtBetween(String tenantId, LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Finding optimization jobs created between: {} and {} for tenant: {}", startDate, endDate, tenantId);

        return springDataRepository.findByTenantIdAndCreatedAtBetween(tenantId, startDate, endDate).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OptimizationJob> findByCreatedBy(String tenantId, String createdBy) {
        log.info("Finding optimization jobs by creator: {} for tenant: {}", createdBy, tenantId);

        return springDataRepository.findByCreatedByAndTenantId(createdBy, tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OptimizationJob> findByNameContaining(String tenantId, String namePattern) {
        log.info("Finding optimization jobs by name pattern: {} for tenant: {}", namePattern, tenantId);

        return springDataRepository.findByTenantIdAndNameContainingIgnoreCase(tenantId, namePattern).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void delete(String tenantId, UUID jobId) {
        log.info("Deleting optimization job: {} for tenant: {}", jobId, tenantId);

        springDataRepository.deleteByUuidAndTenantId(jobId, tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean exists(String tenantId, UUID jobId) {
        return springDataRepository.existsByUuidAndTenantId(jobId, tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByTenantId(String tenantId) {
        return springDataRepository.countByTenantId(tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByStatusAndTenantId(String tenantId, OptimizationStatus status) {
        return springDataRepository.countByStatusAndTenantId(status, tenantId);
    }
}
