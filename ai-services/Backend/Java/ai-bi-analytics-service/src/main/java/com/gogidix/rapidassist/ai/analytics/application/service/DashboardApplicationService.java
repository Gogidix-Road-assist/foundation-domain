package com.gogidix.rapidassist.ai.analytics.application.service;

import com.gogidix.rapidassist.ai.analytics.application.command.CreateDashboardCommand;
import com.gogidix.rapidassist.ai.analytics.application.command.UpdateDashboardCommand;
import com.gogidix.rapidassist.ai.analytics.application.dto.DashboardDto;
import com.gogidix.rapidassist.ai.analytics.application.mapper.DashboardMapper;
import com.gogidix.rapidassist.ai.analytics.application.query.ListDashboardsQuery;
import com.gogidix.rapidassist.ai.analytics.domain.exception.DashboardNotFoundException;
import com.gogidix.rapidassist.ai.analytics.domain.exception.InvalidReportDataException;
import com.gogidix.rapidassist.ai.analytics.domain.model.Dashboard;
import com.gogidix.rapidassist.ai.analytics.domain.repository.DashboardRepositoryPort;
import com.gogidix.rapidassist.ai.analytics.infrastructure.messaging.kafka.event.KafkaEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Application service for Dashboard operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardApplicationService {

    private final DashboardRepositoryPort dashboardRepository;
    private final DashboardMapper dashboardMapper;
    private final KafkaEventPublisher eventPublisher;

    /**
     * Create a new dashboard
     */
    @Transactional
    public DashboardDto createDashboard(CreateDashboardCommand command) {
        log.info("Creating dashboard: {} for tenant: {}", command.getName(), command.getTenantId());

        // Validate command
        validateCreateDashboardCommand(command);

        // Create dashboard
        Dashboard dashboard = createDashboardFromCommand(command);

        // Save dashboard
        dashboard = dashboardRepository.save(dashboard);

        // Publish event
        eventPublisher.publishDashboardCreatedEvent(dashboard);

        log.info("Dashboard created successfully: {}", dashboard.getId());
        return dashboardMapper.toDto(dashboard);
    }

    /**
     * Get dashboard by ID
     */
    @Transactional(readOnly = true)
    public DashboardDto getDashboard(String dashboardId, String tenantId) {
        log.info("Getting dashboard: {} for tenant: {}", dashboardId, tenantId);

        UUID id = UUID.fromString(dashboardId);
        Dashboard dashboard = dashboardRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new DashboardNotFoundException(id));

        return dashboardMapper.toDto(dashboard);
    }

    /**
     * Update dashboard
     */
    @Transactional
    public DashboardDto updateDashboard(UpdateDashboardCommand command) {
        log.info("Updating dashboard: {} for tenant: {}", command.getDashboardId(), command.getTenantId());

        UUID dashboardId = UUID.fromString(command.getDashboardId());
        Dashboard dashboard = dashboardRepository.findByIdAndTenantId(dashboardId, command.getTenantId())
                .orElseThrow(() -> new DashboardNotFoundException(dashboardId));

        // Update fields
        if (command.getName() != null) {
            dashboard.setName(command.getName());
        }
        if (command.getDescription() != null) {
            dashboard.setDescription(command.getDescription());
        }
        if (command.getLayout() != null) {
            dashboard.setLayout(command.getLayout());
        }
        if (command.getTheme() != null) {
            dashboard.setTheme(command.getTheme());
        }
        if (command.getIsPublic() != null) {
            dashboard.setIsPublic(command.getIsPublic());
        }
        if (command.getChartIds() != null) {
            dashboard.setChartIds(command.getChartIds().stream().map(UUID::fromString).toList());
        }
        if (command.getFilters() != null) {
            dashboard.setFilters(command.getFilters());
        }
        if (command.getRefreshInterval() != null) {
            dashboard.setRefreshInterval(command.getRefreshInterval());
        }
        if (command.getAutoRefresh() != null) {
            dashboard.setAutoRefresh(command.getAutoRefresh());
        }
        if (command.getTags() != null) {
            dashboard.setTags(command.getTags());
        }
        if (command.getIsActive() != null) {
            dashboard.setIsActive(command.getIsActive());
        }
        if (command.getDisplayOrder() != null) {
            dashboard.setDisplayOrder(command.getDisplayOrder());
        }
        if (command.getCategory() != null) {
            dashboard.setCategory(command.getCategory());
        }

        dashboard.setUpdatedBy(command.getUpdatedBy());
        dashboard.setUpdatedAt(LocalDateTime.now());

        // Save dashboard
        dashboard = dashboardRepository.save(dashboard);

        // Publish event
        eventPublisher.publishDashboardUpdatedEvent(dashboard);

        log.info("Dashboard updated successfully: {}", dashboard.getId());
        return dashboardMapper.toDto(dashboard);
    }

    /**
     * List dashboards with filters
     */
    @Transactional(readOnly = true)
    public Page<DashboardDto> listDashboards(ListDashboardsQuery query) {
        log.info("Listing dashboards for tenant: {} with filters: {}", query.getTenantId(), query);

        int page = query.getPage() != null ? query.getPage() : 0;
        int size = query.getSize() != null ? query.getSize() : 20;

        List<Dashboard> dashboards;
        long total;

        if (query.getIsPublic() != null && query.getIsActive() != null && query.getCategory() != null) {
            dashboards = dashboardRepository.findByTenantIdAndIsPublic(query.getTenantId(), query.getIsPublic()).stream()
                    .filter(d -> query.getIsActive().equals(d.getIsActive()))
                    .filter(d -> query.getCategory().equals(d.getCategory()))
                    .toList();
            total = dashboards.size();
        } else if (query.getIsPublic() != null && query.getIsActive() != null) {
            dashboards = dashboardRepository.findByTenantIdAndIsPublic(query.getTenantId(), query.getIsPublic()).stream()
                    .filter(d -> query.getIsActive().equals(d.getIsActive()))
                    .toList();
            total = dashboards.size();
        } else if (query.getIsPublic() != null) {
            dashboards = dashboardRepository.findByTenantIdAndIsPublic(query.getTenantId(), query.getIsPublic());
            total = dashboards.size();
        } else if (query.getIsActive() != null) {
            dashboards = dashboardRepository.findByTenantIdAndIsActive(query.getTenantId(), query.getIsActive());
            total = dashboards.size();
        } else if (query.getCategory() != null) {
            dashboards = dashboardRepository.findByTenantIdAndCategory(query.getTenantId(), query.getCategory());
            total = dashboards.size();
        } else {
            dashboards = dashboardRepository.findByTenantId(query.getTenantId());
            total = dashboards.size();
        }

        // Apply pagination
        int start = page * size;
        int end = Math.min(start + size, dashboards.size());
        List<Dashboard> paginatedDashboards = start < dashboards.size() ? dashboards.subList(start, end) : List.of();

        List<DashboardDto> dtoList = dashboardMapper.toDtoList(paginatedDashboards);
        return new PageImpl<>(dtoList, PageRequest.of(page, size), total);
    }

    /**
     * Delete dashboard by ID
     */
    @Transactional
    public void deleteDashboard(String dashboardId, String tenantId) {
        log.info("Deleting dashboard: {} for tenant: {}", dashboardId, tenantId);

        UUID id = UUID.fromString(dashboardId);
        if (!dashboardRepository.existsById(id)) {
            throw new DashboardNotFoundException(id);
        }

        dashboardRepository.deleteById(id);
        log.info("Dashboard deleted successfully: {}", dashboardId);
    }

    private void validateCreateDashboardCommand(CreateDashboardCommand command) {
        if (command.getTenantId() == null || command.getTenantId().isBlank()) {
            throw new InvalidReportDataException("Tenant ID is required");
        }
        if (command.getName() == null || command.getName().isBlank()) {
            throw new InvalidReportDataException("Dashboard name is required");
        }
    }

    private Dashboard createDashboardFromCommand(CreateDashboardCommand command) {
        return Dashboard.builder()
                .id(UUID.randomUUID())
                .tenantId(command.getTenantId())
                .name(command.getName())
                .description(command.getDescription())
                .layout(command.getLayout())
                .theme(command.getTheme())
                .isPublic(command.getIsPublic() != null ? command.getIsPublic() : Boolean.FALSE)
                .createdBy(command.getCreatedBy())
                .updatedBy(command.getCreatedBy())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .chartIds(command.getChartIds() != null ? command.getChartIds().stream().map(UUID::fromString).toList() : List.of())
                .filters(command.getFilters())
                .refreshInterval(command.getRefreshInterval())
                .autoRefresh(command.getAutoRefresh() != null ? command.getAutoRefresh() : Boolean.FALSE)
                .tags(command.getTags())
                .isActive(command.getIsActive() != null ? command.getIsActive() : Boolean.TRUE)
                .displayOrder(command.getDisplayOrder())
                .category(command.getCategory())
                .build();
    }
}
