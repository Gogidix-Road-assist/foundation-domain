package com.gogidix.rapidassist.orchestration.alerting_service.application.service;

import com.gogidix.rapidassist.orchestration.alerting_service.application.command.AcknowledgeAlertCommand;
import com.gogidix.rapidassist.orchestration.alerting_service.application.command.CreateAlertCommand;
import com.gogidix.rapidassist.orchestration.alerting_service.application.command.EscalateAlertCommand;
import com.gogidix.rapidassist.orchestration.alerting_service.application.command.ResolveAlertCommand;
import com.gogidix.rapidassist.orchestration.alerting_service.application.dto.AlertDTO;
import com.gogidix.rapidassist.orchestration.alerting_service.application.mapper.AlertMapper;
import com.gogidix.rapidassist.orchestration.alerting_service.domain.model.Alert;
import com.gogidix.rapidassist.orchestration.alerting_service.domain.port.output.AlertEventPublisherPort;
import com.gogidix.rapidassist.orchestration.alerting_service.domain.port.output.AlertRepositoryPort;
import com.gogidix.rapidassist.orchestration.alerting_service.shared.exception.NotFoundException;
import com.gogidix.rapidassist.orchestration.alerting_service.shared.requestcontext.RequestContext;
import com.gogidix.rapidassist.orchestration.alerting_service.shared.requestcontext.RequestContextHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AlertApplicationService
 */
@ExtendWith(MockitoExtension.class)
class AlertApplicationServiceTest {

    @Mock
    private AlertRepositoryPort alertRepository;

    @Mock
    private AlertEventPublisherPort eventPublisher;

    @Mock
    private AlertMapper alertMapper;

    @InjectMocks
    private AlertApplicationService service;

    private RequestContext requestContext;
    private MockedStatic<RequestContextHolder> requestContextHolderMock;

    @BeforeEach
    void setUp() {
        requestContext = RequestContext.builder()
            .tenantId("tenant-001")
            .userId("user-123")
            .correlationId("corr-456")
            .build();

        requestContextHolderMock = mockStatic(RequestContextHolder.class);
        requestContextHolderMock.when(RequestContextHolder::get).thenReturn(requestContext);

        lenient().when(alertMapper.toDTO(any(Alert.class))).thenAnswer(invocation -> {
            Alert alert = invocation.getArgument(0);
            return AlertDTO.builder()
                .alertId(alert.getAlertId())
                .requestId(alert.getRequestId())
                .tenantId(alert.getTenantId())
                .type(alert.getType())
                .severity(alert.getSeverity())
                .title(alert.getTitle())
                .status(alert.getStatus())
                .build();
        });
    }

    @AfterEach
    void tearDown() {
        requestContextHolderMock.close();
    }

    @Test
    @DisplayName("Should create alert successfully")
    void testCreateAlert() {
        // Given
        CreateAlertCommand command = CreateAlertCommand.builder()
            .requestId("REQ-123")
            .tenantId("tenant-001")
            .type(Alert.AlertType.EMERGENCY)
            .severity(Alert.AlertSeverity.CRITICAL)
            .title("Emergency")
            .description("Test emergency")
            .source("API")
            .build();

        Alert savedAlert = Alert.builder()
            .alertId("ALT-123")
            .requestId("REQ-123")
            .tenantId("tenant-001")
            .type(Alert.AlertType.EMERGENCY)
            .severity(Alert.AlertSeverity.CRITICAL)
            .title("Emergency")
            .status(Alert.AlertStatus.PENDING)
            .createdAt(LocalDateTime.now())
            .build();

        when(alertRepository.save(any(Alert.class))).thenReturn(savedAlert);

        // When
        AlertDTO result = service.createAlert(command);

        // Then
        assertNotNull(result);
        assertEquals("ALT-123", result.getAlertId());
        assertEquals("REQ-123", result.getRequestId());

        verify(alertRepository).save(any(Alert.class));
        verify(eventPublisher).publishAlertCreated(any(Alert.class));
        verify(eventPublisher).publishCriticalAlertDetected(any(Alert.class));
    }

