package com.gogidix.rapidassist.ai.recommendation.domain.repository;

import com.gogidix.rapidassist.ai.recommendation.domain.model.ItemSimilarity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for ItemSimilarity aggregate.
 */
public interface ItemSimilarityRepositoryPort {

    ItemSimilarity save(String tenantId, ItemSimilarity similarity);

    Optional<ItemSimilarity> findById(String tenantId, UUID id);

    Optional<ItemSimilarity> findByItemPair(String tenantId, String itemType, String item1Id, String item2Id);

    List<ItemSimilarity> findByItem1(String tenantId, String itemType, String item1Id);

    List<ItemSimilarity> findByItem2(String tenantId, String itemType, String item2Id);

    List<ItemSimilarity> findByItemType(String tenantId, String itemType);

    List<ItemSimilarity> findSignificantSimilarities(String tenantId, String itemType, Double threshold);

    void delete(String tenantId, UUID id);

    void deleteByItemType(String tenantId, String itemType);

    boolean exists(String tenantId, UUID id);

    List<ItemSimilarity> findNeedingRecalculation(String tenantId, int recalcThresholdDays);
}
