package com.gogidix.rapidassist.ai.analytics.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.analytics.infrastructure.persistence.entity.DataQueryEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data MongoDB repository for DataQueryEntity
 */
@Repository
public interface SpringDataDataQueryRepository extends MongoRepository<DataQueryEntity, String> {

    List<DataQueryEntity> findByTenantId(String tenantId);

    List<DataQueryEntity> findByTenantIdAndIsActive(String tenantId, Boolean isActive);

    List<DataQueryEntity> findByTenantIdAndCategory(String tenantId, String category);

    boolean existsById(String id);
}
