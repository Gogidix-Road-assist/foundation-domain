package com.gogidix.rapidassist.ai.search.optimization.interfaces.rest;

import com.gogidix.rapidassist.ai.search.optimization.application.command.OptimizeQueryCommand;
import com.gogidix.rapidassist.ai.search.optimization.application.command.RankResultsCommand;
import com.gogidix.rapidassist.ai.search.optimization.application.dto.SearchQueryDto;
import com.gogidix.rapidassist.ai.search.optimization.application.dto.SearchResultDto;
import com.gogidix.rapidassist.ai.search.optimization.application.service.SearchOptimizationApplicationService;
import com.gogidix.rapidassist.ai.search.optimization.domain.tenant.TenantContext;
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
 * REST Controller for Search Optimization operations.
 * API endpoint: /api/v1/search-optimization
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/search-optimization")
@RequiredArgsConstructor
@Tag(name = "Search Optimization", description = "Search Optimization APIs")
public class SearchOptimizationController {

    private final SearchOptimizationApplicationService applicationService;

    /**
     * Optimize a search query.
     * POST /api/v1/search-optimization/queries/optimize
     */
    @PostMapping("/queries/optimize")
    @Operation(summary = "Optimize search query", description = "Optimizes and analyzes a search query")
    public ResponseEntity<SearchQueryDto> optimizeQuery(
            @Valid @RequestBody OptimizeQueryRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        log.info("Optimizing query for tenant: {}", tenantId);

        TenantContext.setTenantId(tenantId);

        var command = OptimizeQueryCommand.builder()
                .tenantId(tenantId)
                .userId(request.getUserId())
                .originalQuery(request.getOriginalQuery())
                .context(request.getContext())
                .metadata(request.getMetadata())
                .build();

        SearchQueryDto queryDto = applicationService.optimizeQuery(command);

        return ResponseEntity.status(HttpStatus.CREATED).body(queryDto);
    }

    /**
     * Get a search query by ID.
     * GET /api/v1/search-optimization/queries/{queryId}
     */
    @GetMapping("/queries/{queryId}")
    @Operation(summary = "Get search query", description = "Retrieves a search query by ID")
    public ResponseEntity<SearchQueryDto> getQuery(
            @PathVariable UUID queryId,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        log.info("Getting query: {} for tenant: {}", queryId, tenantId);

        TenantContext.setTenantId(tenantId);

        SearchQueryDto queryDto = applicationService.getQuery(tenantId, queryId);

        return ResponseEntity.ok(queryDto);
    }

    /**
     * Get all queries for a tenant.
     * GET /api/v1/search-optimization/queries
     */
    @GetMapping("/queries")
    @Operation(summary = "Get all queries", description = "Retrieves all search queries for a tenant")
    public ResponseEntity<List<SearchQueryDto>> getQueries(
            @RequestHeader("X-Tenant-ID") String tenantId) {

        log.info("Getting all queries for tenant: {}", tenantId);

        TenantContext.setTenantId(tenantId);

        List<SearchQueryDto> queries = applicationService.getQueriesByTenant(tenantId);

        return ResponseEntity.ok(queries);
    }

    /**
     * Get queries for a user.
     * GET /api/v1/search-optimization/queries/user/{userId}
     */
    @GetMapping("/queries/user/{userId}")
    @Operation(summary = "Get user queries", description = "Retrieves all search queries for a user")
    public ResponseEntity<List<SearchQueryDto>> getUserQueries(
            @PathVariable String userId,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        log.info("Getting queries for user: {} in tenant: {}", userId, tenantId);

        TenantContext.setTenantId(tenantId);

        List<SearchQueryDto> queries = applicationService.getQueriesByUser(tenantId, userId);

        return ResponseEntity.ok(queries);
    }

    /**
     * Get search results for a query.
     * GET /api/v1/search-optimization/queries/{queryId}/results
     */
    @GetMapping("/queries/{queryId}/results")
    @Operation(summary = "Get search results", description = "Retrieves search results for a query")
    public ResponseEntity<List<SearchResultDto>> getResults(
            @PathVariable UUID queryId,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        log.info("Getting results for query: {} in tenant: {}", queryId, tenantId);

        TenantContext.setTenantId(tenantId);

        List<SearchResultDto> results = applicationService.getResults(tenantId, queryId);

        return ResponseEntity.ok(results);
    }

    /**
     * Rank search results for a query.
     * POST /api/v1/search-optimization/queries/{queryId}/rank
     */
    @PostMapping("/queries/{queryId}/rank")
    @Operation(summary = "Rank search results", description = "Ranks search results using specified algorithm")
    public ResponseEntity<List<SearchResultDto>> rankResults(
            @PathVariable UUID queryId,
            @Valid @RequestBody RankResultsRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        log.info("Ranking results for query: {} in tenant: {}", queryId, tenantId);

        TenantContext.setTenantId(tenantId);

        var command = RankResultsCommand.builder()
                .tenantId(tenantId)
                .queryId(queryId)
                .rankingAlgorithm(request.getRankingAlgorithm())
                .parameters(request.getParameters())
                .build();

        List<SearchResultDto> results = applicationService.rankResults(command);

        return ResponseEntity.ok(results);
    }

    /**
     * Delete a search query.
     * DELETE /api/v1/search-optimization/queries/{queryId}
     */
    @DeleteMapping("/queries/{queryId}")
    @Operation(summary = "Delete query", description = "Deletes a search query and its results")
    public ResponseEntity<Void> deleteQuery(
            @PathVariable UUID queryId,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        log.info("Deleting query: {} for tenant: {}", queryId, tenantId);

        TenantContext.setTenantId(tenantId);

        applicationService.deleteQuery(tenantId, queryId);

        return ResponseEntity.noContent().build();
    }

    // Request DTOs

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class OptimizeQueryRequest {
        private String userId;
        private String originalQuery;
        private java.util.Map<String, Object> context;
        private java.util.Map<String, Object> metadata;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class RankResultsRequest {
        private String rankingAlgorithm;
        private java.util.Map<String, Object> parameters;
    }
}
