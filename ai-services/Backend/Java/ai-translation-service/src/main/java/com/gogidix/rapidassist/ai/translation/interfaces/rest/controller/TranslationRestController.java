package com.gogidix.rapidassist.ai.translation.interfaces.rest.controller;

import com.gogidix.rapidassist.ai.translation.application.command.CreateTranslationSessionCommand;
import com.gogidix.rapidassist.ai.translation.application.command.TranslateTextCommand;
import com.gogidix.rapidassist.ai.translation.application.command.UpdateSessionStatusCommand;
import com.gogidix.rapidassist.ai.translation.application.dto.TranslationSessionDto;
import com.gogidix.rapidassist.ai.translation.application.query.GetTranslationSessionQuery;
import com.gogidix.rapidassist.ai.translation.application.query.GetUserSessionsQuery;
import com.gogidix.rapidassist.ai.translation.application.service.TranslationSessionApplicationService;
import com.gogidix.rapidassist.ai.translation.interfaces.rest.request.CreateSessionRequest;
import com.gogidix.rapidassist.ai.translation.interfaces.rest.request.TranslateTextRequest;
import com.gogidix.rapidassist.ai.translation.interfaces.rest.request.UpdateStatusRequest;
import com.gogidix.rapidassist.ai.translation.interfaces.rest.response.TranslationSessionResponse;
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
 * REST Controller for Translation operations.
 * API endpoint: /api/v1/translation
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Translation", description = "Translation Service APIs")
@RequestMapping("/api/v1/translation")
public class TranslationRestController {

    private final TranslationSessionApplicationService applicationService;

    /**
     * Create a new translation session.
     * POST /api/v1/translation/sessions
     */
    @PostMapping("/sessions")
    @Operation(summary = "Create translation session", description = "Creates a new translation session")
    public ResponseEntity<TranslationSessionResponse> createSession(
            @Valid @RequestBody CreateSessionRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        var command = CreateTranslationSessionCommand.builder()
                .tenantId(tenantId)
                .userId(request.getUserId())
                .sessionType(request.getSessionType())
                .sourceLanguage(request.getSourceLanguage())
                .targetLanguage(request.getTargetLanguage())
                .channel(request.getChannel())
                .metadata(request.getMetadata())
                .build();

        TranslationSessionDto sessionDto = applicationService.createSession(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(sessionDto));
    }

    /**
     * Get a translation session by ID.
     * GET /api/v1/translation/sessions/{sessionId}
     */
    @GetMapping("/sessions/{sessionId}")
    @Operation(summary = "Get translation session", description = "Retrieves a translation session by ID")
    public ResponseEntity<TranslationSessionResponse> getSession(
            @PathVariable UUID sessionId,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Include requests in response") @RequestParam(defaultValue = "true") Boolean includeRequests) {

        var query = GetTranslationSessionQuery.builder()
                .tenantId(tenantId)
                .sessionId(sessionId)
                .includeRequests(includeRequests)
                .build();

        TranslationSessionDto sessionDto = applicationService.getSession(query);

        return ResponseEntity.ok(toResponse(sessionDto));
    }

