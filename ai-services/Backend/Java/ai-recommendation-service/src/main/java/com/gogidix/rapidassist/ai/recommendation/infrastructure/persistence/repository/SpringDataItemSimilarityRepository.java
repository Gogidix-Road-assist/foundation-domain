package com.gogidix.rapidassist.ai.recommendation.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.recommendation.infrastructure.persistence.entity.ItemSimilarityEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Spring Data MongoDB repository for ItemSimilarityEntity.
 */
@Repository
public interface SpringDataItemSimilarityRepository extends MongoRepository<ItemSimilarityEntity, UUID> {

    ItemSimilarityEntity findByTenantIdAndItemTypeAndItem1IdAndItem2Id(String tenantId, String itemType, String item1Id, String item2Id);

    List<ItemSimilarityEntity> findByTenantIdAndItemTypeAndItem1Id(String tenantId, String itemType, String item1Id);

    List<ItemSimilarityEntity> findByTenantIdAndItemTypeAndItem2Id(String tenantId, String itemType, String item2Id);

    List<ItemSimilarityEntity> findByTenantIdAndItemType(String tenantId, String itemType);

    List<ItemSimilarityEntity> findByTenantIdAndItemTypeAndSimilarityScoreGreaterThanEqual(String tenantId, String itemType, Double threshold);

    List<ItemSimilarityEntity> findByTenantIdAndLastCalculatedAtBefore(String tenantId, LocalDateTime threshold);

    boolean existsByTenantIdAndUuid(String tenantId, UUID uuid);

    void deleteByTenantIdAndUuid(String tenantId, UUID uuid);

    void deleteByTenantIdAndItemType(String tenantId, String itemType);
}
