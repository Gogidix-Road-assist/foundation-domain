package com.gogidix.rapidassist.idempotency.service.adapters.in.web;

import com.gogidix.rapidassist.idempotency.service.domain.model.IdempotencyRecord;
import com.gogidix.rapidassist.idempotency.service.domain.port.in.GetIdempotencyRecordQuery;
import com.gogidix.rapidassist.idempotency.service.domain.port.in.ReserveIdempotencyKeyCommand;
import com.gogidix.rapidassist.idempotency.service.infrastructure.config.OpenApiConfiguration;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/idempotency")
@Tag(name = "Idempotency", description = "APIs for managing idempotency keys")
@SecurityRequirement(name = OpenApiConfiguration.BEARER_AUTH)
public class IdempotencyController {

    private final ReserveIdempotencyKeyCommand reserveIdempotencyKeyCommand;
    private final GetIdempotencyRecordQuery getIdempotencyRecordQuery;

    public IdempotencyController(ReserveIdempotencyKeyCommand reserveIdempotencyKeyCommand, GetIdempotencyRecordQuery getIdempotencyRecordQuery) {
        this.reserveIdempotencyKeyCommand = reserveIdempotencyKeyCommand;
        this.getIdempotencyRecordQuery = getIdempotencyRecordQuery;
    }

    @PostMapping("/reserve")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Reserve idempotency key",
            description = "Reserves an idempotency key for the current operation. Returns the key status."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Idempotency key reserved successfully",
                    content = @Content(schema = @Schema(implementation = IdempotencyResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid authentication"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public IdempotencyResponse reserve(@Valid @RequestBody ReserveIdempotencyRequest request) {
        String tenantId = RequestContextHolder.get().map(c -> c.tenantId()).orElse(null);
        if (tenantId == null || tenantId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing tenantId in JWT context");
        }

        IdempotencyRecord record = reserveIdempotencyKeyCommand.reserve(tenantId, request.key());
        return new IdempotencyResponse(record.key(), record.status(), record.createdAt());
    }

    @GetMapping("/{key}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get idempotency record",
            description = "Retrieves the current status of an idempotency key"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Idempotency record retrieved successfully",
                    content = @Content(schema = @Schema(implementation = IdempotencyResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Missing or invalid authentication"),
            @ApiResponse(responseCode = "404", description = "Idempotency key not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public IdempotencyResponse get(@Parameter(description = "Idempotency key") @PathVariable String key) {
        String tenantId = RequestContextHolder.get().map(c -> c.tenantId()).orElse(null);
        if (tenantId == null || tenantId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing tenantId in JWT context");
        }

        IdempotencyRecord record = getIdempotencyRecordQuery.get(tenantId, key);
        if (record == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Idempotency key not found");
        }

        return new IdempotencyResponse(record.key(), record.status(), record.createdAt());
    }
}
