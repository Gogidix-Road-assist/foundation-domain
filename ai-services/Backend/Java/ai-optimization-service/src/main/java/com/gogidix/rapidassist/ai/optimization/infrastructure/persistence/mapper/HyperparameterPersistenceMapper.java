package com.gogidix.rapidassist.ai.optimization.infrastructure.persistence.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.ai.optimization.domain.model.Hyperparameter;
import com.gogidix.rapidassist.ai.optimization.infrastructure.persistence.entity.HyperparameterEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper for converting between Hyperparameter domain model and HyperparameterEntity.
 */
@Component
public class HyperparameterPersistenceMapper {

    private final ObjectMapper objectMapper;

    public HyperparameterPersistenceMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public HyperparameterEntity toEntity(Hyperparameter domain) {
        if (domain == null) {
            return null;
        }

        try {
            HyperparameterEntity.HyperparameterEntityBuilder builder = HyperparameterEntity.builder()
                    .uuid(domain.getId())
                    .tenantId(domain.getTenantId())
                    .name(domain.getName())
                    .type(domain.getType())
                    .currentValue(domain.getCurrentValue())
                    .scale(domain.getScale())
                    .isDiscrete(domain.getIsDiscrete())
                    .description(domain.getDescription())
                    .category(domain.getCategory())
                    .createdBy(domain.getCreatedBy())
                    .updatedBy(domain.getUpdatedBy())
                    .createdAt(domain.getCreatedAt())
                    .updatedAt(domain.getUpdatedAt());

            if (domain.getMinValue() != null) {
                builder.minValue(objectMapper.writeValueAsString(domain.getMinValue()));
            }

            if (domain.getMaxValue() != null) {
                builder.maxValue(objectMapper.writeValueAsString(domain.getMaxValue()));
            }

            if (domain.getAllowedValues() != null && !domain.getAllowedValues().isEmpty()) {
                builder.allowedValues(objectMapper.writeValueAsString(domain.getAllowedValues()));
            }

            if (domain.getMetadata() != null && !domain.getMetadata().isEmpty()) {
                builder.metadata(objectMapper.writeValueAsString(domain.getMetadata()));
            }

            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException("Error converting Hyperparameter to entity", e);
        }
    }

    public Hyperparameter toDomain(HyperparameterEntity entity) {
        if (entity == null) {
            return null;
        }

        try {
            Hyperparameter.HyperparameterBuilder builder = Hyperparameter.builder()
                    .id(entity.getUuid())
                    .tenantId(entity.getTenantId())
                    .name(entity.getName())
                    .type(entity.getType())
                    .currentValue(entity.getCurrentValue())
                    .scale(entity.getScale())
                    .isDiscrete(entity.getIsDiscrete())
                    .description(entity.getDescription())
                    .category(entity.getCategory())
                    .createdBy(entity.getCreatedBy())
                    .updatedBy(entity.getUpdatedBy())
                    .createdAt(entity.getCreatedAt())
                    .updatedAt(entity.getUpdatedAt());

            if (entity.getMinValue() != null && !entity.getMinValue().isEmpty()) {
                builder.minValue(objectMapper.readValue(entity.getMinValue(), Object.class));
            }

            if (entity.getMaxValue() != null && !entity.getMaxValue().isEmpty()) {
                builder.maxValue(objectMapper.readValue(entity.getMaxValue(), Object.class));
            }

            if (entity.getAllowedValues() != null && !entity.getAllowedValues().isEmpty()) {
                builder.allowedValues(objectMapper.readValue(entity.getAllowedValues(), List.class));
            }

            if (entity.getMetadata() != null && !entity.getMetadata().isEmpty()) {
                builder.metadata(objectMapper.readValue(entity.getMetadata(), java.util.Map.class));
            }

            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException("Error converting HyperparameterEntity to domain", e);
        }
    }
}
