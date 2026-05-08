package com.gogidix.rapidassist.api.gateway.adapters.in.web;

import com.gogidix.rapidassist.api.gateway.application.service.ComprehensiveGatewayRouteService;
import com.gogidix.rapidassist.api.gateway.domain.model.GatewayRoute;
import com.gogidix.rapidassist.api.gateway.domain.model.RouteMetrics;
import com.gogidix.rapidassist.api.gateway.domain.port.in.GatewayRouteCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/gateway/routes")
@Tag(name = "Gateway Routes", description = "API for managing gateway routes")
@SecurityRequirement(name = "Bearer Authentication")
public class GatewayRouteController {

    private static final Logger logger = LoggerFactory.getLogger(GatewayRouteController.class);

    @Autowired
    private ComprehensiveGatewayRouteService routeService;

    @PostMapping
    @Operation(
        summary = "Create a new gateway route",
        description = "Creates a new route in the API Gateway with the specified configuration"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Route created successfully",
            content = @Content(schema = @Schema(implementation = GatewayRoute.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "409", description = "Route already exists")
    })
    public CompletableFuture<ResponseEntity<GatewayRoute>> createRoute(
        @Parameter(description = "Route creation request", required = true)
        @Valid @RequestBody CreateRouteRequest request
    ) {
        logger.info("Creating gateway route: {} for tenant: {}", request.routeId(), request.tenantId());

        GatewayRouteCommand.CreateRouteCommand command = new GatewayRouteCommand.CreateRouteCommand(
            request.tenantId(),
            request.routeId(),
            request.path(),
            request.uri(),
            request.serviceId(),
            request.methods(),
            request.predicates(),
            request.filters(),
            request.order(),
            request.metadata(),
            request.stripPrefix(),
            request.retryConfig(),
            request.timeoutConfig(),
            request.rateLimitConfig(),
            request.circuitBreakerConfig(),
            request.tags(),
            request.createdBy()
        );

        return routeService.createRoute(command)
            .thenApply(route -> ResponseEntity.status(HttpStatus.CREATED).body(route));
    }

