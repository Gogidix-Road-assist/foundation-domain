package com.gogidix.rapidassist.ai.search.optimization.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SearchQuery domain model
 */
@DisplayName("SearchQuery Domain Model Tests")
class SearchQueryTest {

    @Test
    @DisplayName("Should create search query with builder")
    void shouldCreateSearchQueryWithBuilder() {
        // Given
        UUID id = UUID.randomUUID();
        String tenantId = "tenant-123";
        String userId = "user-123";
        String originalQuery = "laptop computers";
        String optimizedQuery = "laptop computers cheap";
        String queryIntent = "purchase";
        String queryType = "product_search";
        Map<String, Object> queryContext = new HashMap<>();
        queryContext.put("category", "electronics");
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("session_id", "session-123");
        Integer resultCount = 10;
        Double relevanceScore = 0.85;
        String status = "completed";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        String createdBy = "user-123";
        String updatedBy = "user-123";
        Long version = 1L;

        // When
        SearchQuery searchQuery = SearchQuery.builder()
                .id(id)
                .tenantId(tenantId)
                .userId(userId)
                .originalQuery(originalQuery)
                .optimizedQuery(optimizedQuery)
                .queryIntent(queryIntent)
                .queryType(queryType)
                .queryContext(queryContext)
                .metadata(metadata)
                .resultCount(resultCount)
                .relevanceScore(relevanceScore)
                .status(status)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .createdBy(createdBy)
                .updatedBy(updatedBy)
                .version(version)
                .build();

        // Then
        assertNotNull(searchQuery);
        assertEquals(id, searchQuery.getId());
        assertEquals(tenantId, searchQuery.getTenantId());
        assertEquals(userId, searchQuery.getUserId());
        assertEquals(originalQuery, searchQuery.getOriginalQuery());
        assertEquals(optimizedQuery, searchQuery.getOptimizedQuery());
        assertEquals(queryIntent, searchQuery.getQueryIntent());
        assertEquals(queryType, searchQuery.getQueryType());
        assertEquals(queryContext, searchQuery.getQueryContext());
        assertEquals(metadata, searchQuery.getMetadata());
        assertEquals(resultCount, searchQuery.getResultCount());
        assertEquals(relevanceScore, searchQuery.getRelevanceScore(), 0.001);
        assertEquals(status, searchQuery.getStatus());
        assertEquals(version, searchQuery.getVersion());
    }

    @Test
    @DisplayName("Should create with no-args constructor")
    void shouldCreateWithNoArgsConstructor() {
        // When
        SearchQuery searchQuery = new SearchQuery();

        // Then
        assertNotNull(searchQuery);
    }

    @Test
    @DisplayName("Should create with all-args constructor")
    void shouldCreateWithAllArgsConstructor() {
        // Given
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        // When
        SearchQuery searchQuery = new SearchQuery(
                id, "tenant-123", "user-123", "laptop", "laptop cheap",
                "purchase", "product_search", null, null, 10, 0.85,
                "completed", now, now, "user-1", "user-1", 1L
        );

        // Then
        assertEquals(id, searchQuery.getId());
        assertEquals("laptop", searchQuery.getOriginalQuery());
        assertEquals("laptop cheap", searchQuery.getOptimizedQuery());
    }

    @Test
    @DisplayName("Should handle null query context")
    void shouldHandleNullQueryContext() {
        // Given
        SearchQuery searchQuery = SearchQuery.builder()
                .queryContext(null)
                .build();

        // Then
        assertNull(searchQuery.getQueryContext());
    }

    @Test
    @DisplayName("Should handle null metadata")
    void shouldHandleNullMetadata() {
        // Given
        SearchQuery searchQuery = SearchQuery.builder()
                .metadata(null)
                .build();

        // Then
        assertNull(searchQuery.getMetadata());
    }

    @Test
    @DisplayName("Should handle null optimized query")
    void shouldHandleNullOptimizedQuery() {
        // Given
        SearchQuery searchQuery = SearchQuery.builder()
                .originalQuery("laptop")
                .optimizedQuery(null)
                .build();

        // Then
        assertNull(searchQuery.getOptimizedQuery());
        assertEquals("laptop", searchQuery.getOriginalQuery());
    }

    @Test
    @DisplayName("Should handle null relevance score")
    void shouldHandleNullRelevanceScore() {
        // Given
        SearchQuery searchQuery = SearchQuery.builder()
                .relevanceScore(null)
                .build();

        // Then
        assertNull(searchQuery.getRelevanceScore());
    }

    @Test
    @DisplayName("Should handle null result count")
    void shouldHandleNullResultCount() {
        // Given
        SearchQuery searchQuery = SearchQuery.builder()
                .resultCount(null)
                .build();

        // Then
        assertNull(searchQuery.getResultCount());
    }

