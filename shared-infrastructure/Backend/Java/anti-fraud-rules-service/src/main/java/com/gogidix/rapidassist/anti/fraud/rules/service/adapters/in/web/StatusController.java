package com.gogidix.rapidassist.anti.fraud.rules.service.adapters.in.web;

import com.gogidix.rapidassist.anti.fraud.rules.service.domain.port.in.GetStatusQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Status", description = "APIs for service health and status")
public class StatusController {

    private final GetStatusQuery getStatusQuery;

    public StatusController(GetStatusQuery getStatusQuery) {
        this.getStatusQuery = getStatusQuery;
    }

    @GetMapping("/status")
    @Operation(summary = "Get service status", description = "Returns the current status of the anti-fraud rules service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service status retrieved successfully")
    })
    public String status() {
        return getStatusQuery.getStatus();
    }
}
