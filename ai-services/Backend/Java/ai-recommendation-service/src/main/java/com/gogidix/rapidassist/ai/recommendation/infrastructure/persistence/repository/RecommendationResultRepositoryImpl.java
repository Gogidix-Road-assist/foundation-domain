package com.gogidix.rapidassist.ai.recommendation.infrastructure.persistence.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.ai.recommendation.domain.model.RecommendationResult;
import com.gogidix.rapidassist.ai.recommendation.domain.repository.RecommendationResultRepositoryPort;
import com.gogidix.rapidassist.ai.recommendation.infrastructure.persistence.entity.RecommendationResultEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of RecommendationResultRepositoryPort using MongoDB.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationResultRepositoryImpl implements RecommendationResultRepositoryPort {

    private final SpringDataRecommendationResultRepository springDataRepository;
    private final ObjectMapper objectMapper;

    @Override
    public RecommendationResult save(String tenantId, RecommendationResult result) {
        log.debug("Saving recommendation result: {} for tenant: {}", result.getId(), tenantId);
        RecommendationResultEntity entity = toEntity(result);
        RecommendationResultEntity saved = springDataRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<RecommendationResult> findById(String tenantId, UUID id) {
        log.debug("Finding recommendation result by id: {} for tenant: {}", id, tenantId);
        return springDataRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public Optional<RecommendationResult> findByRequestId(String tenantId, UUID requestId) {
        log.debug("Finding recommendation result by request id: {} for tenant: {}", requestId, tenantId);
        return Optional.ofNullable(springDataRepository.findByTenantIdAndRequestId(tenantId, requestId))
                .map(this::toDomain);
    }

    @Override
    public Optional<RecommendationResult> findByCacheKey(String tenantId, String cacheKey) {
        log.debug("Finding recommendation result by cache key: {} for tenant: {}", cacheKey, tenantId);
        return Optional.ofNullable(springDataRepository.findByTenantIdAndCacheKey(tenantId, cacheKey))
                .map(this::toDomain);
    }

    @Override
    public List<RecommendationResult> findByUserId(String tenantId, String userId) {
        log.debug("Finding recommendation results for user: {} in tenant: {}", userId, tenantId);
        return springDataRepository.findByTenantIdAndUserId(tenantId, userId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<RecommendationResult> findByUserIdAndItemType(String tenantId, String userId, String itemType) {
        log.debug("Finding recommendation results for user: {} and item type: {} in tenant: {}", userId, itemType, tenantId);
        return springDataRepository.findByTenantIdAndUserIdAndItemType(tenantId, userId, itemType).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String tenantId, UUID id) {
        log.debug("Deleting recommendation result: {} for tenant: {}", id, tenantId);
        springDataRepository.deleteByTenantIdAndUuid(tenantId, id);
    }

    @Override
    public void deleteByRequestId(String tenantId, UUID requestId) {
        log.debug("Deleting recommendation result for request: {} in tenant: {}", requestId, tenantId);
        springDataRepository.deleteByTenantIdAndRequestId(tenantId, requestId);
    }

    @Override
    public boolean exists(String tenantId, UUID id) {
        return springDataRepository.existsByTenantIdAndUuid(tenantId, id);
    }

    @Override
    public List<RecommendationResult> findExpiredResults(String tenantId) {
        log.debug("Finding expired recommendation results in tenant: {}", tenantId);
        LocalDateTime now = LocalDateTime.now();
        return springDataRepository.findByTenantIdAndExpiresAtBefore(tenantId, now).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteExpiredResults(String tenantId) {
        log.debug("Deleting expired recommendation results in tenant: {}", tenantId);
        LocalDateTime now = LocalDateTime.now();
        springDataRepository.deleteByTenantIdAndExpiresAtBefore(tenantId, now);
    }

    private RecommendationResultEntity toEntity(RecommendationResult domain) {
        String itemsJson = null;
        if (domain.getItems() != null) {
            try {
                itemsJson = objectMapper.writeValueAsString(domain.getItems());
            } catch (JsonProcessingException e) {
                log.error("Error serializing items", e);
            }
        }

        return RecommendationResultEntity.builder()
                .uuid(domain.getId())
                .tenantId(domain.getTenantId())
                .requestId(domain.getRequestId())
                .userId(domain.getUserId())
                .recommendationType(domain.getRecommendationType())
                .itemType(domain.getItemType())
                .status(domain.getStatus())
                .items(itemsJson)
                .totalResults(domain.getTotalResults())
                .confidenceScore(domain.getConfidenceScore())
                .processingTimeMs(domain.getProcessingTimeMs())
                .algorithm(domain.getAlgorithm())
                .metadata(domain.getMetadata() != null ? domain.getMetadata().toString() : null)
                .createdAt(domain.getCreatedAt())
                .expiresAt(domain.getExpiresAt())
                .cacheKey(domain.getCacheKey())
                .cached(domain.getCached())
                .build();
    }

    private RecommendationResult toDomain(RecommendationResultEntity entity) {
        List<RecommendationResult.RecommendedItem> items = null;
        if (entity.getItems() != null) {
            try {
                items = objectMapper.readValue(entity.getItems(), new TypeReference<List<RecommendationResult.RecommendedItem>>() {});
            } catch (JsonProcessingException e) {
                log.error("Error deserializing items", e);
            }
        }

        return RecommendationResult.builder()
                .id(entity.getUuid())
                .tenantId(entity.getTenantId())
                .requestId(entity.getRequestId())
                .userId(entity.getUserId())
                .recommendationType(entity.getRecommendationType())
                .itemType(entity.getItemType())
                .status(entity.getStatus())
                .items(items)
                .totalResults(entity.getTotalResults())
                .confidenceScore(entity.getConfidenceScore())
                .processingTimeMs(entity.getProcessingTimeMs())
                .algorithm(entity.getAlgorithm())
                .createdAt(entity.getCreatedAt())
                .expiresAt(entity.getExpiresAt())
                .cacheKey(entity.getCacheKey())
                .cached(entity.getCached())
                .build();
    }
}
