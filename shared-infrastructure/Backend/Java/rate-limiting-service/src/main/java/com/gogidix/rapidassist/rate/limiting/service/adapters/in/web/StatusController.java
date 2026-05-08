package com.gogidix.rapidassist.rate.limiting.service.adapters.in.web;

import com.gogidix.rapidassist.rate.limiting.service.domain.port.in.GetStatusQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Status", description = "Service status endpoints")
@SecurityRequirement(name = "OAuth2")
public class StatusController {

    private final GetStatusQuery getStatusQuery;

    public StatusController(GetStatusQuery getStatusQuery) {
        this.getStatusQuery = getStatusQuery;
    }

    @GetMapping("/status")
    @Operation(summary = "Get service status", description = "Returns the current operational status of the rate limiting service")
    @ApiResponse(responseCode = "200", description = "Service is operational", content = @Content(schema = @Schema(type = "string", example = "UP")))
    public String status() {
        return getStatusQuery.getStatus();
    }
}
