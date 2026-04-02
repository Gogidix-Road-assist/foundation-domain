package com.gogidix.rapidassist.shared.ai.contracts.domain.port;

import com.gogidix.rapidassist.shared.ai.contracts.domain.model.AIRequest;
import com.gogidix.rapidassist.shared.ai.contracts.domain.model.AIResponse;
import com.gogidix.rapidassist.shared.ai.contracts.domain.model.AIExecutionMetadata;

/**
 * Primary port interface for AI service execution.
 * All AI services must implement this port to ensure consistent contract.
 *
 * This port follows the Hexagonal Architecture pattern where:
 * - Application layer uses this port (IN/DRIVING port)
 * - AI services implement this port (adapter)
 *
 * @param <R> The type of AI request
 * @param <P> The type of AI response
 */
public interface IAIExecutionPort<R extends AIRequest, P extends AIResponse> {

    /**
     * Executes the AI operation with the given request.
     * This method MUST:
     * - Validate the request
     * - Execute within tenant context
     * - Record observability metrics
     * - Return a valid response or throw AIExecutionException
     *
     * @param request The AI request containing input data and tenant context
     * @return AIResponse containing the result and execution metadata
     * @throws AIExecutionException if execution fails
     */
    P execute(R request);

    /**
     * Validates the request before execution.
     * Implementations should validate:
     * - Required fields are present
     * - Tenant context is valid
     * - Input data meets service-specific constraints
     *
     * @param request The request to validate
     * @throws IllegalArgumentException if validation fails
     */
    void validate(R request);

    /**
     * Checks if the AI service is available for the given tenant.
     * This can be used for feature flags, rate limiting, or service health checks.
     *
     * @param tenantId The tenant identifier
     * @return true if the service is available for the tenant
     */
    default boolean isAvailable(String tenantId) {
        return true;
    }

    /**
     * Gets the service type identifier for this AI service.
     * Examples: CONTENT_MODERATION, NLP_PROCESSING, IMAGE_RECOGNITION
     *
     * @return The service type identifier
     */
    AIExecutionMetadata.ServiceType getServiceType();
}
