package com.gogidix.rapidassist.payments.adapter.service.adapters.in.web;

import com.gogidix.rapidassist.payments.adapter.service.domain.port.in.GetStatusQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Status controller for Payments Adapter Service.
 * <p>
 * Provides a simple health/status endpoint.
 *
 * @author Gogidix
 * @since 1.0.0
 */
@RestController
@Tag(name = "Status", description = "Service status endpoints")
public class StatusController {

    private final GetStatusQuery getStatusQuery;

    public StatusController(GetStatusQuery getStatusQuery) {
        this.getStatusQuery = getStatusQuery;
    }

    /**
     * Returns the current status of the payments adapter service.
     *
     * @return Status message
     */
    @GetMapping("/status")
    @Operation(
            summary = "Get service status",
            description = "Returns the current operational status of the payments adapter service.",
            operationId = "getStatus"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Service is operational",
            content = @Content(schema = @Schema(type = "string", example = "UP"))
    )
    public String status() {
        return getStatusQuery.getStatus();
    }
}