    /**
     * Get all sessions for a user.
     * GET /api/v1/translation/sessions/user/{userId}
     */
    @GetMapping("/sessions/user/{userId}")
    @Operation(summary = "Get user sessions", description = "Retrieves all translation sessions for a user")
    public ResponseEntity<List<TranslationSessionResponse>> getUserSessions(
            @PathVariable String userId,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Filter by status") @RequestParam(required = false) String status,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") Integer size) {

        var query = GetUserSessionsQuery.builder()
                .tenantId(tenantId)
                .userId(userId)
                .status(status)
                .page(page)
                .size(size)
                .sortBy("createdAt")
                .sortDirection("desc")
                .build();

        List<TranslationSessionDto> sessions = applicationService.getUserSessions(query);

        return ResponseEntity.ok(sessions.stream()
                .map(this::toResponse)
                .toList());
    }

    /**
     * Translate text in a session.
     * POST /api/v1/translation/sessions/{sessionId}/translate
     */
    @PostMapping("/sessions/{sessionId}/translate")
    @Operation(summary = "Translate text", description = "Translates text in a translation session")
    public ResponseEntity<TranslationSessionResponse> translateText(
            @PathVariable UUID sessionId,
            @Valid @RequestBody TranslateTextRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        var command = TranslateTextCommand.builder()
                .tenantId(tenantId)
                .sessionId(sessionId)
                .sourceText(request.getSourceText())
                .sourceLanguage(request.getSourceLanguage())
                .targetLanguage(request.getTargetLanguage())
                .metadata(request.getMetadata())
                .build();

        TranslationSessionDto sessionDto = applicationService.translateText(command);

        return ResponseEntity.ok(toResponse(sessionDto));
    }

    /**
     * Update session status.
     * PATCH /api/v1/translation/sessions/{sessionId}/status
     */
    @PatchMapping("/sessions/{sessionId}/status")
    @Operation(summary = "Update session status", description = "Updates the status of a translation session")
    public ResponseEntity<TranslationSessionResponse> updateStatus(
            @PathVariable UUID sessionId,
            @Valid @RequestBody UpdateStatusRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        var command = UpdateSessionStatusCommand.builder()
                .tenantId(tenantId)
                .sessionId(sessionId)
                .status(request.getStatus())
                .reason(request.getReason())
                .build();

        TranslationSessionDto sessionDto = applicationService.updateStatus(command);

        return ResponseEntity.ok(toResponse(sessionDto));
    }

    /**
     * Delete a session.
     * DELETE /api/v1/translation/sessions/{sessionId}
     */
    @DeleteMapping("/sessions/{sessionId}")
    @Operation(summary = "Delete session", description = "Deletes a translation session")
    public ResponseEntity<Void> deleteSession(
            @PathVariable UUID sessionId,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        applicationService.deleteSession(tenantId, sessionId);

        return ResponseEntity.noContent().build();
    }

    /**
     * Get active sessions for a tenant.
     * GET /api/v1/translation/sessions/active
     */
    @GetMapping("/sessions/active")
    @Operation(summary = "Get active sessions", description = "Retrieves all active translation sessions")
    public ResponseEntity<List<TranslationSessionResponse>> getActiveSessions(
            @RequestHeader("X-Tenant-ID") String tenantId) {

        List<TranslationSessionDto> sessions = applicationService.getActiveSessions(tenantId);

        return ResponseEntity.ok(sessions.stream()
                .map(this::toResponse)
                .toList());
    }

    /**
     * Process idle sessions.
     * POST /api/v1/translation/sessions/process-idle
     */
    @PostMapping("/sessions/process-idle")
    @Operation(summary = "Process idle sessions", description = "Processes idle sessions for auto-completion or termination")
    public ResponseEntity<Integer> processIdleSessions(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Idle threshold in minutes") @RequestParam(defaultValue = "30") Integer idleThresholdMinutes) {

        int processedCount = applicationService.processIdleSessions(tenantId, idleThresholdMinutes);

        return ResponseEntity.ok(processedCount);
    }

    /**
     * Convert DTO to Response.
     */
    private TranslationSessionResponse toResponse(TranslationSessionDto dto) {
        return TranslationSessionResponse.builder()
                .id(dto.getId())
                .tenantId(dto.getTenantId())
                .userId(dto.getUserId())
                .sessionId(dto.getSessionId())
                .status(dto.getStatus())
                .sessionType(dto.getSessionType())
                .defaultSourceLanguage(dto.getDefaultSourceLanguage())
                .defaultTargetLanguage(dto.getDefaultTargetLanguage())
                .channel(dto.getChannel())
                .metadata(dto.getMetadata())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .lastActivityAt(dto.getLastActivityAt())
                .requestCount(dto.getRequestCount())
                .completedRequestCount(dto.getCompletedRequestCount())
                .failedRequestCount(dto.getFailedRequestCount())
                .successRate(dto.getSuccessRate())
                .cachedRequestCount(dto.getCachedRequestCount())
                .cacheHitRate(dto.getCacheHitRate())
                .averageProcessingTimeMs(dto.getAverageProcessingTimeMs())
                .averageQualityScore(dto.getAverageQualityScore())
                .durationSeconds(dto.getDurationSeconds())
                .mostUsedLanguagePair(dto.getMostUsedLanguagePair())
                .build();
    }
}
