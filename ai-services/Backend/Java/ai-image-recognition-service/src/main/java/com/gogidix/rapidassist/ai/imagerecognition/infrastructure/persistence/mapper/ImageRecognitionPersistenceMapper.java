package com.gogidix.rapidassist.ai.imagerecognition.infrastructure.persistence.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.ai.imagerecognition.domain.aggregate.ImageRecognition;
import com.gogidix.rapidassist.ai.imagerecognition.domain.model.*;
import com.gogidix.rapidassist.ai.imagerecognition.infrastructure.persistence.entity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Mapper for converting between domain models and MongoDB entities.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ImageRecognitionPersistenceMapper {

    private final ObjectMapper objectMapper;

    // ImageRecognition mappings
    public ImageRecognitionEntity toEntity(ImageRecognition domain) {
        if (domain == null) {
            return null;
        }

        try {
            return ImageRecognitionEntity.builder()
                    .uuid(domain.getId())
                    .tenantId(domain.getTenantId())
                    .userId(domain.getUserId())
                    .requestId(domain.getRequestId())
                    .imageUrl(domain.getImageUrl())
                    .imageStoragePath(domain.getImageStoragePath())
                    .status(domain.getStatus())
                    .recognitionType(domain.getRecognitionType())
                    .aiModelUsed(domain.getAiModelUsed())
                    .processingTimeMs(domain.getProcessingTimeMs())
                    .errorMessage(domain.getErrorMessage())
                    .metadata(domain.getMetadata() != null ? objectMapper.writeValueAsString(domain.getMetadata()) : null)
                    .createdAt(domain.getCreatedAt())
                    .updatedAt(domain.getUpdatedAt())
                    .completedAt(domain.getCompletedAt())
                    .createdBy(domain.getCreatedBy())
                    .updatedBy(domain.getUpdatedBy())
                    .build();
        } catch (Exception e) {
            log.error("Error converting domain to entity", e);
            throw new RuntimeException("Failed to convert domain to entity", e);
        }
    }

    public ImageRecognition toDomain(ImageRecognitionEntity entity) {
        if (entity == null) {
            return null;
        }

        try {
            Map<String, Object> metadata = new HashMap<>();
            if (entity.getMetadata() != null) {
                metadata = objectMapper.readValue(entity.getMetadata(), Map.class);
            }

            return ImageRecognition.builder()
                    .id(entity.getUuid())
                    .tenantId(entity.getTenantId())
                    .userId(entity.getUserId())
                    .requestId(entity.getRequestId())
                    .imageUrl(entity.getImageUrl())
                    .imageStoragePath(entity.getImageStoragePath())
                    .status(entity.getStatus())
                    .recognitionType(entity.getRecognitionType())
                    .aiModelUsed(entity.getAiModelUsed())
                    .processingTimeMs(entity.getProcessingTimeMs())
                    .errorMessage(entity.getErrorMessage())
                    .metadata(metadata)
                    .createdAt(entity.getCreatedAt())
                    .updatedAt(entity.getUpdatedAt())
                    .completedAt(entity.getCompletedAt())
                    .createdBy(entity.getCreatedBy())
                    .updatedBy(entity.getUpdatedBy())
                    .build();
        } catch (Exception e) {
            log.error("Error converting entity to domain", e);
            throw new RuntimeException("Failed to convert entity to domain", e);
        }
    }

    // RecognizedObject mappings
    public RecognizedObjectEntity toEntity(RecognizedObject domain) {
        if (domain == null) {
            return null;
        }

        try {
            return RecognizedObjectEntity.builder()
                    .uuid(domain.getId())
                    .tenantId(domain.getTenantId())
                    .imageRecognitionId(domain.getImageRecognitionId())
                    .label(domain.getLabel())
                    .objectType(domain.getObjectType())
                    .confidence(domain.getConfidence())
                    .boundingBox(domain.getBoundingBox() != null ? objectMapper.writeValueAsString(domain.getBoundingBox()) : null)
                    .attributes(domain.getAttributes() != null ? objectMapper.writeValueAsString(domain.getAttributes()) : null)
                    .build();
        } catch (Exception e) {
            log.error("Error converting domain to entity", e);
            throw new RuntimeException("Failed to convert domain to entity", e);
        }
    }

    public RecognizedObject toDomain(RecognizedObjectEntity entity) {
        if (entity == null) {
            return null;
        }

        try {
            Map<String, Object> attributes = new HashMap<>();
            if (entity.getAttributes() != null) {
                attributes = objectMapper.readValue(entity.getAttributes(), Map.class);
            }

            BoundingBox boundingBox = null;
            if (entity.getBoundingBox() != null) {
                boundingBox = objectMapper.readValue(entity.getBoundingBox(), BoundingBox.class);
            }

            return RecognizedObject.builder()
                    .id(entity.getUuid())
                    .tenantId(entity.getTenantId())
                    .imageRecognitionId(entity.getImageRecognitionId())
                    .label(entity.getLabel())
                    .objectType(entity.getObjectType())
                    .confidence(entity.getConfidence())
                    .boundingBox(boundingBox)
                    .attributes(attributes)
                    .build();
        } catch (Exception e) {
            log.error("Error converting entity to domain", e);
            throw new RuntimeException("Failed to convert entity to domain", e);
        }
    }

    // SceneLabel mappings
    public SceneLabelEntity toEntity(SceneLabel domain) {
        if (domain == null) {
            return null;
        }

        try {
            return SceneLabelEntity.builder()
                    .uuid(domain.getId())
                    .tenantId(domain.getTenantId())
                    .imageRecognitionId(domain.getImageRecognitionId())
                    .label(domain.getLabel())
                    .category(domain.getCategory())
                    .confidence(domain.getConfidence())
                    .tags(domain.getTags() != null ? objectMapper.writeValueAsString(domain.getTags()) : null)
                    .description(domain.getDescription())
                    .build();
        } catch (Exception e) {
            log.error("Error converting domain to entity", e);
            throw new RuntimeException("Failed to convert domain to entity", e);
        }
    }

    public SceneLabel toDomain(SceneLabelEntity entity) {
        if (entity == null) {
            return null;
        }

        try {
            java.util.List<String> tags = new java.util.ArrayList<>();
            if (entity.getTags() != null) {
                tags = objectMapper.readValue(entity.getTags(), java.util.List.class);
            }

            return SceneLabel.builder()
                    .id(entity.getUuid())
                    .tenantId(entity.getTenantId())
                    .imageRecognitionId(entity.getImageRecognitionId())
                    .label(entity.getLabel())
                    .category(entity.getCategory())
                    .confidence(entity.getConfidence())
                    .tags(tags)
                    .description(entity.getDescription())
                    .build();
        } catch (Exception e) {
            log.error("Error converting entity to domain", e);
            throw new RuntimeException("Failed to convert entity to domain", e);
        }
    }

    // BrandDetection mappings
    public BrandDetectionEntity toEntity(BrandDetection domain) {
        if (domain == null) {
            return null;
        }

        try {
            return BrandDetectionEntity.builder()
                    .uuid(domain.getId())
                    .tenantId(domain.getTenantId())
                    .imageRecognitionId(domain.getImageRecognitionId())
                    .brandName(domain.getBrandName())
                    .logoVariant(domain.getLogoVariant())
                    .confidence(domain.getConfidence())
                    .boundingBox(domain.getBoundingBox() != null ? objectMapper.writeValueAsString(domain.getBoundingBox()) : null)
                    .attributes(domain.getAttributes() != null ? objectMapper.writeValueAsString(domain.getAttributes()) : null)
                    .build();
        } catch (Exception e) {
            log.error("Error converting domain to entity", e);
            throw new RuntimeException("Failed to convert domain to entity", e);
        }
    }

    public BrandDetection toDomain(BrandDetectionEntity entity) {
        if (entity == null) {
            return null;
        }

        try {
            Map<String, Object> attributes = new HashMap<>();
            if (entity.getAttributes() != null) {
                attributes = objectMapper.readValue(entity.getAttributes(), Map.class);
            }

            BoundingBox boundingBox = null;
            if (entity.getBoundingBox() != null) {
                boundingBox = objectMapper.readValue(entity.getBoundingBox(), BoundingBox.class);
            }

            return BrandDetection.builder()
                    .id(entity.getUuid())
                    .tenantId(entity.getTenantId())
                    .imageRecognitionId(entity.getImageRecognitionId())
                    .brandName(entity.getBrandName())
                    .logoVariant(entity.getLogoVariant())
                    .confidence(entity.getConfidence())
                    .boundingBox(boundingBox)
                    .attributes(attributes)
                    .build();
        } catch (Exception e) {
            log.error("Error converting entity to domain", e);
            throw new RuntimeException("Failed to convert entity to domain", e);
        }
    }

    // ImageFeature mappings
    public ImageFeatureEntity toEntity(ImageFeature domain) {
        if (domain == null) {
            return null;
        }

        try {
            return ImageFeatureEntity.builder()
                    .uuid(domain.getId())
                    .tenantId(domain.getTenantId())
                    .imageRecognitionId(domain.getImageRecognitionId())
                    .featureType(domain.getFeatureType())
                    .featureName(domain.getFeatureName())
                    .featureValue(domain.getFeatureValue())
                    .featureVector(domain.getFeatureVector() != null ? objectMapper.writeValueAsString(domain.getFeatureVector()) : null)
                    .metadata(domain.getMetadata() != null ? objectMapper.writeValueAsString(domain.getMetadata()) : null)
                    .build();
        } catch (Exception e) {
            log.error("Error converting domain to entity", e);
            throw new RuntimeException("Failed to convert domain to entity", e);
        }
    }

    public ImageFeature toDomain(ImageFeatureEntity entity) {
        if (entity == null) {
            return null;
        }

        try {
            Map<String, Object> metadata = new HashMap<>();
            if (entity.getMetadata() != null) {
                metadata = objectMapper.readValue(entity.getMetadata(), Map.class);
            }

            java.util.List<Double> featureVector = new java.util.ArrayList<>();
            if (entity.getFeatureVector() != null) {
                featureVector = objectMapper.readValue(entity.getFeatureVector(), java.util.List.class);
            }

            return ImageFeature.builder()
                    .id(entity.getUuid())
                    .tenantId(entity.getTenantId())
                    .imageRecognitionId(entity.getImageRecognitionId())
                    .featureType(entity.getFeatureType())
                    .featureName(entity.getFeatureName())
                    .featureValue(entity.getFeatureValue())
                    .featureVector(featureVector)
                    .metadata(metadata)
                    .build();
        } catch (Exception e) {
            log.error("Error converting entity to domain", e);
            throw new RuntimeException("Failed to convert entity to domain", e);
        }
    }
}
