package com.gogidix.rapidassist.shared.request.context.library.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

/**
 * Request context containing information about the current request.
 * Used for multi-tenancy, tracing, and user context propagation across threads.
 *
 * @author Rapid Assist
 * @version 1.0.0
 * @since 1.0.0
 *
 * @param correlationId the correlation ID for request tracing
 * @param country the country code
 * @param tenantId the tenant ID (required)
 * @param userId the user ID
 * @param requestId the request ID (must be valid UUID if provided)
 */
public record RequestContext(
        String correlationId,
        String country,
        @NotBlank(message = "TenantId is required")
        String tenantId,

        String userId,

        @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-"
                        + "[0-9a-f]{4}-[0-9a-f]{12}$",
                message = "RequestId must be a valid UUID")
        String requestId
) {

    /**
     * Validates the request context.
     *
     * @throws IllegalStateException if validation fails
     */
    public void validate() {
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalStateException("TenantId is required and cannot be blank");
        }

        if (requestId != null && !isValidUUID(requestId)) {
            throw new IllegalStateException("RequestId must be a valid UUID format: " + requestId);
        }
    }

    /**
     * Creates a validated request context.
     *
     * @return this context after validation
     * @throws IllegalStateException if validation fails
     */
    public RequestContext validated() {
        validate();
        return this;
    }

    /**
     * Checks if the given string is a valid UUID.
     *
     * @param uuid the string to check
     * @return true if the string is a valid UUID, false otherwise
     */
    private static boolean isValidUUID(final String uuid) {
        if (uuid == null || uuid.isBlank()) {
            return false;
        }
        try {
            UUID.fromString(uuid);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Creates a builder for RequestContext.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for RequestContext.
     */
    public static class Builder {
        private String correlationId;
        private String country;
        private String tenantId;
        private String userId;
        private String requestId;

        /**
         * Sets the correlation ID.
         *
         * @param correlationId the correlation ID
         * @return this builder
         */
        public Builder correlationId(final String correlationId) {
            this.correlationId = correlationId;
            return this;
        }

        /**
         * Sets the country code.
         *
         * @param country the country code
         * @return this builder
         */
        public Builder country(final String country) {
            this.country = country;
            return this;
        }

        /**
         * Sets the tenant ID.
         *
         * @param tenantId the tenant ID
         * @return this builder
         */
        public Builder tenantId(final String tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        /**
         * Sets the user ID.
         *
         * @param userId the user ID
         * @return this builder
         */
        public Builder userId(final String userId) {
            this.userId = userId;
            return this;
        }

        /**
         * Sets the request ID.
         *
         * @param requestId the request ID
         * @return this builder
         */
        public Builder requestId(final String requestId) {
            this.requestId = requestId;
            return this;
        }

        /**
         * Builds the RequestContext and validates it.
         *
         * @return validated RequestContext
         * @throws IllegalStateException if validation fails
         */
        public RequestContext build() {
            RequestContext context = new RequestContext(correlationId, country, tenantId, userId, requestId);
            context.validate();
            return context;
        }
    }
}
