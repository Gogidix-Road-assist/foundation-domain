package com.gogidix.rapidassist.orchestration.dispatching.interfaces.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.orchestration.dispatching.application.dto.*;
import com.gogidix.rapidassist.orchestration.dispatching.application.service.DispatchService;
import com.gogidix.rapidassist.orchestration.dispatching.domain.model.DispatchProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DispatchController.class)
class DispatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DispatchService dispatchService;

    private DispatchResponse testResponse;
    private CreateDispatchRequest createRequest;

    @BeforeEach
    void setUp() {
        createTestData();
    }

    private void createTestData() {
        testResponse = DispatchResponse.builder()
            .dispatchId("DSP-12345678")
            .requestId("REQ-001")
            .tenantId("tenant-001")
            .serviceType("TOWING")
            .status(DispatchResponse.DispatchStatusDTO.PENDING)
            .priority(DispatchResponse.DispatchPriorityDTO.HIGH)
            .location(DispatchResponse.LocationDTO.builder()
                .latitude(40.7128)
                .longitude(-74.0060)
                .address("New York, NY")
                .build())
            .assignmentMethod(DispatchResponse.AssignmentMethodDTO.AUTOMATIC)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        createRequest = CreateDispatchRequest.builder()
            .requestId("REQ-001")
            .tenantId("tenant-001")
            .serviceType("TOWING")
            .priority(CreateDispatchRequest.DispatchPriorityDTO.HIGH)
            .location(CreateDispatchRequest.LocationDTO.builder()
                .latitude(40.7128)
                .longitude(-74.0060)
                .address("New York, NY")
                .build())
            .assignmentMethod(CreateDispatchRequest.AssignmentMethodDTO.AUTOMATIC)
            .build();
    }

    @Test
    void createDispatch_ShouldReturnCreated() throws Exception {
        // Given
        when(dispatchService.createDispatch(any(CreateDispatchRequest.class)))
            .thenReturn(testResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/dispatches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dispatchId").value("DSP-12345678"))
                .andExpect(jsonPath("$.requestId").value("REQ-001"))
                .andExpect(jsonPath("$.tenantId").value("tenant-001"))
                .andExpect(jsonPath("$.serviceType").value("TOWING"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void createDispatch_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given
        CreateDispatchRequest invalidRequest = CreateDispatchRequest.builder()
            .requestId("") // Invalid: empty
            .tenantId("tenant-001")
            .serviceType("TOWING")
            .priority(CreateDispatchRequest.DispatchPriorityDTO.HIGH)
            .location(null) // Invalid: null
            .build();

        // When & Then
        mockMvc.perform(post("/api/v1/dispatches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getDispatch_ShouldReturnDispatch() throws Exception {
        // Given
        when(dispatchService.getDispatch("DSP-12345678")).thenReturn(testResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/dispatches/DSP-12345678"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dispatchId").value("DSP-12345678"))
                .andExpect(jsonPath("$.requestId").value("REQ-001"));
    }

    @Test
    void getDispatchesByRequest_ShouldReturnListOfDispatches() throws Exception {
        // Given
        when(dispatchService.getDispatchesByRequest("REQ-001"))
            .thenReturn(Collections.singletonList(testResponse));

        // When & Then
        mockMvc.perform(get("/api/v1/dispatches")
                .param("requestId", "REQ-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].requestId").value("REQ-001"));
    }

    @Test
    void getActiveDispatchesByTenant_ShouldReturnActiveDispatches() throws Exception {
        // Given
        when(dispatchService.getActiveDispatchesByTenant("tenant-001"))
            .thenReturn(Collections.singletonList(testResponse));

        // When & Then
        mockMvc.perform(get("/api/v1/dispatches/tenant/tenant-001/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tenantId").value("tenant-001"));
    }

    @Test
    void updateDispatchStatus_ShouldReturnUpdatedDispatch() throws Exception {
        // Given
        testResponse.setStatus(DispatchResponse.DispatchStatusDTO.IN_PROGRESS);
        UpdateDispatchStatusRequest updateRequest = UpdateDispatchStatusRequest.builder()
            .status(UpdateDispatchStatusRequest.DispatchStatusDTO.IN_PROGRESS)
            .performedBy("user-001")
            .notes("Started service")
            .build();

        when(dispatchService.updateDispatchStatus(eq("DSP-12345678"),
            any(UpdateDispatchStatusRequest.class))).thenReturn(testResponse);

        // When & Then
        mockMvc.perform(put("/api/v1/dispatches/DSP-12345678/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void assignProvider_ShouldReturnCreatedAssignment() throws Exception {
        // Given
        AssignmentResponse assignmentResponse = AssignmentResponse.builder()
            .assignmentId("assignment-id")
            .dispatchId("DSP-12345678")
            .providerId("PROV-001")
            .status(AssignmentResponse.AssignmentStatusDTO.PENDING)
            .assignedAt(LocalDateTime.now())
            .build();

        ProviderAssignmentRequest assignmentRequest = ProviderAssignmentRequest.builder()
            .providerId("PROV-001")
            .assignmentScore(95.0)
            .estimatedDistance(5000.0)
            .estimatedDuration(1800)
            .build();

        when(dispatchService.assignProvider(eq("DSP-12345678"),
            any(ProviderAssignmentRequest.class))).thenReturn(assignmentResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/dispatches/DSP-12345678/assignments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assignmentRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.providerId").value("PROV-001"))
                .andExpect(jsonPath("$.dispatchId").value("DSP-12345678"));
    }

    @Test
    void getAssignments_ShouldReturnListOfAssignments() throws Exception {
        // Given
        AssignmentResponse assignmentResponse = AssignmentResponse.builder()
            .assignmentId("assignment-id")
            .dispatchId("DSP-12345678")
            .providerId("PROV-001")
            .status(AssignmentResponse.AssignmentStatusDTO.ACCEPTED)
            .build();

        when(dispatchService.getAssignmentsByDispatch("DSP-12345678"))
            .thenReturn(Collections.singletonList(assignmentResponse));

        // When & Then
        mockMvc.perform(get("/api/v1/dispatches/DSP-12345678/assignments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].dispatchId").value("DSP-12345678"));
    }

    @Test
    void acceptAssignment_ShouldReturnAcceptedAssignment() throws Exception {
        // Given
        AssignmentResponse assignmentResponse = AssignmentResponse.builder()
            .assignmentId("assignment-id")
            .dispatchId("DSP-12345678")
            .providerId("PROV-001")
            .status(AssignmentResponse.AssignmentStatusDTO.ACCEPTED)
            .build();

        when(dispatchService.acceptAssignment("DSP-12345678", "PROV-001"))
            .thenReturn(assignmentResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/dispatches/DSP-12345678/assignments/PROV-001/accept"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"));
    }

    @Test
    void rejectAssignment_ShouldReturnRejectedAssignment() throws Exception {
        // Given
        AssignmentResponse assignmentResponse = AssignmentResponse.builder()
            .assignmentId("assignment-id")
            .dispatchId("DSP-12345678")
            .providerId("PROV-001")
            .status(AssignmentResponse.AssignmentStatusDTO.REJECTED)
            .build();

        when(dispatchService.rejectAssignment("DSP-12345678", "PROV-001", "Too far"))
            .thenReturn(assignmentResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/dispatches/DSP-12345678/assignments/PROV-001/reject")
                .param("reason", "Too far"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));
    }

    @Test
    void getProviderAssignments_ShouldReturnProviderAssignments() throws Exception {
        // Given
        AssignmentResponse assignmentResponse = AssignmentResponse.builder()
            .assignmentId("assignment-id")
            .dispatchId("DSP-12345678")
            .providerId("PROV-001")
            .status(AssignmentResponse.AssignmentStatusDTO.PENDING)
            .build();

        when(dispatchService.getProviderAssignments("PROV-001"))
            .thenReturn(Collections.singletonList(assignmentResponse));

        // When & Then
        mockMvc.perform(get("/api/v1/dispatches/providers/PROV-001/assignments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].providerId").value("PROV-001"));
    }

    @Test
    void getProviderActiveDispatches_ShouldReturnActiveDispatches() throws Exception {
        // Given
        testResponse.setAssignedProviderId("PROV-001");
        testResponse.setStatus(DispatchResponse.DispatchStatusDTO.ASSIGNED);

        when(dispatchService.getProviderActiveDispatches("PROV-001"))
            .thenReturn(Collections.singletonList(testResponse));

        // When & Then
        mockMvc.perform(get("/api/v1/dispatches/providers/PROV-001/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].assignedProviderId").value("PROV-001"));
    }

    @Test
    void getAvailableProviders_ShouldReturnAvailableProviders() throws Exception {
        // Given
        DispatchProvider provider = DispatchProvider.builder()
            .id("provider-id")
            .providerId("PROV-001")
            .tenantId("tenant-001")
            .currentStatus(DispatchProvider.ProviderStatus.AVAILABLE)
            .build();

        when(dispatchService.getAvailableProviders("tenant-001"))
            .thenReturn(Collections.singletonList(provider));

        // When & Then
        mockMvc.perform(get("/api/v1/dispatches/providers/available")
                .param("tenantId", "tenant-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].providerId").value("PROV-001"));
    }

    @Test
    void updateProviderLocation_ShouldReturnOk() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/v1/dispatches/providers/PROV-001/location")
                .param("latitude", "40.7589")
                .param("longitude", "-73.9851")
                .param("address", "Times Square, NYC"))
                .andExpect(status().isOk());
    }
}
