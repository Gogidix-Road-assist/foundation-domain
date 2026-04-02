package com.gogidix.rapidassist.ai.tagging.application.port.out;

import com.gogidix.rapidassist.ai.tagging.domain.model.TagAnalytics;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TagAnalyticsRepositoryPort {
    TagAnalytics save(TagAnalytics analytics);
    Optional<TagAnalytics> findById(UUID id);
    Optional<TagAnalytics> findByIdAndTenantId(UUID id, String tenantId);
    List<TagAnalytics> findByTenantId(String tenantId);
    List<TagAnalytics> findByTagIdAndTenantId(UUID tagId, String tenantId);
    List<TagAnalytics> findByTenantIdAndPeriod(String tenantId, String period);
    void deleteById(UUID id);
    void deleteByIdAndTenantId(UUID id, String tenantId);
}
