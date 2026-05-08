package com.gogidix.rapidassist.shared.audit.library.application;

import com.gogidix.rapidassist.shared.audit.library.infrastructure.database.AuditLogEntity;
import com.gogidix.rapidassist.shared.audit.library.infrastructure.database.AuditLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuditQueryService.
 */
@ExtendWith(MockitoExtension.class)
class AuditQueryServiceTest {

    @Mock
    private AuditLogRepository repository;

    private AuditQueryService service;

    @BeforeEach
    void setUp() {
        service = new AuditQueryService(repository);
    }

    @Test
    void testFindByTenant() {
        // Arrange
        List<AuditLogEntity> logs = createTestAuditLogs();
        when(repository.findByTenantId("tenant-001")).thenReturn(logs);

        // Act
        List<AuditLogEntity> result = service.findByTenant("tenant-001");

        // Assert
        assertEquals(2, result.size());
        verify(repository, times(1)).findByTenantId("tenant-001");
    }

    @Test
    void testFindByTenantWithPagination() {
        // Arrange
        List<AuditLogEntity> logs = createTestAuditLogs();
        Page<AuditLogEntity> page = new PageImpl<>(logs);
        Pageable pageable = PageRequest.of(0, 10);

        when(repository.findAllByTenantId(eq("tenant-001"), eq(pageable))).thenReturn(page);

        // Act
        Page<AuditLogEntity> result = service.findByTenant("tenant-001", pageable);

        // Assert
        assertEquals(2, result.getContent().size());
        verify(repository, times(1)).findAllByTenantId(eq("tenant-001"), eq(pageable));
    }

    @Test
    void testFindByActor() {
        // Arrange
        List<AuditLogEntity> logs = createTestAuditLogs();
        when(repository.findByActorId("user-123")).thenReturn(logs);

        // Act
        List<AuditLogEntity> result = service.findByActor("user-123");

        // Assert
        assertEquals(2, result.size());
        verify(repository, times(1)).findByActorId("user-123");
    }

    @Test
    void testFindByEntity() {
        // Arrange
        List<AuditLogEntity> logs = createTestAuditLogs();
        when(repository.findByEntityTypeAndEntityId("Customer", "customer-456")).thenReturn(logs);

        // Act
        List<AuditLogEntity> result = service.findByEntity("Customer", "customer-456");

        // Assert
        assertEquals(2, result.size());
        verify(repository, times(1)).findByEntityTypeAndEntityId("Customer", "customer-456");
    }

    @Test
    void testFindByDateRange() {
        // Arrange
        Instant startDate = Instant.now().minusSeconds(3600);
        Instant endDate = Instant.now();
        List<AuditLogEntity> logs = createTestAuditLogs();
        when(repository.findByOccurredAtBetween(startDate, endDate)).thenReturn(logs);

        // Act
        List<AuditLogEntity> result = service.findByDateRange(startDate, endDate);

        // Assert
        assertEquals(2, result.size());
        verify(repository, times(1)).findByOccurredAtBetween(startDate, endDate);
    }

    @Test
    void testFindByEventType() {
        // Arrange
        List<AuditLogEntity> logs = createTestAuditLogs();
        when(repository.findByEventType("USER_CREATED")).thenReturn(logs);

        // Act
        List<AuditLogEntity> result = service.findByEventType("USER_CREATED");

        // Assert
        assertEquals(2, result.size());
        verify(repository, times(1)).findByEventType("USER_CREATED");
    }

    @Test
    void testFindByCorrelationId() {
        // Arrange
        List<AuditLogEntity> logs = createTestAuditLogs();
        when(repository.findByCorrelationId("corr-456")).thenReturn(logs);

        // Act
        List<AuditLogEntity> result = service.findByCorrelationId("corr-456");

        // Assert
        assertEquals(2, result.size());
        verify(repository, times(1)).findByCorrelationId("corr-456");
    }

    @Test
    void testFindByTenantAndActorAndDateRange() {
        // Arrange
        Instant startDate = Instant.now().minusSeconds(3600);
        Instant endDate = Instant.now();
        List<AuditLogEntity> logs = createTestAuditLogs();
        when(repository.findByTenantAndActorAndDateRange("tenant-001", "user-123", startDate, endDate))
                .thenReturn(logs);

        // Act
        List<AuditLogEntity> result = service.findByTenantAndActorAndDateRange(
                "tenant-001", "user-123", startDate, endDate
        );

        // Assert
        assertEquals(2, result.size());
        verify(repository, times(1)).findByTenantAndActorAndDateRange(
                "tenant-001", "user-123", startDate, endDate
        );
    }

    @Test
    void testFindByTenantAndEntity() {
        // Arrange
        List<AuditLogEntity> logs = createTestAuditLogs();
        when(repository.findByTenantAndEntity("tenant-001", "Customer", "customer-456"))
                .thenReturn(logs);

        // Act
        List<AuditLogEntity> result = service.findByTenantAndEntity(
                "tenant-001", "Customer", "customer-456"
        );

        // Assert
        assertEquals(2, result.size());
        verify(repository, times(1)).findByTenantAndEntity(
                "tenant-001", "Customer", "customer-456"
        );
    }

    @Test
    void testCountByTenant() {
        // Arrange
        when(repository.countByTenantId("tenant-001")).thenReturn(42L);

        // Act
        long count = service.countByTenant("tenant-001");

        // Assert
        assertEquals(42L, count);
        verify(repository, times(1)).countByTenantId("tenant-001");
    }

    @Test
    void testFindByEventIdFound() {
        // Arrange
        AuditLogEntity log = createTestAuditLog();
        when(repository.findByEventId("event-123")).thenReturn(Optional.of(log));

        // Act
        AuditLogEntity result = service.findByEventId("event-123");

        // Assert
        assertNotNull(result);
        assertEquals("event-123", result.getEventId());
        verify(repository, times(1)).findByEventId("event-123");
    }

    @Test
    void testFindByEventIdNotFound() {
        // Arrange
        when(repository.findByEventId("event-999")).thenReturn(Optional.empty());

        // Act
        AuditLogEntity result = service.findByEventId("event-999");

        // Assert
        assertNull(result);
        verify(repository, times(1)).findByEventId("event-999");
    }

    @Test
    void testFindAllWithPagination() {
        // Arrange
        List<AuditLogEntity> logs = createTestAuditLogs();
        Page<AuditLogEntity> page = new PageImpl<>(logs);
        Pageable pageable = PageRequest.of(0, 10);

        when(repository.findAll(pageable)).thenReturn(page);

        // Act
        Page<AuditLogEntity> result = service.findAll(pageable);

        // Assert
        assertEquals(2, result.getContent().size());
        verify(repository, times(1)).findAll(pageable);
    }

    // Helper methods

    private List<AuditLogEntity> createTestAuditLogs() {
        return Arrays.asList(
                createTestAuditLog("event-123", "USER_CREATED"),
                createTestAuditLog("event-456", "USER_UPDATED")
        );
    }

    private AuditLogEntity createTestAuditLog() {
        return createTestAuditLog("event-123", "USER_CREATED");
    }

    private AuditLogEntity createTestAuditLog(String eventId, String eventType) {
        Instant now = Instant.now();
        return new AuditLogEntity(
                "1.0",
                eventId,
                eventType,
                now,
                "corr-456",
                "US",
                "tenant-001",
                "subtenant-001",
                "user-123",
                "USER",
                "John Doe",
                "Customer",
                "customer-456",
                "Acme Corp",
                null,
                "{\"name\": \"John Doe\"}"
        );
    }
}
