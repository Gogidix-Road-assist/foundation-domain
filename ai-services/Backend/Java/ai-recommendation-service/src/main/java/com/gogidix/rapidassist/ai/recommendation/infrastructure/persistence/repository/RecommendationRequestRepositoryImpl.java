package com.gogidix.rapidassist.ai.recommendation.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.recommendation.domain.model.RecommendationRequest;
import com.gogidix.rapidassist.ai.recommendation.domain.model.RecommendationStatus;
import com.gogidix.rapidassist.ai.recommendation.domain.repository.RecommendationRequestRepositoryPort;
import com.gogidix.rapidassist.ai.recommendation.infrastructure.persistence.entity.RecommendationRequestEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of RecommendationRequestRepositoryPort using MongoDB.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationRequestRepositoryImpl implements RecommendationRequestRepositoryPort {

    private final SpringDataRecommendationRequestRepository springDataRepository;

    @Override
    public RecommendationRequest save(String tenantId, RecommendationRequest request) {
        log.debug("Saving recommendation request: {} for tenant: {}", request.getId(), tenantId);
        RecommendationRequestEntity entity = toEntity(request);
        RecommendationRequestEntity saved = springDataRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<RecommendationRequest> findById(String tenantId, UUID id) {
        log.debug("Finding recommendation request by id: {} for tenant: {}", id, tenantId);
        return springDataRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public List<RecommendationRequest> findByUserId(String tenantId, String userId) {
        log.debug("Finding recommendation requests for user: {} in tenant: {}", userId, tenantId);
        return springDataRepository.findByTenantIdAndUserId(tenantId, userId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<RecommendationRequest> findByStatus(String tenantId, RecommendationStatus status) {
        log.debug("Finding recommendation requests with status: {} in tenant: {}", status, tenantId);
        return springDataRepository.findByTenantIdAndStatus(tenantId, status).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<RecommendationRequest> findPendingRequests(String tenantId) {
        log.debug("Finding pending recommendation requests in tenant: {}", tenantId);
        return springDataRepository.findByTenantIdAndStatus(tenantId, RecommendationStatus.PENDING).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<RecommendationRequest> findExpiredRequests(String tenantId) {
        log.debug("Finding expired recommendation requests in tenant: {}", tenantId);
        LocalDateTime now = LocalDateTime.now();
        return springDataRepository.findByTenantIdAndExpiresAtBefore(tenantId, now).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String tenantId, UUID id) {
        log.debug("Deleting recommendation request: {} for tenant: {}", id, tenantId);
        springDataRepository.deleteByTenantIdAndUuid(tenantId, id);
    }

    @Override
    public void deleteByUserId(String tenantId, String userId) {
        log.debug("Deleting recommendation requests for user: {} in tenant: {}", userId, tenantId);
        springDataRepository.deleteByTenantIdAndUserId(tenantId, userId);
    }

    @Override
    public boolean exists(String tenantId, UUID id) {
        return springDataRepository.existsByTenantIdAndUuid(tenantId, id);
    }

    private RecommendationRequestEntity toEntity(RecommendationRequest domain) {
        return RecommendationRequestEntity.builder()
                .uuid(domain.getId())
                .tenantId(domain.getTenantId())
                .userId(domain.getUserId())
                .recommendationType(domain.getRecommendationType())
                .itemType(domain.getItemType())
                .contextType(domain.getContextType())
                .contextId(domain.getContextId())
                .limit(domain.getLimit())
                .filters(domain.getFilters() != null ? domain.getFilters().toString() : null)
                .parameters(domain.getParameters() != null ? domain.getParameters().toString() : null)
                .status(domain.getStatus())
                .errorMessage(domain.getErrorMessage())
                .createdAt(domain.getCreatedAt())
                .processedAt(domain.getProcessedAt())
                .expiresAt(domain.getExpiresAt())
                .metadata(domain.getMetadata())
                .build();
    }

    private RecommendationRequest toDomain(RecommendationRequestEntity entity) {
        return RecommendationRequest.builder()
                .id(entity.getUuid())
                .tenantId(entity.getTenantId())
                .userId(entity.getUserId())
                .recommendationType(entity.getRecommendationType())
                .itemType(entity.getItemType())
                .contextType(entity.getContextType())
                .contextId(entity.getContextId())
                .limit(entity.getLimit())
                .status(entity.getStatus())
                .errorMessage(entity.getErrorMessage())
                .createdAt(entity.getCreatedAt())
                .processedAt(entity.getProcessedAt())
                .expiresAt(entity.getExpiresAt())
                .metadata(entity.getMetadata())
                .build();
    }
}
