package com.gogidix.rapidassist.ai.recommendation.interfaces.rest.controller;

import com.gogidix.rapidassist.ai.recommendation.application.command.CreateRecommendationRequestCommand;
import com.gogidix.rapidassist.ai.recommendation.application.command.UpdateUserPreferenceCommand;
import com.gogidix.rapidassist.ai.recommendation.application.dto.RecommendationResultDto;
import com.gogidix.rapidassist.ai.recommendation.application.dto.UserPreferenceDto;
import com.gogidix.rapidassist.ai.recommendation.application.query.GetRecommendationQuery;
import com.gogidix.rapidassist.ai.recommendation.application.query.GetUserPreferencesQuery;
import com.gogidix.rapidassist.ai.recommendation.application.service.RecommendationApplicationService;
import com.gogidix.rapidassist.ai.recommendation.domain.model.RecommendationType;
import com.gogidix.rapidassist.ai.recommendation.interfaces.rest.request.CreateRecommendationRequest;
import com.gogidix.rapidassist.ai.recommendation.interfaces.rest.request.UpdatePreferenceRequest;
import com.gogidix.rapidassist.ai.recommendation.interfaces.rest.response.RecommendationResponse;
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
 * REST Controller for Recommendation operations.
 * API endpoint: /api/v1/recommendations
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Recommendation", description = "AI Recommendation APIs")
@RequestMapping("/api/v1/recommendations")
public class RecommendationRestController {

    private final RecommendationApplicationService applicationService;

    /**
     * Create a recommendation request.
     * POST /api/v1/recommendations
     */
    @PostMapping
    @Operation(summary = "Create recommendation", description = "Creates and processes a recommendation request")
    public ResponseEntity<RecommendationResponse> createRecommendation(
            @Valid @RequestBody CreateRecommendationRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        log.info("Creating recommendation for user: {} in tenant: {}", request.getUserId(), tenantId);

        var command = CreateRecommendationRequestCommand.builder()
                .tenantId(tenantId)
                .userId(request.getUserId())
                .recommendationType(request.getRecommendationType())
                .itemType(request.getItemType())
                .contextType(request.getContextType())
                .contextId(request.getContextId())
                .limit(request.getLimit())
                .filters(request.getFilters())
                .parameters(request.getParameters())
                .ttlMinutes(request.getTtlMinutes())
                .build();

        RecommendationResultDto resultDto = applicationService.createRecommendation(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(resultDto));
    }

    /**
     * Get recommendation result by ID.
     * GET /api/v1/recommendations/{resultId}
     */
    @GetMapping("/{resultId}")
    @Operation(summary = "Get recommendation", description = "Retrieves a recommendation result by ID")
    public ResponseEntity<RecommendationResponse> getRecommendation(
            @PathVariable UUID resultId,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Use cache") @RequestParam(defaultValue = "true") Boolean useCache) {

        log.info("Getting recommendation: {} for tenant: {}", resultId, tenantId);

        var query = GetRecommendationQuery.builder()
                .tenantId(tenantId)
                .resultId(resultId)
                .useCache(useCache)
                .build();

        RecommendationResultDto resultDto = applicationService.getRecommendation(query);

        return ResponseEntity.ok(toResponse(resultDto));
    }

    /**
     * Update user preference.
     * POST /api/v1/recommendations/preferences
     */
    @PostMapping("/preferences")
    @Operation(summary = "Update preference", description = "Updates or creates a user preference")
    public ResponseEntity<UserPreferenceDto> updatePreference(
            @Valid @RequestBody UpdatePreferenceRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        log.info("Updating preference for user: {} in tenant: {}", request.getUserId(), tenantId);

        var command = UpdateUserPreferenceCommand.builder()
                .tenantId(tenantId)
                .userId(request.getUserId())
                .itemType(request.getItemType())
                .itemId(request.getItemId())
                .preferenceKey(request.getPreferenceKey())
                .preferenceValue(request.getPreferenceValue())
                .scoreDelta(request.getScoreDelta())
                .metadata(request.getMetadata())
                .build();

        UserPreferenceDto preferenceDto = applicationService.updateUserPreference(command);

        return ResponseEntity.ok(preferenceDto);
    }

    /**
     * Get user preferences.
     * GET /api/v1/recommendations/preferences/{userId}
     */
    @GetMapping("/preferences/{userId}")
    @Operation(summary = "Get user preferences", description = "Retrieves all preferences for a user")
    public ResponseEntity<List<UserPreferenceDto>> getUserPreferences(
            @PathVariable String userId,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Filter by item type") @RequestParam(required = false) String itemType,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") Integer size) {

        log.info("Getting preferences for user: {} in tenant: {}", userId, tenantId);

        var query = GetUserPreferencesQuery.builder()
                .tenantId(tenantId)
                .userId(userId)
                .itemType(itemType)
                .page(page)
                .size(size)
                .build();

        List<UserPreferenceDto> preferences = applicationService.getUserPreferences(query);

        return ResponseEntity.ok(preferences);
    }

    /**
     * Delete expired recommendation results.
     * DELETE /api/v1/recommendations/expired
     */
    @DeleteMapping("/expired")
    @Operation(summary = "Delete expired results", description = "Deletes all expired recommendation results")
    public ResponseEntity<Integer> deleteExpiredResults(
            @RequestHeader("X-Tenant-ID") String tenantId) {

        log.info("Deleting expired results for tenant: {}", tenantId);

        int deletedCount = applicationService.deleteExpiredResults(tenantId);

        return ResponseEntity.ok(deletedCount);
    }

    /**
     * Health check endpoint.
     * GET /api/v1/recommendations/health
     */
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check if the service is running")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("AI Recommendation Service is running");
    }

    private RecommendationResponse toResponse(RecommendationResultDto dto) {
        return RecommendationResponse.builder()
                .id(dto.getId())
                .tenantId(dto.getTenantId())
                .requestId(dto.getRequestId())
                .userId(dto.getUserId())
                .recommendationType(dto.getRecommendationType())
                .itemType(dto.getItemType())
                .status(dto.getStatus())
                .items(dto.getItems())
                .totalResults(dto.getTotalResults())
                .confidenceScore(dto.getConfidenceScore())
                .processingTimeMs(dto.getProcessingTimeMs())
                .algorithm(dto.getAlgorithm())
                .metadata(dto.getMetadata())
                .createdAt(dto.getCreatedAt())
                .expiresAt(dto.getExpiresAt())
                .cached(dto.getCached())
                .build();
    }
}
