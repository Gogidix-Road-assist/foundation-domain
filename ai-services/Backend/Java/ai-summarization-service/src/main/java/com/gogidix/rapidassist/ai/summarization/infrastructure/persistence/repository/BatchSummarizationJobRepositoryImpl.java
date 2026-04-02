package com.gogidix.rapidassist.ai.summarization.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.summarization.domain.model.BatchSummarizationJob;
import com.gogidix.rapidassist.ai.summarization.domain.repository.BatchSummarizationJobRepositoryPort;
import com.gogidix.rapidassist.ai.summarization.infrastructure.persistence.entity.BatchSummarizationJobEntity;
import com.gogidix.rapidassist.ai.summarization.infrastructure.persistence.mapper.BatchSummarizationJobPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class BatchSummarizationJobRepositoryImpl implements BatchSummarizationJobRepositoryPort {

    private final SpringDataBatchSummarizationJobRepository springDataRepository;
    private final BatchSummarizationJobPersistenceMapper persistenceMapper;

    @Override
    @Transactional
    public BatchSummarizationJob save(String tenantId, BatchSummarizationJob job) {
        log.info("Saving batch summarization job: {} for tenant: {}", job.getId(), tenantId);
        BatchSummarizationJobEntity entity = persistenceMapper.toEntity(job);
        BatchSummarizationJobEntity savedEntity = springDataRepository.save(entity);
        return persistenceMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BatchSummarizationJob> findById(String tenantId, UUID id) {
        log.info("Finding batch job by ID: {} for tenant: {}", id, tenantId);
        return springDataRepository.findByUuidAndTenantId(id, tenantId)
                .map(persistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BatchSummarizationJob> findByJobId(String tenantId, String jobId) {
        log.info("Finding batch job by jobId: {} for tenant: {}", jobId, tenantId);
        return springDataRepository.findByJobIdAndTenantId(jobId, tenantId)
                .map(persistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BatchSummarizationJob> findByTenantId(String tenantId) {
        log.info("Finding all batch jobs for tenant: {}", tenantId);
        return springDataRepository.findByTenantId(tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BatchSummarizationJob> findByStatus(String tenantId, String status) {
        log.info("Finding batch jobs by status: {} for tenant: {}", status, tenantId);
        com.gogidix.rapidassist.ai.summarization.domain.model.SummarizationStatus jobStatus =
                com.gogidix.rapidassist.ai.summarization.domain.model.SummarizationStatus.valueOf(status);
        return springDataRepository.findByStatusAndTenantId(jobStatus, tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BatchSummarizationJob> findByCreatedBy(String tenantId, String createdBy) {
        log.info("Finding batch jobs by createdBy: {} for tenant: {}", createdBy, tenantId);
        return springDataRepository.findByCreatedByAndTenantId(createdBy, tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void delete(String tenantId, UUID id) {
        log.info("Deleting batch job: {} for tenant: {}", id, tenantId);
        springDataRepository.deleteByUuidAndTenantId(id, tenantId);
    }
}
