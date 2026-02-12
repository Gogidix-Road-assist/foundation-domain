package com.gogidix.rapidassist.orchestration.alerting_service.interfaces.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.orchestration.alerting_service.application.command.AcknowledgeAlertCommand;
import com.gogidix.rapidassist.orchestration.alerting_service.application.command.CreateAlertCommand;
import com.gogidix.rapidassist.orchestration.alerting_service.application.dto.AlertDTO;
import com.gogidix.rapidassist.orchestration.alerting_service.domain.model.Alert;
import com.gogidix.rapidassist.orchestration.alerting_service.domain.port.input.AlertServicePort;
import com.gogidix.rapidassist.orchestration.alerting_service.shared.requestcontext.RequestContext;
import com.gogidix.rapidassist.orchestration.alerting_service.shared.requestcontext.RequestContextHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for AlertController
 */
@WebMvcTest(AlertController.class)
@ActiveProfiles("test")
class AlertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AlertServicePort alertService;

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
    }

    @AfterEach
    void tearDown() {
        requestContextHolderMock.close();
    }

    @Test
    @DisplayName("Should create alert successfully")
    void testCreateAlert() throws Exception {
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

        AlertDTO response = AlertDTO.builder()
            .alertId("ALT-123")
            .requestId("REQ-123")
            .tenantId("tenant-001")
            .type(Alert.AlertType.EMERGENCY)
            .severity(Alert.AlertSeverity.CRITICAL)
            .title("Emergency")
            .status(Alert.AlertStatus.PENDING)
            .createdAt(LocalDateTime.now())
            .build();

        when(alertService.createAlert(any(CreateAlertCommand.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/v1/alerts")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Tenant-ID", "tenant-001")
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.alertId").value("ALT-123"))
                .andExpect(jsonPath("$.requestId").value("REQ-123"))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(alertService).createAlert(any(CreateAlertCommand.class));
    }

    @Test
    @DisplayName("Should get alert by ID successfully")
    void testGetAlert() throws Exception {
        // Given
        AlertDTO alert = AlertDTO.builder()
            .alertId("ALT-123")
            .requestId("REQ-123")
            .tenantId("tenant-001")
            .type(Alert.AlertType.EMERGENCY)
            .severity(Alert.AlertSeverity.CRITICAL)
            .title("Emergency")
            .status(Alert.AlertStatus.PENDING)
            .build();

        when(alertService.getAlert(any())).thenReturn(alert);

        // When & Then
        mockMvc.perform(get("/api/v1/alerts/ALT-123")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Tenant-ID", "tenant-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.alertId").value("ALT-123"))
                .andExpect(jsonPath("$.requestId").value("REQ-123"));

        verify(alertService).getAlert(any());
    }

    @Test
    @DisplayName("Should list alerts successfully")
    void testListAlerts() throws Exception {
        // Given
        AlertDTO alert = AlertDTO.builder()
            .alertId("ALT-123")
            .requestId("REQ-123")
            .tenantId("tenant-001")
            .type(Alert.AlertType.EMERGENCY)
            .status(Alert.AlertStatus.PENDING)
            .build();

        when(alertService.listAlerts(any())).thenReturn(List.of(alert));

        // When & Then
        mockMvc.perform(get("/api/v1/alerts")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Tenant-ID", "tenant-001")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].alertId").value("ALT-123"));

        verify(alertService).listAlerts(any());
    }

    @Test
    @DisplayName("Should acknowledge alert successfully")
    void testAcknowledgeAlert() throws Exception {
        // Given
        AcknowledgeAlertCommand command = AcknowledgeAlertCommand.builder()
            .acknowledgedBy("user-123")
            .assignedTo("provider-456")
            .build();

        AlertDTO response = AlertDTO.builder()
            .alertId("ALT-123")
            .status(Alert.AlertStatus.ACKNOWLEDGED)
            .acknowledgedBy("user-123")
            .assignedTo("provider-456")
            .acknowledgedAt(LocalDateTime.now())
            .build();

        when(alertService.acknowledgeAlert(eq("ALT-123"), any(AcknowledgeAlertCommand.class)))
            .thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/v1/alerts/ALT-123/acknowledge")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Tenant-ID", "tenant-001")
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACKNOWLEDGED"))
                .andExpect(jsonPath("$.acknowledgedBy").value("user-123"))
                .andExpect(jsonPath("$.assignedTo").value("provider-456"));

        verify(alertService).acknowledgeAlert(eq("ALT-123"), any(AcknowledgeAlertCommand.class));
    }

    @Test
    @DisplayName("Should resolve alert successfully")
    void testResolveAlert() throws Exception {
        // Given
        var command = com.gogidix.rapidassist.orchestration.alerting_service.application.command.ResolveAlertCommand.builder()
            .resolvedBy("provider-456")
            .resolutionNotes("Issue fixed")
            .build();

        AlertDTO response = AlertDTO.builder()
            .alertId("ALT-123")
            .status(Alert.AlertStatus.RESOLVED)
            .resolvedBy("provider-456")
            .resolutionNotes("Issue fixed")
            .resolvedAt(LocalDateTime.now())
            .build();

        when(alertService.resolveAlert(eq("ALT-123"), any()))
            .thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/v1/alerts/ALT-123/resolve")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Tenant-ID", "tenant-001")
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RESOLVED"))
                .andExpect(jsonPath("$.resolvedBy").value("provider-456"));

        verify(alertService).resolveAlert(eq("ALT-123"), any());
    }

    @Test
    @DisplayName("Should close alert successfully")
    void testCloseAlert() throws Exception {
        // Given
        AlertDTO response = AlertDTO.builder()
            .alertId("ALT-123")
            .status(Alert.AlertStatus.CLOSED)
            .build();

        when(alertService.closeAlert("ALT-123")).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/v1/alerts/ALT-123/close")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Tenant-ID", "tenant-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CLOSED"));

        verify(alertService).closeAlert("ALT-123");
    }

    @Test
    @DisplayName("Should delete alert successfully")
    void testDeleteAlert() throws Exception {
        // Given
        doNothing().when(alertService).deleteAlert("ALT-123");

        // When & Then
        mockMvc.perform(delete("/api/v1/alerts/ALT-123")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Tenant-ID", "tenant-001"))
                .andExpect(status().isNoContent());

        verify(alertService).deleteAlert("ALT-123");
    }

    @Test
    @DisplayName("Should get alerts by request ID successfully")
    void testGetAlertsByRequestId() throws Exception {
        // Given
        AlertDTO alert = AlertDTO.builder()
            .alertId("ALT-123")
            .requestId("REQ-456")
            .tenantId("tenant-001")
            .build();

        when(alertService.getAlertsByRequestId("REQ-456")).thenReturn(List.of(alert));

        // When & Then
        mockMvc.perform(get("/api/v1/alerts/request/REQ-456")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Tenant-ID", "tenant-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].requestId").value("REQ-456"));

        verify(alertService).getAlertsByRequestId("REQ-456");
    }

    @Test
    @DisplayName("Should get critical alerts successfully")
    void testGetCriticalAlerts() throws Exception {
        // Given
        AlertDTO alert = AlertDTO.builder()
            .alertId("ALT-123")
            .severity(Alert.AlertSeverity.CRITICAL)
            .status(Alert.AlertStatus.PENDING)
            .build();

        when(alertService.getCriticalAlerts()).thenReturn(List.of(alert));

        // When & Then
        mockMvc.perform(get("/api/v1/alerts/critical")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Tenant-ID", "tenant-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].severity").value("CRITICAL"));

        verify(alertService).getCriticalAlerts();
    }

    @Test
    @DisplayName("Should return health status")
    void testHealth() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/alerts/health")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Alerting service is healthy"));
    }
}
