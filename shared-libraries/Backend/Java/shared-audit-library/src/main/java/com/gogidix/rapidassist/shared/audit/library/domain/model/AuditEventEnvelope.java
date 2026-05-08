package com.gogidix.rapidassist.shared.audit.library.domain.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.Map;

public record AuditEventEnvelope(
        @NotBlank String specVersion,
        @NotBlank String eventId,
        @NotBlank String eventType,
        @NotNull Instant occurredAt,
        @NotBlank String correlationId,
        @NotBlank String country,
        @NotBlank String tenantId,
        String subTenantId,
        @Valid @NotNull AuditActor actor,
        @Valid @NotNull AuditEntityRef entity,
        Map<String, Object> attributes,
        @NotNull Object payload
) {
}
