package com.gogidix.rapidassist.ai.matching.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.matching.domain.model.BatchJobStatus;
import com.gogidix.rapidassist.ai.matching.domain.model.BatchMatchingJob;
import com.gogidix.rapidassist.ai.matching.domain.repository.BatchMatchingJobRepositoryPort;
import com.gogidix.rapidassist.ai.matching.infrastructure.persistence.entity.BatchMatchingJobEntity;
import com.gogidix.rapidassist.ai.matching.infrastructure.persistence.mapper.BatchMatchingJobPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of BatchMatchingJobRepositoryPort.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class BatchMatchingJobRepositoryImpl implements BatchMatchingJobRepositoryPort {

    private final SpringDataBatchMatchingJobRepository springDataRepository;
    private final BatchMatchingJobPersistenceMapper persistenceMapper;

    @Override
    @Transactional
    public BatchMatchingJob save(String tenantId, BatchMatchingJob batchJob) {
        log.info("Saving batch matching job: {} for tenant: {}", batchJob.getId(), tenantId);

        BatchMatchingJobEntity entity = persistenceMapper.toEntity(batchJob);
        BatchMatchingJobEntity savedEntity = springDataRepository.save(entity);

        return persistenceMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BatchMatchingJob> findById(String tenantId, UUID id) {
        log.info("Finding batch matching job by ID: {} for tenant: {}", id, tenantId);

        return springDataRepository.findByUuidAndTenantId(id, tenantId)
                .map(persistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BatchMatchingJob> findByJobCode(String tenantId, String jobCode) {
        log.info("Finding batch matching job by job code: {} for tenant: {}", jobCode, tenantId);

        return springDataRepository.findByJobCodeAndTenantId(jobCode, tenantId)
                .map(persistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BatchMatchingJob> findByTenantId(String tenantId) {
        log.info("Finding all batch matching jobs for tenant: {}", tenantId);

        return springDataRepository.findByTenantId(tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BatchMatchingJob> findByStatus(String tenantId, String status) {
        log.info("Finding batch matching jobs by status: {} for tenant: {}", status, tenantId);

        BatchJobStatus jobStatus = BatchJobStatus.valueOf(status);
        return springDataRepository.findByStatusAndTenantId(jobStatus, tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BatchMatchingJob> findActiveJobs(String tenantId) {
        log.info("Finding active batch matching jobs for tenant: {}", tenantId);

        List<BatchJobStatus> activeStatuses = List.of(
                BatchJobStatus.PENDING,
                BatchJobStatus.RUNNING
        );
        return springDataRepository.findByStatusInAndTenantId(activeStatuses, tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void delete(String tenantId, UUID id) {
        log.info("Deleting batch matching job: {} for tenant: {}", id, tenantId);

        springDataRepository.deleteByUuidAndTenantId(id, tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean exists(String tenantId, UUID id) {
        return springDataRepository.existsByUuidAndTenantId(id, tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByTenantId(String tenantId) {
        return springDataRepository.countByTenantId(tenantId);
    }
}
