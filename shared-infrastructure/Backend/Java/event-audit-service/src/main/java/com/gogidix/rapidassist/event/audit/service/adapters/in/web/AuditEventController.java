package com.gogidix.rapidassist.event.audit.service.adapters.in.web;

import com.gogidix.rapidassist.event.audit.service.domain.model.AuditEvent;
import com.gogidix.rapidassist.event.audit.service.domain.port.in.AppendAuditEventCommand;
import com.gogidix.rapidassist.event.audit.service.domain.port.in.QueryAuditEventsQuery;
import com.gogidix.rapidassist.event.audit.service.infrastructure.config.OpenApiConfiguration;
import com.gogidix.rapidassist.event.audit.service.infrastructure.web.AuditEventRequest;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/audit/events")
@Tag(name = "Audit Events", description = "APIs for managing audit event trails")
@SecurityRequirement(name = OpenApiConfiguration.BEARER_AUTH)
public class AuditEventController {

    private final AppendAuditEventCommand appendCommand;
    private final QueryAuditEventsQuery query;

    public AuditEventController(AppendAuditEventCommand appendCommand, QueryAuditEventsQuery query) {
        this.appendCommand = appendCommand;
        this.query = query;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(
            summary = "Append audit event",
            description = "Appends a new audit event to the audit trail. Requires valid JWT token with tenantId."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "202",
                    description = "Audit event accepted for processing",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid authentication"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public String append(@Valid @RequestBody AuditEventRequest request) {
        String tenantId = RequestContextHolder.get().map(c -> c.tenantId()).orElse(null);
        String country = RequestContextHolder.get().map(c -> c.country()).orElse(null);
        String correlationId = RequestContextHolder.get().map(c -> c.correlationId()).orElse(null);

        if (tenantId == null || tenantId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing tenantId in JWT context");
        }

        AuditEvent event = new AuditEvent(
                tenantId,
                country,
                correlationId,
                request.getOccurredAt(),
                request.getEventType(),
                request.getEntityType(),
                request.getEntityId(),
                request.getPayload()
        );

        return appendCommand.append(event);
    }

    @GetMapping
    @Operation(
            summary = "Query audit events",
            description = "Queries audit events with optional filters. All events are scoped to the authenticated tenant."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Audit events retrieved successfully",
                    content = @Content(schema = @Schema(implementation = AuditEvent.class))
            ),
            @ApiResponse(responseCode = "401", description = "Missing or invalid authentication"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public List<AuditEvent> query(
            @Parameter(description = "Filter by country code") @RequestParam(required = false) String country,
            @Parameter(description = "Filter by entity type") @RequestParam(required = false) String entityType,
            @Parameter(description = "Filter by entity ID") @RequestParam(required = false) String entityId,
            @Parameter(description = "Filter by start timestamp") @RequestParam(required = false) Instant from,
            @Parameter(description = "Filter by end timestamp") @RequestParam(required = false) Instant to,
            @Parameter(description = "Maximum number of results") @RequestParam(defaultValue = "100") int limit
    ) {
        String tenantId = RequestContextHolder.get().map(c -> c.tenantId()).orElse(null);
        if (tenantId == null || tenantId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing tenantId in JWT context");
        }
        return query.query(tenantId, country, entityType, entityId, from, to, limit);
    }
}
