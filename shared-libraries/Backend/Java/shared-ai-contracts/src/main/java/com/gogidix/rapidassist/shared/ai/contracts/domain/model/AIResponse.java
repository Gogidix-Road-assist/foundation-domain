package com.gogidix.rapidassist.shared.ai.contracts.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.Map;
import java.util.TreeMap;

/**
 * Base response model for all AI service operations.
 * Provides consistent structure for all AI responses including:
 * - Execution metadata
 * - Success/failure status
 * - Result data
 * - Tenant isolation information
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class AIResponse {

    /**
     * The request ID that generated this response.
     */
    @JsonProperty("request_id")
    protected String requestId;

    /**
     * Tenant identifier for this response.
     */
    @JsonProperty("tenant_id")
    protected String tenantId;

    /**
     * Indicates if the AI operation was successful.
     */
    @JsonProperty("success")
    protected boolean success;

    /**
     * HTTP-like status code for the operation.
     * 200-299: Success
     * 400-499: Client errors
     * 500-599: Server errors
     */
    @JsonProperty("status_code")
    protected int statusCode;

    /**
     * Human-readable status message.
     */
    @JsonProperty("status_message")
    protected String statusMessage;

    /**
     * Detailed error message if the operation failed.
     */
    @JsonProperty("error_message")
    protected String errorMessage;

    /**
     * Error code for categorization of failures.
     */
    @JsonProperty("error_code")
    protected String errorCode;

    /**
     * When the response was generated.
     */
    @JsonProperty("response_timestamp")
    protected Instant responseTimestamp;

    /**
     * Execution metadata including timing, resource usage, etc.
     */
    @JsonProperty("execution_metadata")
    protected AIExecutionMetadata executionMetadata;

    /**
     * Additional service-specific metadata.
     */
    @JsonProperty("metadata")
    protected Map<String, Object> metadata;

    /**
     * Default constructor.
     */
    protected AIResponse() {
        this.responseTimestamp = Instant.now();
        this.statusCode = 200;
        this.success = true;
        this.metadata = new TreeMap<>();
    }

    /**
     * Constructor with request ID and tenant ID.
     *
     * @param requestId The request ID
     * @param tenantId  The tenant ID
     */
    protected AIResponse(String requestId, String tenantId) {
        this();
        this.requestId = requestId;
        this.tenantId = tenantId;
    }

    /**
     * Creates a success response.
     *
     * @param requestId The request ID
     * @param tenantId  The tenant ID
     * @param <T>       The response type
     * @return A new success response
     */
    public static <T extends AIResponse> T success(String requestId, String tenantId) {
        throw new UnsupportedOperationException("Subclasses must implement this method");
    }

    /**
     * Creates a failure response.
     *
     * @param requestId    The request ID
     * @param tenantId     The tenant ID
     * @param errorMessage The error message
     * @param errorCode    The error code
     * @param <T>          The response type
     * @return A new failure response
     */
    public static <T extends AIResponse> T failure(String requestId, String tenantId, String errorMessage, String errorCode) {
        throw new UnsupportedOperationException("Subclasses must implement this method");
    }

    // Getters and Setters

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
        this.success = statusCode >= 200 && statusCode < 300;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public Instant getResponseTimestamp() {
        return responseTimestamp;
    }

    public void setResponseTimestamp(Instant responseTimestamp) {
        this.responseTimestamp = responseTimestamp;
    }

    public AIExecutionMetadata getExecutionMetadata() {
        return executionMetadata;
    }

    public void setExecutionMetadata(AIExecutionMetadata executionMetadata) {
        this.executionMetadata = executionMetadata;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    /**
     * Gets the result data for this response.
     * Each service type must define its specific result.
     *
     * @return The result data
     */
    public abstract Object getResultData();

    /**
     * Checks if this response has a specific error code.
     *
     * @param errorCode The error code to check
     * @return true if the error code matches
     */
    public boolean hasError(String errorCode) {
        return !this.success && errorCode != null && errorCode.equals(this.errorCode);
    }
}
