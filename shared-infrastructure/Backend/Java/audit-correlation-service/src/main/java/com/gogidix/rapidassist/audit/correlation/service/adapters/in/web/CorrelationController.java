package com.gogidix.rapidassist.audit.correlation.service.adapters.in.web;

import com.gogidix.rapidassist.audit.correlation.service.domain.model.CorrelationRecord;
import com.gogidix.rapidassist.audit.correlation.service.domain.port.in.GetCorrelationQuery;
import com.gogidix.rapidassist.audit.correlation.service.domain.port.in.UpsertCorrelationCommand;
import com.gogidix.rapidassist.audit.correlation.service.infrastructure.web.UpsertCorrelationRequest;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/correlations")
@Tag(name = "Correlation Records", description = "APIs for managing audit trail correlation records")
public class CorrelationController {

    private final UpsertCorrelationCommand upsert;
    private final GetCorrelationQuery get;

    public CorrelationController(UpsertCorrelationCommand upsert, GetCorrelationQuery get) {
        this.upsert = upsert;
        this.get = get;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Create or update a correlation record", description = "Upserts a correlation record for audit trail tracking. Creates new record if it doesn't exist, otherwise updates existing record. Requires valid tenantId in JWT context.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Correlation record accepted for processing"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid authentication")
    })
    public void upsert(@Valid @RequestBody UpsertCorrelationRequest request) {
        String tenantId = RequestContextHolder.get().map(c -> c.tenantId()).orElse(null);
        String country = RequestContextHolder.get().map(c -> c.country()).orElse(null);

        if (tenantId == null || tenantId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing tenantId in JWT context");
        }

        upsert.upsert(new CorrelationRecord(
                tenantId,
                country,
                request.getCorrelationId(),
                Instant.now(),
                request.getTags()
        ));
    }

    @GetMapping("/{correlationId}")
    @Operation(summary = "Get correlation record by ID", description = "Retrieves a correlation record by its correlation ID. Requires valid tenantId in JWT context.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved correlation record"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid authentication"),
            @ApiResponse(responseCode = "404", description = "Correlation record not found")
    })
    public CorrelationRecord get(
            @Parameter(description = "Correlation ID to retrieve")
            @PathVariable String correlationId) {
        String tenantId = RequestContextHolder.get().map(c -> c.tenantId()).orElse(null);
        if (tenantId == null || tenantId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing tenantId in JWT context");
        }

        return get.get(tenantId, correlationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Correlation not found"));
    }
}
