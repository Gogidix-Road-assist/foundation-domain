package com.gogidix.rapidassist.ai.modelmanagement.infrastructure.persistence.adapter;

import com.gogidix.rapidassist.ai.modelmanagement.domain.model.TrainingJob;
import com.gogidix.rapidassist.ai.modelmanagement.domain.model.TrainingJobStatus;
import com.gogidix.rapidassist.ai.modelmanagement.domain.repository.TrainingJobRepositoryPort;
import com.gogidix.rapidassist.ai.modelmanagement.infrastructure.persistence.entity.TrainingJobEntity;
import com.gogidix.rapidassist.ai.modelmanagement.infrastructure.persistence.repository.SpringDataTrainingJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Repository adapter implementing TrainingJobRepositoryPort.
 */
@Component
@RequiredArgsConstructor
public class TrainingJobRepositoryAdapter implements TrainingJobRepositoryPort {

    private final SpringDataTrainingJobRepository springDataRepository;

    @Override
    public TrainingJob save(String tenantId, TrainingJob trainingJob) {
        TrainingJobEntity entity = toEntity(trainingJob);
        entity.setTenantId(tenantId);
        TrainingJobEntity savedEntity = springDataRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<TrainingJob> findById(String tenantId, UUID id) {
        return springDataRepository.findByUuidAndTenantId(id, tenantId)
                .map(this::toDomain);
    }

    @Override
    public List<TrainingJob> findByModelId(String tenantId, UUID modelId) {
        return springDataRepository.findByModelIdAndTenantIdOrderByCreatedAtDesc(modelId, tenantId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<TrainingJob> findByStatus(String tenantId, TrainingJobStatus status) {
        return springDataRepository.findByTenantIdAndStatus(tenantId, status).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<TrainingJob> findByName(String tenantId, String name) {
        return springDataRepository.findByTenantIdAndName(tenantId, name)
                .map(this::toDomain);
    }

    @Override
    public List<TrainingJob> findRunningJobs(String tenantId) {
        return springDataRepository.findByTenantIdAndStatusOrderByCreatedAtDesc(
                tenantId, TrainingJobStatus.RUNNING).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<TrainingJob> findCompletedJobs(String tenantId, UUID modelId) {
        return springDataRepository.findByModelIdAndTenantIdAndStatusOrderByCreatedAtDesc(
                modelId, tenantId, TrainingJobStatus.COMPLETED).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean exists(String tenantId, UUID id) {
        return springDataRepository.existsByUuidAndTenantId(id, tenantId);
    }

    @Override
    public void delete(String tenantId, UUID id) {
        springDataRepository.deleteByUuidAndTenantId(id, tenantId);
    }

    @Override
    public void deleteByModelId(String tenantId, UUID modelId) {
        springDataRepository.deleteByModelIdAndTenantId(modelId, tenantId);
    }

    private TrainingJob toDomain(TrainingJobEntity entity) {
        return TrainingJob.builder()
                .id(entity.getUuid())
                .tenantId(entity.getTenantId())
                .modelId(entity.getModelId())
                .name(entity.getName())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .trainingDataSetId(entity.getTrainingDataSetId())
                .validationDataSetId(entity.getValidationDataSetId())
                .hyperparameters(entity.getHyperparameters())
                .trainingConfig(entity.getTrainingConfig())
                .algorithmType(entity.getAlgorithmType())
                .epochs(entity.getEpochs())
                .batchSize(entity.getBatchSize())
                .learningRate(entity.getLearningRate())
                .executorType(entity.getExecutorType())
                .cpuUnits(entity.getCpuUnits())
                .memoryMB(entity.getMemoryMB())
                .acceleratorType(entity.getAcceleratorType())
                .acceleratorCount(entity.getAcceleratorCount())
                .outputArtifactsPath(entity.getOutputArtifactsPath())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .durationSeconds(entity.getDurationSeconds())
                .progressPercentage(entity.getProgressPercentage())
                .currentEpoch(entity.getCurrentEpoch())
                .trainingMetrics(entity.getTrainingMetrics())
                .validationMetrics(entity.getValidationMetrics())
                .errorMessage(entity.getErrorMessage())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .version(entity.getVersion())
                .build();
    }

    private TrainingJobEntity toEntity(TrainingJob trainingJob) {
        return TrainingJobEntity.builder()
                .uuid(trainingJob.getId() != null ? trainingJob.getId() : UUID.randomUUID())
                .tenantId(trainingJob.getTenantId())
                .modelId(trainingJob.getModelId())
                .name(trainingJob.getName())
                .description(trainingJob.getDescription())
                .status(trainingJob.getStatus())
                .trainingDataSetId(trainingJob.getTrainingDataSetId())
                .validationDataSetId(trainingJob.getValidationDataSetId())
                .hyperparameters(trainingJob.getHyperparameters())
                .trainingConfig(trainingJob.getTrainingConfig())
                .algorithmType(trainingJob.getAlgorithmType())
                .epochs(trainingJob.getEpochs())
                .batchSize(trainingJob.getBatchSize())
                .learningRate(trainingJob.getLearningRate())
                .executorType(trainingJob.getExecutorType())
                .cpuUnits(trainingJob.getCpuUnits())
                .memoryMB(trainingJob.getMemoryMB())
                .acceleratorType(trainingJob.getAcceleratorType())
                .acceleratorCount(trainingJob.getAcceleratorCount())
                .outputArtifactsPath(trainingJob.getOutputArtifactsPath())
                .startTime(trainingJob.getStartTime())
                .endTime(trainingJob.getEndTime())
                .durationSeconds(trainingJob.getDurationSeconds())
                .progressPercentage(trainingJob.getProgressPercentage())
                .currentEpoch(trainingJob.getCurrentEpoch())
                .trainingMetrics(trainingJob.getTrainingMetrics())
                .validationMetrics(trainingJob.getValidationMetrics())
                .errorMessage(trainingJob.getErrorMessage())
                .createdBy(trainingJob.getCreatedBy())
                .createdAt(trainingJob.getCreatedAt())
                .updatedAt(trainingJob.getUpdatedAt())
                .version(trainingJob.getVersion())
                .build();
    }
}
