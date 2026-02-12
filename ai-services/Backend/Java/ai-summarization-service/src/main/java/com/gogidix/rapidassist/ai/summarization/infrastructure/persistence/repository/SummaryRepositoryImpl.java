package com.gogidix.rapidassist.ai.summarization.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.summarization.domain.model.Summary;
import com.gogidix.rapidassist.ai.summarization.domain.repository.SummaryRepositoryPort;
import com.gogidix.rapidassist.ai.summarization.infrastructure.persistence.entity.SummaryEntity;
import com.gogidix.rapidassist.ai.summarization.infrastructure.persistence.mapper.SummaryPersistenceMapper;
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
public class SummaryRepositoryImpl implements SummaryRepositoryPort {

    private final SpringDataSummaryRepository springDataRepository;
    private final SummaryPersistenceMapper persistenceMapper;

    @Override
    @Transactional
    public Summary save(String tenantId, Summary summary) {
        log.info("Saving summary: {} for tenant: {}", summary.getId(), tenantId);
        SummaryEntity entity = persistenceMapper.toEntity(summary);
        SummaryEntity savedEntity = springDataRepository.save(entity);
        return persistenceMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Summary> findById(String tenantId, UUID id) {
        log.info("Finding summary by ID: {} for tenant: {}", id, tenantId);
        return springDataRepository.findByUuidAndTenantId(id, tenantId)
                .map(persistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Summary> findBySummarizationRequestId(String tenantId, UUID requestId) {
        log.info("Finding summary by requestId: {} for tenant: {}", requestId, tenantId);
        return springDataRepository.findBySummarizationRequestIdAndTenantId(requestId, tenantId)
                .map(persistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Summary> findByTenantId(String tenantId) {
        log.info("Finding all summaries for tenant: {}", tenantId);
        return springDataRepository.findByTenantId(tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Summary> findByCreatedBy(String tenantId, String createdBy) {
        log.info("Finding summaries by createdBy: {} for tenant: {}", createdBy, tenantId);
        return springDataRepository.findByCreatedByAndTenantId(createdBy, tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void delete(String tenantId, UUID id) {
        log.info("Deleting summary: {} for tenant: {}", id, tenantId);
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
