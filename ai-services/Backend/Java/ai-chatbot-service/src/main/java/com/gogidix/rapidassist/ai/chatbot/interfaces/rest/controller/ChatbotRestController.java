
package com.gogidix.rapidassist.ai.chatbot.interfaces.rest.controller;



import com.gogidix.rapidassist.ai.chatbot.application.command.*;

import com.gogidix.rapidassist.ai.chatbot.application.dto.ChatbotSessionDto;

import com.gogidix.rapidassist.ai.chatbot.application.service.ChatbotSessionApplicationService;

import com.gogidix.rapidassist.ai.chatbot.domain.tenant.TenantContext;

import com.gogidix.rapidassist.ai.chatbot.interfaces.rest.request.*;

import com.gogidix.rapidassist.ai.chatbot.interfaces.rest.response.ChatbotSessionResponse;

import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.Parameter;

import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;



import java.util.List;

import java.util.UUID;



/**

 * REST Controller for Chatbot Session operations.

 * API endpoint: /api/v1/chatbot

 */






@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Chatbot", description = "Chatbot Session APIs")
public class ChatbotRestController {

    private final ChatbotSessionApplicationService applicationService;

    /**
     * Create a new chatbot session.
     * POST /api/v1/chatbot/sessions
     */
    @PostMapping("/sessions")
    @Operation(summary = "Create chatbot session", description = "Creates a new chatbot session for a user")
    public ResponseEntity<ChatbotSessionResponse> createSession(
            @Valid @RequestBody CreateSessionRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        var command = CreateChatbotSessionCommand.builder()
                .tenantId(tenantId)
                .userId(request.getUserId())
                .channel(request.getChannel())
                .metadata(request.getMetadata())
                .build();

        ChatbotSessionDto sessionDto = applicationService.createSession(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(sessionDto));
    }

    /**
     * Get a chatbot session by ID.
     * GET /api/v1/chatbot/sessions/{sessionId}
     */
    @GetMapping("/sessions/{sessionId}")
    @Operation(summary = "Get chatbot session", description = "Retrieves a chatbot session by ID")
    public ResponseEntity<ChatbotSessionResponse> getSession(
            @PathVariable UUID sessionId,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Include messages in response") @RequestParam(defaultValue = "false") Boolean includeMessages,
            @Parameter(description = "Include contexts in response") @RequestParam(defaultValue = "false") Boolean includeContexts,
            @Parameter(description = "Include flows in response") @RequestParam(defaultValue = "false") Boolean includeFlows) {

        TenantContext.setTenantId(tenantId);

        var query = com.gogidix.rapidassist.ai.chatbot.application.query.GetChatbotSessionQuery.builder()
                .tenantId(tenantId)
                .sessionId(sessionId)
                .includeMessages(includeMessages)
                .includeContexts(includeContexts)
                .includeFlows(includeFlows)
                .build();

        ChatbotSessionDto sessionDto = applicationService.getSession(query);

        return ResponseEntity.ok(toResponse(sessionDto));
    }

    /**
     * Get all sessions for a user.
     * GET /api/v1/chatbot/sessions/user/{userId}
     */
    @GetMapping("/sessions/user/{userId}")
    @Operation(summary = "Get user sessions", description = "Retrieves all chatbot sessions for a user")
    public ResponseEntity<List<ChatbotSessionResponse>> getUserSessions(
            @PathVariable String userId,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Filter by status") @RequestParam(required = false) String status,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "Sort by") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDirection) {

        TenantContext.setTenantId(tenantId);

        var query = com.gogidix.rapidassist.ai.chatbot.application.query.GetUserSessionsQuery.builder()
                .tenantId(tenantId)
                .userId(userId)
                .status(status)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();

        List<ChatbotSessionDto> sessions = applicationService.getUserSessions(query);

        return ResponseEntity.ok(sessions.stream()
                .map(this::toResponse)
                .toList());
    }

    /**
     * Send a message to a session.
     * POST /api/v1/chatbot/sessions/{sessionId}/messages
     */
    @PostMapping("/sessions/{sessionId}/messages")
    @Operation(summary = "Send message", description = "Sends a message to a chatbot session")
    public ResponseEntity<ChatbotSessionResponse> sendMessage(
            @PathVariable UUID sessionId,
            @Valid @RequestBody SendMessageRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        var command = SendMessageCommand.builder()
                .tenantId(tenantId)
                .sessionId(sessionId)
                .content(request.getContent())
                .direction(request.getDirection())
                .messageType(request.getMessageType())
                .metadata(request.getMetadata())
                .build();

        ChatbotSessionDto sessionDto = applicationService.sendMessage(command);

        return ResponseEntity.ok(toResponse(sessionDto));
    }

