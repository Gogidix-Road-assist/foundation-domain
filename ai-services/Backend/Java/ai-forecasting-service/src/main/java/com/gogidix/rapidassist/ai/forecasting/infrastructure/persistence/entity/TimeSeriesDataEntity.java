package com.gogidix.rapidassist.ai.forecasting.infrastructure.persistence.entity;

import com.gogidix.rapidassist.ai.forecasting.domain.model.DataGranularity;
import com.gogidix.rapidassist.ai.forecasting.domain.model.FrequencyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * MongoDB Entity for TimeSeriesData.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "time_series_data")
public class TimeSeriesDataEntity {

    @Id
    private Long id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private String dataSourceName;

    private String description;
    private DataGranularity granularity;
    private String dataPointsJson;
    private FrequencyType frequencyType;
    private Boolean hasSeasonality;
    private Boolean hasTrend;
    private Integer seasonalityPeriod;
    private String metadataJson;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer totalDataPoints;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Long version;
}
