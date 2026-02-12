package com.gogidix.rapidassist.shared.request.context.library.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for RequestContext validation.
 */
class RequestContextTest {

    @Test
    void shouldBuildValidRequestContext() {
        RequestContext context = RequestContext.builder()
                .correlationId("corr-123")
                .tenantId("tenant-456")
                .userId("user-789")
                .requestId(UUID.randomUUID().toString())
                .country("US")
                .build();

        assertThat(context.tenantId()).isEqualTo("tenant-456");
        assertThat(context.userId()).isEqualTo("user-789");
        assertThat(context.country()).isEqualTo("US");
        assertThat(context.correlationId()).isEqualTo("corr-123");
    }

    @Test
    void shouldRequireTenantId() {
        assertThatThrownBy(() -> {
            RequestContext.builder()
                    .tenantId(null)
                    .build();
        })
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("TenantId is required");
    }

    @Test
    void shouldRejectBlankTenantId() {
        assertThatThrownBy(() -> {
            RequestContext.builder()
                    .tenantId("")
                    .build();
        })
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("TenantId is required");
    }

    @Test
    void shouldRejectWhitespaceTenantId() {
        assertThatThrownBy(() -> {
            RequestContext.builder()
                    .tenantId("   ")
                    .build();
        })
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("TenantId is required");
    }

    @Test
    void shouldValidateRequestIdFormat() {
        String invalidRequestId = "not-a-uuid";

        assertThatThrownBy(() -> {
            RequestContext.builder()
                    .tenantId("tenant-123")
                    .requestId(invalidRequestId)
                    .build();
        })
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("RequestId must be a valid UUID");
    }

    @Test
    void shouldAcceptValidRequestId() {
        String validRequestId = UUID.randomUUID().toString();

        RequestContext context = RequestContext.builder()
                .tenantId("tenant-123")
                .requestId(validRequestId)
                .build();

        assertThat(context.requestId()).isEqualTo(validRequestId);
    }

    @Test
    void shouldAcceptNullRequestId() {
        RequestContext context = RequestContext.builder()
                .tenantId("tenant-123")
                .requestId(null)
                .build();

        assertThat(context.requestId()).isNull();
    }

    @Test
    void shouldAcceptNullUserId() {
        RequestContext context = RequestContext.builder()
                .tenantId("tenant-123")
                .userId(null)
                .build();

        assertThat(context.userId()).isNull();
    }

    @Test
    void shouldAcceptNullCountry() {
        RequestContext context = RequestContext.builder()
                .tenantId("tenant-123")
                .country(null)
                .build();

        assertThat(context.country()).isNull();
    }

    @Test
    void shouldAcceptNullCorrelationId() {
        RequestContext context = RequestContext.builder()
                .tenantId("tenant-123")
                .correlationId(null)
                .build();

        assertThat(context.correlationId()).isNull();
    }

    @Test
    void shouldValidateMethodThrowException() {
        RequestContext context = new RequestContext(null, null, null, null, null);

        assertThatThrownBy(context::validate)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("TenantId is required");
    }

    @Test
    void shouldValidateMethodPassForValidContext() {
        RequestContext context = RequestContext.builder()
                .tenantId("tenant-123")
                .build();

        context.validate(); // Should not throw
    }

    @Test
    void shouldValidateMethodCheckInvalidUUID() {
        RequestContext context = new RequestContext(null, null, "tenant-123", null, "invalid-uuid");

        assertThatThrownBy(context::validate)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("RequestId must be a valid UUID");
    }

    @Test
    void shouldBuildMinimalValidContext() {
        RequestContext context = RequestContext.builder()
                .tenantId("tenant-123")
                .build();

        assertThat(context.tenantId()).isEqualTo("tenant-123");
        assertThat(context.userId()).isNull();
        assertThat(context.requestId()).isNull();
        assertThat(context.country()).isNull();
        assertThat(context.correlationId()).isNull();
    }

    @Test
    void shouldSupportBuilderPattern() {
        RequestContext context = RequestContext.builder()
                .correlationId("corr-1")
                .tenantId("tenant-1")
                .userId("user-1")
                .requestId(UUID.randomUUID().toString())
                .country("UK")
                .build();

        assertThat(context.correlationId()).isEqualTo("corr-1");
        assertThat(context.tenantId()).isEqualTo("tenant-1");
        assertThat(context.userId()).isEqualTo("user-1");
        assertThat(context.country()).isEqualTo("UK");
    }

    @Test
    void shouldAcceptUUIDWithoutDashes() {
        // UUID validation should reject UUIDs without dashes
        assertThatThrownBy(() -> RequestContext.builder()
                .tenantId("tenant-123")
                .requestId("123e4567e89b12d3a456426614174000")
                .build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("RequestId must be a valid UUID format");
    }

    @Test
    void shouldAcceptValidUUIDFormats() {
        String validUUID = UUID.randomUUID().toString();

        RequestContext context = RequestContext.builder()
                .tenantId("tenant-123")
                .requestId(validUUID)
                .build();

        assertThat(context.requestId()).isEqualTo(validUUID);
    }

    @Test
    void shouldAllowEmptyOptionalFields() {
        RequestContext context = RequestContext.builder()
                .tenantId("tenant-123")
                .userId("")
                .country("")
                .correlationId("")
                .build();

        assertThat(context.tenantId()).isEqualTo("tenant-123");
        assertThat(context.userId()).isEmpty();
        assertThat(context.country()).isEmpty();
        assertThat(context.correlationId()).isEmpty();
    }
}
