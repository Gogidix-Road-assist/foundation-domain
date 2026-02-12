package com.gogidix.rapidassist.shared.ai.contracts.application;

import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("AIExecutionContext Tests")
class AIExecutionContextTest {

    private static final String TENANT_ID = "tenant-123";
    private static final String REQUEST_ID = "req-456";
    private static final String CORRELATION_ID = "corr-789";
    private static final String USER_ID = "user-abc";

    private RequestContext createValidRequestContext() {
        return RequestContext.builder()
                .tenantId(TENANT_ID)
                .requestId(REQUEST_ID)
                .correlationId(CORRELATION_ID)
                .userId(USER_ID)
                .build();
    }

    @Test
    @DisplayName("Should create context from valid request context")
    void shouldCreateContextFromValidRequestContext() {
        RequestContext requestContext = createValidRequestContext();
        AIExecutionContext aiContext = AIExecutionContext.from(requestContext);

        assertThat(aiContext.getTenantId()).isEqualTo(TENANT_ID);
        assertThat(aiContext.getRequestId()).isEqualTo(REQUEST_ID);
        assertThat(aiContext.getCorrelationId()).isEqualTo(CORRELATION_ID);
        assertThat(aiContext.getUserId()).isEqualTo(USER_ID);
        assertThat(aiContext.getRequestContext()).isEqualTo(requestContext);
    }

    @Test
    @DisplayName("Should throw exception when request context is null")
    void shouldThrowWhenRequestContextIsNull() {
        assertThatThrownBy(() -> AIExecutionContext.from(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("RequestContext cannot be null");
    }

    @Test
    @DisplayName("Should set default values for optional properties")
    void shouldSetDefaultValuesForOptionalProperties() {
        RequestContext requestContext = createValidRequestContext();
        AIExecutionContext aiContext = AIExecutionContext.from(requestContext);

        assertThat(aiContext.getTimeoutSeconds()).isEqualTo(30);
        assertThat(aiContext.getMaxRetries()).isEqualTo(3);
        assertThat(aiContext.isLoggingEnabled()).isTrue();
        assertThat(aiContext.isMetricsEnabled()).isTrue();
    }

    @Test
    @DisplayName("Should build context with all properties")
    void shouldBuildContextWithAllProperties() {
        RequestContext requestContext = createValidRequestContext();
        AIExecutionContext aiContext = AIExecutionContext.builder(requestContext)
                .modelVersion("gpt-4")
                .confidenceThreshold(0.85)
                .timeoutSeconds(60)
                .maxRetries(5)
                .enableLogging(false)
                .enableMetrics(false)
                .traceId("trace-123")
                .spanId("span-456")
                .build();

        assertThat(aiContext.getModelVersion()).isEqualTo("gpt-4");
        assertThat(aiContext.getConfidenceThreshold()).isEqualTo(0.85);
        assertThat(aiContext.getTimeoutSeconds()).isEqualTo(60);
        assertThat(aiContext.getMaxRetries()).isEqualTo(5);
        assertThat(aiContext.isLoggingEnabled()).isFalse();
        assertThat(aiContext.isMetricsEnabled()).isFalse();
        assertThat(aiContext.getTraceId()).isEqualTo("trace-123");
        assertThat(aiContext.getSpanId()).isEqualTo("span-456");
    }

    @Test
    @DisplayName("Should convert to builder and back")
    void shouldConvertToBuilderAndBack() {
        RequestContext requestContext = createValidRequestContext();
        AIExecutionContext original = AIExecutionContext.builder(requestContext)
                .modelVersion("bert-base")
                .confidenceThreshold(0.90)
                .timeoutSeconds(45)
                .maxRetries(2)
                .build();

        AIExecutionContext rebuilt = original.toBuilder().build();

        assertThat(rebuilt.getModelVersion()).isEqualTo("bert-base");
        assertThat(rebuilt.getConfidenceThreshold()).isEqualTo(0.90);
        assertThat(rebuilt.getTimeoutSeconds()).isEqualTo(45);
        assertThat(rebuilt.getMaxRetries()).isEqualTo(2);
        assertThat(rebuilt.getTenantId()).isEqualTo(TENANT_ID);
    }

    @Test
    @DisplayName("Should support builder chaining")
    void shouldSupportBuilderChaining() {
        RequestContext requestContext = createValidRequestContext();
        AIExecutionContext aiContext = AIExecutionContext.builder(requestContext)
                .modelVersion("model-v1")
                .confidenceThreshold(0.75)
                .timeoutSeconds(90)
                .enableLogging(true)
                .enableMetrics(true)
                .build();

        assertThat(aiContext.getModelVersion()).isEqualTo("model-v1");
        assertThat(aiContext.getConfidenceThreshold()).isEqualTo(0.75);
        assertThat(aiContext.getTimeoutSeconds()).isEqualTo(90);
        assertThat(aiContext.isLoggingEnabled()).isTrue();
        assertThat(aiContext.isMetricsEnabled()).isTrue();
    }

    @Test
    @DisplayName("Should handle null optional properties")
    void shouldHandleNullOptionalProperties() {
        RequestContext requestContext = createValidRequestContext();
        AIExecutionContext aiContext = AIExecutionContext.builder(requestContext)
                .modelVersion(null)
                .confidenceThreshold(null)
                .traceId(null)
                .spanId(null)
                .build();

        assertThat(aiContext.getModelVersion()).isNull();
        assertThat(aiContext.getConfidenceThreshold()).isNull();
        assertThat(aiContext.getTraceId()).isNull();
        assertThat(aiContext.getSpanId()).isNull();
    }

    @Test
    @DisplayName("Should maintain request context through transformations")
    void shouldMaintainRequestContext() {
        RequestContext requestContext = createValidRequestContext();
        AIExecutionContext aiContext = AIExecutionContext.from(requestContext);

        // The underlying request context should be the same instance
        assertThat(aiContext.getRequestContext()).isSameAs(requestContext);
    }

    @Test
    @DisplayName("Should create independent contexts from same request context")
    void shouldCreateIndependentContexts() {
        RequestContext requestContext = createValidRequestContext();
        AIExecutionContext context1 = AIExecutionContext.builder(requestContext)
                .modelVersion("model-v1")
                .build();
        AIExecutionContext context2 = AIExecutionContext.builder(requestContext)
                .modelVersion("model-v2")
                .build();

        assertThat(context1.getModelVersion()).isEqualTo("model-v1");
        assertThat(context2.getModelVersion()).isEqualTo("model-v2");
        // Both should have the same underlying request context
        assertThat(context1.getRequestContext()).isSameAs(context2.getRequestContext());
    }

    @Test
    @DisplayName("Should support modifying context via builder")
    void shouldSupportModifyingContextViaBuilder() {
        RequestContext requestContext = createValidRequestContext();
        AIExecutionContext original = AIExecutionContext.builder(requestContext)
                .timeoutSeconds(30)
                .build();

        // Create a modified version
        AIExecutionContext modified = original.toBuilder()
                .timeoutSeconds(60)
                .modelVersion("gpt-4")
                .build();

        assertThat(original.getTimeoutSeconds()).isEqualTo(30);
        assertThat(original.getModelVersion()).isNull();

        assertThat(modified.getTimeoutSeconds()).isEqualTo(60);
        assertThat(modified.getModelVersion()).isEqualTo("gpt-4");
    }
}
