package com.gogidix.rapidassist.ai.forecasting.infrastructure.persistence.entity;

import com.gogidix.rapidassist.ai.forecasting.domain.model.ForecastModelType;
import com.gogidix.rapidassist.ai.forecasting.domain.model.ModelStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * MongoDB Entity for ForecastModel.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "forecast_model")
public class ForecastModelEntity {

    @Id
    private Long id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    private String modelName;
    private String description;
    private ForecastModelType modelType;
    private ModelStatus status;
    private String version;
    private String hyperparametersJson;
    private String trainingParametersJson;
    private Integer trainingDataPoints;
    private BigDecimal trainingAccuracy;
    private BigDecimal validationAccuracy;
    private BigDecimal testAccuracy;
    private String featureImportance;
    private String metadataJson;
    private LocalDateTime lastTrainedAt;
    private LocalDateTime deployedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
