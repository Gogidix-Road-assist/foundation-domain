package com.gogidix.rapidassist.ai.sentiment.interfaces.rest.controller;

import com.gogidix.rapidassist.ai.sentiment.application.command.AnalyzeSentimentCommand;
import com.gogidix.rapidassist.ai.sentiment.application.command.BatchAnalyzeSentimentCommand;
import com.gogidix.rapidassist.ai.sentiment.application.dto.BatchSentimentResultDto;
import com.gogidix.rapidassist.ai.sentiment.application.dto.SentimentAnalysisDto;
import com.gogidix.rapidassist.ai.sentiment.application.dto.SentimentTrendDto;
import com.gogidix.rapidassist.ai.sentiment.application.query.GetSentimentAnalysisQuery;
import com.gogidix.rapidassist.ai.sentiment.application.query.GetUserSentimentAnalysesQuery;
import com.gogidix.rapidassist.ai.sentiment.application.service.SentimentAnalysisApplicationService;
import com.gogidix.rapidassist.ai.sentiment.domain.tenant.TenantContext;
import com.gogidix.rapidassist.ai.sentiment.interfaces.rest.request.AnalyzeSentimentRequest;
import com.gogidix.rapidassist.ai.sentiment.interfaces.rest.request.BatchAnalyzeSentimentRequest;
import com.gogidix.rapidassist.ai.sentiment.interfaces.rest.response.SentimentAnalysisResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * REST Controller for Sentiment Analysis operations.
 * API endpoint: /api/v1/sentiment
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/sentiment")
@RequiredArgsConstructor
@Tag(name = "Sentiment Analysis", description = "Sentiment Analysis APIs")
public class SentimentAnalysisRestController {

    private final SentimentAnalysisApplicationService applicationService;

