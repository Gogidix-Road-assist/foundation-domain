package com.gogidix.rapidassist.ai.recommendation.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.recommendation.domain.model.ItemSimilarity;
import com.gogidix.rapidassist.ai.recommendation.domain.repository.ItemSimilarityRepositoryPort;
import com.gogidix.rapidassist.ai.recommendation.infrastructure.persistence.entity.ItemSimilarityEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of ItemSimilarityRepositoryPort using MongoDB.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ItemSimilarityRepositoryImpl implements ItemSimilarityRepositoryPort {

    private final SpringDataItemSimilarityRepository springDataRepository;

    @Override
    public ItemSimilarity save(String tenantId, ItemSimilarity similarity) {
        log.debug("Saving item similarity: {} for tenant: {}", similarity.getId(), tenantId);
        ItemSimilarityEntity entity = toEntity(similarity);
        ItemSimilarityEntity saved = springDataRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<ItemSimilarity> findById(String tenantId, UUID id) {
        log.debug("Finding item similarity by id: {} for tenant: {}", id, tenantId);
        return springDataRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public Optional<ItemSimilarity> findByItemPair(String tenantId, String itemType, String item1Id, String item2Id) {
        log.debug("Finding similarity for item pair: {} <-> {} in tenant: {}", item1Id, item2Id, tenantId);
        return Optional.ofNullable(springDataRepository
                .findByTenantIdAndItemTypeAndItem1IdAndItem2Id(tenantId, itemType, item1Id, item2Id))
                .map(this::toDomain);
    }

    @Override
    public List<ItemSimilarity> findByItem1(String tenantId, String itemType, String item1Id) {
        log.debug("Finding similarities for item1: {} in tenant: {}", item1Id, tenantId);
        return springDataRepository.findByTenantIdAndItemTypeAndItem1Id(tenantId, itemType, item1Id).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemSimilarity> findByItem2(String tenantId, String itemType, String item2Id) {
        log.debug("Finding similarities for item2: {} in tenant: {}", item2Id, tenantId);
        return springDataRepository.findByTenantIdAndItemTypeAndItem2Id(tenantId, itemType, item2Id).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemSimilarity> findByItemType(String tenantId, String itemType) {
        log.debug("Finding similarities for item type: {} in tenant: {}", itemType, tenantId);
        return springDataRepository.findByTenantIdAndItemType(tenantId, itemType).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemSimilarity> findSignificantSimilarities(String tenantId, String itemType, Double threshold) {
        log.debug("Finding significant similarities for item type: {} with threshold: {} in tenant: {}", itemType, threshold, tenantId);
        return springDataRepository.findByTenantIdAndItemTypeAndSimilarityScoreGreaterThanEqual(tenantId, itemType, threshold).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String tenantId, UUID id) {
        log.debug("Deleting similarity: {} for tenant: {}", id, tenantId);
        springDataRepository.deleteByTenantIdAndUuid(tenantId, id);
    }

    @Override
    public void deleteByItemType(String tenantId, String itemType) {
        log.debug("Deleting similarities for item type: {} in tenant: {}", itemType, tenantId);
        springDataRepository.deleteByTenantIdAndItemType(tenantId, itemType);
    }

    @Override
    public boolean exists(String tenantId, UUID id) {
        return springDataRepository.existsByTenantIdAndUuid(tenantId, id);
    }

    @Override
    public List<ItemSimilarity> findNeedingRecalculation(String tenantId, int recalcThresholdDays) {
        log.debug("Finding similarities needing recalculation in tenant: {} with threshold: {} days", tenantId, recalcThresholdDays);
        LocalDateTime threshold = LocalDateTime.now().minusDays(recalcThresholdDays);
        return springDataRepository.findByTenantIdAndLastCalculatedAtBefore(tenantId, threshold).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private ItemSimilarityEntity toEntity(ItemSimilarity domain) {
        return ItemSimilarityEntity.builder()
                .uuid(domain.getId())
                .tenantId(domain.getTenantId())
                .itemType(domain.getItemType())
                .item1Id(domain.getItem1Id())
                .item2Id(domain.getItem2Id())
                .similarityScore(domain.getSimilarityScore())
                .similarityAlgorithm(domain.getSimilarityAlgorithm())
                .coOccurrenceCount(domain.getCoOccurrenceCount())
                .lastCalculatedAt(domain.getLastCalculatedAt())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .metadata(domain.getMetadata())
                .build();
    }

    private ItemSimilarity toDomain(ItemSimilarityEntity entity) {
        return ItemSimilarity.builder()
                .id(entity.getUuid())
                .tenantId(entity.getTenantId())
                .itemType(entity.getItemType())
                .item1Id(entity.getItem1Id())
                .item2Id(entity.getItem2Id())
                .similarityScore(entity.getSimilarityScore())
                .similarityAlgorithm(entity.getSimilarityAlgorithm())
                .coOccurrenceCount(entity.getCoOccurrenceCount())
                .lastCalculatedAt(entity.getLastCalculatedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .metadata(entity.getMetadata())
                .build();
    }
}
