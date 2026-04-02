package com.gogidix.rapidassist.request.routing.service.adapters.in.web;

import com.gogidix.rapidassist.request.routing.service.config.OpenApiConfiguration;
import com.gogidix.rapidassist.request.routing.service.domain.port.in.DeleteRoutingRuleCommand;
import com.gogidix.rapidassist.request.routing.service.domain.port.in.ResolveRouteQuery;
import com.gogidix.rapidassist.request.routing.service.domain.port.in.UpsertRoutingRuleCommand;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/routing")
@Tag(name = "Request Routing", description = "Request routing and load balancing APIs")
@SecurityRequirement(name = OpenApiConfiguration.BEARER_AUTH)
public class RequestRoutingController {

    private final UpsertRoutingRuleCommand upsertCommand;
    private final DeleteRoutingRuleCommand deleteCommand;
    private final ResolveRouteQuery resolveQuery;

    public RequestRoutingController(
            UpsertRoutingRuleCommand upsertCommand,
            DeleteRoutingRuleCommand deleteCommand,
            ResolveRouteQuery resolveQuery
    ) {
        this.upsertCommand = upsertCommand;
        this.deleteCommand = deleteCommand;
        this.resolveQuery = resolveQuery;
    }

    @PostMapping("/rules")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Upsert routing rule", description = "Creates or updates a routing rule for the given route key")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Routing rule upserted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid authentication"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public void upsert(@Valid @RequestBody UpsertRoutingRuleRequest request) {
        String tenantId = RequestContextHolder.get().map(c -> c.tenantId()).orElse(null);
        if (tenantId == null || tenantId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing tenantId in JWT context");
        }
        upsertCommand.upsert(tenantId, request.routeKey(), request.destinationBaseUrl());
    }

    @DeleteMapping("/rules/{routeKey}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete routing rule", description = "Deletes a routing rule for the given route key")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Routing rule deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid authentication"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public void delete(@PathVariable String routeKey) {
        String tenantId = RequestContextHolder.get().map(c -> c.tenantId()).orElse(null);
        if (tenantId == null || tenantId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing tenantId in JWT context");
        }
        deleteCommand.delete(tenantId, routeKey);
    }

    @GetMapping("/resolve/{routeKey}")
    @Operation(summary = "Resolve route", description = "Resolves the destination base URL for the given route key")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Route resolved successfully",
                    content = @Content(schema = @Schema(implementation = ResolvedRouteResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid authentication"),
            @ApiResponse(responseCode = "404", description = "No route configured for the given route key"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResolvedRouteResponse resolve(@PathVariable String routeKey) {
        String tenantId = RequestContextHolder.get().map(c -> c.tenantId()).orElse(null);
        if (tenantId == null || tenantId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing tenantId in JWT context");
        }

        String destination = resolveQuery.resolveDestinationBaseUrl(tenantId, routeKey)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No route configured"));

        return new ResolvedRouteResponse(routeKey, destination);
    }
}
