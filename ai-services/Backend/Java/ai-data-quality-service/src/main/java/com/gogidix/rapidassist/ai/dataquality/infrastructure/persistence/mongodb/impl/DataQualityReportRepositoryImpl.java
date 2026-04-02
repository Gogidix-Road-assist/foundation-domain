package com.gogidix.rapidassist.ai.dataquality.infrastructure.persistence.mongodb.impl;

import com.gogidix.rapidassist.ai.dataquality.domain.model.DataQualityReport;
import com.gogidix.rapidassist.ai.dataquality.domain.repository.DataQualityReportRepositoryPort;
import com.gogidix.rapidassist.ai.dataquality.infrastructure.persistence.mongodb.adapter.DataQualityRepositoryAdapter;
import com.gogidix.rapidassist.ai.dataquality.infrastructure.persistence.mongodb.repository.SpringDataQualityReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DataQualityReportRepositoryImpl implements DataQualityReportRepositoryPort {

    private final SpringDataQualityReportRepository springRepository;

    @Override
    public DataQualityReport save(DataQualityReport report) {
        var entity = DataQualityRepositoryAdapter.toEntity(report);
        var saved = springRepository.save(entity);
        return DataQualityRepositoryAdapter.toDomain(saved);
    }

    @Override
    public Optional<DataQualityReport> findById(String tenantId, UUID id) {
        return springRepository.findByTenantIdAndId(tenantId, id)
                .map(DataQualityRepositoryAdapter::toDomain);
    }

    @Override
    public List<DataQualityReport> findByTenantId(String tenantId) {
        return springRepository.findByTenantId(tenantId).stream()
                .map(DataQualityRepositoryAdapter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<DataQualityReport> findByTenantIdAndReportType(String tenantId, String reportType) {
        return springRepository.findByTenantIdAndReportType(tenantId, reportType).stream()
                .map(DataQualityRepositoryAdapter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<DataQualityReport> findByTenantIdAndReportPeriodBetween(String tenantId, LocalDateTime startDate, LocalDateTime endDate) {
        return springRepository.findByTenantIdAndReportPeriodBetween(tenantId, startDate, endDate).stream()
                .map(DataQualityRepositoryAdapter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<DataQualityReport> findRecentReportsByTenantId(String tenantId, int limit) {
        return springRepository.findRecentReports(tenantId).stream()
                .limit(limit)
                .map(DataQualityRepositoryAdapter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<DataQualityReport> findLatestReportByTenantIdAndType(String tenantId, String reportType) {
        return springRepository.findLatestByTenantIdAndType(tenantId, reportType)
                .map(DataQualityRepositoryAdapter::toDomain);
    }

    @Override
    public void deleteById(String tenantId, UUID id) {
        springRepository.findByTenantIdAndId(tenantId, id).ifPresent(entity -> {
            springRepository.delete(entity);
        });
    }

    @Override
    public long countByTenantId(String tenantId) {
        return springRepository.countByTenantId(tenantId);
    }
}
