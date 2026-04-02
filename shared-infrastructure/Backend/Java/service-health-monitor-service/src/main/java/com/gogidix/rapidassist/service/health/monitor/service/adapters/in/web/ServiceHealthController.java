package com.gogidix.rapidassist.service.health.monitor.service.adapters.in.web;

import com.gogidix.rapidassist.service.health.monitor.service.config.OpenApiConfiguration;
import com.gogidix.rapidassist.service.health.monitor.service.domain.model.ServiceHealthReport;
import com.gogidix.rapidassist.service.health.monitor.service.domain.port.in.QueryLatestServiceHealthQuery;
import com.gogidix.rapidassist.service.health.monitor.service.domain.port.in.ReportServiceHealthCommand;
import com.gogidix.rapidassist.service.health.monitor.service.infrastructure.web.ReportServiceHealthRequest;
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
@RequestMapping("/api/v1/service-health")
@Tag(name = "Service Health Monitor", description = "Service health monitoring and reporting APIs")
@SecurityRequirement(name = OpenApiConfiguration.BEARER_AUTH)
public class ServiceHealthController {

    private final ReportServiceHealthCommand report;
    private final QueryLatestServiceHealthQuery query;

    public ServiceHealthController(ReportServiceHealthCommand report, QueryLatestServiceHealthQuery query) {
        this.report = report;
        this.query = query;
    }

    @PostMapping("/report")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Report service health", description = "Reports health status for a service instance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Health report accepted"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid authentication"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public void report(@Valid @RequestBody ReportServiceHealthRequest request) {
        RequestContext ctx = RequestContextHolder.get().orElse(null);
        if (ctx == null || ctx.tenantId() == null || ctx.tenantId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing tenantId in JWT context");
        }

        report.report(new ServiceHealthReport(
                ctx.tenantId(),
                ctx.country(),
                request.getServiceName(),
                request.getInstanceId(),
                request.getStatus(),
                Instant.now(),
                request.getDetails()
        ));
    }

    @GetMapping("/latest")
    @Operation(summary = "Get latest health reports", description = "Retrieves the latest health reports for services, optionally filtered by service name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Health reports retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ServiceHealthReport.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid authentication"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public List<ServiceHealthReport> latest(
            @Parameter(description = "Optional service name filter")
            @RequestParam(required = false) String serviceName,
            @Parameter(description = "Maximum number of reports to return")
            @RequestParam(defaultValue = "50") int limit
    ) {
        RequestContext ctx = RequestContextHolder.get().orElse(null);
        if (ctx == null || ctx.tenantId() == null || ctx.tenantId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing tenantId in JWT context");
        }

        return query.latest(ctx.tenantId(), serviceName, limit);
    }
}
