package com.gogidix.rapidassist.ai.summarization.domain.repository;

import com.gogidix.rapidassist.ai.summarization.domain.model.SummaryQualityMetrics;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SummaryQualityMetricsRepositoryPort {
    SummaryQualityMetrics save(String tenantId, SummaryQualityMetrics metrics);
    Optional<SummaryQualityMetrics> findById(String tenantId, UUID id);
    Optional<SummaryQualityMetrics> findBySummaryId(String tenantId, UUID summaryId);
    List<SummaryQualityMetrics> findByTenantId(String tenantId);
    void delete(String tenantId, UUID id);
}
