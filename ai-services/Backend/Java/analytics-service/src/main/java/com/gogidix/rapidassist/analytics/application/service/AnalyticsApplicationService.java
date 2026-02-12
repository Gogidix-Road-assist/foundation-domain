package com.gogidix.rapidassist.analytics.application.service;

import com.gogidix.rapidassist.analytics.application.dto.AnalyticsDto;
import com.gogidix.rapidassist.analytics.application.mapper.AnalyticsMapper;
import com.gogidix.rapidassist.analytics.domain.model.Analytics;
import com.gogidix.rapidassist.analytics.domain.port.out.AnalyticsRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Application Service for Analytics operations.
 * Implements business logic and orchestrates domain operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsApplicationService {

    private final AnalyticsRepositoryPort analyticsRepository;
    private final AnalyticsMapper mapper;

    public AnalyticsDto createAnalytics(String tenantId, AnalyticsDto dto) {
        log.info("Creating analytics for tenant: {}", tenantId);

        var analytics = Analytics.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .analyticsType(dto.getAnalyticsType())
                .dataSource(dto.getDataSource())
                .metrics(dto.getMetrics())
                .dimensions(dto.getDimensions())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .status("PENDING")
                .totalRecords(dto.getTotalRecords())
                .aggregationType(dto.getAggregationType())
                .computedBy(dto.getComputedBy())
                .description(dto.getDescription())
                .metadata(dto.getMetadata())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        var saved = analyticsRepository.save(tenantId, analytics);
        return mapper.toDto(saved);
    }

    public AnalyticsDto getAnalytics(String tenantId, UUID id) {
        log.info("Getting analytics: {} for tenant: {}", id, tenantId);

        var analytics = analyticsRepository.findById(tenantId, id)
                .orElseThrow(() -> new IllegalArgumentException("Analytics not found: " + id));

        return mapper.toDto(analytics);
    }

    public List<AnalyticsDto> getAnalyticsByTenant(String tenantId) {
        log.info("Getting all analytics for tenant: {}", tenantId);

        var analytics = analyticsRepository.findByTenantId(tenantId);
        return mapper.toDtoList(analytics);
    }

    public List<AnalyticsDto> getAnalyticsByStatus(String tenantId, String status) {
        log.info("Getting analytics by status: {} for tenant: {}", status, tenantId);

        var analytics = analyticsRepository.findByTenantIdAndStatus(tenantId, status);
        return mapper.toDtoList(analytics);
    }

    public List<AnalyticsDto> getAnalyticsByType(String tenantId, String analyticsType) {
        log.info("Getting analytics by type: {} for tenant: {}", analyticsType, tenantId);

        var analytics = analyticsRepository.findByTenantIdAndAnalyticsType(tenantId, analyticsType);
        return mapper.toDtoList(analytics);
    }

    public List<AnalyticsDto> getAnalyticsByTimeRange(String tenantId, LocalDateTime startTime, LocalDateTime endTime) {
        log.info("Getting analytics by time range for tenant: {}", tenantId);

        var analytics = analyticsRepository.findByTenantIdAndTimeRange(tenantId, startTime, endTime);
        return mapper.toDtoList(analytics);
    }

    public AnalyticsDto computeAnalytics(String tenantId, UUID id) {
        log.info("Computing analytics: {} for tenant: {}", id, tenantId);

        var analytics = analyticsRepository.findById(tenantId, id)
                .orElseThrow(() -> new IllegalArgumentException("Analytics not found: " + id));

        if (!analytics.isReadyForComputation()) {
            throw new IllegalStateException("Analytics is not ready for computation");
        }

        // Simulate computation
        Double computedValue = performComputation(analytics);
        analytics.markAsComputed(computedValue);

        var saved = analyticsRepository.save(tenantId, analytics);
        return mapper.toDto(saved);
    }

    public void deleteAnalytics(String tenantId, UUID id) {
        log.info("Deleting analytics: {} for tenant: {}", id, tenantId);

        if (!analyticsRepository.exists(tenantId, id)) {
            throw new IllegalArgumentException("Analytics not found: " + id);
        }

        analyticsRepository.delete(tenantId, id);
    }

    private Double performComputation(Analytics analytics) {
        // Simulate aggregation computation
        // In real implementation, this would query metrics and perform actual aggregation
        return 1000.0; // Placeholder
    }
}
