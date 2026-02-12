package com.gogidix.rapidassist.ai.tagging.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.tagging.infrastructure.persistence.entity.TagAnalyticsEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataTagAnalyticsRepository extends MongoRepository<TagAnalyticsEntity, String> {

    Optional<TagAnalyticsEntity> findByUuidAndTenantId(UUID uuid, String tenantId);
    List<TagAnalyticsEntity> findByTenantId(String tenantId);
    List<TagAnalyticsEntity> findByTagIdAndTenantId(UUID tagId, String tenantId);
    List<TagAnalyticsEntity> findByTenantIdAndPeriod(String tenantId, String period);
    void deleteByUuidAndTenantId(UUID uuid, String tenantId);
}
