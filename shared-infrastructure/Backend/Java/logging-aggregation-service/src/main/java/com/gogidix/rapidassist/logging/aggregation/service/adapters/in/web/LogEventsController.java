package com.gogidix.rapidassist.logging.aggregation.service.adapters.in.web;

import com.gogidix.rapidassist.logging.aggregation.service.domain.model.LogEvent;
import com.gogidix.rapidassist.logging.aggregation.service.domain.port.in.IngestLogEventCommand;
import com.gogidix.rapidassist.logging.aggregation.service.domain.port.in.QueryLogEventsQuery;
import com.gogidix.rapidassist.logging.aggregation.service.infrastructure.web.IngestLogEventRequest;
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
@RequestMapping("/api/v1/logs")
@Tag(name = "Log Events", description = "APIs for ingesting and querying log events")
@SecurityRequirement(name = "bearerAuth")
public class LogEventsController {

    private final IngestLogEventCommand ingest;
    private final QueryLogEventsQuery query;

    public LogEventsController(IngestLogEventCommand ingest, QueryLogEventsQuery query) {
        this.ingest = ingest;
        this.query = query;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Ingest a log event", description = "Ingests a log event into the centralized logging system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Log event accepted for processing"),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content),
            @ApiResponse(responseCode = "401", description = "Missing or invalid authentication", content = @Content)
    })
    public void ingest(@Valid @RequestBody IngestLogEventRequest request) {
        RequestContext ctx = RequestContextHolder.get().orElse(null);
        if (ctx == null || ctx.tenantId() == null || ctx.tenantId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing tenantId in JWT context");
        }

        String correlationId = request.getCorrelationId();
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = ctx.correlationId();
        }

        ingest.ingest(new LogEvent(
                ctx.tenantId(),
                ctx.country(),
                correlationId,
                Instant.now(),
                request.getLevel(),
                request.getLogger(),
                request.getMessage(),
                request.getAttributes()
        ));
    }

    @GetMapping
    @Operation(summary = "Query log events", description = "Queries log events with optional filters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Log events retrieved successfully",
                    content = @Content(schema = @Schema(implementation = LogEvent.class))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid authentication", content = @Content)
    })
    public List<LogEvent> query(
            @Parameter(description = "Correlation ID to filter by") @RequestParam(required = false) String correlationId,
            @Parameter(description = "Start timestamp for filtering") @RequestParam(required = false) Instant from,
            @Parameter(description = "End timestamp for filtering") @RequestParam(required = false) Instant to,
            @Parameter(description = "Log level to filter by (DEBUG, INFO, WARN, ERROR)") @RequestParam(required = false) String level,
            @Parameter(description = "Maximum number of results to return") @RequestParam(defaultValue = "100") int limit
    ) {
        RequestContext ctx = RequestContextHolder.get().orElse(null);
        if (ctx == null || ctx.tenantId() == null || ctx.tenantId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing tenantId in JWT context");
        }

        return query.query(ctx.tenantId(), correlationId, from, to, level, limit);
    }
}
