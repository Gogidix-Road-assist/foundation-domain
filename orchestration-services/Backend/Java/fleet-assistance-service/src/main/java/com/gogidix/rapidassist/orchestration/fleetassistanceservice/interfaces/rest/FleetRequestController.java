package com.gogidix.rapidassist.orchestration.fleetassistanceservice.interfaces.rest;

import com.gogidix.rapidassist.orchestration.fleetassistanceservice.application.dto.request.CreateFleetRequestDto;
import com.gogidix.rapidassist.orchestration.fleetassistanceservice.application.dto.response.FleetRequestResponseDto;
import com.gogidix.rapidassist.orchestration.fleetassistanceservice.application.service.FleetRequestService;
import com.gogidix.rapidassist.orchestration.fleetassistanceservice.domain.model.FleetRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Fleet Assistance Requests
 * Port: 8085
 */
@RestController
@RequestMapping("/fleet-assistance")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Fleet Assistance", description = "Fleet assistance request management APIs")
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = "${ALLOWED_ORIGINS:http://localhost:3000}")
public class FleetRequestController {

    private final FleetRequestService fleetRequestService;

    @PostMapping
    @Operation(summary = "Create fleet assistance request", description = "Create a new fleet assistance request for the current tenant")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Request created successfully",
                    content = @Content(schema = @Schema(implementation = FleetRequestResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<FleetRequestResponseDto> createRequest(
            @Parameter(description = "Fleet request details", required = true)
            @RequestBody @Valid CreateFleetRequestDto dto) {

        log.info("REST: Creating fleet assistance request");
        FleetRequestResponseDto response = fleetRequestService.createRequest(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{requestId}")
    @Operation(summary = "Get fleet request by ID", description = "Retrieve a specific fleet assistance request")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Request not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<FleetRequestResponseDto> getRequest(
            @Parameter(description = "Request ID", required = true)
            @PathVariable String requestId) {

        log.info("REST: Fetching fleet request: {}", requestId);
        FleetRequestResponseDto response = fleetRequestService.getRequest(requestId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "List all fleet requests", description = "Get all fleet assistance requests for the current tenant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Requests retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<FleetRequestResponseDto>> getAllRequests() {
        log.info("REST: Fetching all fleet requests");
        List<FleetRequestResponseDto> responses = fleetRequestService.getAllRequests();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/fleet/{fleetId}")
    @Operation(summary = "Get requests by fleet", description = "Get all assistance requests for a specific fleet")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Requests retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<FleetRequestResponseDto>> getRequestsByFleet(
            @Parameter(description = "Fleet ID", required = true)
            @PathVariable String fleetId) {

        log.info("REST: Fetching requests for fleet: {}", fleetId);
        List<FleetRequestResponseDto> responses = fleetRequestService.getRequestsByFleet(fleetId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get requests by status", description = "Get all assistance requests with a specific status")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Requests retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<FleetRequestResponseDto>> getRequestsByStatus(
            @Parameter(description = "Request status", required = true)
            @PathVariable FleetRequest.RequestStatus status) {

        log.info("REST: Fetching requests with status: {}", status);
        List<FleetRequestResponseDto> responses = fleetRequestService.getRequestsByStatus(status);
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{requestId}/assign")
    @Operation(summary = "Assign provider to request", description = "Assign a fleet provider to an assistance request")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Provider assigned successfully"),
            @ApiResponse(responseCode = "404", description = "Request not found"),
            @ApiResponse(responseCode = "400", description = "Invalid operation")
    })
    public ResponseEntity<FleetRequestResponseDto> assignProvider(
            @Parameter(description = "Request ID", required = true)
            @PathVariable String requestId,
            @Parameter(description = "Provider ID", required = true)
            @RequestParam String providerId) {

        log.info("REST: Assigning provider {} to request {}", providerId, requestId);
        FleetRequestResponseDto response = fleetRequestService.assignProvider(requestId, providerId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{requestId}/start")
    @Operation(summary = "Start service", description = "Mark the assistance service as started")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Service started successfully"),
            @ApiResponse(responseCode = "404", description = "Request not found")
    })
    public ResponseEntity<FleetRequestResponseDto> startService(
            @Parameter(description = "Request ID", required = true)
            @PathVariable String requestId) {

        log.info("REST: Starting service for request {}", requestId);
        FleetRequestResponseDto response = fleetRequestService.startService(requestId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{requestId}/complete")
    @Operation(summary = "Complete service", description = "Mark the assistance service as completed")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Service completed successfully"),
            @ApiResponse(responseCode = "404", description = "Request not found")
    })
    public ResponseEntity<FleetRequestResponseDto> completeService(
            @Parameter(description = "Request ID", required = true)
            @PathVariable String requestId) {

        log.info("REST: Completing service for request {}", requestId);
        FleetRequestResponseDto response = fleetRequestService.completeService(requestId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{requestId}/cancel")
    @Operation(summary = "Cancel request", description = "Cancel an assistance request")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request cancelled successfully"),
            @ApiResponse(responseCode = "404", description = "Request not found")
    })
    public ResponseEntity<FleetRequestResponseDto> cancelRequest(
            @Parameter(description = "Request ID", required = true)
            @PathVariable String requestId) {

        log.info("REST: Cancelling request {}", requestId);
        FleetRequestResponseDto response = fleetRequestService.cancelRequest(requestId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{requestId}")
    @Operation(summary = "Delete request", description = "Soft delete an assistance request")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Request deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Request not found")
    })
    public ResponseEntity<Void> deleteRequest(
            @Parameter(description = "Request ID", required = true)
            @PathVariable String requestId) {

        log.info("REST: Deleting request {}", requestId);
        fleetRequestService.deleteRequest(requestId);
        return ResponseEntity.noContent().build();
    }
}