    @PutMapping("/{routeId}")
    public CompletableFuture<ResponseEntity<GatewayRoute>> updateRoute(
        @PathVariable String routeId,
        @Valid @RequestBody UpdateRouteRequest request
    ) {
        logger.info("Updating gateway route: {} for tenant: {}", routeId, request.tenantId());

        GatewayRouteCommand.UpdateRouteCommand command = new GatewayRouteCommand.UpdateRouteCommand(
            request.tenantId(),
            routeId,
            request.path(),
            request.uri(),
            request.serviceId(),
            request.methods(),
            request.predicates(),
            request.filters(),
            request.order(),
            request.metadata(),
            request.stripPrefix(),
            request.retryConfig(),
            request.timeoutConfig(),
            request.rateLimitConfig(),
            request.circuitBreakerConfig(),
            request.tags(),
            request.updatedBy()
        );

        return routeService.updateRoute(command)
            .thenApply(routeOpt -> routeOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()));
    }

    @DeleteMapping("/{routeId}")
    public CompletableFuture<ResponseEntity<Void>> deleteRoute(
        @PathVariable String routeId,
        @RequestParam String tenantId,
        @RequestParam String deletedBy
    ) {
        logger.info("Deleting gateway route: {} for tenant: {}", routeId, tenantId);

        return routeService.deleteRoute(tenantId, routeId, deletedBy)
            .thenApply(deleted -> deleted ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build());
    }

    @PostMapping("/{routeId}/enable")
    public CompletableFuture<ResponseEntity<GatewayRoute>> enableRoute(
        @PathVariable String routeId,
        @RequestParam String tenantId,
        @RequestParam String enabledBy
    ) {
        logger.info("Enabling gateway route: {} for tenant: {}", routeId, tenantId);

        return routeService.enableRoute(tenantId, routeId, enabledBy)
            .thenApply(routeOpt -> routeOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()));
    }

    @PostMapping("/{routeId}/disable")
    public CompletableFuture<ResponseEntity<GatewayRoute>> disableRoute(
        @PathVariable String routeId,
        @RequestParam String tenantId,
        @RequestParam String disabledBy
    ) {
        logger.info("Disabling gateway route: {} for tenant: {}", routeId, tenantId);

        return routeService.disableRoute(tenantId, routeId, disabledBy)
            .thenApply(routeOpt -> routeOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()));
    }

    @PostMapping("/bulk")
    public CompletableFuture<ResponseEntity<List<GatewayRoute>>> bulkCreateRoutes(
        @Valid @RequestBody BulkCreateRoutesRequest request
    ) {
        logger.info("Bulk creating {} gateway routes for tenant: {}",
            request.routes().size(), request.tenantId());

        List<GatewayRouteCommand.CreateRouteCommand> commands = request.routes().stream()
            .map(req -> new GatewayRouteCommand.CreateRouteCommand(
                request.tenantId(),
                req.routeId(),
                req.path(),
                req.uri(),
                req.serviceId(),
                req.methods(),
                req.predicates(),
                req.filters(),
                req.order(),
                req.metadata(),
                req.stripPrefix(),
                req.retryConfig(),
                req.timeoutConfig(),
                req.rateLimitConfig(),
                req.circuitBreakerConfig(),
                req.tags(),
                request.createdBy()
            ))
            .toList();

        GatewayRouteCommand.BulkCreateRoutesCommand command =
            new GatewayRouteCommand.BulkCreateRoutesCommand(
                request.tenantId(),
                commands,
                request.createdBy()
            );

        return routeService.bulkCreateRoutes(command)
            .thenApply(routes -> ResponseEntity.status(HttpStatus.CREATED).body(routes));
    }

    @GetMapping("/{routeId}")
    public CompletableFuture<ResponseEntity<GatewayRoute>> getRoute(
        @PathVariable String routeId,
        @RequestParam String tenantId
    ) {
        return routeService.getRouteById(tenantId, routeId)
            .thenApply(routeOpt -> routeOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()));
    }

    @GetMapping
    public CompletableFuture<ResponseEntity<List<GatewayRoute>>> getRoutesByTenant(
        @RequestParam String tenantId
    ) {
        return routeService.getRoutesByTenant(tenantId)
            .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/service/{serviceId}")
    public CompletableFuture<ResponseEntity<List<GatewayRoute>>> getRoutesByService(
        @PathVariable String serviceId,
        @RequestParam String tenantId
    ) {
        return routeService.getRoutesByService(tenantId, serviceId)
            .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/active")
    public CompletableFuture<ResponseEntity<List<GatewayRoute>>> getActiveRoutes(
        @RequestParam String tenantId
    ) {
        return routeService.getActiveRoutes(tenantId)
            .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/search")
    public CompletableFuture<ResponseEntity<List<GatewayRoute>>> searchRoutes(
        @RequestParam String tenantId,
        @RequestParam String keyword
    ) {
        return routeService.searchRoutes(tenantId, keyword)
            .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/metrics")
    public CompletableFuture<ResponseEntity<RouteMetrics>> getRouteMetrics(
        @RequestParam String tenantId,
        @RequestParam String routeId,
        @RequestParam long fromEpoch,
        @RequestParam long toEpoch
    ) {
        Instant from = Instant.ofEpochMilli(fromEpoch);
        Instant to = Instant.ofEpochMilli(toEpoch);

        return routeService.getAggregatedMetrics(tenantId, routeId, from, to)
            .thenApply(ResponseEntity::ok);
    }

    @PostMapping("/validate")
    public CompletableFuture<ResponseEntity<Map<String, Boolean>>> validateRoute(
        @RequestParam String tenantId,
        @RequestParam String routeId
    ) {
        return routeService.validateRouteConfiguration(tenantId, routeId)
            .thenApply(valid -> ResponseEntity.ok(Map.of("valid", valid)));
    }

    // Request records
    @Schema(description = "Request to create a new gateway route")
    record CreateRouteRequest(
        @Schema(description = "Tenant identifier", example = "tenant123", required = true)
        @NotBlank(message = "Tenant ID is required")
        String tenantId,

        @Schema(description = "Unique route identifier", example = "user-service-route", required = true)
        @NotBlank(message = "Route ID is required")
        String routeId,

        @Schema(description = "Route path pattern", example = "/api/users/**", required = true)
        @NotBlank(message = "Path is required")
        String path,

        @Schema(description = "Target service URI", example = "lb://USER-SERVICE", required = true)
        @NotBlank(message = "URI is required")
        String uri,

        @Schema(description = "Service identifier", example = "user-service")
        String serviceId,

        @Schema(description = "Allowed HTTP methods")
        java.util.Set<GatewayRoute.HttpMethod> methods,

        @Schema(description = "Route predicates")
        List<GatewayRoute.RoutePredicate> predicates,

        @Schema(description = "Route filters")
        List<GatewayRoute.RouteFilter> filters,

        @Schema(description = "Route order", example = "0")
        Integer order,

        @Schema(description = "Additional metadata")
        java.util.Map<String, Object> metadata,

        @Schema(description = "Strip prefix level", example = "1")
        int stripPrefix,

        @Schema(description = "Retry configuration")
        GatewayRoute.RetryConfig retryConfig,

        @Schema(description = "Timeout configuration")
        GatewayRoute.TimeoutConfig timeoutConfig,

        @Schema(description = "Rate limit configuration")
        GatewayRoute.RateLimitConfig rateLimitConfig,

        @Schema(description = "Circuit breaker configuration")
        GatewayRoute.CircuitBreakerConfig circuitBreakerConfig,

        @Schema(description = "Route tags for organization")
        java.util.Set<String> tags,

        @Schema(description = "Creator user ID", example = "user123", required = true)
        @NotBlank(message = "Created by is required")
        String createdBy
    ) {}

    @Schema(description = "Request to update an existing gateway route")
    record UpdateRouteRequest(
        @Schema(description = "Tenant identifier", example = "tenant123", required = true)
        @NotBlank(message = "Tenant ID is required")
        String tenantId,

        @Schema(description = "Route path pattern", example = "/api/users/**", required = true)
        @NotBlank(message = "Path is required")
        String path,

        @Schema(description = "Target service URI", example = "lb://USER-SERVICE", required = true)
        @NotBlank(message = "URI is required")
        String uri,

        @Schema(description = "Service identifier", example = "user-service")
        String serviceId,

        @Schema(description = "Allowed HTTP methods")
        java.util.Set<GatewayRoute.HttpMethod> methods,

        @Schema(description = "Route predicates")
        List<GatewayRoute.RoutePredicate> predicates,

        @Schema(description = "Route filters")
        List<GatewayRoute.RouteFilter> filters,

        @Schema(description = "Route order", example = "0")
        Integer order,

        @Schema(description = "Additional metadata")
        java.util.Map<String, Object> metadata,

        @Schema(description = "Strip prefix level", example = "1")
        int stripPrefix,

        @Schema(description = "Retry configuration")
        GatewayRoute.RetryConfig retryConfig,

        @Schema(description = "Timeout configuration")
        GatewayRoute.TimeoutConfig timeoutConfig,

        @Schema(description = "Rate limit configuration")
        GatewayRoute.RateLimitConfig rateLimitConfig,

        @Schema(description = "Circuit breaker configuration")
        GatewayRoute.CircuitBreakerConfig circuitBreakerConfig,

        @Schema(description = "Route tags for organization")
        java.util.Set<String> tags,

        @Schema(description = "Updater user ID", example = "user123", required = true)
        @NotBlank(message = "Updated by is required")
        String updatedBy
    ) {}

    @Schema(description = "Request to bulk create multiple gateway routes")
    record BulkCreateRoutesRequest(
        @Schema(description = "Tenant identifier", example = "tenant123", required = true)
        @NotBlank(message = "Tenant ID is required")
        String tenantId,

        @Schema(description = "List of routes to create", required = true)
        @jakarta.validation.constraints.NotEmpty(message = "Routes list cannot be empty")
        List<@Valid CreateRouteRequest> routes,

        @Schema(description = "Creator user ID", example = "user123", required = true)
        @NotBlank(message = "Created by is required")
        String createdBy
    ) {}
}
