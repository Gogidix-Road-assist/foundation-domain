package com.gogidix.rapidassist.ai.optimization.infrastructure.persistence.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.ai.optimization.domain.model.OptimizationConfiguration;
import com.gogidix.rapidassist.ai.optimization.infrastructure.persistence.entity.OptimizationConfigurationEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between OptimizationConfiguration domain model and OptimizationConfigurationEntity.
 */
@Component
public class OptimizationConfigurationPersistenceMapper {

    private final ObjectMapper objectMapper;

    public OptimizationConfigurationPersistenceMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public OptimizationConfigurationEntity toEntity(OptimizationConfiguration domain) {
        if (domain == null) {
            return null;
        }

        try {
            OptimizationConfigurationEntity.OptimizationConfigurationEntityBuilder builder = OptimizationConfigurationEntity.builder()
                    .uuid(domain.getId())
                    .tenantId(domain.getTenantId())
                    .algorithm(domain.getAlgorithm())
                    .maxIterations(domain.getMaxIterations())
                    .populationSize(domain.getPopulationSize())
                    .mutationRate(domain.getMutationRate())
                    .crossoverRate(domain.getCrossoverRate())
                    .learningRate(domain.getLearningRate())
                    .tolerance(domain.getTolerance())
                    .timeoutSeconds(domain.getTimeoutSeconds())
                    .parallelJobs(domain.getParallelJobs())
                    .objective(domain.getObjective())
                    .direction(domain.getDirection())
                    .description(domain.getDescription())
                    .createdBy(domain.getCreatedBy())
                    .updatedBy(domain.getUpdatedBy())
                    .createdAt(domain.getCreatedAt())
                    .updatedAt(domain.getUpdatedAt());

            if (domain.getAlgorithmParameters() != null && !domain.getAlgorithmParameters().isEmpty()) {
                builder.algorithmParameters(objectMapper.writeValueAsString(domain.getAlgorithmParameters()));
            }

            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException("Error converting OptimizationConfiguration to entity", e);
        }
    }

    public OptimizationConfiguration toDomain(OptimizationConfigurationEntity entity) {
        if (entity == null) {
            return null;
        }

        try {
            OptimizationConfiguration.OptimizationConfigurationBuilder builder = OptimizationConfiguration.builder()
                    .id(entity.getUuid())
                    .tenantId(entity.getTenantId())
                    .algorithm(entity.getAlgorithm())
                    .maxIterations(entity.getMaxIterations())
                    .populationSize(entity.getPopulationSize())
                    .mutationRate(entity.getMutationRate())
                    .crossoverRate(entity.getCrossoverRate())
                    .learningRate(entity.getLearningRate())
                    .tolerance(entity.getTolerance())
                    .timeoutSeconds(entity.getTimeoutSeconds())
                    .parallelJobs(entity.getParallelJobs())
                    .objective(entity.getObjective())
                    .direction(entity.getDirection())
                    .description(entity.getDescription())
                    .createdBy(entity.getCreatedBy())
                    .updatedBy(entity.getUpdatedBy())
                    .createdAt(entity.getCreatedAt())
                    .updatedAt(entity.getUpdatedAt());

            if (entity.getAlgorithmParameters() != null && !entity.getAlgorithmParameters().isEmpty()) {
                builder.algorithmParameters(objectMapper.readValue(entity.getAlgorithmParameters(), java.util.Map.class));
            }

            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException("Error converting OptimizationConfigurationEntity to domain", e);
        }
    }
}
