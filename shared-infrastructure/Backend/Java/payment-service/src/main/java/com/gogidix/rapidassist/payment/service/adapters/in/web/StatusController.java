package com.gogidix.rapidassist.payment.service.adapters.in.web;

import com.gogidix.rapidassist.payment.service.domain.port.in.GetStatusQuery;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Status controller for Payment Service.
 * <p>
 * Provides a simple health/status endpoint that confirms the service is running.
 *
 * @author Gogidix
 * @since 1.0.0
 */
@RestController
@Tag(name = "Status", description = "Service status endpoints")
@SecurityRequirement(name = "OAuth2")
public class StatusController {

    private final GetStatusQuery getStatusQuery;

    public StatusController(GetStatusQuery getStatusQuery) {
        this.getStatusQuery = getStatusQuery;
    }

    /**
     * Returns the current status of the payment service.
     *
     * @return Status message
     */
    @GetMapping("/status")
    @Operation(
            summary = "Get service status",
            description = "Returns the current operational status of the payment service.",
            operationId = "getStatus"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Service is operational",
            content = @Content(schema = @Schema(type = "string", example = "UP"))
    )
    @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class))
    )
    public String status() {
        String tenantId = RequestContextHolder.get()
                .map(c -> c.tenantId())
                .orElse(null);
        if (tenantId == null || tenantId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing tenantId in JWT context");
        }
        return getStatusQuery.getStatus();
    }
}
