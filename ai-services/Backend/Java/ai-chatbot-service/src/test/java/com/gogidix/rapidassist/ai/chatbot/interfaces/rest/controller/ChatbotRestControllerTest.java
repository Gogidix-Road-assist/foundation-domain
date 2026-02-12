package com.gogidix.rapidassist.ai.chatbot.interfaces.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.ai.chatbot.application.dto.ChatbotSessionDto;
import com.gogidix.rapidassist.ai.chatbot.application.service.ChatbotSessionApplicationService;
import com.gogidix.rapidassist.ai.chatbot.domain.model.MessageDirection;
import com.gogidix.rapidassist.ai.chatbot.domain.model.MessageType;
import com.gogidix.rapidassist.ai.chatbot.domain.model.SessionStatus;
import com.gogidix.rapidassist.ai.chatbot.domain.tenant.TenantContext;
import com.gogidix.rapidassist.ai.chatbot.interfaces.rest.request.AddContextRequest;
import com.gogidix.rapidassist.ai.chatbot.interfaces.rest.request.CreateSessionRequest;
import com.gogidix.rapidassist.ai.chatbot.interfaces.rest.request.SendMessageRequest;
import com.gogidix.rapidassist.ai.chatbot.interfaces.rest.request.UpdateStatusRequest;
import com.gogidix.rapidassist.ai.chatbot.interfaces.rest.response.ChatbotSessionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Comprehensive unit tests for ChatbotRestController.
 * Tests all REST endpoints, request/response handling, and error scenarios.
 */