    /**
     * Analyze sentiment of text.
     * POST /api/v1/sentiment/analyze
     */
    @PostMapping("/analyze")
    @Operation(summary = "Analyze sentiment", description = "Analyzes the sentiment of the provided text")
    public ResponseEntity<SentimentAnalysisResponse> analyzeSentiment(
            @Valid @RequestBody AnalyzeSentimentRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        var command = AnalyzeSentimentCommand.builder()
                .tenantId(tenantId)
                .userId(request.getUserId())
                .text(request.getText())
                .language(request.getLanguage())
                .sourceType(request.getSourceType())
                .sourceId(request.getSourceId())
                .includeEmotions(request.getIncludeEmotions())
                .includeAspects(request.getIncludeAspects())
                .metadata(request.getMetadata())
                .build();

        SentimentAnalysisDto dto = applicationService.analyzeSentiment(command);

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(dto));
    }

    /**
     * Batch analyze sentiment.
     * POST /api/v1/sentiment/batch-analyze
     */
    @PostMapping("/batch-analyze")
    @Operation(summary = "Batch analyze sentiment", description = "Analyzes the sentiment of multiple texts")
    public ResponseEntity<BatchSentimentResultDto> batchAnalyzeSentiment(
            @Valid @RequestBody BatchAnalyzeSentimentRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        var command = BatchAnalyzeSentimentCommand.builder()
                .tenantId(tenantId)
                .userId(request.getUserId())
                .texts(request.getTexts())
                .language(request.getLanguage())
                .sourceType(request.getSourceType())
                .sourceId(request.getSourceId())
                .includeEmotions(request.getIncludeEmotions())
                .includeAspects(request.getIncludeAspects())
                .metadata(request.getMetadata())
                .build();

        BatchSentimentResultDto result = applicationService.batchAnalyzeSentiment(command);

        return ResponseEntity.ok(result);
    }

    /**
     * Get sentiment analysis by ID.
     * GET /api/v1/sentiment/analyses/{analysisId}
     */
    @GetMapping("/analyses/{analysisId}")
    @Operation(summary = "Get sentiment analysis", description = "Retrieves a sentiment analysis by ID")
    public ResponseEntity<SentimentAnalysisResponse> getSentimentAnalysis(
            @PathVariable UUID analysisId,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Include emotions in response") @RequestParam(defaultValue = "true") Boolean includeEmotions,
            @Parameter(description = "Include aspects in response") @RequestParam(defaultValue = "true") Boolean includeAspects) {

        TenantContext.setTenantId(tenantId);

        var query = GetSentimentAnalysisQuery.builder()
                .tenantId(tenantId)
                .analysisId(analysisId)
                .includeEmotions(includeEmotions)
                .includeAspects(includeAspects)
                .build();

        SentimentAnalysisDto dto = applicationService.getSentimentAnalysis(query);

        return ResponseEntity.ok(toResponse(dto));
    }

    /**
     * Get user sentiment analyses.
     * GET /api/v1/sentiment/analyses/user/{userId}
     */
    @GetMapping("/analyses/user/{userId}")
    @Operation(summary = "Get user sentiment analyses", description = "Retrieves all sentiment analyses for a user")
    public ResponseEntity<List<SentimentAnalysisResponse>> getUserSentimentAnalyses(
            @PathVariable String userId,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Filter by status") @RequestParam(required = false) String status,
            @Parameter(description = "Filter by sentiment type") @RequestParam(required = false) String sentimentType,
            @Parameter(description = "Start date") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "Sort by") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDirection) {

        TenantContext.setTenantId(tenantId);

        var query = GetUserSentimentAnalysesQuery.builder()
                .tenantId(tenantId)
                .userId(userId)
                .status(status)
                .sentimentType(sentimentType)
                .startDate(startDate)
                .endDate(endDate)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();

        List<SentimentAnalysisDto> dtos = applicationService.getUserSentimentAnalyses(query);

        return ResponseEntity.ok(dtos.stream()
                .map(this::toResponse)
                .toList());
    }

    /**
     * Get sentiment trend.
     * GET /api/v1/sentiment/trends
     */
    @GetMapping("/trends")
    @Operation(summary = "Get sentiment trend", description = "Retrieves sentiment trend analysis")
    public ResponseEntity<SentimentTrendDto> getSentimentTrend(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "User ID (optional)") @RequestParam(required = false) String userId,
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        TenantContext.setTenantId(tenantId);

        SentimentTrendDto trend = applicationService.getSentimentTrend(tenantId, userId, startDate, endDate);

        return ResponseEntity.ok(trend);
    }

    /**
     * Delete sentiment analysis.
     * DELETE /api/v1/sentiment/analyses/{analysisId}
     */
    @DeleteMapping("/analyses/{analysisId}")
    @Operation(summary = "Delete sentiment analysis", description = "Deletes a sentiment analysis")
    public ResponseEntity<Void> deleteSentimentAnalysis(
            @PathVariable UUID analysisId,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        applicationService.deleteSentimentAnalysis(tenantId, analysisId);

        return ResponseEntity.noContent().build();
    }

    /**
     * Convert DTO to Response.
     */
    private SentimentAnalysisResponse toResponse(SentimentAnalysisDto dto) {
        return SentimentAnalysisResponse.builder()
                .id(dto.getId())
                .tenantId(dto.getTenantId())
                .userId(dto.getUserId())
                .sourceType(dto.getSourceType())
                .sourceId(dto.getSourceId())
                .text(dto.getText())
                .language(dto.getLanguage())
                .status(dto.getStatus())
                .overallSentiment(dto.getOverallSentiment())
                .sentimentCategory(dto.getSentimentCategory())
                .sentimentScore(dto.getSentimentScore())
                .confidence(dto.getConfidence())
                .emotions(dto.getEmotions())
                .aspects(dto.getAspects())
                .wordCount(dto.getWordCount())
                .characterCount(dto.getCharacterCount())
                .errorMessage(dto.getErrorMessage())
                .metadata(dto.getMetadata())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .analyzedAt(dto.getAnalyzedAt())
                .createdBy(dto.getCreatedBy())
                .updatedBy(dto.getUpdatedBy())
                .durationSeconds(dto.getDurationSeconds())
                .dominantEmotion(dto.getDominantEmotion())
                .positiveAspectCount(dto.getPositiveAspectCount())
                .negativeAspectCount(dto.getNegativeAspectCount())
                .neutralAspectCount(dto.getNeutralAspectCount())
                .build();
    }
}