    /**
     * Update session status.
     * PATCH /api/v1/chatbot/sessions/{sessionId}/status
     */
    @PatchMapping("/sessions/{sessionId}/status")
    @Operation(summary = "Update session status", description = "Updates the status of a chatbot session")
    public ResponseEntity<ChatbotSessionResponse> updateStatus(
            @PathVariable UUID sessionId,
            @Valid @RequestBody UpdateStatusRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        var command = UpdateSessionStatusCommand.builder()
                .tenantId(tenantId)
                .sessionId(sessionId)
                .status(request.getStatus())
                .reason(request.getReason())
                .build();

        ChatbotSessionDto sessionDto = applicationService.updateStatus(command);

        return ResponseEntity.ok(toResponse(sessionDto));
    }

    /**
     * Add context to a session.
     * POST /api/v1/chatbot/sessions/{sessionId}/contexts
     */
    @PostMapping("/sessions/{sessionId}/contexts")
    @Operation(summary = "Add context", description = "Adds context to a chatbot session")
    public ResponseEntity<ChatbotSessionResponse> addContext(
            @PathVariable UUID sessionId,
            @Valid @RequestBody AddContextRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        var command = AddContextCommand.builder()
                .tenantId(tenantId)
                .sessionId(sessionId)
                .contextKey(request.getContextKey())
                .contextValue(request.getContextValue())
                .contextType(request.getContextType())
                .additionalContext(request.getAdditionalContext())
                .ttl(request.getTtl())
                .build();

        ChatbotSessionDto sessionDto = applicationService.addContext(command);

        return ResponseEntity.ok(toResponse(sessionDto));
    }

    /**
     * Delete a session.
     * DELETE /api/v1/chatbot/sessions/{sessionId}
     */
    @DeleteMapping("/sessions/{sessionId}")
    @Operation(summary = "Delete session", description = "Deletes a chatbot session")
    public ResponseEntity<Void> deleteSession(
            @PathVariable UUID sessionId,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        applicationService.deleteSession(tenantId, sessionId);

        return ResponseEntity.noContent().build();
    }

    /**
     * Get active sessions for a tenant.
     * GET /api/v1/chatbot/sessions/active
     */
    @GetMapping("/sessions/active")
    @Operation(summary = "Get active sessions", description = "Retrieves all active chatbot sessions for a tenant")
    public ResponseEntity<List<ChatbotSessionResponse>> getActiveSessions(
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        List<ChatbotSessionDto> sessions = applicationService.getActiveSessions(tenantId);

        return ResponseEntity.ok(sessions.stream()
                .map(this::toResponse)
                .toList());
    }

    /**
     * Process idle sessions.
     * POST /api/v1/chatbot/sessions/process-idle
     */
    @PostMapping("/sessions/process-idle")
    @Operation(summary = "Process idle sessions", description = "Processes idle sessions for auto-completion or termination")
    public ResponseEntity<Integer> processIdleSessions(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Idle threshold in minutes") @RequestParam(defaultValue = "30") Integer idleThresholdMinutes) {

        TenantContext.setTenantId(tenantId);

        int processedCount = applicationService.processIdleSessions(tenantId, idleThresholdMinutes);

        return ResponseEntity.ok(processedCount);
    }

    /**
     * Convert DTO to Response.
     */
    private ChatbotSessionResponse toResponse(ChatbotSessionDto dto) {
        return ChatbotSessionResponse.builder()
                .id(dto.getId())
                .tenantId(dto.getTenantId())
                .userId(dto.getUserId())
                .sessionId(dto.getSessionId())
                .status(dto.getStatus())
                .channel(dto.getChannel())
                .metadata(dto.getMetadata())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .lastActivityAt(dto.getLastActivityAt())
                .createdBy(dto.getCreatedBy())
                .updatedBy(dto.getUpdatedBy())
                .messages(dto.getMessages())
                .contexts(dto.getContexts())
                .flows(dto.getFlows())
                .messageCount(dto.getMessageCount())
                .durationSeconds(dto.getDurationSeconds())
                .currentFlowState(dto.getCurrentFlowState())
                .healthScore(dto.getHealthScore())
                .recommendedAction(dto.getRecommendedAction())
                .build();
    }
}
