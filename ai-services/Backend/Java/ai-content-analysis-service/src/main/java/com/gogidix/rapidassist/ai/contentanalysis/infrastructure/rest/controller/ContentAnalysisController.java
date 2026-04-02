package com.gogidix.rapidassist.ai.contentanalysis.infrastructure.rest.controller;

import com.gogidix.rapidassist.ai.contentanalysis.application.command.AnalyzeContentCommand;
import com.gogidix.rapidassist.ai.contentanalysis.application.command.BulkAnalyzeContentCommand;
import com.gogidix.rapidassist.ai.contentanalysis.application.command.ExtractTopicsCommand;
import com.gogidix.rapidassist.ai.contentanalysis.application.dto.*;
import com.gogidix.rapidassist.ai.contentanalysis.application.query.*;
import com.gogidix.rapidassist.ai.contentanalysis.application.service.ContentAnalysisApplicationService;
import com.gogidix.rapidassist.ai.contentanalysis.domain.tenant.TenantContext;
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
 * REST Controller for Content Analysis operations.
 * API endpoint: /api/v1/content-analysis
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/content-analysis")
@RequiredArgsConstructor
@Tag(name = "Content Analysis", description = "Content Analysis APIs")
public class ContentAnalysisController {

    private final ContentAnalysisApplicationService applicationService;

    /**
     * Analyze content
     * POST /api/v1/content-analysis/analyze
     */
    @PostMapping("/analyze")
    @Operation(summary = "Analyze content", description = "Performs comprehensive content analysis including quality, readability, sentiment, and SEO")
    public ResponseEntity<ContentAnalysisDto> analyzeContent(
            @Valid @RequestBody AnalyzeContentRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        var command = AnalyzeContentCommand.builder()
                .tenantId(tenantId)
                .contentId(request.getContentId())
                .contentType(request.getContentType())
                .contentTitle(request.getContentTitle())
                .contentBody(request.getContentBody())
                .contentLanguage(request.getContentLanguage())
                .requestedBy(request.getRequestedBy())
                .analysisType(request.getAnalysisType())
                .analysisOptions(request.getAnalysisOptions())
                .callbackUrl(request.getCallbackUrl())
                .priority(request.getPriority())
                .build();

        ContentAnalysisDto result = applicationService.analyzeContent(command);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * Get analysis by ID
     * GET /api/v1/content-analysis/{id}
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get analysis by ID", description = "Retrieves a content analysis by ID")
    public ResponseEntity<ContentAnalysisDto> getAnalysis(
            @PathVariable UUID id,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Include topics in response") @RequestParam(defaultValue = "false") Boolean includeTopics,
            @Parameter(description = "Include metrics in response") @RequestParam(defaultValue = "false") Boolean includeMetrics,
            @Parameter(description = "Include sentiment in response") @RequestParam(defaultValue = "false") Boolean includeSentiment,
            @Parameter(description = "Include SEO in response") @RequestParam(defaultValue = "false") Boolean includeSEO,
            @Parameter(description = "Include readability in response") @RequestParam(defaultValue = "false") Boolean includeReadability) {

        TenantContext.setTenantId(tenantId);

        var query = GetContentAnalysisQuery.builder()
                .tenantId(tenantId)
                .analysisId(id)
                .includeTopics(includeTopics)
                .includeMetrics(includeMetrics)
                .includeSentiment(includeSentiment)
                .includeSEO(includeSEO)
                .includeReadability(includeReadability)
                .build();

        ContentAnalysisDto result = applicationService.getAnalysis(query);

        return ResponseEntity.ok(result);
    }

    /**
     * List all analyses
     * GET /api/v1/content-analysis
     */
    @GetMapping
    @Operation(summary = "List analyses", description = "Lists all content analyses with pagination")
    public ResponseEntity<List<ContentAnalysisDto>> listAnalyses(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Filter by status") @RequestParam(required = false) String status,
            @Parameter(description = "Filter by content type") @RequestParam(required = false) String contentType,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "Sort by") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDirection) {

        TenantContext.setTenantId(tenantId);

        var query = ListAnalysesQuery.builder()
                .tenantId(tenantId)
                .status(status)
                .contentType(contentType)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();

        List<ContentAnalysisDto> results = applicationService.listAnalyses(query);

        return ResponseEntity.ok(results);
    }

