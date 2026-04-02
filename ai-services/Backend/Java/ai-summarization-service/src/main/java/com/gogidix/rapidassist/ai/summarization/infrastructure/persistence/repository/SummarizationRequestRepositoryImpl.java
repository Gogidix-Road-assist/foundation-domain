package com.gogidix.rapidassist.ai.summarization.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.summarization.domain.model.SummarizationRequest;
import com.gogidix.rapidassist.ai.summarization.domain.repository.SummarizationRequestRepositoryPort;
import com.gogidix.rapidassist.ai.summarization.infrastructure.persistence.entity.SummarizationRequestEntity;
import com.gogidix.rapidassist.ai.summarization.infrastructure.persistence.mapper.SummarizationRequestPersistenceMapper;
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
public class SummarizationRequestRepositoryImpl implements SummarizationRequestRepositoryPort {

    private final SpringDataSummarizationRequestRepository springDataRepository;
    private final SummarizationRequestPersistenceMapper persistenceMapper;

    @Override
    @Transactional
    public SummarizationRequest save(String tenantId, SummarizationRequest request) {
        log.info("Saving summarization request: {} for tenant: {}", request.getId(), tenantId);
        SummarizationRequestEntity entity = persistenceMapper.toEntity(request);
        SummarizationRequestEntity savedEntity = springDataRepository.save(entity);
        return persistenceMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SummarizationRequest> findById(String tenantId, UUID id) {
        log.info("Finding summarization request by ID: {} for tenant: {}", id, tenantId);
        return springDataRepository.findByUuidAndTenantId(id, tenantId)
                .map(persistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SummarizationRequest> findByRequestId(String tenantId, String requestId) {
        log.info("Finding summarization request by requestId: {} for tenant: {}", requestId, tenantId);
        return springDataRepository.findByRequestIdAndTenantId(requestId, tenantId)
                .map(persistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SummarizationRequest> findByTenantId(String tenantId) {
        log.info("Finding all summarization requests for tenant: {}", tenantId);
        return springDataRepository.findByTenantId(tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SummarizationRequest> findByStatus(String tenantId, String status) {
        log.info("Finding summarization requests by status: {} for tenant: {}", status, tenantId);
        com.gogidix.rapidassist.ai.summarization.domain.model.SummarizationStatus summarizationStatus =
                com.gogidix.rapidassist.ai.summarization.domain.model.SummarizationStatus.valueOf(status);
        return springDataRepository.findByStatusAndTenantId(summarizationStatus, tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SummarizationRequest> findByCreatedBy(String tenantId, String createdBy) {
        log.info("Finding summarization requests by createdBy: {} for tenant: {}", createdBy, tenantId);
        return springDataRepository.findByCreatedByAndTenantId(createdBy, tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void delete(String tenantId, UUID id) {
        log.info("Deleting summarization request: {} for tenant: {}", id, tenantId);
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
