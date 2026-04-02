package com.gogidix.rapidassist.ai.predictive.analytics.application.service;

import com.gogidix.rapidassist.ai.predictive.analytics.domain.model.ModelStatus;
import com.gogidix.rapidassist.ai.predictive.analytics.domain.model.ModelType;
import com.gogidix.rapidassist.ai.predictive.analytics.domain.model.PredictiveModel;
import com.gogidix.rapidassist.ai.predictive.analytics.domain.repository.PredictiveModelRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Application service for managing predictive models.
 * Handles business logic for model lifecycle management.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PredictiveModelService {

    private final PredictiveModelRepositoryPort modelRepository;

    /**
     * Create a new predictive model
     */
    @Transactional
    public PredictiveModel createModel(String tenantId, String name, String description,
                                       ModelType modelType, String algorithm) {
        log.info("Creating new predictive model: {} for tenant: {}", name, tenantId);

        PredictiveModel model = PredictiveModel.initialize(tenantId, name, modelType, algorithm);
        model.setDescription(description);

        PredictiveModel savedModel = modelRepository.save(model);
        log.info("Created predictive model with ID: {}", savedModel.getId());

        return savedModel;
    }

    /**
     * Get model by ID
     */
    public PredictiveModel getModelById(UUID modelId, String tenantId) {
        return modelRepository.findByIdAndTenantId(modelId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Model not found with ID: " + modelId + " for tenant: " + tenantId));
    }

    /**
     * Get model by name
     */
    public PredictiveModel getModelByName(String name, String tenantId) {
        return modelRepository.findByNameAndTenantId(name, tenantId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Model not found with name: " + name + " for tenant: " + tenantId));
    }

    /**
     * Get all models for a tenant
     */
    public List<PredictiveModel> getAllModels(String tenantId) {
        return modelRepository.findByTenantId(tenantId);
    }

    /**
     * Get active models for a tenant
     */
    public List<PredictiveModel> getActiveModels(String tenantId) {
        return modelRepository.findActiveModelsByTenantId(tenantId);
    }

    /**
     * Get models by type
     */
    public List<PredictiveModel> getModelsByType(ModelType modelType, String tenantId) {
        return modelRepository.findByModelTypeAndTenantId(modelType, tenantId);
    }

    /**
     * Update model status
     */
    @Transactional
    public PredictiveModel updateModelStatus(UUID modelId, String tenantId, ModelStatus newStatus) {
        PredictiveModel model = getModelById(modelId, tenantId);
        model.setStatus(newStatus);
        model.setUpdatedAt(LocalDateTime.now());

        if (newStatus == ModelStatus.ACTIVE) {
            model.setDeployedAt(LocalDateTime.now());
        } else if (newStatus == ModelStatus.TRAINED) {
            model.setLastTrainedAt(LocalDateTime.now());
        }

        return modelRepository.save(model);
    }

    /**
     * Update model performance metrics
     */
    @Transactional
    public PredictiveModel updateModelMetrics(UUID modelId, String tenantId,
                                              java.util.Map<String, Double> metrics) {
        PredictiveModel model = getModelById(modelId, tenantId);
        model.setPerformanceMetrics(metrics);
        model.setUpdatedAt(LocalDateTime.now());

        return modelRepository.save(model);
    }

    /**
     * Delete a model
     */
    @Transactional
    public void deleteModel(UUID modelId, String tenantId) {
        log.info("Deleting model with ID: {} for tenant: {}", modelId, tenantId);
        modelRepository.deleteByIdAndTenantId(modelId, tenantId);
    }

    /**
     * Get models needing retraining
     */
    public List<PredictiveModel> getModelsNeedingRetraining(String tenantId, int thresholdDays) {
        return modelRepository.findModelsNeedingRetraining(tenantId, thresholdDays);
    }

    /**
     * Check if model can be used for predictions
     */
    public boolean canModelPredict(UUID modelId, String tenantId) {
        PredictiveModel model = getModelById(modelId, tenantId);
        return model.canPredict();
    }
}
