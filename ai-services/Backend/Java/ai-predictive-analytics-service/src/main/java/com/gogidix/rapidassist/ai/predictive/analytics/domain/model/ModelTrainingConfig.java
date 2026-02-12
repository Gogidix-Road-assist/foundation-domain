package com.gogidix.rapidassist.ai.predictive.analytics.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Domain model for model training configuration.
 * Contains hyperparameters and training settings.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelTrainingConfig {

    // Training parameters
    private Integer trainingEpochs;
    private Double learningRate;
    private Double validationSplit;
    private Integer batchSize;
    private String optimizer;

    // Regularization parameters
    private Double regularizationRate;
    private Double dropoutRate;

    // Early stopping configuration
    private Boolean earlyStoppingEnabled;
    private Integer earlyStoppingPatience;
    private Integer earlyStoppingMinDelta;

    // Cross-validation configuration
    private Integer crossValidationFolds;
    private Boolean crossValidationEnabled;

    // Feature engineering settings
    private Boolean featureScalingEnabled;
    private Boolean featureSelectionEnabled;
    private Integer maxFeatures;

    // Hyperparameters (algorithm-specific)
    private Map<String, Object> hyperparameters;

    // Data configuration
    private String dataSource;
    private String dataFilter;
    private Integer trainingDataSize;
    private Integer testDataSize;

    // Random seed for reproducibility
    private Long randomSeed;

    /**
     * Business logic: Get default training config for time series models
     */
    public static ModelTrainingConfig getDefaultTimeSeriesConfig() {
        return ModelTrainingConfig.builder()
                .trainingEpochs(100)
                .learningRate(0.001)
                .validationSplit(0.2)
                .batchSize(32)
                .earlyStoppingEnabled(true)
                .earlyStoppingPatience(10)
                .featureScalingEnabled(true)
                .randomSeed(42L)
                .build();
    }

    /**
     * Business logic: Get default training config for regression models
     */
    public static ModelTrainingConfig getDefaultRegressionConfig() {
        return ModelTrainingConfig.builder()
                .trainingEpochs(200)
                .learningRate(0.01)
                .validationSplit(0.2)
                .batchSize(64)
                .crossValidationEnabled(true)
                .crossValidationFolds(5)
                .featureScalingEnabled(true)
                .featureSelectionEnabled(true)
                .randomSeed(42L)
                .build();
    }

    /**
     * Business logic: Get default training config for classification models
     */
    public static ModelTrainingConfig getDefaultClassificationConfig() {
        return ModelTrainingConfig.builder()
                .trainingEpochs(150)
                .learningRate(0.01)
                .validationSplit(0.2)
                .batchSize(32)
                .crossValidationEnabled(true)
                .crossValidationFolds(5)
                .featureScalingEnabled(true)
                .randomSeed(42L)
                .build();
    }
}
