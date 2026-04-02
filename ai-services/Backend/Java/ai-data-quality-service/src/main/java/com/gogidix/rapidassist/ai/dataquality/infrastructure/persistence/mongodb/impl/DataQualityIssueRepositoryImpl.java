package com.gogidix.rapidassist.ai.dataquality.infrastructure.persistence.mongodb.impl;

import com.gogidix.rapidassist.ai.dataquality.domain.model.DataQualityIssue;
import com.gogidix.rapidassist.ai.dataquality.domain.repository.DataQualityIssueRepositoryPort;
import com.gogidix.rapidassist.ai.dataquality.infrastructure.persistence.mongodb.adapter.DataQualityRepositoryAdapter;
import com.gogidix.rapidassist.ai.dataquality.infrastructure.persistence.mongodb.repository.SpringDataQualityIssueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DataQualityIssueRepositoryImpl implements DataQualityIssueRepositoryPort {

    private final SpringDataQualityIssueRepository springRepository;

    @Override
    public DataQualityIssue save(DataQualityIssue issue) {
        var entity = DataQualityRepositoryAdapter.toEntity(issue);
        var saved = springRepository.save(entity);
        return DataQualityRepositoryAdapter.toDomain(saved);
    }

    @Override
    public Optional<DataQualityIssue> findById(String tenantId, UUID id) {
        return springRepository.findByTenantIdAndId(tenantId, id)
                .map(DataQualityRepositoryAdapter::toDomain);
    }

    @Override
    public List<DataQualityIssue> findByTenantId(String tenantId) {
        return springRepository.findByTenantId(tenantId).stream()
                .map(DataQualityRepositoryAdapter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<DataQualityIssue> findByTenantIdAndStatus(String tenantId, DataQualityIssue.IssueStatus status) {
        return springRepository.findByTenantIdAndStatus(tenantId, status).stream()
                .map(DataQualityRepositoryAdapter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<DataQualityIssue> findByTenantIdAndSeverity(String tenantId, DataQualityIssue.IssueSeverity severity) {
        return springRepository.findByTenantIdAndSeverity(tenantId, severity).stream()
                .map(DataQualityRepositoryAdapter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<DataQualityIssue> findByTenantIdAndEntityType(String tenantId, String entityType) {
        return springRepository.findByTenantIdAndEntityType(tenantId, entityType).stream()
                .map(DataQualityRepositoryAdapter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<DataQualityIssue> findByCheckId(String tenantId, UUID checkId) {
        return springRepository.findByCheckId(tenantId, checkId).stream()
                .map(DataQualityRepositoryAdapter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<DataQualityIssue> findByRuleId(String tenantId, UUID ruleId) {
        return springRepository.findByRuleId(tenantId, ruleId).stream()
                .map(DataQualityRepositoryAdapter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<DataQualityIssue> findOpenIssuesByTenantId(String tenantId) {
        return springRepository.findOpenIssues(tenantId).stream()
                .map(DataQualityRepositoryAdapter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<DataQualityIssue> findCriticalIssuesByTenantId(String tenantId) {
        return springRepository.findCriticalIssues(tenantId).stream()
                .map(DataQualityRepositoryAdapter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<DataQualityIssue> findStaleIssuesByTenantId(String tenantId, int staleThresholdDays) {
        LocalDateTime threshold = LocalDateTime.now().minusDays(staleThresholdDays);
        return springRepository.findStaleIssues(tenantId, threshold).stream()
                .map(DataQualityRepositoryAdapter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<DataQualityIssue> findIssuesDetectedBetween(String tenantId, LocalDateTime startDate, LocalDateTime endDate) {
        return springRepository.findIssuesDetectedBetween(tenantId, startDate, endDate).stream()
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
    public long countByTenantIdAndStatus(String tenantId, DataQualityIssue.IssueStatus status) {
        return springRepository.countByTenantIdAndStatus(tenantId, status);
    }

    @Override
    public long countByTenantIdAndSeverity(String tenantId, DataQualityIssue.IssueSeverity severity) {
        return springRepository.countByTenantIdAndSeverity(tenantId, severity);
    }
}
