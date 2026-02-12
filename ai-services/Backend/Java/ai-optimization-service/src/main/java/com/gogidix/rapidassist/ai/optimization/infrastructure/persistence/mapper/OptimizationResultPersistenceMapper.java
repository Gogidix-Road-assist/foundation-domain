package com.gogidix.rapidassist.ai.optimization.infrastructure.persistence.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.ai.optimization.domain.model.OptimizationResult;
import com.gogidix.rapidassist.ai.optimization.infrastructure.persistence.entity.OptimizationResultEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper for converting between OptimizationResult domain model and OptimizationResultEntity.
 */
@Component
public class OptimizationResultPersistenceMapper {

    private final ObjectMapper objectMapper;

    public OptimizationResultPersistenceMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public OptimizationResultEntity toEntity(OptimizationResult domain) {
        if (domain == null) {
            return null;
        }

        try {
            OptimizationResultEntity.OptimizationResultEntityBuilder builder = OptimizationResultEntity.builder()
                    .uuid(domain.getId())
                    .tenantId(domain.getTenantId())
                    .optimizationJobId(domain.getOptimizationJobId())
                    .iteration(domain.getIteration())
                    .objectiveValue(domain.getObjectiveValue())
                    .accuracy(domain.getAccuracy())
                    .loss(domain.getLoss())
                    .latency(domain.getLatency())
                    .cost(domain.getCost())
                    .status(domain.getStatus())
                    .executionTimeMs(domain.getExecutionTimeMs())
                    .convergenceStatus(domain.getConvergenceStatus())
                    .notes(domain.getNotes())
                    .createdBy(domain.getCreatedBy())
                    .createdAt(domain.getCreatedAt());

            if (domain.getParameters() != null) {
                builder.parameters(objectMapper.writeValueAsString(domain.getParameters()));
            }

            if (domain.getMetrics() != null && !domain.getMetrics().isEmpty()) {
                builder.metrics(objectMapper.writeValueAsString(domain.getMetrics()));
            }

            if (domain.getAdditionalMetrics() != null && !domain.getAdditionalMetrics().isEmpty()) {
                builder.additionalMetrics(objectMapper.writeValueAsString(domain.getAdditionalMetrics()));
            }

            if (domain.getMetadata() != null && !domain.getMetadata().isEmpty()) {
                builder.metadata(objectMapper.writeValueAsString(domain.getMetadata()));
            }

            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException("Error converting OptimizationResult to entity", e);
        }
    }

    public OptimizationResult toDomain(OptimizationResultEntity entity) {
        if (entity == null) {
            return null;
        }

        try {
            OptimizationResult.OptimizationResultBuilder builder = OptimizationResult.builder()
                    .id(entity.getUuid())
                    .tenantId(entity.getTenantId())
                    .optimizationJobId(entity.getOptimizationJobId())
                    .iteration(entity.getIteration())
                    .objectiveValue(entity.getObjectiveValue())
                    .accuracy(entity.getAccuracy())
                    .loss(entity.getLoss())
                    .latency(entity.getLatency())
                    .cost(entity.getCost())
                    .status(entity.getStatus())
                    .executionTimeMs(entity.getExecutionTimeMs())
                    .convergenceStatus(entity.getConvergenceStatus())
                    .notes(entity.getNotes())
                    .createdBy(entity.getCreatedBy())
                    .createdAt(entity.getCreatedAt());

            if (entity.getParameters() != null && !entity.getParameters().isEmpty()) {
                builder.parameters(objectMapper.readValue(entity.getParameters(), java.util.Map.class));
            }

            if (entity.getMetrics() != null && !entity.getMetrics().isEmpty()) {
                builder.metrics(objectMapper.readValue(entity.getMetrics(), List.class));
            }

            if (entity.getAdditionalMetrics() != null && !entity.getAdditionalMetrics().isEmpty()) {
                builder.additionalMetrics(objectMapper.readValue(entity.getAdditionalMetrics(), java.util.Map.class));
            }

            if (entity.getMetadata() != null && !entity.getMetadata().isEmpty()) {
                builder.metadata(objectMapper.readValue(entity.getMetadata(), java.util.Map.class));
            }

            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException("Error converting OptimizationResultEntity to domain", e);
        }
    }
}