@WebMvcTest(ChatbotRestController.class)
@DisplayName("ChatbotRestController Tests")
class ChatbotRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ChatbotSessionApplicationService applicationService;

    private String tenantId = "tenant-123";
    private String userId = "user-456";
    private UUID sessionId = UUID.randomUUID();
    private ChatbotSessionDto sessionDto;
    private ChatbotSessionResponse sessionResponse;

    @BeforeEach
    void setUp() {
        TenantContext.setTenantId(tenantId);

        sessionDto = new ChatbotSessionDto();
        sessionDto.setId(sessionId);
        sessionDto.setTenantId(tenantId);
        sessionDto.setUserId(userId);
        sessionDto.setSessionId(UUID.randomUUID().toString());
        sessionDto.setStatus(SessionStatus.INITIALIZED);
        sessionDto.setChannel("WEB");
        sessionDto.setCreatedAt(LocalDateTime.now());
        sessionDto.setUpdatedAt(LocalDateTime.now());
        sessionDto.setLastActivityAt(LocalDateTime.now());
        sessionDto.setMessageCount(0);
        sessionDto.setDurationSeconds(0L);

        sessionResponse = ChatbotSessionResponse.builder()
            .id(sessionDto.getId())
            .tenantId(sessionDto.getTenantId())
            .userId(sessionDto.getUserId())
            .sessionId(sessionDto.getSessionId())
            .status(sessionDto.getStatus())
            .channel(sessionDto.getChannel())
            .createdAt(sessionDto.getCreatedAt())
            .updatedAt(sessionDto.getUpdatedAt())
            .lastActivityAt(sessionDto.getLastActivityAt())
            .messageCount(sessionDto.getMessageCount())
            .durationSeconds(sessionDto.getDurationSeconds())
            .build();
    }

    @Test
    @DisplayName("POST /sessions - Should create new session")
    void shouldCreateNewSession() throws Exception {
        // Arrange
        CreateSessionRequest request = new CreateSessionRequest();
        request.setUserId(userId);
        request.setChannel("WEB");
        request.setMetadata(Map.of("key", "value"));

        when(applicationService.createSession(any())).thenReturn(sessionDto);

        // Act & Assert
        mockMvc.perform(post("/api/v1/chatbot/sessions")
                .header("X-Tenant-ID", tenantId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(sessionId.toString()))
                .andExpect(jsonPath("$.tenantId").value(tenantId))
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.status").value("INITIALIZED"))
                .andExpect(jsonPath("$.channel").value("WEB"));

        verify(applicationService).createSession(any());
    }

    @Test
    @DisplayName("POST /sessions - Should return 400 for invalid request")
    void shouldReturnBadRequestForInvalidCreateRequest() throws Exception {
        // Arrange
        CreateSessionRequest request = new CreateSessionRequest();
        // Missing required fields

        // Act & Assert
        mockMvc.perform(post("/api/v1/chatbot/sessions")
                .header("X-Tenant-ID", tenantId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(applicationService, never()).createSession(any());
    }

    @Test
    @DisplayName("GET /sessions/{sessionId} - Should get session by ID")
    void shouldGetSessionById() throws Exception {
        // Arrange
        when(applicationService.getSession(any())).thenReturn(sessionDto);

        // Act & Assert
        mockMvc.perform(get("/api/v1/chatbot/sessions/{sessionId}", sessionId)
                .header("X-Tenant-ID", tenantId)
                .param("includeMessages", "false")
                .param("includeContexts", "false")
                .param("includeFlows", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sessionId.toString()))
                .andExpect(jsonPath("$.tenantId").value(tenantId))
                .andExpect(jsonPath("$.userId").value(userId));

        verify(applicationService).getSession(any());
    }

    @Test
    @DisplayName("GET /sessions/user/{userId} - Should get user sessions")
    void shouldGetUserSessions() throws Exception {
        // Arrange
        List<ChatbotSessionDto> sessions = List.of(sessionDto);

        when(applicationService.getUserSessions(any())).thenReturn(sessions);

        // Act & Assert
        mockMvc.perform(get("/api/v1/chatbot/sessions/user/{userId}", userId)
                .header("X-Tenant-ID", tenantId)
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].userId").value(userId));

        verify(applicationService).getUserSessions(any());
    }

    @Test
    @DisplayName("GET /sessions/user/{userId} - Should filter by status")
    void shouldGetUserSessionsFilteredByStatus() throws Exception {
        // Arrange
        List<ChatbotSessionDto> sessions = List.of(sessionDto);

        when(applicationService.getUserSessions(any())).thenReturn(sessions);

        // Act & Assert
        mockMvc.perform(get("/api/v1/chatbot/sessions/user/{userId}", userId)
                .header("X-Tenant-ID", tenantId)
                .param("status", "ACTIVE")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(applicationService).getUserSessions(any());
    }

    @Test
    @DisplayName("POST /sessions/{sessionId}/messages - Should send message")
    void shouldSendMessage() throws Exception {
        // Arrange
        SendMessageRequest request = new SendMessageRequest();
        request.setContent("Hello");
        request.setDirection(MessageDirection.INBOUND);
        request.setMessageType("USER");

        sessionDto.setStatus(SessionStatus.ACTIVE);

        when(applicationService.sendMessage(any())).thenReturn(sessionDto);

        // Act & Assert
        mockMvc.perform(post("/api/v1/chatbot/sessions/{sessionId}/messages", sessionId)
                .header("X-Tenant-ID", tenantId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sessionId.toString()))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(applicationService).sendMessage(any());
    }

    @Test
    @DisplayName("POST /sessions/{sessionId}/messages - Should return 400 for invalid message")
    void shouldReturnBadRequestForInvalidMessage() throws Exception {
        // Arrange
        SendMessageRequest request = new SendMessageRequest();
        request.setContent(""); // Invalid: empty content

        // Act & Assert
        mockMvc.perform(post("/api/v1/chatbot/sessions/{sessionId}/messages", sessionId)
                .header("X-Tenant-ID", tenantId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(applicationService, never()).sendMessage(any());
    }

    @Test
    @DisplayName("PATCH /sessions/{sessionId}/status - Should update session status")
    void shouldUpdateSessionStatus() throws Exception {
        // Arrange
        UpdateStatusRequest request = new UpdateStatusRequest();
        request.setStatus(SessionStatus.ACTIVE);

        sessionDto.setStatus(SessionStatus.ACTIVE);

        when(applicationService.updateStatus(any())).thenReturn(sessionDto);

        // Act & Assert
        mockMvc.perform(patch("/api/v1/chatbot/sessions/{sessionId}/status", sessionId)
                .header("X-Tenant-ID", tenantId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(applicationService).updateStatus(any());
    }

    @Test
    @DisplayName("PATCH /sessions/{sessionId}/status - Should update status with reason")
    void shouldUpdateSessionStatusWithReason() throws Exception {
        // Arrange
        UpdateStatusRequest request = new UpdateStatusRequest();
        request.setStatus(SessionStatus.COMPLETED);
        request.setReason("Test completion");

        sessionDto.setStatus(SessionStatus.COMPLETED);

        when(applicationService.updateStatus(any())).thenReturn(sessionDto);

        // Act & Assert
        mockMvc.perform(patch("/api/v1/chatbot/sessions/{sessionId}/status", sessionId)
                .header("X-Tenant-ID", tenantId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        verify(applicationService).updateStatus(any());
    }

    @Test
    @DisplayName("POST /sessions/{sessionId}/contexts - Should add context")
    void shouldAddContext() throws Exception {
        // Arrange
        AddContextRequest request = new AddContextRequest();
        request.setContextKey("test-key");
        request.setContextValue("test-value");
        request.setContextType("USER_DEFINED");
        request.setTtl(3600);

        when(applicationService.addContext(any())).thenReturn(sessionDto);

        // Act & Assert
        mockMvc.perform(post("/api/v1/chatbot/sessions/{sessionId}/contexts", sessionId)
                .header("X-Tenant-ID", tenantId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sessionId.toString()));

        verify(applicationService).addContext(any());
    }

    @Test
    @DisplayName("POST /sessions/{sessionId}/contexts - Should return 400 for invalid context")
    void shouldReturnBadRequestForInvalidContext() throws Exception {
        // Arrange
        AddContextRequest request = new AddContextRequest();
        // Missing required fields

        // Act & Assert
        mockMvc.perform(post("/api/v1/chatbot/sessions/{sessionId}/contexts", sessionId)
                .header("X-Tenant-ID", tenantId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(applicationService, never()).addContext(any());
    }

    @Test
    @DisplayName("DELETE /sessions/{sessionId} - Should delete session")
    void shouldDeleteSession() throws Exception {
        // Arrange
        doNothing().when(applicationService).deleteSession(tenantId, sessionId);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/chatbot/sessions/{sessionId}", sessionId)
                .header("X-Tenant-ID", tenantId))
                .andExpect(status().isNoContent());

        verify(applicationService).deleteSession(tenantId, sessionId);
    }

    @Test
    @DisplayName("GET /sessions/active - Should get active sessions")
    void shouldGetActiveSessions() throws Exception {
        // Arrange
        List<ChatbotSessionDto> activeSessions = List.of(sessionDto);

        when(applicationService.getActiveSessions(tenantId)).thenReturn(activeSessions);

        // Act & Assert
        mockMvc.perform(get("/api/v1/chatbot/sessions/active")
                .header("X-Tenant-ID", tenantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(applicationService).getActiveSessions(tenantId);
    }

    @Test
    @DisplayName("GET /sessions/active - Should return empty list when no active sessions")
    void shouldReturnEmptyListWhenNoActiveSessions() throws Exception {
        // Arrange
        when(applicationService.getActiveSessions(tenantId)).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/v1/chatbot/sessions/active")
                .header("X-Tenant-ID", tenantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(applicationService).getActiveSessions(tenantId);
    }

    @Test
    @DisplayName("POST /sessions/process-idle - Should process idle sessions")
    void shouldProcessIdleSessions() throws Exception {
        // Arrange
        int processedCount = 5;
        when(applicationService.processIdleSessions(tenantId, 30)).thenReturn(processedCount);

        // Act & Assert
        mockMvc.perform(post("/api/v1/chatbot/sessions/process-idle")
                .header("X-Tenant-ID", tenantId)
                .param("idleThresholdMinutes", "30"))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(processedCount)));

        verify(applicationService).processIdleSessions(tenantId, 30);
    }

    @Test
    @DisplayName("POST /sessions/process-idle - Should use default threshold")
    void shouldUseDefaultIdleThreshold() throws Exception {
        // Arrange
        when(applicationService.processIdleSessions(tenantId, 30)).thenReturn(0);

        // Act & Assert
        mockMvc.perform(post("/api/v1/chatbot/sessions/process-idle")
                .header("X-Tenant-ID", tenantId))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));

        verify(applicationService).processIdleSessions(tenantId, 30);
    }

    @Test
    @DisplayName("GET /sessions/{sessionId} - Should handle include parameters")
    void shouldHandleIncludeParameters() throws Exception {
        // Arrange
        sessionDto.setMessages(List.of());
        sessionDto.setContexts(List.of());
        sessionDto.setFlows(List.of());

        when(applicationService.getSession(any())).thenReturn(sessionDto);

        // Act & Assert
        mockMvc.perform(get("/api/v1/chatbot/sessions/{sessionId}", sessionId)
                .header("X-Tenant-ID", tenantId)
                .param("includeMessages", "true")
                .param("includeContexts", "true")
                .param("includeFlows", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sessionId.toString()));

        verify(applicationService).getSession(any());
    }

    @Test
    @DisplayName("POST /sessions/{sessionId}/messages - Should handle message with metadata")
    void shouldHandleMessageWithMetadata() throws Exception {
        // Arrange
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("lang", "en");
        metadata.put("source", "web");

        SendMessageRequest request = new SendMessageRequest();
        request.setContent("Hello");
        request.setDirection(MessageDirection.INBOUND);
        request.setMessageType("USER");
        request.setMetadata(metadata);

        when(applicationService.sendMessage(any())).thenReturn(sessionDto);

        // Act & Assert
        mockMvc.perform(post("/api/v1/chatbot/sessions/{sessionId}/messages", sessionId)
                .header("X-Tenant-ID", tenantId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(applicationService).sendMessage(argThat(cmd ->
            cmd.getMetadata() != null &&
            cmd.getMetadata().size() == 2 &&
            "en".equals(cmd.getMetadata().get("lang"))
        ));
    }

    @Test
    @DisplayName("POST /sessions - Should handle session creation with metadata")
    void shouldHandleSessionCreationWithMetadata() throws Exception {
        // Arrange
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("source", "mobile");
        metadata.put("campaign", "summer-2024");

        CreateSessionRequest request = new CreateSessionRequest();
        request.setUserId(userId);
        request.setChannel("MOBILE");
        request.setMetadata(metadata);

        when(applicationService.createSession(any())).thenReturn(sessionDto);

        // Act & Assert
        mockMvc.perform(post("/api/v1/chatbot/sessions")
                .header("X-Tenant-ID", tenantId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(applicationService).createSession(argThat(cmd ->
            cmd.getMetadata() != null &&
            cmd.getMetadata().size() == 2 &&
            "mobile".equals(cmd.getMetadata().get("source"))
        ));
    }

    @Test
    @DisplayName("GET /sessions/user/{userId} - Should handle pagination")
    void shouldHandlePagination() throws Exception {
        // Arrange
        List<ChatbotSessionDto> sessions = List.of(sessionDto);

        when(applicationService.getUserSessions(any())).thenReturn(sessions);

        // Act & Assert
        mockMvc.perform(get("/api/v1/chatbot/sessions/user/{userId}", userId)
                .header("X-Tenant-ID", tenantId)
                .param("page", "2")
                .param("size", "50")
                .param("sortBy", "createdAt")
                .param("sortDirection", "asc"))
                .andExpect(status().isOk());

        verify(applicationService).getUserSessions(argThat(query ->
            query.getPage() == 2 &&
            query.getSize() == 50 &&
            "createdAt".equals(query.getSortBy()) &&
            "asc".equals(query.getSortDirection())
        ));
    }
}
