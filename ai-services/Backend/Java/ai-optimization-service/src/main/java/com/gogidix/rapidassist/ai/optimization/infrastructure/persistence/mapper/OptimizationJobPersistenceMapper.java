package com.gogidix.rapidassist.ai.optimization.infrastructure.persistence.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.ai.optimization.domain.model.OptimizationJob;
import com.gogidix.rapidassist.ai.optimization.infrastructure.persistence.entity.OptimizationJobEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between OptimizationJob domain model and OptimizationJobEntity.
 */
@Component
public class OptimizationJobPersistenceMapper {

    private final ObjectMapper objectMapper;

    public OptimizationJobPersistenceMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public OptimizationJobEntity toEntity(OptimizationJob domain) {
        if (domain == null) {
            return null;
        }

        try {
            OptimizationJobEntity.OptimizationJobEntityBuilder builder = OptimizationJobEntity.builder()
                    .uuid(domain.getId())
                    .tenantId(domain.getTenantId())
                    .name(domain.getName())
                    .description(domain.getDescription())
                    .type(domain.getType())
                    .status(domain.getStatus())
                    .algorithm(domain.getAlgorithm())
                    .modelType(domain.getModelType())
                    .modelId(domain.getModelId())
                    .datasetId(domain.getDatasetId())
                    .objective(domain.getObjective())
                    .direction(domain.getDirection())
                    .currentIteration(domain.getCurrentIteration())
                    .maxIterations(domain.getMaxIterations())
                    .bestObjectiveValue(domain.getBestObjectiveValue())
                    .bestParameters(domain.getBestParameters())
                    .convergenceThreshold(domain.getConvergenceThreshold())
                    .convergenceStatus(domain.getConvergenceStatus())
                    .totalExecutionTimeMs(domain.getTotalExecutionTimeMs())
                    .startTime(domain.getStartTime())
                    .endTime(domain.getEndTime())
                    .error(domain.getError())
                    .createdBy(domain.getCreatedBy())
                    .updatedBy(domain.getUpdatedBy())
                    .createdAt(domain.getCreatedAt())
                    .updatedAt(domain.getUpdatedAt());

            if (domain.getConfiguration() != null) {
                builder.configurationId(domain.getConfiguration().getId().toString());
            }

            if (domain.getMetadata() != null && !domain.getMetadata().isEmpty()) {
                builder.metadata(objectMapper.writeValueAsString(domain.getMetadata()));
            }

            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException("Error converting OptimizationJob to entity", e);
        }
    }

    public OptimizationJob toDomain(OptimizationJobEntity entity) {
        if (entity == null) {
            return null;
        }

        try {
            OptimizationJob.OptimizationJobBuilder builder = OptimizationJob.builder()
                    .id(entity.getUuid())
                    .tenantId(entity.getTenantId())
                    .name(entity.getName())
                    .description(entity.getDescription())
                    .type(entity.getType())
                    .status(entity.getStatus())
                    .algorithm(entity.getAlgorithm())
                    .modelType(entity.getModelType())
                    .modelId(entity.getModelId())
                    .datasetId(entity.getDatasetId())
                    .objective(entity.getObjective())
                    .direction(entity.getDirection())
                    .currentIteration(entity.getCurrentIteration())
                    .maxIterations(entity.getMaxIterations())
                    .bestObjectiveValue(entity.getBestObjectiveValue())
                    .bestParameters(entity.getBestParameters())
                    .convergenceThreshold(entity.getConvergenceThreshold())
                    .convergenceStatus(entity.getConvergenceStatus())
                    .totalExecutionTimeMs(entity.getTotalExecutionTimeMs())
                    .startTime(entity.getStartTime())
                    .endTime(entity.getEndTime())
                    .error(entity.getError())
                    .createdBy(entity.getCreatedBy())
                    .updatedBy(entity.getUpdatedBy())
                    .createdAt(entity.getCreatedAt())
                    .updatedAt(entity.getUpdatedAt());

            if (entity.getMetadata() != null && !entity.getMetadata().isEmpty()) {
                builder.metadata(objectMapper.readValue(entity.getMetadata(), java.util.Map.class));
            }

            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException("Error converting OptimizationJobEntity to domain", e);
        }
    }
}
