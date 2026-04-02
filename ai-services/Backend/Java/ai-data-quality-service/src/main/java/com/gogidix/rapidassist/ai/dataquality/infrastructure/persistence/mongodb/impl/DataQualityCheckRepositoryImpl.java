package com.gogidix.rapidassist.ai.dataquality.infrastructure.persistence.mongodb.impl;

import com.gogidix.rapidassist.ai.dataquality.domain.model.DataQualityCheck;
import com.gogidix.rapidassist.ai.dataquality.domain.repository.DataQualityCheckRepositoryPort;
import com.gogidix.rapidassist.ai.dataquality.infrastructure.persistence.mongodb.adapter.DataQualityRepositoryAdapter;
import com.gogidix.rapidassist.ai.dataquality.infrastructure.persistence.mongodb.repository.SpringDataQualityCheckRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DataQualityCheckRepositoryImpl implements DataQualityCheckRepositoryPort {

    private final SpringDataQualityCheckRepository springRepository;

    @Override
    public DataQualityCheck save(DataQualityCheck check) {
        var entity = DataQualityRepositoryAdapter.toEntity(check);
        var saved = springRepository.save(entity);
        return DataQualityRepositoryAdapter.toDomain(saved);
    }

    @Override
    public Optional<DataQualityCheck> findById(String tenantId, UUID id) {
        return springRepository.findByTenantIdAndId(tenantId, id)
                .map(DataQualityRepositoryAdapter::toDomain);
    }

    @Override
    public List<DataQualityCheck> findByTenantId(String tenantId) {
        return springRepository.findByTenantId(tenantId).stream()
                .map(DataQualityRepositoryAdapter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<DataQualityCheck> findByTenantIdAndRuleId(String tenantId, UUID ruleId) {
        return springRepository.findByTenantIdAndRuleId(tenantId, ruleId).stream()
                .map(DataQualityRepositoryAdapter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<DataQualityCheck> findByTenantIdAndStatus(String tenantId, DataQualityCheck.CheckStatus status) {
        return springRepository.findByTenantIdAndStatus(tenantId, status).stream()
                .map(DataQualityRepositoryAdapter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<DataQualityCheck> findByTenantIdAndEntityType(String tenantId, String entityType) {
        return springRepository.findByTenantIdAndEntityType(tenantId, entityType).stream()
                .map(DataQualityRepositoryAdapter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<DataQualityCheck> findByTenantIdAndExecutedAtBetween(String tenantId, LocalDateTime startDate, LocalDateTime endDate) {
        return springRepository.findByTenantIdAndExecutedAtBetween(tenantId, startDate, endDate).stream()
                .map(DataQualityRepositoryAdapter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<DataQualityCheck> findFailedChecksByTenantId(String tenantId) {
        return springRepository.findFailedChecks(tenantId).stream()
                .map(DataQualityRepositoryAdapter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<DataQualityCheck> findRecentChecksByTenantId(String tenantId, int limit) {
        return springRepository.findRecentChecks(tenantId, LocalDateTime.now().minusDays(7))
                .stream()
                .limit(limit)
                .map(DataQualityRepositoryAdapter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String tenantId, UUID id) {
        springRepository.findByTenantIdAndId(tenantId, id).ifPresent(entity -> {
            springRepository.delete(entity);
        });
    }

    @Override
    public long countByTenantIdAndStatus(String tenantId, DataQualityCheck.CheckStatus status) {
        return springRepository.countByTenantIdAndStatus(tenantId, status);
    }
}
