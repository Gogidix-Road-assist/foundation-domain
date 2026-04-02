package com.gogidix.rapidassist.ai.analytics.application.service;

import com.gogidix.rapidassist.ai.analytics.application.command.CreateDashboardCommand;
import com.gogidix.rapidassist.ai.analytics.application.dto.DashboardDto;
import com.gogidix.rapidassist.ai.analytics.application.mapper.DashboardMapper;
import com.gogidix.rapidassist.ai.analytics.domain.exception.DashboardNotFoundException;
import com.gogidix.rapidassist.ai.analytics.domain.exception.InvalidReportDataException;
import com.gogidix.rapidassist.ai.analytics.domain.model.Dashboard;
import com.gogidix.rapidassist.ai.analytics.domain.repository.DashboardRepositoryPort;
import com.gogidix.rapidassist.ai.analytics.infrastructure.messaging.kafka.event.KafkaEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DashboardApplicationService
 */
@ExtendWith(MockitoExtension.class)
class DashboardApplicationServiceTest {

    @Mock
    private DashboardRepositoryPort dashboardRepository;

    @Mock
    private DashboardMapper dashboardMapper;

    @Mock
    private KafkaEventPublisher eventPublisher;

    @InjectMocks
    private DashboardApplicationService dashboardService;

    private CreateDashboardCommand validCommand;
    private Dashboard testDashboard;
    private DashboardDto testDashboardDto;

    @BeforeEach
    void setUp() {
        validCommand = CreateDashboardCommand.builder()
                .tenantId("tenant-123")
                .name("Test Dashboard")
                .description("Test Description")
                .createdBy("user-123")
                .build();

        testDashboard = Dashboard.builder()
                .id(UUID.randomUUID())
                .tenantId("tenant-123")
                .name("Test Dashboard")
                .description("Test Description")
                .createdBy("user-123")
                .createdAt(LocalDateTime.now())
                .isActive(true)
                .isPublic(false)
                .build();

        testDashboardDto = DashboardDto.builder()
                .id(testDashboard.getId().toString())
                .tenantId(testDashboard.getTenantId())
                .name(testDashboard.getName())
                .description(testDashboard.getDescription())
                .createdBy(testDashboard.getCreatedBy())
                .build();
    }

    @Test
    void createDashboard_WithValidCommand_ShouldReturnDashboardDto() {
        // Given
        when(dashboardRepository.save(any(Dashboard.class))).thenReturn(testDashboard);
        when(dashboardMapper.toDto(any(Dashboard.class))).thenReturn(testDashboardDto);

        // When
        DashboardDto result = dashboardService.createDashboard(validCommand);

        // Then
        assertNotNull(result);
        assertEquals("Test Dashboard", result.getName());
        verify(dashboardRepository, times(1)).save(any(Dashboard.class));
        verify(eventPublisher, times(1)).publishDashboardCreatedEvent(any(Dashboard.class));
    }

    @Test
    void createDashboard_WithMissingTenantId_ShouldThrowException() {
        // Given
        CreateDashboardCommand invalidCommand = CreateDashboardCommand.builder()
                .name("Test Dashboard")
                .build();

        // When/Then
        assertThrows(InvalidReportDataException.class, () -> dashboardService.createDashboard(invalidCommand));
        verify(dashboardRepository, never()).save(any(Dashboard.class));
    }

    @Test
    void createDashboard_WithMissingName_ShouldThrowException() {
        // Given
        CreateDashboardCommand invalidCommand = CreateDashboardCommand.builder()
                .tenantId("tenant-123")
                .build();

        // When/Then
        assertThrows(InvalidReportDataException.class, () -> dashboardService.createDashboard(invalidCommand));
        verify(dashboardRepository, never()).save(any(Dashboard.class));
    }

    @Test
    void getDashboard_WithValidId_ShouldReturnDashboardDto() {
        // Given
        when(dashboardRepository.findByIdAndTenantId(testDashboard.getId(), "tenant-123"))
                .thenReturn(Optional.of(testDashboard));
        when(dashboardMapper.toDto(testDashboard)).thenReturn(testDashboardDto);

        // When
        DashboardDto result = dashboardService.getDashboard(testDashboard.getId().toString(), "tenant-123");

        // Then
        assertNotNull(result);
        assertEquals("Test Dashboard", result.getName());
        verify(dashboardRepository, times(1)).findByIdAndTenantId(testDashboard.getId(), "tenant-123");
    }

    @Test
    void getDashboard_WithInvalidId_ShouldThrowException() {
        // Given
        UUID invalidId = UUID.randomUUID();
        when(dashboardRepository.findByIdAndTenantId(invalidId, "tenant-123"))
                .thenReturn(Optional.empty());

        // When/Then
        assertThrows(DashboardNotFoundException.class,
                () -> dashboardService.getDashboard(invalidId.toString(), "tenant-123"));
    }

    @Test
    void deleteDashboard_WithValidId_ShouldDeleteSuccessfully() {
        // Given
        when(dashboardRepository.existsById(testDashboard.getId())).thenReturn(true);
        doNothing().when(dashboardRepository).deleteById(testDashboard.getId());

        // When
        dashboardService.deleteDashboard(testDashboard.getId().toString(), "tenant-123");

        // Then
        verify(dashboardRepository, times(1)).deleteById(testDashboard.getId());
    }

    @Test
    void deleteDashboard_WithInvalidId_ShouldThrowException() {
        // Given
        UUID invalidId = UUID.randomUUID();
        when(dashboardRepository.existsById(invalidId)).thenReturn(false);

        // When/Then
        assertThrows(DashboardNotFoundException.class,
                () -> dashboardService.deleteDashboard(invalidId.toString(), "tenant-123"));
        verify(dashboardRepository, never()).deleteById(any(UUID.class));
    }
}
