package com.gogidix.rapidassist.ai.predictive.analytics.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.predictive.analytics.domain.model.Forecast;
import com.gogidix.rapidassist.ai.predictive.analytics.domain.repository.ForecastRepositoryPort;
import com.gogidix.rapidassist.ai.predictive.analytics.infrastructure.persistence.entity.ForecastEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of ForecastRepositoryPort using MongoDB.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ForecastRepositoryImpl implements ForecastRepositoryPort {

    private final SpringDataForecastRepository springDataRepository;

    @Override
    public Forecast save(Forecast forecast) {
        ForecastEntity entity = toEntity(forecast);
        ForecastEntity savedEntity = springDataRepository.save(entity);
        log.debug("Saved forecast with UUID: {}", savedEntity.getUuid());
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Forecast> findByIdAndTenantId(UUID id, String tenantId) {
        return springDataRepository.findByUuidAndTenantId(id, tenantId)
                .map(this::toDomain);
    }

    @Override
    public List<Forecast> findByModelIdAndTenantId(UUID modelId, String tenantId) {
        return springDataRepository.findByModelIdAndTenantId(modelId, tenantId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Forecast> findByGeneratedAtBetweenAndTenantId(LocalDateTime startDate, LocalDateTime endDate, String tenantId) {
        return springDataRepository.findByGeneratedAtBetweenAndTenantId(startDate, endDate, tenantId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Forecast> findRecentForecastsByTenantId(String tenantId, int limit) {
        List<ForecastEntity> entities = springDataRepository.findByTenantId(tenantId);
        return entities.stream()
                .limit(limit)
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Forecast> findByForecastStartDateBetweenAndTenantId(LocalDateTime startDate, LocalDateTime endDate, String tenantId) {
        return springDataRepository.findByForecastStartDateBetweenAndTenantId(startDate, endDate, tenantId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByIdAndTenantId(UUID id, String tenantId) {
        springDataRepository.deleteByUuidAndTenantId(id, tenantId);
        log.debug("Deleted forecast with UUID: {}", id);
    }

    @Override
    public long countByTenantId(String tenantId) {
        return springDataRepository.countByTenantId(tenantId);
    }

    @Override
    public long countByModelIdAndTenantId(UUID modelId, String tenantId) {
        return springDataRepository.countByModelIdAndTenantId(modelId, tenantId);
    }

    @Override
    public void deleteOldForecasts(LocalDateTime cutoffDate) {
        springDataRepository.deleteByGeneratedAtBefore(cutoffDate);
        log.debug("Deleted old forecasts generated before: {}", cutoffDate);
    }

    /**
     * Convert domain model to entity
     */
    private ForecastEntity toEntity(Forecast domain) {
        // Convert ForecastDataPoint lists to maps for storage
        List<java.util.Map<String, Object>> forecastDataList = null;
        if (domain.getForecastData() != null) {
            forecastDataList = domain.getForecastData().stream()
                    .map(point -> {
                        java.util.Map<String, Object> map = new java.util.HashMap<>();
                        map.put("timestamp", point.getTimestamp());
                        map.put("value", point.getValue());
                        map.put("confidence", point.getConfidence());
                        map.put("metadata", point.getMetadata());
                        return map;
                    })
                    .collect(Collectors.toList());
        }

        return ForecastEntity.builder()
                .uuid(domain.getId())
                .tenantId(domain.getTenantId())
                .modelId(domain.getModelId())
                .forecastName(domain.getForecastName())
                .forecastStartDate(domain.getForecastStartDate())
                .forecastEndDate(domain.getForecastEndDate())
                .forecastHorizon(domain.getForecastHorizon())
                .forecastData(forecastDataList)
                .accuracyMetrics(domain.getAccuracyMetrics())
                .meanAbsoluteError(domain.getMeanAbsoluteError())
                .meanAbsolutePercentageError(domain.getMeanAbsolutePercentageError())
                .status(domain.getStatus())
                .frequency(domain.getFrequency())
                .metadata(domain.getMetadata())
                .createdAt(domain.getCreatedAt())
                .generatedAt(domain.getGeneratedAt())
                .build();
    }

    /**
     * Convert entity to domain model
     */
    private Forecast toDomain(ForecastEntity entity) {
        // Convert maps back to ForecastDataPoint objects
        List<Forecast.ForecastDataPoint> forecastDataList = null;
        if (entity.getForecastData() != null) {
            forecastDataList = entity.getForecastData().stream()
                    .map(map -> Forecast.ForecastDataPoint.builder()
                            .timestamp((LocalDateTime) map.get("timestamp"))
                            .value((Double) map.get("value"))
                            .confidence((Double) map.get("confidence"))
                            .metadata((java.util.Map<String, Object>) map.get("metadata"))
                            .build())
                    .collect(Collectors.toList());
        }

        return Forecast.builder()
                .id(entity.getUuid())
                .tenantId(entity.getTenantId())
                .modelId(entity.getModelId())
                .forecastName(entity.getForecastName())
                .forecastStartDate(entity.getForecastStartDate())
                .forecastEndDate(entity.getForecastEndDate())
                .forecastHorizon(entity.getForecastHorizon())
                .forecastData(forecastDataList)
                .accuracyMetrics(entity.getAccuracyMetrics())
                .meanAbsoluteError(entity.getMeanAbsoluteError())
                .meanAbsolutePercentageError(entity.getMeanAbsolutePercentageError())
                .status(entity.getStatus())
                .frequency(entity.getFrequency())
                .metadata(entity.getMetadata())
                .createdAt(entity.getCreatedAt())
                .generatedAt(entity.getGeneratedAt())
                .build();
    }
}
