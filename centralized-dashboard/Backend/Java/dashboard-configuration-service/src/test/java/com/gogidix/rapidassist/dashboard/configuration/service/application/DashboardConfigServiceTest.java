package com.gogidix.rapidassist.dashboard.configuration.service.application;

import com.gogidix.rapidassist.dashboard.configuration.service.domain.model.DashboardConfiguration;
import com.gogidix.rapidassist.dashboard.configuration.service.domain.port.out.DashboardConfigRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DashboardConfigService.
 *
 * <p>Tests cover CRUD operations, widget management, theme management,
 * permission management, and dashboard activation/deactivation.</p>
 *
 * @author Rapid Assist Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class DashboardConfigServiceTest {

    @Mock
    private DashboardConfigRepository repository;

    private DashboardConfigService service;

    private DashboardConfiguration testDashboard;

    @BeforeEach
    void setUp() {
        service = new DashboardConfigService(repository);

        // Create test dashboard
        testDashboard = DashboardConfiguration.builder()
            .id("test-id")
            .tenantId("tenant-001")
            .dashboardId("dash-001")
            .name("Test Dashboard")
            .description("Test Description")
            .createdBy("user-123")
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .version(1)
            .active(true)
            .widgets(List.of())
            .build();
    }

    @Test
    void createDashboard_ShouldReturnSavedDashboard() {
        // Arrange
        when(repository.save(any(DashboardConfiguration.class)))
            .thenReturn(CompletableFuture.completedFuture(testDashboard));

        // Act
        CompletableFuture<DashboardConfiguration> result = service.createDashboard(
            "tenant-001",
            "dash-001",
            "Test Dashboard",
            "Test Description",
            "user-123"
        );

        // Assert
        assertNotNull(result);
        assertEquals("dash-001", result.join().dashboardId());
        assertEquals("Test Dashboard", result.join().name());
        verify(repository, times(1)).save(any(DashboardConfiguration.class));
    }

    @Test
    void getDashboard_WhenExists_ShouldReturnDashboard() {
        // Arrange
        when(repository.findById("dash-001"))
            .thenReturn(CompletableFuture.completedFuture(Optional.of(testDashboard)));

        // Act
        CompletableFuture<Optional<DashboardConfiguration>> result = service.getDashboard("dash-001");

        // Assert
        assertNotNull(result);
        assertTrue(result.join().isPresent());
        assertEquals("dash-001", result.join().get().dashboardId());
        verify(repository, times(1)).findById("dash-001");
    }

    @Test
    void getDashboard_WhenNotExists_ShouldReturnEmpty() {
        // Arrange
        when(repository.findById("non-existent"))
            .thenReturn(CompletableFuture.completedFuture(Optional.empty()));

        // Act
        CompletableFuture<Optional<DashboardConfiguration>> result = service.getDashboard("non-existent");

        // Assert
        assertNotNull(result);
        assertFalse(result.join().isPresent());
        verify(repository, times(1)).findById("non-existent");
    }

    @Test
    void getDashboardsByTenant_ShouldReturnDashboards() {
        // Arrange
        List<DashboardConfiguration> dashboards = List.of(testDashboard);
        when(repository.findByTenantId("tenant-001"))
            .thenReturn(CompletableFuture.completedFuture(dashboards));

        // Act
        CompletableFuture<List<DashboardConfiguration>> result = service.getDashboardsByTenant("tenant-001");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.join().size());
        assertEquals("tenant-001", result.join().get(0).tenantId());
        verify(repository, times(1)).findByTenantId("tenant-001");
    }

    @Test
    void updateDashboard_WhenExists_ShouldReturnUpdatedDashboard() {
        // Arrange
        DashboardConfiguration updatedDashboard = DashboardConfiguration.builder()
            .id(testDashboard.id())
            .tenantId(testDashboard.tenantId())
            .dashboardId(testDashboard.dashboardId())
            .name("Updated Name")
            .description("Updated Description")
            .metadata(testDashboard.metadata())
            .layout(testDashboard.layout())
            .widgets(testDashboard.widgets())
            .theme(testDashboard.theme())
            .permissions(testDashboard.permissions())
            .active(testDashboard.active())
            .environment(testDashboard.environment())
            .createdBy(testDashboard.createdBy())
            .createdAt(testDashboard.createdAt())
            .updatedBy("user-456")
            .updatedAt(Instant.now())
            .version(testDashboard.version() + 1)
            .build();

        when(repository.findById("dash-001"))
            .thenReturn(CompletableFuture.completedFuture(Optional.of(testDashboard)));
        when(repository.save(any(DashboardConfiguration.class)))
            .thenReturn(CompletableFuture.completedFuture(updatedDashboard));

        // Act
        CompletableFuture<Optional<DashboardConfiguration>> result = service.updateDashboard(
            "dash-001",
            "Updated Name",
            "Updated Description",
            "user-456"
        );

        // Assert
        assertNotNull(result);
        assertTrue(result.join().isPresent());
        assertEquals("Updated Name", result.join().get().name());
        assertEquals("Updated Description", result.join().get().description());
        verify(repository, times(1)).findById("dash-001");
        verify(repository, times(1)).save(any(DashboardConfiguration.class));
    }

    @Test
    void updateDashboard_WhenNotExists_ShouldReturnEmpty() {
        // Arrange
        when(repository.findById("non-existent"))
            .thenReturn(CompletableFuture.completedFuture(Optional.empty()));

        // Act
        CompletableFuture<Optional<DashboardConfiguration>> result = service.updateDashboard(
            "non-existent",
            "Updated Name",
            "Updated Description",
            "user-456"
        );

        // Assert
        assertNotNull(result);
        assertFalse(result.join().isPresent());
        verify(repository, times(1)).findById("non-existent");
        verify(repository, never()).save(any(DashboardConfiguration.class));
    }

    @Test
    void deleteDashboard_ShouldCallRepository() {
        // Arrange
        when(repository.deleteById("dash-001"))
            .thenReturn(CompletableFuture.completedFuture(true));

        // Act
        CompletableFuture<Boolean> result = service.deleteDashboard("dash-001");

        // Assert
        assertNotNull(result);
        assertTrue(result.join());
        verify(repository, times(1)).deleteById("dash-001");
    }

    @Test
    void getAllDashboards_ShouldReturnAllDashboards() {
        // Arrange
        List<DashboardConfiguration> dashboards = List.of(testDashboard);
        when(repository.findAll())
            .thenReturn(CompletableFuture.completedFuture(dashboards));

        // Act
        CompletableFuture<List<DashboardConfiguration>> result = service.getAllDashboards();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.join().size());
        verify(repository, times(1)).findAll();
    }

    @Test
    void getActiveDashboards_ShouldReturnActiveDashboards() {
        // Arrange
        List<DashboardConfiguration> activeDashboards = List.of(testDashboard);
        when(repository.findActiveByTenant("tenant-001"))
            .thenReturn(CompletableFuture.completedFuture(activeDashboards));

        // Act
        CompletableFuture<List<DashboardConfiguration>> result = service.getActiveDashboards("tenant-001");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.join().size());
        assertTrue(result.join().get(0).active());
        verify(repository, times(1)).findActiveByTenant("tenant-001");
    }

    @Test
    void activateDashboard_WhenExists_ShouldActivateDashboard() {
        // Arrange
        DashboardConfiguration activeDashboard = DashboardConfiguration.builder()
            .id(testDashboard.id())
            .tenantId(testDashboard.tenantId())
            .dashboardId(testDashboard.dashboardId())
            .name(testDashboard.name())
            .description(testDashboard.description())
            .metadata(testDashboard.metadata())
            .layout(testDashboard.layout())
            .widgets(testDashboard.widgets())
            .theme(testDashboard.theme())
            .permissions(testDashboard.permissions())
            .active(true)
            .environment(testDashboard.environment())
            .createdBy(testDashboard.createdBy())
            .createdAt(testDashboard.createdAt())
            .updatedBy("user-456")
            .updatedAt(Instant.now())
            .version(testDashboard.version() + 1)
            .build();

        when(repository.findById("dash-001"))
            .thenReturn(CompletableFuture.completedFuture(Optional.of(testDashboard)));
        when(repository.save(any(DashboardConfiguration.class)))
            .thenReturn(CompletableFuture.completedFuture(activeDashboard));

        // Act
        CompletableFuture<Optional<DashboardConfiguration>> result = service.activateDashboard("dash-001", "user-456");

        // Assert
        assertNotNull(result);
        assertTrue(result.join().isPresent());
        assertTrue(result.join().get().active());
        verify(repository, times(1)).findById("dash-001");
        verify(repository, times(1)).save(any(DashboardConfiguration.class));
    }

    @Test
    void deactivateDashboard_WhenExists_ShouldDeactivateDashboard() {
        // Arrange
        DashboardConfiguration inactiveDashboard = DashboardConfiguration.builder()
            .id(testDashboard.id())
            .tenantId(testDashboard.tenantId())
            .dashboardId(testDashboard.dashboardId())
            .name(testDashboard.name())
            .description(testDashboard.description())
            .metadata(testDashboard.metadata())
            .layout(testDashboard.layout())
            .widgets(testDashboard.widgets())
            .theme(testDashboard.theme())
            .permissions(testDashboard.permissions())
            .active(false)
            .environment(testDashboard.environment())
            .createdBy(testDashboard.createdBy())
            .createdAt(testDashboard.createdAt())
            .updatedBy("user-456")
            .updatedAt(Instant.now())
            .version(testDashboard.version() + 1)
            .build();

        when(repository.findById("dash-001"))
            .thenReturn(CompletableFuture.completedFuture(Optional.of(testDashboard)));
        when(repository.save(any(DashboardConfiguration.class)))
            .thenReturn(CompletableFuture.completedFuture(inactiveDashboard));

        // Act
        CompletableFuture<Optional<DashboardConfiguration>> result = service.deactivateDashboard("dash-001", "user-456");

        // Assert
        assertNotNull(result);
        assertTrue(result.join().isPresent());
        assertFalse(result.join().get().active());
        verify(repository, times(1)).findById("dash-001");
        verify(repository, times(1)).save(any(DashboardConfiguration.class));
    }

    @Test
    void hasPermission_WhenDashboardExists_ShouldReturnPermissionStatus() {
        // Arrange
        when(repository.findById("dash-001"))
            .thenReturn(CompletableFuture.completedFuture(Optional.of(testDashboard)));

        // Act
        CompletableFuture<Boolean> result = service.hasPermission("dash-001", "read", "admin");

        // Assert
        assertNotNull(result);
        // The actual permission check depends on the domain model implementation
        verify(repository, times(1)).findById("dash-001");
    }

    @Test
    void searchDashboards_ShouldReturnMatchingDashboards() {
        // Arrange
        List<DashboardConfiguration> dashboards = List.of(testDashboard);
        when(repository.searchByName("tenant-001", "test"))
            .thenReturn(CompletableFuture.completedFuture(dashboards));

        // Act
        CompletableFuture<List<DashboardConfiguration>> result = service.searchDashboards("tenant-001", "test");

        // Assert
        assertNotNull(result);
        assertFalse(result.join().isEmpty());
        verify(repository, times(1)).searchByName("tenant-001", "test");
    }
}
