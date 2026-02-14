package com.gogidix.rapidassist.shared.idempotency.library.autoconfigure;

import com.gogidix.rapidassist.shared.idempotency.library.domain.IdempotencyContext;
import com.gogidix.rapidassist.shared.idempotency.library.domain.IdempotencyContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * Unit tests for IdempotencyKeyFilter.
 */
@ExtendWith(MockitoExtension.class)
class IdempotencyKeyFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private IdempotencyProperties properties;
    private IdempotencyKeyFilter filter;

    @BeforeEach
    void setUp() {
        properties = new IdempotencyProperties();
        filter = new IdempotencyKeyFilter(properties);
        IdempotencyContextHolder.clear();
    }

    @AfterEach
    void tearDown() {
        IdempotencyContextHolder.clear();
    }

    /**
     * Helper method to capture the context during filter execution.
     * The filter clears the context in finally block, so we must capture it
     * during the filter chain invocation, not after doFilter returns.
     */
    private IdempotencyContext captureContextDuringFilter() throws ServletException, IOException {
        final IdempotencyContext[] contextHolder = new IdempotencyContext[1];
        doAnswer((Answer<Void>) invocation -> {
            contextHolder[0] = IdempotencyContextHolder.get().orElse(null);
            return null;
        }).when(filterChain).doFilter(request, response);
        filter.doFilter(request, response, filterChain);
        // Verify the filterChain was called (this also records the interaction)
        verify(filterChain).doFilter(request, response);
        return contextHolder[0];
    }

    @Test
    void testFilterWithValidKeyHeader() throws ServletException, IOException {
        // Arrange
        String key = "test-idempotency-key-123";
        properties.setKeyHeader("Idempotency-Key");
        when(request.getHeader("Idempotency-Key")).thenReturn(key);

        // Act
        IdempotencyContext context = captureContextDuringFilter();

        // Assert
        assertNotNull(context);
        assertEquals(key, context.idempotencyKey());
        // Context is cleared after filter completes
        assertTrue(IdempotencyContextHolder.get().isEmpty());
    }

    @Test
    void testFilterWithoutKeyHeader() throws ServletException, IOException {
        // Arrange
        properties.setKeyHeader("Idempotency-Key");
        properties.setRequired(false);
        when(request.getHeader("Idempotency-Key")).thenReturn(null);

        // Act
        filter.doFilter(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        assertTrue(IdempotencyContextHolder.get().isEmpty());
    }

    @Test
    void testFilterWithEmptyKeyHeader() throws ServletException, IOException {
        // Arrange
        properties.setKeyHeader("Idempotency-Key");
        properties.setRequired(false);
        when(request.getHeader("Idempotency-Key")).thenReturn("");

        // Act
        filter.doFilter(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        assertTrue(IdempotencyContextHolder.get().isEmpty());
    }

    @Test
    void testFilterWithBlankKeyHeader() throws ServletException, IOException {
        // Arrange
        properties.setKeyHeader("Idempotency-Key");
        properties.setRequired(false);
        when(request.getHeader("Idempotency-Key")).thenReturn("   ");

        // Act
        filter.doFilter(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        assertTrue(IdempotencyContextHolder.get().isEmpty());
    }

    @Test
    void testFilterWithRequiredKeyMissing() throws ServletException, IOException {
        // Arrange
        properties.setKeyHeader("Idempotency-Key");
        properties.setRequired(true);
        when(request.getHeader("Idempotency-Key")).thenReturn(null);

        // Act
        filter.doFilter(request, response, filterChain);

        // Assert
        verify(response).sendError(eq(HttpServletResponse.SC_BAD_REQUEST), anyString());
        verify(filterChain, never()).doFilter(request, response);
        assertTrue(IdempotencyContextHolder.get().isEmpty());
    }

    @Test
    void testFilterWithRequiredKeyEmpty() throws ServletException, IOException {
        // Arrange
        properties.setKeyHeader("Idempotency-Key");
        properties.setRequired(true);
        when(request.getHeader("Idempotency-Key")).thenReturn("");

        // Act
        filter.doFilter(request, response, filterChain);

        // Assert
        verify(response).sendError(eq(HttpServletResponse.SC_BAD_REQUEST), anyString());
        verify(filterChain, never()).doFilter(request, response);
        assertTrue(IdempotencyContextHolder.get().isEmpty());
    }

    @Test
    void testFilterWithCustomHeaderName() throws ServletException, IOException {
        // Arrange
        String key = "custom-key-456";
        properties.setKeyHeader("X-Custom-Idempotency-Key");
        when(request.getHeader("X-Custom-Idempotency-Key")).thenReturn(key);

        // Act
        IdempotencyContext context = captureContextDuringFilter();

        // Assert
        assertNotNull(context);
        assertEquals(key, context.idempotencyKey());
    }

    @Test
    void testFilterContextClearedAfterChain() throws ServletException, IOException {
        // Arrange
        String key = "clear-test-key";
        properties.setKeyHeader("Idempotency-Key");
        when(request.getHeader("Idempotency-Key")).thenReturn(key);

        // Act
        filter.doFilter(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        // Context should be cleared after filter chain completes
        assertTrue(IdempotencyContextHolder.get().isEmpty());
    }

    @Test
    void testFilterContextClearedOnException() throws ServletException, IOException {
        // Arrange
        String key = "exception-test-key";
        properties.setKeyHeader("Idempotency-Key");
        when(request.getHeader("Idempotency-Key")).thenReturn(key);
        doThrow(new RuntimeException("Test exception"))
                .when(filterChain).doFilter(request, response);

        // Act
        assertThrows(RuntimeException.class, () -> filter.doFilter(request, response, filterChain));

        // Assert
        // Context should still be cleared even when exception occurs
        assertTrue(IdempotencyContextHolder.get().isEmpty());
    }

    @Test
    void testFilterWithWhitespaceKey() throws ServletException, IOException {
        // Arrange
        String key = "  whitespace-key  ";
        properties.setKeyHeader("Idempotency-Key");
        when(request.getHeader("Idempotency-Key")).thenReturn(key);

        // Act
        IdempotencyContext context = captureContextDuringFilter();

        // Assert
        assertNotNull(context);
        // Key is stored as-is (no trimming)
        assertEquals(key, context.idempotencyKey());
    }

    @Test
    void testFilterWithSpecialCharactersInKey() throws ServletException, IOException {
        // Arrange
        String key = "key-with-special-chars-!@#$%^&*()";
        properties.setKeyHeader("Idempotency-Key");
        when(request.getHeader("Idempotency-Key")).thenReturn(key);

        // Act
        IdempotencyContext context = captureContextDuringFilter();

        // Assert
        assertNotNull(context);
        assertEquals(key, context.idempotencyKey());
    }

    @Test
    void testFilterWithLongKey() throws ServletException, IOException {
        // Arrange
        String key = "very-long-idempotency-key-" + "x".repeat(200);
        properties.setKeyHeader("Idempotency-Key");
        when(request.getHeader("Idempotency-Key")).thenReturn(key);

        // Act
        IdempotencyContext context = captureContextDuringFilter();

        // Assert
        assertNotNull(context);
        assertEquals(key, context.idempotencyKey());
    }

    @Test
    void testFilterDefaultHeaderName() throws ServletException, IOException {
        // Arrange
        String key = "default-header-key";
        // Use default property value (no explicit setKeyHeader call)
        properties = new IdempotencyProperties();
        filter = new IdempotencyKeyFilter(properties);
        when(request.getHeader("Idempotency-Key")).thenReturn(key);

        // Act
        IdempotencyContext context = captureContextDuringFilter();

        // Assert
        assertNotNull(context);
        assertEquals(key, context.idempotencyKey());
    }
}
