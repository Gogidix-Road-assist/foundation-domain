package com.gogidix.rapidassist.shared.exception.library.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for custom exception types.
 */
class CustomExceptionsTest {

    @Test
    void testNotFoundExceptionWithEntityAndId() {
        NotFoundException ex = new NotFoundException("Customer", "customer-123");

        assertEquals(404, ex.getStatusCode().value());
        assertEquals("Resource not found", ex.getBody().getTitle());
        assertTrue(ex.getBody().getDetail().contains("Customer"));
        assertTrue(ex.getBody().getDetail().contains("customer-123"));
        assertEquals("Customer", ex.getBody().getProperties().get("entityType"));
        assertEquals("customer-123", ex.getBody().getProperties().get("entityId"));
    }

    @Test
    void testNotFoundExceptionWithMessage() {
        String message = "Resource not found in database";
        NotFoundException ex = new NotFoundException(message);

        assertEquals(404, ex.getStatusCode().value());
        assertEquals("Resource not found", ex.getBody().getTitle());
        assertEquals(message, ex.getBody().getDetail());
    }

    @Test
    void testBadRequestExceptionWithDetail() {
        String detail = "Invalid date format";
        BadRequestException ex = new BadRequestException(detail);

        assertEquals(400, ex.getStatusCode().value());
        assertEquals("Bad request", ex.getBody().getTitle());
        assertEquals(detail, ex.getBody().getDetail());
    }

    @Test
    void testBadRequestExceptionWithTitleAndDetail() {
        BadRequestException ex = new BadRequestException("Validation error", "Email is required");

        assertEquals(400, ex.getStatusCode().value());
        assertEquals("Validation error", ex.getBody().getTitle());
        assertEquals("Email is required", ex.getBody().getDetail());
    }

    @Test
    void testBadRequestExceptionWithErrorCode() {
        BadRequestException ex = new BadRequestException("Invalid input", "Field cannot be null", "ERR_NULL_FIELD");

        assertEquals(400, ex.getStatusCode().value());
        assertEquals("Invalid input", ex.getBody().getTitle());
        assertEquals("ERR_NULL_FIELD", ex.getBody().getProperties().get("errorCode"));
    }

    @Test
    void testConflictExceptionWithEntityAndField() {
        ConflictException ex = new ConflictException("Customer", "email", "john@example.com");

        assertEquals(409, ex.getStatusCode().value());
        assertEquals("Resource conflict", ex.getBody().getTitle());
        assertTrue(ex.getBody().getDetail().contains("Customer"));
        assertTrue(ex.getBody().getDetail().contains("email"));
        assertTrue(ex.getBody().getDetail().contains("john@example.com"));
        assertEquals("Customer", ex.getBody().getProperties().get("entityType"));
        assertEquals("email", ex.getBody().getProperties().get("field"));
        assertEquals("john@example.com", ex.getBody().getProperties().get("value"));
    }

    @Test
    void testConflictExceptionWithMessage() {
        String detail = "Concurrent modification detected";
        ConflictException ex = new ConflictException(detail);

        assertEquals(409, ex.getStatusCode().value());
        assertEquals("Resource conflict", ex.getBody().getTitle());
        assertEquals(detail, ex.getBody().getDetail());
    }

    @Test
    void testForbiddenExceptionWithResourceAndAction() {
        ForbiddenException ex = new ForbiddenException("customer-123", "DELETE");

        assertEquals(403, ex.getStatusCode().value());
        assertEquals("Access forbidden", ex.getBody().getTitle());
        assertTrue(ex.getBody().getDetail().contains("DELETE"));
        assertTrue(ex.getBody().getDetail().contains("customer-123"));
        assertEquals("customer-123", ex.getBody().getProperties().get("resourceId"));
        assertEquals("DELETE", ex.getBody().getProperties().get("action"));
    }

    @Test
    void testForbiddenExceptionWithDetail() {
        String detail = "Insufficient permissions";
        ForbiddenException ex = new ForbiddenException(detail);

        assertEquals(403, ex.getStatusCode().value());
        assertEquals("Access forbidden", ex.getBody().getTitle());
        assertEquals(detail, ex.getBody().getDetail());
    }

