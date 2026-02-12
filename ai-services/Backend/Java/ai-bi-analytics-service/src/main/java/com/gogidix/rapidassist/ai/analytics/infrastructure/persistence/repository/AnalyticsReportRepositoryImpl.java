package com.gogidix.rapidassist.ai.analytics.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.analytics.domain.model.AnalyticsReport;
import com.gogidix.rapidassist.ai.analytics.domain.repository.AnalyticsReportRepositoryPort;
import com.gogidix.rapidassist.ai.analytics.infrastructure.persistence.entity.AnalyticsReportEntity;
import com.gogidix.rapidassist.ai.analytics.infrastructure.persistence.mapper.AnalyticsReportPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of AnalyticsReportRepositoryPort using MongoDB
 */
@Repository
@RequiredArgsConstructor
public class AnalyticsReportRepositoryImpl implements AnalyticsReportRepositoryPort {

    private final SpringDataAnalyticsReportRepository springDataRepository;
    private final AnalyticsReportPersistenceMapper persistenceMapper;

    @Override
    public AnalyticsReport save(AnalyticsReport report) {
        AnalyticsReportEntity entity = persistenceMapper.toEntity(report);
        entity.setId(report.getId() != null ? report.getId().toString() : UUID.randomUUID().toString());
        AnalyticsReportEntity savedEntity = springDataRepository.save(entity);
        return persistenceMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<AnalyticsReport> findById(UUID id) {
        return springDataRepository.findById(id.toString())
                .map(persistenceMapper::toDomain);
    }

    @Override
    public Optional<AnalyticsReport> findByIdAndTenantId(UUID id, String tenantId) {
        return springDataRepository.findById(id.toString())
                .filter(entity -> entity.getTenantId().equals(tenantId))
                .map(persistenceMapper::toDomain);
    }

    @Override
    public List<AnalyticsReport> findByTenantId(String tenantId) {
        List<AnalyticsReportEntity> entities = springDataRepository.findByTenantId(tenantId);
        return persistenceMapper.toDomainList(entities);
    }

    @Override
    public List<AnalyticsReport> findByTenantIdAndStatus(String tenantId, String status) {
        List<AnalyticsReportEntity> entities = springDataRepository.findByTenantIdAndStatus(tenantId, status);
        return persistenceMapper.toDomainList(entities);
    }

    @Override
    public List<AnalyticsReport> findByTenantIdAndReportType(String tenantId, String reportType) {
        List<AnalyticsReportEntity> entities = springDataRepository.findByTenantIdAndReportType(tenantId, reportType);
        return persistenceMapper.toDomainList(entities);
    }

    @Override
    public void deleteById(UUID id) {
        springDataRepository.deleteById(id.toString());
    }

    @Override
    public boolean existsById(UUID id) {
        return springDataRepository.existsById(id.toString());
    }
}