    /**
     * Bulk analyze content
     * POST /api/v1/content-analysis/bulk
     */
    @PostMapping("/bulk")
    @Operation(summary = "Bulk analyze content", description = "Analyzes multiple content items in bulk")
    public ResponseEntity<List<ContentAnalysisDto>> bulkAnalyzeContent(
            @Valid @RequestBody BulkAnalyzeContentRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        var command = BulkAnalyzeContentCommand.builder()
                .tenantId(tenantId)
                .contentItems(request.getContentItems())
                .requestedBy(request.getRequestedBy())
                .analysisType(request.getAnalysisType())
                .analysisOptions(request.getAnalysisOptions())
                .build();

        List<ContentAnalysisDto> results = applicationService.bulkAnalyzeContent(command);

        return ResponseEntity.status(HttpStatus.CREATED).body(results);
    }

    /**
     * Get analysis for specific content
     * GET /api/v1/content-analysis/content/{contentId}
     */
    @GetMapping("/content/{contentId}")
    @Operation(summary = "Get analysis for content", description = "Retrieves analysis for a specific content ID")
    public ResponseEntity<ContentAnalysisDto> getAnalysisByContentId(
            @PathVariable String contentId,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Include topics in response") @RequestParam(defaultValue = "false") Boolean includeTopics,
            @Parameter(description = "Include metrics in response") @RequestParam(defaultValue = "false") Boolean includeMetrics,
            @Parameter(description = "Include sentiment in response") @RequestParam(defaultValue = "false") Boolean includeSentiment,
            @Parameter(description = "Include SEO in response") @RequestParam(defaultValue = "false") Boolean includeSEO,
            @Parameter(description = "Include readability in response") @RequestParam(defaultValue = "false") Boolean includeReadability) {

        TenantContext.setTenantId(tenantId);

        var query = GetAnalysisByContentIdQuery.builder()
                .tenantId(tenantId)
                .contentId(contentId)
                .includeTopics(includeTopics)
                .includeMetrics(includeMetrics)
                .includeSentiment(includeSentiment)
                .includeSEO(includeSEO)
                .includeReadability(includeReadability)
                .build();

        ContentAnalysisDto result = applicationService.getAnalysisByContentId(query);

        return ResponseEntity.ok(result);
    }

    /**
     * Get detailed metrics
     * GET /api/v1/content-analysis/metrics/{id}
     */
    @GetMapping("/metrics/{id}")
    @Operation(summary = "Get detailed metrics", description = "Retrieves detailed metrics for an analysis")
    public ResponseEntity<ContentMetricsDto> getMetrics(
            @PathVariable UUID id,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        var query = GetMetricsQuery.builder()
                .tenantId(tenantId)
                .analysisId(id)
                .build();

        ContentMetricsDto result = applicationService.getMetrics(query);

        return ResponseEntity.ok(result);
    }

    /**
     * Extract topics from content
     * POST /api/v1/content-analysis/topics/extract
     */
    @PostMapping("/topics/extract")
    @Operation(summary = "Extract topics", description = "Extracts topics from content")
    public ResponseEntity<List<ContentTopicDto>> extractTopics(
            @Valid @RequestBody ExtractTopicsRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        var command = ExtractTopicsCommand.builder()
                .tenantId(tenantId)
                .contentId(request.getContentId())
                .contentBody(request.getContentBody())
                .contentLanguage(request.getContentLanguage())
                .maxTopics(request.getMaxTopics())
                .minRelevanceScore(request.getMinRelevanceScore())
                .requestedBy(request.getRequestedBy())
                .build();

        List<ContentTopicDto> results = applicationService.extractTopics(command);

        return ResponseEntity.ok(results);
    }

    /**
     * Delete analysis
     * DELETE /api/v1/content-analysis/{id}
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete analysis", description = "Deletes a content analysis")
    public ResponseEntity<Void> deleteAnalysis(
            @PathVariable UUID id,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        TenantContext.setTenantId(tenantId);

        applicationService.deleteAnalysis(tenantId, id);

        return ResponseEntity.noContent().build();
    }

    /**
     * Request DTO classes
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class AnalyzeContentRequest {
        private String contentId;
        private String contentType;
        private String contentTitle;
        private String contentBody;
        private String contentLanguage;
        private String requestedBy;
        private String analysisType;
        private List<String> analysisOptions;
        private String callbackUrl;
        private Integer priority;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class BulkAnalyzeContentRequest {
        private List<BulkAnalyzeContentCommand.ContentItem> contentItems;
        private String requestedBy;
        private String analysisType;
        private List<String> analysisOptions;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ExtractTopicsRequest {
        private String contentId;
        private String contentBody;
        private String contentLanguage;
        private Integer maxTopics;
        private Double minRelevanceScore;
        private String requestedBy;
    }
}
