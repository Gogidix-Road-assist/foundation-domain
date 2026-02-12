package com.gogidix.rapidassist.ai.search.optimization.application.service;

import com.gogidix.rapidassist.ai.search.optimization.application.command.OptimizeQueryCommand;
import com.gogidix.rapidassist.ai.search.optimization.application.command.RankResultsCommand;
import com.gogidix.rapidassist.ai.search.optimization.application.dto.SearchQueryDto;
import com.gogidix.rapidassist.ai.search.optimization.application.dto.SearchResultDto;
import com.gogidix.rapidassist.ai.search.optimization.application.port.out.SearchQueryRepositoryPort;
import com.gogidix.rapidassist.ai.search.optimization.application.port.out.SearchResultRepositoryPort;
import com.gogidix.rapidassist.ai.search.optimization.domain.exception.InvalidSearchQueryException;
import com.gogidix.rapidassist.ai.search.optimization.domain.exception.SearchQueryNotFoundException;
import com.gogidix.rapidassist.ai.search.optimization.domain.model.QueryIntent;
import com.gogidix.rapidassist.ai.search.optimization.domain.model.QueryType;
import com.gogidix.rapidassist.ai.search.optimization.domain.model.SearchQuery;
import com.gogidix.rapidassist.ai.search.optimization.domain.model.SearchResult;
import com.gogidix.rapidassist.ai.search.optimization.domain.model.SearchStatus;
import com.gogidix.rapidassist.ai.search.optimization.domain.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Application Service for Search Optimization operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SearchOptimizationApplicationService {

    private final SearchQueryRepositoryPort queryRepository;
    private final SearchResultRepositoryPort resultRepository;

    /**
     * Optimize a search query.
     */
    @Transactional
    public SearchQueryDto optimizeQuery(OptimizeQueryCommand command) {
        log.info("Optimizing query for tenant: {}, user: {}, query: {}",
                command.getTenantId(), command.getUserId(), command.getOriginalQuery());

        TenantContext.setTenantId(command.getTenantId());

        // Validate input
        if (command.getOriginalQuery() == null || command.getOriginalQuery().isBlank()) {
            throw new InvalidSearchQueryException("Query cannot be null or empty");
        }

        // Analyze query intent and type (simplified - in real implementation would use AI/ML)
        String queryIntent = analyzeQueryIntent(command.getOriginalQuery());
        String queryType = determineQueryType(command.getOriginalQuery());

        // Optimize query (simplified implementation)
        String optimizedQuery = optimizeQueryString(command.getOriginalQuery());

        // Create search query domain model
        SearchQuery searchQuery = SearchQuery.builder()
                .id(UUID.randomUUID())
                .tenantId(command.getTenantId())
                .userId(command.getUserId())
                .originalQuery(command.getOriginalQuery())
                .optimizedQuery(optimizedQuery)
                .queryIntent(queryIntent)
                .queryType(queryType)
                .queryContext(command.getContext())
                .metadata(command.getMetadata())
                .status(SearchStatus.PROCESSING.name())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Save query
        SearchQuery savedQuery = queryRepository.save(searchQuery);

        log.info("Query optimized successfully: {}", savedQuery.getId());

        return toDto(savedQuery);
    }

    /**
     * Rank search results for a query.
     */
    @Transactional
    public List<SearchResultDto> rankResults(RankResultsCommand command) {
        log.info("Ranking results for query: {} in tenant: {}", command.getQueryId(), command.getTenantId());

        TenantContext.setTenantId(command.getTenantId());

        // Verify query exists
        SearchQuery query = queryRepository.findByIdAndTenantId(command.getQueryId(), command.getTenantId())
                .orElseThrow(() -> new SearchQueryNotFoundException(command.getQueryId()));

        // Get results for this query
        List<SearchResult> results = resultRepository.findByQueryIdAndTenantId(
                command.getQueryId(), command.getTenantId());

        // Apply ranking algorithm (simplified implementation)
        results = applyRankingAlgorithm(results, command.getRankingAlgorithm());

        // Update scores and positions
        int position = 1;
        for (SearchResult result : results) {
            result.setRankingScore(calculateRankingScore(result, command.getParameters()));
            result.setPosition(position++);
            resultRepository.save(result);
        }

        // Update query status
        query.setStatus(SearchStatus.COMPLETED.name());
        query.setUpdatedAt(LocalDateTime.now());
        queryRepository.save(query);

        log.info("Results ranked successfully for query: {}", command.getQueryId());

        return results.stream()
                .map(this::toResultDto)
                .toList();
    }

    /**
     * Get search query by ID.
     */
    @Transactional(readOnly = true)
    public SearchQueryDto getQuery(String tenantId, UUID queryId) {
        log.debug("Getting query: {} for tenant: {}", queryId, tenantId);

        TenantContext.setTenantId(tenantId);

        SearchQuery query = queryRepository.findByIdAndTenantId(queryId, tenantId)
                .orElseThrow(() -> new SearchQueryNotFoundException(queryId));

        return toDto(query);
    }

    /**
     * Get search results for a query.
     */
    @Transactional(readOnly = true)
    public List<SearchResultDto> getResults(String tenantId, UUID queryId) {
        log.debug("Getting results for query: {} in tenant: {}", queryId, tenantId);

        TenantContext.setTenantId(tenantId);

        List<SearchResult> results = resultRepository.findByQueryIdAndTenantId(queryId, tenantId);

        return results.stream()
                .map(this::toResultDto)
                .toList();
    }

    /**
     * Get all queries for a tenant.
     */
    @Transactional(readOnly = true)
    public List<SearchQueryDto> getQueriesByTenant(String tenantId) {
        log.debug("Getting all queries for tenant: {}", tenantId);

        TenantContext.setTenantId(tenantId);

        List<SearchQuery> queries = queryRepository.findByTenantId(tenantId);

        return queries.stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * Get queries by user.
     */
    @Transactional(readOnly = true)
    public List<SearchQueryDto> getQueriesByUser(String tenantId, String userId) {
        log.debug("Getting queries for user: {} in tenant: {}", userId, tenantId);

        TenantContext.setTenantId(tenantId);

        List<SearchQuery> queries = queryRepository.findByUserId(tenantId, userId);

        return queries.stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * Delete a query.
     */
    @Transactional
    public void deleteQuery(String tenantId, UUID queryId) {
        log.info("Deleting query: {} for tenant: {}", queryId, tenantId);

        TenantContext.setTenantId(tenantId);

        // Delete associated results
        resultRepository.deleteByQueryId(queryId);

        // Delete query
        queryRepository.deleteByIdAndTenantId(queryId, tenantId);

        log.info("Query deleted successfully: {}", queryId);
    }

    // Private helper methods

    private String analyzeQueryIntent(String query) {
        // Simplified intent analysis
        String lowerQuery = query.toLowerCase();

        if (lowerQuery.contains("buy") || lowerQuery.contains("purchase") || lowerQuery.contains("price")) {
            return QueryIntent.TRANSACTIONAL.name();
        } else if (lowerQuery.contains("how to") || lowerQuery.contains("what is") || lowerQuery.contains("why")) {
            return QueryIntent.INFORMATIONAL.name();
        } else if (lowerQuery.contains("best") || lowerQuery.contains("top") || lowerQuery.contains("review")) {
            return QueryIntent.COMMERCIAL.name();
        } else if (lowerQuery.contains("near me") || lowerQuery.contains("location")) {
            return QueryIntent.LOCAL.name();
        } else {
            return QueryIntent.UNKNOWN.name();
        }
    }

    private String determineQueryType(String query) {
        // Simplified type determination
        if (query.contains("\"")) {
            return QueryType.PHRASE.name();
        } else if (query.contains("AND") || query.contains("OR") || query.contains("NOT")) {
            return QueryType.BOOLEAN.name();
        } else if (query.contains("*") || query.contains("?")) {
            return QueryType.WILDCARD.name();
        } else {
            return QueryType.SIMPLE.name();
        }
    }

    private String optimizeQueryString(String originalQuery) {
        // Simplified query optimization
        return originalQuery.trim()
                .replaceAll("\\s+", " ")
                .toLowerCase();
    }

    private List<SearchResult> applyRankingAlgorithm(List<SearchResult> results, String algorithm) {
        // Simplified ranking - sort by relevance score
        return results.stream()
                .sorted((r1, r2) -> Double.compare(
                        r2.getRelevanceScore() != null ? r2.getRelevanceScore() : 0.0,
                        r1.getRelevanceScore() != null ? r1.getRelevanceScore() : 0.0))
                .toList();
    }

    private Double calculateRankingScore(SearchResult result, java.util.Map<String, Object> parameters) {
        // Simplified ranking score calculation
        double relevanceScore = result.getRelevanceScore() != null ? result.getRelevanceScore() : 0.0;
        return relevanceScore * 1.0; // Can be enhanced with more complex logic
    }

    private SearchQueryDto toDto(SearchQuery query) {
        return SearchQueryDto.builder()
                .id(query.getId())
                .tenantId(query.getTenantId())
                .userId(query.getUserId())
                .originalQuery(query.getOriginalQuery())
                .optimizedQuery(query.getOptimizedQuery())
                .queryIntent(query.getQueryIntent())
                .queryType(query.getQueryType())
                .queryContext(query.getQueryContext())
                .metadata(query.getMetadata())
                .resultCount(query.getResultCount())
                .relevanceScore(query.getRelevanceScore())
                .status(query.getStatus())
                .createdAt(query.getCreatedAt())
                .updatedAt(query.getUpdatedAt())
                .createdBy(query.getCreatedBy())
                .updatedBy(query.getUpdatedBy())
                .build();
    }

    private SearchResultDto toResultDto(SearchResult result) {
        return SearchResultDto.builder()
                .id(result.getId())
                .tenantId(result.getTenantId())
                .queryId(result.getQueryId())
                .resultType(result.getResultType())
                .title(result.getTitle())
                .description(result.getDescription())
                .url(result.getUrl())
                .relevanceScore(result.getRelevanceScore())
                .rankingScore(result.getRankingScore())
                .position(result.getPosition())
                .attributes(result.getAttributes())
                .metadata(result.getMetadata())
                .status(result.getStatus())
                .createdAt(result.getCreatedAt())
                .updatedAt(result.getUpdatedAt())
                .createdBy(result.getCreatedBy())
                .updatedBy(result.getUpdatedBy())
                .build();
    }
}