    @Test
    @DisplayName("Should acknowledge alert successfully")
    void testAcknowledgeAlert() {
        // Given
        Alert alert = Alert.builder()
            .alertId("ALT-123")
            .tenantId("tenant-001")
            .status(Alert.AlertStatus.PENDING)
            .build();

        AcknowledgeAlertCommand command = AcknowledgeAlertCommand.builder()
            .acknowledgedBy("user-123")
            .assignedTo("provider-456")
            .build();

        when(alertRepository.findByAlertId("ALT-123")).thenReturn(Optional.of(alert));
        when(alertRepository.save(any(Alert.class))).thenReturn(alert);

        // When
        AlertDTO result = service.acknowledgeAlert("ALT-123", command);

        // Then
        assertNotNull(result);
        verify(alertRepository).save(any(Alert.class));
        verify(eventPublisher).publishAlertAcknowledged(any(Alert.class));
    }

    @Test
    @DisplayName("Should throw exception when acknowledging non-existent alert")
    void testAcknowledgeNonExistentAlert() {
        // Given
        AcknowledgeAlertCommand command = AcknowledgeAlertCommand.builder()
            .acknowledgedBy("user-123")
            .assignedTo("provider-456")
            .build();

        when(alertRepository.findByAlertId("ALT-999")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> service.acknowledgeAlert("ALT-999", command));

        verify(alertRepository, never()).save(any(Alert.class));
        verify(eventPublisher, never()).publishAlertAcknowledged(any(Alert.class));
    }

    @Test
    @DisplayName("Should escalate alert successfully")
    void testEscalateAlert() {
        // Given
        Alert alert = Alert.builder()
            .alertId("ALT-123")
            .tenantId("tenant-001")
            .status(Alert.AlertStatus.ACKNOWLEDGED)
            .escalationLevel(1)
            .build();

        EscalateAlertCommand command = EscalateAlertCommand.builder()
            .escalationLevel(2)
            .assignedTo("manager-456")
            .build();

        when(alertRepository.findByAlertId("ALT-123")).thenReturn(Optional.of(alert));
        when(alertRepository.save(any(Alert.class))).thenReturn(alert);

        // When
        AlertDTO result = service.escalateAlert("ALT-123", command);

        // Then
        assertNotNull(result);
        verify(alertRepository).save(any(Alert.class));
        verify(eventPublisher).publishAlertEscalated(any(Alert.class));
    }

    @Test
    @DisplayName("Should resolve alert successfully")
    void testResolveAlert() {
        // Given
        Alert alert = Alert.builder()
            .alertId("ALT-123")
            .tenantId("tenant-001")
            .status(Alert.AlertStatus.IN_PROGRESS)
            .build();

        ResolveAlertCommand command = ResolveAlertCommand.builder()
            .resolvedBy("provider-456")
            .resolutionNotes("Issue fixed")
            .build();

        when(alertRepository.findByAlertId("ALT-123")).thenReturn(Optional.of(alert));
        when(alertRepository.save(any(Alert.class))).thenReturn(alert);

        // When
        AlertDTO result = service.resolveAlert("ALT-123", command);

        // Then
        assertNotNull(result);
        verify(alertRepository).save(any(Alert.class));
        verify(eventPublisher).publishAlertResolved(any(Alert.class));
    }

    @Test
    @DisplayName("Should close alert successfully")
    void testCloseAlert() {
        // Given
        Alert alert = Alert.builder()
            .alertId("ALT-123")
            .tenantId("tenant-001")
            .status(Alert.AlertStatus.RESOLVED)
            .resolvedBy("provider-456")
            .build();

        when(alertRepository.findByAlertId("ALT-123")).thenReturn(Optional.of(alert));
        when(alertRepository.save(any(Alert.class))).thenReturn(alert);

        // When
        AlertDTO result = service.closeAlert("ALT-123");

        // Then
        assertNotNull(result);
        verify(alertRepository).save(any(Alert.class));
        verify(eventPublisher).publishAlertClosed(any(Alert.class));
    }

