package com.gogidix.rapidassist.orchestration.fleetorganization.interfaces.rest;

import com.gogidix.rapidassist.orchestration.fleetorganization.application.dto.request.CreateOrganizationRequestDto;
import com.gogidix.rapidassist.orchestration.fleetorganization.application.dto.request.UpdateOrganizationRequestDto;
import com.gogidix.rapidassist.orchestration.fleetorganization.application.dto.response.OrganizationResponseDto;
import com.gogidix.rapidassist.orchestration.fleetorganization.application.service.OrganizationService;
import com.gogidix.rapidassist.orchestration.fleetorganization.domain.model.Organization;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Organization management
 * Full CRUD operations with OpenAPI documentation
 */
@RestController
@RequestMapping("/organizations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Organization Management", description = "APIs for managing fleet organizational hierarchy")
@CrossOrigin(origins = "${ALLOWED_ORIGINS:http://localhost:3000}")
public class OrganizationController {

    private final OrganizationService service;

    @PostMapping
    @Operation(summary = "Create a new organization", description = "Creates a new organization unit in the hierarchy")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Organization created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Organization already exists")
    })
    public ResponseEntity<OrganizationResponseDto> createOrganization(
            @Valid @RequestBody CreateOrganizationRequestDto request) {
        log.info("REST request to create organization: {}", request.getName());
        OrganizationResponseDto response = service.createOrganization(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{organizationId}")
    @Operation(summary = "Get organization by ID", description = "Retrieves a specific organization by its ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Organization found"),
        @ApiResponse(responseCode = "404", description = "Organization not found")
    })
    public ResponseEntity<OrganizationResponseDto> getOrganization(
            @Parameter(description = "Organization ID") @PathVariable String organizationId) {
        log.info("REST request to get organization: {}", organizationId);
        OrganizationResponseDto response = service.getOrganization(organizationId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all organizations", description = "Retrieves all organizations for the current tenant")
    @ApiResponse(responseCode = "200", description = "Organizations retrieved successfully")
    public ResponseEntity<List<OrganizationResponseDto>> getAllOrganizations() {
        log.info("REST request to get all organizations");
        List<OrganizationResponseDto> response = service.getAllOrganizations();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get organizations by type", description = "Retrieves organizations filtered by type")
    @ApiResponse(responseCode = "200", description = "Organizations retrieved successfully")
    public ResponseEntity<List<OrganizationResponseDto>> getOrganizationsByType(
            @Parameter(description = "Organization type") @PathVariable Organization.OrganizationType type) {
        log.info("REST request to get organizations by type: {}", type);
        List<OrganizationResponseDto> response = service.getOrganizationsByType(type);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/parent/{parentId}")
    @Operation(summary = "Get child organizations", description = "Retrieves direct children of a parent organization")
    @ApiResponse(responseCode = "200", description = "Child organizations retrieved successfully")
    public ResponseEntity<List<OrganizationResponseDto>> getChildOrganizations(
            @Parameter(description = "Parent organization ID") @PathVariable String parentId) {
        log.info("REST request to get child organizations of: {}", parentId);
        List<OrganizationResponseDto> response = service.getChildOrganizations(parentId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{organizationId}")
    @Operation(summary = "Update organization", description = "Updates an existing organization")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Organization updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Organization not found")
    })
    public ResponseEntity<OrganizationResponseDto> updateOrganization(
            @Parameter(description = "Organization ID") @PathVariable String organizationId,
            @Valid @RequestBody UpdateOrganizationRequestDto request) {
        log.info("REST request to update organization: {}", organizationId);
        OrganizationResponseDto response = service.updateOrganization(organizationId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{organizationId}")
    @Operation(summary = "Delete organization", description = "Soft deletes an organization")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Organization deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Organization not found")
    })
    public ResponseEntity<Void> deleteOrganization(
            @Parameter(description = "Organization ID") @PathVariable String organizationId) {
        log.info("REST request to delete organization: {}", organizationId);
        service.deleteOrganization(organizationId);
        return ResponseEntity.noContent().build();
    }
}