    @Test
    void testForbiddenExceptionWithPermission() {
        ForbiddenException ex = new ForbiddenException("Customer", "customer.delete", "You don't have permission to delete customers");

        assertEquals(403, ex.getStatusCode().value());
        assertEquals("Access forbidden", ex.getBody().getTitle());
        assertEquals("Customer", ex.getBody().getProperties().get("resource"));
        assertEquals("customer.delete", ex.getBody().getProperties().get("requiredPermission"));
    }

    @Test
    void testUnauthorizedExceptionWithMessage() {
        String detail = "Invalid token";
        UnauthorizedException ex = new UnauthorizedException(detail);

        assertEquals(401, ex.getStatusCode().value());
        assertEquals("Unauthorized", ex.getBody().getTitle());
        assertEquals(detail, ex.getBody().getDetail());
    }

    @Test
    void testUnauthorizedExceptionWithErrorCode() {
        UnauthorizedException ex = new UnauthorizedException("Token expired", "TOKEN_EXPIRED");

        assertEquals(401, ex.getStatusCode().value());
        assertEquals("TOKEN_EXPIRED", ex.getBody().getProperties().get("errorCode"));
    }

    @Test
    void testUnauthorizedExceptionExpiredToken() {
        UnauthorizedException ex = UnauthorizedException.expiredToken();

        assertEquals(401, ex.getStatusCode().value());
        assertEquals("TOKEN_EXPIRED", ex.getBody().getProperties().get("errorCode"));
        assertTrue(ex.getBody().getDetail().contains("expired"));
    }

    @Test
    void testUnauthorizedExceptionInvalidToken() {
        UnauthorizedException ex = UnauthorizedException.invalidToken();

        assertEquals(401, ex.getStatusCode().value());
        assertEquals("INVALID_TOKEN", ex.getBody().getProperties().get("errorCode"));
        assertTrue(ex.getBody().getDetail().contains("Invalid"));
    }

    @Test
    void testUnauthorizedExceptionMissingCredentials() {
        UnauthorizedException ex = UnauthorizedException.missingCredentials();

        assertEquals(401, ex.getStatusCode().value());
        assertEquals("MISSING_CREDENTIALS", ex.getBody().getProperties().get("errorCode"));
        assertTrue(ex.getBody().getDetail().contains("required"));
    }

    @Test
    void testServiceUnavailableExceptionWithServiceAndDetail() {
        ServiceUnavailableException ex = new ServiceUnavailableException("Payment Gateway", "Maintenance mode");

        assertEquals(503, ex.getStatusCode().value());
        assertEquals("Service unavailable", ex.getBody().getTitle());
        assertTrue(ex.getBody().getDetail().contains("Payment Gateway"));
        assertTrue(ex.getBody().getDetail().contains("Maintenance mode"));
        assertEquals("Payment Gateway", ex.getBody().getProperties().get("service"));
    }

    @Test
    void testServiceUnavailableExceptionWithDetail() {
        String detail = "Database connection failed";
        ServiceUnavailableException ex = new ServiceUnavailableException(detail);

        assertEquals(503, ex.getStatusCode().value());
        assertEquals("Service unavailable", ex.getBody().getTitle());
        assertEquals(detail, ex.getBody().getDetail());
    }

    @Test
    void testServiceUnavailableExceptionWithRetryAfter() {
        ServiceUnavailableException ex = new ServiceUnavailableException("External API", "Rate limit exceeded", 300);

        assertEquals(503, ex.getStatusCode().value());
        assertEquals("External API", ex.getBody().getProperties().get("service"));
        // retryAfter may be stored as Integer or wrapped in a List depending on Spring version
        Object retryAfter = ex.getBody().getProperties().get("retryAfter");
        assertTrue(retryAfter.equals(300) || (retryAfter instanceof java.util.List && ((java.util.List<?>) retryAfter).get(0).equals(300)));
        // Retry-After header is returned as a List by HttpHeaders
        var headerValues = ex.getHeaders().get("Retry-After");
        assertTrue(headerValues != null && !headerValues.isEmpty());
        assertTrue(headerValues.get(0).equals("300"));
    }
}