    @Test
    @DisplayName("Should handle zero result count")
    void shouldHandleZeroResultCount() {
        // Given
        SearchQuery searchQuery = SearchQuery.builder()
                .resultCount(0)
                .build();

        // Then
        assertEquals(0, searchQuery.getResultCount());
    }

    @Test
    @DisplayName("Should handle null version")
    void shouldHandleNullVersion() {
        // Given
        SearchQuery searchQuery = SearchQuery.builder()
                .version(null)
                .build();

        // Then
        assertNull(searchQuery.getVersion());
    }

    @Test
    @DisplayName("Should handle different query intents")
    void shouldHandleDifferentQueryIntents() {
        // Given
        SearchQuery informationalQuery = SearchQuery.builder()
                .queryIntent("informational")
                .build();

        SearchQuery navigationalQuery = SearchQuery.builder()
                .queryIntent("navigational")
                .build();

        SearchQuery transactionalQuery = SearchQuery.builder()
                .queryIntent("transactional")
                .build();

        // Then
        assertEquals("informational", informationalQuery.getQueryIntent());
        assertEquals("navigational", navigationalQuery.getQueryIntent());
        assertEquals("transactional", transactionalQuery.getQueryIntent());
    }

    @Test
    @DisplayName("Should handle different query types")
    void shouldHandleDifferentQueryTypes() {
        // Given
        SearchQuery productSearch = SearchQuery.builder()
                .queryType("product_search")
                .build();

        SearchQuery categorySearch = SearchQuery.builder()
                .queryType("category_search")
                .build();

        SearchQuery generalSearch = SearchQuery.builder()
                .queryType("general_search")
                .build();

        // Then
        assertEquals("product_search", productSearch.getQueryType());
        assertEquals("category_search", categorySearch.getQueryType());
        assertEquals("general_search", generalSearch.getQueryType());
    }

    @Test
    @DisplayName("Should handle different statuses")
    void shouldHandleDifferentStatuses() {
        // Given
        SearchQuery pendingQuery = SearchQuery.builder()
                .status("pending")
                .build();

        SearchQuery processingQuery = SearchQuery.builder()
                .status("processing")
                .build();

        SearchQuery completedQuery = SearchQuery.builder()
                .status("completed")
                .build();

        SearchQuery failedQuery = SearchQuery.builder()
                .status("failed")
                .build();

        // Then
        assertEquals("pending", pendingQuery.getStatus());
        assertEquals("processing", processingQuery.getStatus());
        assertEquals("completed", completedQuery.getStatus());
        assertEquals("failed", failedQuery.getStatus());
    }

    @Test
    @DisplayName("Should handle query with context")
    void shouldHandleQueryWithContext() {
        // Given
        Map<String, Object> context = new HashMap<>();
        context.put("category", "electronics");
        context.put("price_range", "100-500");
        context.put("brand", "dell");

        SearchQuery searchQuery = SearchQuery.builder()
                .queryContext(context)
                .build();

        // Then
        assertNotNull(searchQuery.getQueryContext());
        assertEquals(3, searchQuery.getQueryContext().size());
        assertEquals("electronics", searchQuery.getQueryContext().get("category"));
    }

    @Test
    @DisplayName("Should handle query with metadata")
    void shouldHandleQueryWithMetadata() {
        // Given
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("session_id", "session-123");
        metadata.put("user_location", "US");
        metadata.put("device_type", "mobile");

        SearchQuery searchQuery = SearchQuery.builder()
                .metadata(metadata)
                .build();

        // Then
        assertNotNull(searchQuery.getMetadata());
        assertEquals(3, searchQuery.getMetadata().size());
        assertEquals("session-123", searchQuery.getMetadata().get("session_id"));
    }

    @Test
    @DisplayName("Should handle high relevance score")
    void shouldHandleHighRelevanceScore() {
        // Given
        SearchQuery searchQuery = SearchQuery.builder()
                .relevanceScore(0.95)
                .build();

        // Then
        assertEquals(0.95, searchQuery.getRelevanceScore(), 0.001);
    }

    @Test
    @DisplayName("Should handle low relevance score")
    void shouldHandleLowRelevanceScore() {
        // Given
        SearchQuery searchQuery = SearchQuery.builder()
                .relevanceScore(0.25)
                .build();

        // Then
        assertEquals(0.25, searchQuery.getRelevanceScore(), 0.001);
    }

    @Test
    @DisplayName("Should increment version")
    void shouldIncrementVersion() {
        // Given
        SearchQuery searchQuery = SearchQuery.builder()
                .version(1L)
                .build();

        // When
        searchQuery.setVersion(2L);

        // Then
        assertEquals(2L, searchQuery.getVersion());
    }
}
