package com.gogidix.rapidassist.ai.categorization.infrastructure.persistence.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.ai.categorization.application.port.out.CategorizationRequestRepositoryPort;
import com.gogidix.rapidassist.ai.categorization.domain.model.CategorizationRequest;
import com.gogidix.rapidassist.ai.categorization.domain.model.CategorizationStatus;
import com.gogidix.rapidassist.ai.categorization.infrastructure.persistence.entity.CategorizationRequestEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CategorizationRequestRepositoryImpl implements CategorizationRequestRepositoryPort {

    private final SpringDataCategorizationRequestRepository springDataRepository;
    private final ObjectMapper objectMapper;

    @Override
    public CategorizationRequest save(String tenantId, CategorizationRequest request) {
        log.info("Saving categorization request: {} for tenant: {}", request.getId(), tenantId);
        CategorizationRequestEntity entity = toEntity(request);
        CategorizationRequestEntity saved = springDataRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<CategorizationRequest> findById(String tenantId, UUID requestId) {
        return springDataRepository.findByUuidAndTenantId(requestId, tenantId)
                .map(this::toDomain);
    }

    @Override
    public Optional<CategorizationRequest> findByContentId(String tenantId, String contentId) {
        return springDataRepository.findByContentIdAndTenantId(contentId, tenantId)
                .map(this::toDomain);
    }

    @Override
    public List<CategorizationRequest> findByTenantId(String tenantId) {
        return springDataRepository.findByTenantId(tenantId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<CategorizationRequest> findByStatus(String tenantId, String status) {
        return springDataRepository.findByTenantIdAndStatus(tenantId, status).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<CategorizationRequest> findPendingRequests(String tenantId) {
        return springDataRepository.findByTenantIdAndStatus(tenantId, "PENDING").stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void delete(String tenantId, UUID requestId) {
        springDataRepository.deleteByUuidAndTenantId(requestId, tenantId);
    }

    @Override
    public boolean exists(String tenantId, UUID requestId) {
        return springDataRepository.existsByUuidAndTenantId(requestId, tenantId);
    }

    private CategorizationRequestEntity toEntity(CategorizationRequest domain) {
        return CategorizationRequestEntity.builder()
                .uuid(domain.getId())
                .tenantId(domain.getTenantId())
                .contentId(domain.getContentId())
                .contentType(domain.getContentType())
                .content(domain.getContent())
                .taxonomyId(domain.getTaxonomyId())
                .batchRequest(domain.isBatchRequest())
                .maxCategories(domain.getMaxCategories())
                .minConfidence(domain.getMinConfidence())
                .status(domain.getStatus() != null ? domain.getStatus().name() : null)
                .errorMessage(domain.getErrorMessage())
                .metadata(serializeMap(domain.getMetadata()))
                .createdAt(domain.getCreatedAt())
                .processedAt(domain.getProcessedAt())
                .createdBy(domain.getCreatedBy())
                .build();
    }

    private CategorizationRequest toDomain(CategorizationRequestEntity entity) {
        return CategorizationRequest.builder()
                .id(entity.getUuid())
                .tenantId(entity.getTenantId())
                .contentId(entity.getContentId())
                .contentType(entity.getContentType())
                .content(entity.getContent())
                .taxonomyId(entity.getTaxonomyId())
                .batchRequest(entity.getBatchRequest() != null ? entity.getBatchRequest() : false)
                .maxCategories(entity.getMaxCategories())
                .minConfidence(entity.getMinConfidence())
                .status(entity.getStatus() != null ?
                    CategorizationStatus.valueOf(entity.getStatus()) : null)
                .errorMessage(entity.getErrorMessage())
                .metadata(deserializeMap(entity.getMetadata()))
                .createdAt(entity.getCreatedAt())
                .processedAt(entity.getProcessedAt())
                .createdBy(entity.getCreatedBy())
                .build();
    }

    private String serializeMap(java.util.Map<String, Object> map) {
        try {
            return map != null ? objectMapper.writeValueAsString(map) : null;
        } catch (Exception e) {
            log.error("Error serializing map", e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private java.util.Map<String, Object> deserializeMap(String json) {
        try {
            return json != null ? objectMapper.readValue(json, java.util.Map.class) : null;
        } catch (Exception e) {
            log.error("Error deserializing map", e);
            return null;
        }
    }
}
