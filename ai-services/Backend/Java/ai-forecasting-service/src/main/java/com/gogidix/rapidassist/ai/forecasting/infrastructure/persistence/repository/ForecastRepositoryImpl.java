package com.gogidix.rapidassist.ai.forecasting.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.forecasting.domain.model.Forecast;
import com.gogidix.rapidassist.ai.forecasting.domain.model.ForecastStatus;
import com.gogidix.rapidassist.ai.forecasting.domain.repository.ForecastRepositoryPort;
import com.gogidix.rapidassist.ai.forecasting.infrastructure.persistence.entity.ForecastEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of ForecastRepositoryPort.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class ForecastRepositoryImpl implements ForecastRepositoryPort {

    private final SpringDataForecastRepository springDataRepository;

    @Override
    public Forecast save(String tenantId, Forecast forecast) {
        log.info("Saving forecast: {} for tenant: {}", forecast.getId(), tenantId);
        ForecastEntity entity = toEntity(forecast);
        entity.setTenantId(tenantId);
        ForecastEntity saved = springDataRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Forecast> findById(String tenantId, UUID forecastId) {
        log.info("Finding forecast by ID: {} for tenant: {}", forecastId, tenantId);
        return springDataRepository.findByUuidAndTenantId(forecastId, tenantId)
                .map(this::toDomain);
    }

    @Override
    public List<Forecast> findByTenantId(String tenantId) {
        log.info("Finding all forecasts for tenant: {}", tenantId);
        return springDataRepository.findByTenantId(tenantId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Forecast> findByModelId(String tenantId, UUID modelId) {
        log.info("Finding forecasts for model: {} in tenant: {}", modelId, tenantId);
        return springDataRepository.findByModelIdAndTenantId(modelId, tenantId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Forecast> findByStatus(String tenantId, ForecastStatus status) {
        log.info("Finding forecasts by status: {} for tenant: {}", status, tenantId);
        return springDataRepository.findByStatusAndTenantId(status, tenantId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Forecast> findByDateRange(String tenantId, LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Finding forecasts by date range for tenant: {}", tenantId);
        return springDataRepository.findByTenantIdAndDateRange(tenantId, startDate, endDate).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Forecast> findCompletedForecasts(String tenantId) {
        log.info("Finding completed forecasts for tenant: {}", tenantId);
        return springDataRepository.findByStatusAndTenantId(ForecastStatus.COMPLETED, tenantId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Forecast> findFailedForecasts(String tenantId) {
        log.info("Finding failed forecasts for tenant: {}", tenantId);
        return springDataRepository.findByStatusAndTenantId(ForecastStatus.FAILED, tenantId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void delete(String tenantId, UUID forecastId) {
        log.info("Deleting forecast: {} for tenant: {}", forecastId, tenantId);
        springDataRepository.deleteByUuidAndTenantId(forecastId, tenantId);
    }

    @Override
    public boolean exists(String tenantId, UUID forecastId) {
        return springDataRepository.existsByUuidAndTenantId(forecastId, tenantId);
    }

    @Override
    public long countByTenantId(String tenantId) {
        return springDataRepository.countByTenantId(tenantId);
    }

    private Forecast toDomain(ForecastEntity entity) {
        return Forecast.builder()
                .id(entity.getUuid())
                .tenantId(entity.getTenantId())
                .modelId(entity.getModelId())
                .forecastName(entity.getForecastName())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .forecastHorizon(entity.getForecastHorizon())
                .granularity(entity.getGranularity())
                .confidenceIntervalLower(entity.getConfidenceIntervalLower())
                .confidenceIntervalUpper(entity.getConfidenceIntervalUpper())
                .meanAbsoluteError(entity.getMeanAbsoluteError())
                .meanSquaredError(entity.getMeanSquaredError())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    private ForecastEntity toEntity(Forecast domain) {
        return ForecastEntity.builder()
                .uuid(domain.getId() != null ? domain.getId() : UUID.randomUUID())
                .tenantId(domain.getTenantId())
                .modelId(domain.getModelId())
                .forecastName(domain.getForecastName())
                .description(domain.getDescription())
                .status(domain.getStatus())
                .startDate(domain.getStartDate())
                .endDate(domain.getEndDate())
                .forecastHorizon(domain.getForecastHorizon())
                .granularity(domain.getGranularity())
                .confidenceIntervalLower(domain.getConfidenceIntervalLower())
                .confidenceIntervalUpper(domain.getConfidenceIntervalUpper())
                .meanAbsoluteError(domain.getMeanAbsoluteError())
                .meanSquaredError(domain.getMeanSquaredError())
                .createdAt(domain.getCreatedAt() != null ? domain.getCreatedAt() : LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy(domain.getCreatedBy())
                .updatedBy(domain.getUpdatedBy())
                .build();
    }
}
