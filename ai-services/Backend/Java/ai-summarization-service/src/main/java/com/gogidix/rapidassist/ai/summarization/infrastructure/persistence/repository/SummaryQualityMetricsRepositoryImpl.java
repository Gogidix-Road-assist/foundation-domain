package com.gogidix.rapidassist.ai.summarization.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.summarization.domain.model.SummaryQualityMetrics;
import com.gogidix.rapidassist.ai.summarization.domain.repository.SummaryQualityMetricsRepositoryPort;
import com.gogidix.rapidassist.ai.summarization.infrastructure.persistence.entity.SummaryQualityMetricsEntity;
import com.gogidix.rapidassist.ai.summarization.infrastructure.persistence.mapper.SummaryQualityMetricsPersistenceMapper;
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
public class SummaryQualityMetricsRepositoryImpl implements SummaryQualityMetricsRepositoryPort {

    private final SpringDataSummaryQualityMetricsRepository springDataRepository;
    private final SummaryQualityMetricsPersistenceMapper persistenceMapper;

    @Override
    @Transactional
    public SummaryQualityMetrics save(String tenantId, SummaryQualityMetrics metrics) {
        log.info("Saving quality metrics: {} for tenant: {}", metrics.getId(), tenantId);
        SummaryQualityMetricsEntity entity = persistenceMapper.toEntity(metrics);
        SummaryQualityMetricsEntity savedEntity = springDataRepository.save(entity);
        return persistenceMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SummaryQualityMetrics> findById(String tenantId, UUID id) {
        log.info("Finding quality metrics by ID: {} for tenant: {}", id, tenantId);
        return springDataRepository.findByUuidAndTenantId(id, tenantId)
                .map(persistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SummaryQualityMetrics> findBySummaryId(String tenantId, UUID summaryId) {
        log.info("Finding quality metrics by summaryId: {} for tenant: {}", summaryId, tenantId);
        return springDataRepository.findBySummaryIdAndTenantId(summaryId, tenantId)
                .map(persistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SummaryQualityMetrics> findByTenantId(String tenantId) {
        log.info("Finding all quality metrics for tenant: {}", tenantId);
        return springDataRepository.findByTenantId(tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void delete(String tenantId, UUID id) {
        log.info("Deleting quality metrics: {} for tenant: {}", id, tenantId);
        springDataRepository.deleteByUuidAndTenantId(id, tenantId);
    }
}
