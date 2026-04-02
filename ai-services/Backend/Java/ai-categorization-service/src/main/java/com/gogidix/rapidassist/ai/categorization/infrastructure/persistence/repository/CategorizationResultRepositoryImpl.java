package com.gogidix.rapidassist.ai.categorization.infrastructure.persistence.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.ai.categorization.application.port.out.CategorizationResultRepositoryPort;
import com.gogidix.rapidassist.ai.categorization.domain.model.CategorizationResult;
import com.gogidix.rapidassist.ai.categorization.domain.model.CategorizationStatus;
import com.gogidix.rapidassist.ai.categorization.infrastructure.persistence.entity.CategorizationResultEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CategorizationResultRepositoryImpl implements CategorizationResultRepositoryPort {

    private final SpringDataCategorizationResultRepository springDataRepository;
    private final ObjectMapper objectMapper;

    @Override
    public CategorizationResult save(String tenantId, CategorizationResult result) {
        log.info("Saving categorization result: {} for tenant: {}", result.getId(), tenantId);
        CategorizationResultEntity entity = toEntity(result);
        CategorizationResultEntity saved = springDataRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<CategorizationResult> findById(String tenantId, UUID resultId) {
        return springDataRepository.findByUuidAndTenantId(resultId, tenantId)
                .map(this::toDomain);
    }

    @Override
    public Optional<CategorizationResult> findByRequestId(String tenantId, UUID requestId) {
        return springDataRepository.findByRequestIdAndTenantId(requestId, tenantId)
                .map(this::toDomain);
    }

    @Override
    public List<CategorizationResult> findByContentId(String tenantId, String contentId) {
        return springDataRepository.findByContentIdAndTenantId(contentId, tenantId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<CategorizationResult> findByTenantId(String tenantId) {
        return springDataRepository.findByTenantId(tenantId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<CategorizationResult> findByStatus(String tenantId, String status) {
        return springDataRepository.findByTenantIdAndStatus(tenantId, status).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void delete(String tenantId, UUID resultId) {
        springDataRepository.deleteByUuidAndTenantId(resultId, tenantId);
    }

    @Override
    public boolean exists(String tenantId, UUID resultId) {
        return springDataRepository.existsByUuidAndTenantId(resultId, tenantId);
    }

    private CategorizationResultEntity toEntity(CategorizationResult domain) {
        return CategorizationResultEntity.builder()
                .uuid(domain.getId())
                .tenantId(domain.getTenantId())
                .requestId(domain.getRequestId())
                .contentId(domain.getContentId())
                .contentType(domain.getContentType())
                .taxonomyId(domain.getTaxonomyId())
                .predictions(serializePredictions(domain.getPredictions()))
                .status(domain.getStatus() != null ? domain.getStatus().name() : null)
                .errorMessage(domain.getErrorMessage())
                .modelVersion(domain.getModelVersion())
                .processingTimeMs(domain.getProcessingTimeMs())
                .metadata(serializeMap(domain.getMetadata()))
                .createdAt(domain.getCreatedAt())
                .createdBy(domain.getCreatedBy())
                .build();
    }

    private CategorizationResult toDomain(CategorizationResultEntity entity) {
        return CategorizationResult.builder()
                .id(entity.getUuid())
                .tenantId(entity.getTenantId())
                .requestId(entity.getRequestId())
                .contentId(entity.getContentId())
                .contentType(entity.getContentType())
                .taxonomyId(entity.getTaxonomyId())
                .predictions(deserializePredictions(entity.getPredictions()))
                .status(entity.getStatus() != null ?
                    CategorizationStatus.valueOf(entity.getStatus()) : null)
                .errorMessage(entity.getErrorMessage())
                .modelVersion(entity.getModelVersion())
                .processingTimeMs(entity.getProcessingTimeMs())
                .metadata(deserializeMap(entity.getMetadata()))
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .build();
    }

    private String serializePredictions(List<CategorizationResult.CategoryPrediction> predictions) {
        try {
            return predictions != null ? objectMapper.writeValueAsString(predictions) : null;
        } catch (Exception e) {
            log.error("Error serializing predictions", e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private List<CategorizationResult.CategoryPrediction> deserializePredictions(String json) {
        try {
            return json != null ? objectMapper.readValue(json, 
                objectMapper.getTypeFactory().constructCollectionType(List.class, 
                    CategorizationResult.CategoryPrediction.class)) : new java.util.ArrayList<>();
        } catch (Exception e) {
            log.error("Error deserializing predictions", e);
            return new java.util.ArrayList<>();
        }
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
