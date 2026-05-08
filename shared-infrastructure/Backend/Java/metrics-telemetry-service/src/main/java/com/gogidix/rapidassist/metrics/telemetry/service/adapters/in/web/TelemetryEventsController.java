package com.gogidix.rapidassist.metrics.telemetry.service.adapters.in.web;

import com.gogidix.rapidassist.metrics.telemetry.service.domain.model.TelemetryEvent;
import com.gogidix.rapidassist.metrics.telemetry.service.domain.port.in.IngestTelemetryEventCommand;
import com.gogidix.rapidassist.metrics.telemetry.service.domain.port.in.QueryTelemetryEventsQuery;
import com.gogidix.rapidassist.metrics.telemetry.service.infrastructure.web.IngestTelemetryEventRequest;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/telemetry")
@Tag(name = "Telemetry Events", description = "APIs for ingesting and querying telemetry events")
@SecurityRequirement(name = "bearerAuth")
public class TelemetryEventsController {

    private final IngestTelemetryEventCommand ingest;
    private final QueryTelemetryEventsQuery query;

    public TelemetryEventsController(IngestTelemetryEventCommand ingest, QueryTelemetryEventsQuery query) {
        this.ingest = ingest;
        this.query = query;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Ingest a telemetry event", description = "Ingests a telemetry event into the metrics system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Telemetry event accepted for processing"),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content),
            @ApiResponse(responseCode = "401", description = "Missing or invalid authentication", content = @Content)
    })
    public void ingest(@Valid @RequestBody IngestTelemetryEventRequest request) {
        RequestContext ctx = RequestContextHolder.get().orElse(null);
        if (ctx == null || ctx.tenantId() == null || ctx.tenantId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing tenantId in JWT context");
        }

        String correlationId = request.getCorrelationId();
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = ctx.correlationId();
        }

        ingest.ingest(new TelemetryEvent(
                ctx.tenantId(),
                ctx.country(),
                correlationId,
                Instant.now(),
                request.getType(),
                request.getName(),
                request.getValue(),
                request.getAttributes()
        ));
    }

    @GetMapping
    @Operation(summary = "Query telemetry events", description = "Queries telemetry events with optional filters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Telemetry events retrieved successfully",
                    content = @Content(schema = @Schema(implementation = TelemetryEvent.class))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid authentication", content = @Content)
    })
    public List<TelemetryEvent> query(
            @Parameter(description = "Correlation ID to filter by") @RequestParam(required = false) String correlationId,
            @Parameter(description = "Start timestamp for filtering") @RequestParam(required = false) Instant from,
            @Parameter(description = "End timestamp for filtering") @RequestParam(required = false) Instant to,
            @Parameter(description = "Event type to filter by") @RequestParam(required = false) String type,
            @Parameter(description = "Event name to filter by") @RequestParam(required = false) String name,
            @Parameter(description = "Maximum number of results to return") @RequestParam(defaultValue = "100") int limit
    ) {
        RequestContext ctx = RequestContextHolder.get().orElse(null);
        if (ctx == null || ctx.tenantId() == null || ctx.tenantId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing tenantId in JWT context");
        }

        return query.query(ctx.tenantId(), correlationId, from, to, type, name, limit);
    }
}