    @Test
    @DisplayName("Should delete alert successfully")
    void testDeleteAlert() {
        // Given
        Alert alert = Alert.builder()
            .alertId("ALT-123")
            .tenantId("tenant-001")
            .build();

        when(alertRepository.findByAlertId("ALT-123")).thenReturn(Optional.of(alert));
        doNothing().when(alertRepository).deleteByAlertId("ALT-123");

        // When
        service.deleteAlert("ALT-123");

        // Then
        verify(alertRepository).deleteByAlertId("ALT-123");
    }

    @Test
    @DisplayName("Should get alert by ID successfully")
    void testGetAlert() {
        // Given
        Alert alert = Alert.builder()
            .alertId("ALT-123")
            .tenantId("tenant-001")
            .build();

        when(alertRepository.findByAlertId("ALT-123")).thenReturn(Optional.of(alert));

        // When
        AlertDTO result = service.getAlert(
            com.gogidix.rapidassist.orchestration.alerting_service.application.query.GetAlertQuery.builder()
                .alertId("ALT-123")
                .tenantId("tenant-001")
                .build()
        );

        // Then
        assertNotNull(result);
        assertEquals("ALT-123", result.getAlertId());
    }

    @Test
    @DisplayName("Should throw exception when getting alert with wrong tenant")
    void testGetAlertWrongTenant() {
        // Given
        Alert alert = Alert.builder()
            .alertId("ALT-123")
            .tenantId("tenant-999")
            .build();

        when(alertRepository.findByAlertId("ALT-123")).thenReturn(Optional.of(alert));

        // When & Then
        assertThrows(NotFoundException.class, () ->
            service.getAlert(
                com.gogidix.rapidassist.orchestration.alerting_service.application.query.GetAlertQuery.builder()
                    .alertId("ALT-123")
                    .tenantId("tenant-001")
                    .build()
            )
        );
    }

    @Test
    @DisplayName("Should get alerts by request ID")
    void testGetAlertsByRequestId() {
        // Given
        Alert alert = Alert.builder()
            .alertId("ALT-123")
            .requestId("REQ-456")
            .tenantId("tenant-001")
            .build();

        when(alertRepository.findByRequestId("REQ-456")).thenReturn(List.of(alert));

        // When
        List<AlertDTO> results = service.getAlertsByRequestId("REQ-456");

        // Then
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("REQ-456", results.get(0).getRequestId());
    }

    @Test
    @DisplayName("Should get active alerts for tenant")
    void testGetActiveAlertsByTenant() {
        // Given
        Alert alert = Alert.builder()
            .alertId("ALT-123")
            .tenantId("tenant-001")
            .status(Alert.AlertStatus.IN_PROGRESS)
            .build();

        when(alertRepository.findByTenantIdAndStatusIn(eq("tenant-001"), anyList()))
            .thenReturn(List.of(alert));

        // When
        List<AlertDTO> results = service.getActiveAlertsByTenant("tenant-001");

        // Then
        assertNotNull(results);
        assertEquals(1, results.size());
    }

    @Test
    @DisplayName("Should get critical alerts")
    void testGetCriticalAlerts() {
        // Given
        Alert alert = Alert.builder()
            .alertId("ALT-123")
            .tenantId("tenant-001")
            .severity(Alert.AlertSeverity.CRITICAL)
            .status(Alert.AlertStatus.PENDING)
            .build();

        when(alertRepository.findBySeverityAndStatusIn(eq(Alert.AlertSeverity.CRITICAL), anyList()))
            .thenReturn(List.of(alert));

        // When
        List<AlertDTO> results = service.getCriticalAlerts();

        // Then
        assertNotNull(results);
        assertEquals(1, results.size());
    }

    @Test
    @DisplayName("Should get escalation required alerts")
    void testGetEscalationRequiredAlerts() {
        // Given
        Alert alert = Alert.builder()
            .alertId("ALT-123")
            .tenantId("tenant-001")
            .escalationRequired(true)
            .build();

        when(alertRepository.findEscalationRequiredAlerts()).thenReturn(List.of(alert));

        // When
        List<AlertDTO> results = service.getEscalationRequiredAlerts();

        // Then
        assertNotNull(results);
        assertEquals(1, results.size());
    }
}
