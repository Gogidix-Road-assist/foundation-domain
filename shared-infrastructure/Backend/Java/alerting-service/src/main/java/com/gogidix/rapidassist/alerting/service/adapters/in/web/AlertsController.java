package com.gogidix.rapidassist.alerting.service.adapters.in.web;

import com.gogidix.rapidassist.alerting.service.domain.model.AlertEvent;
import com.gogidix.rapidassist.alerting.service.domain.port.in.IngestAlertEventCommand;
import com.gogidix.rapidassist.alerting.service.domain.port.in.QueryAlertEventsQuery;
import com.gogidix.rapidassist.alerting.service.infrastructure.web.IngestAlertEventRequest;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RequestMapping("/api/v1/alerts")
@Tag(name = "Alerts", description = "APIs for ingesting and querying alert events")
public class AlertsController {

    private final IngestAlertEventCommand ingest;
    private final QueryAlertEventsQuery query;

    public AlertsController(IngestAlertEventCommand ingest, QueryAlertEventsQuery query) {
        this.ingest = ingest;
        this.query = query;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Ingest a new alert event", description = "Ingests an alert event into the system. Requires valid tenantId in JWT context.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Alert event accepted for processing"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid authentication")
    })
    public void ingest(@Valid @RequestBody IngestAlertEventRequest request) {
        RequestContext ctx = RequestContextHolder.get().orElse(null);
        if (ctx == null || ctx.tenantId() == null || ctx.tenantId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing tenantId in JWT context");
        }

        String correlationId = request.getCorrelationId();
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = ctx.correlationId();
        }

        ingest.ingest(new AlertEvent(
                ctx.tenantId(),
                ctx.country(),
                correlationId,
                Instant.now(),
                request.getRuleId(),
                request.getSeverity(),
                request.getMessage(),
                request.getAttributes()
        ));
    }

    @GetMapping
    @Operation(summary = "Query alert events", description = "Queries alert events with optional filters for ruleId and time range. Requires valid tenantId in JWT context.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved alert events"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid authentication")
    })
    public List<AlertEvent> query(
            @Parameter(description = "Filter by alert rule ID")
            @RequestParam(required = false) String ruleId,
            @Parameter(description = "Start of time range (ISO-8601 format)")
            @RequestParam(required = false) Instant from,
            @Parameter(description = "End of time range (ISO-8601 format)")
            @RequestParam(required = false) Instant to,
            @Parameter(description = "Maximum number of results to return")
            @RequestParam(defaultValue = "100") int limit
    ) {
        RequestContext ctx = RequestContextHolder.get().orElse(null);
        if (ctx == null || ctx.tenantId() == null || ctx.tenantId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing tenantId in JWT context");
        }

        return query.query(ctx.tenantId(), ruleId, from, to, limit);
    }
}
