package com.gogidix.rapidassist.maps.geocoding.adapter.service.adapters.in.web;

import com.gogidix.rapidassist.maps.geocoding.adapter.service.domain.port.in.GetStatusQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Status", description = "Service health and status endpoints")
public class StatusController {

    private final GetStatusQuery getStatusQuery;

    public StatusController(GetStatusQuery getStatusQuery) {
        this.getStatusQuery = getStatusQuery;
    }

    @GetMapping("/status")
    @Operation(summary = "Get service status", description = "Returns the current status of the Maps Geocoding Adapter Service")
    @ApiResponse(responseCode = "200", description = "Status retrieved successfully")
    public String status() {
        return getStatusQuery.getStatus();
    }
}

