package com.gogidix.rapidassist.template.messaging.service.adapters.in.web;

import com.gogidix.rapidassist.template.messaging.service.config.OpenApiConfiguration;
import com.gogidix.rapidassist.template.messaging.service.domain.port.in.GetStatusQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Status", description = "Service status and health check APIs")
public class StatusController {

    private final GetStatusQuery getStatusQuery;

    public StatusController(GetStatusQuery getStatusQuery) {
        this.getStatusQuery = getStatusQuery;
    }

    @GetMapping("/status")
    @Operation(summary = "Get service status", description = "Returns the current status of the template messaging service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public String status() {
        return getStatusQuery.getStatus();
    }
}
