package com.gogidix.rapidassist.insurer.adapter.service.adapters.in.web;

import com.gogidix.rapidassist.insurer.adapter.service.application.dto.CreateInsurerMappingRequest;
import com.gogidix.rapidassist.insurer.adapter.service.application.dto.InsurerMappingResponse;
import com.gogidix.rapidassist.insurer.adapter.service.application.dto.UpdateInsurerMappingRequest;
import com.gogidix.rapidassist.insurer.adapter.service.application.service.InsurerMappingService;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/insurer-mappings")
@Tag(name = "Insurer Mappings", description = "Insurer adapter mapping management endpoints")
public class InsurerMappingController {

    private final InsurerMappingService insurerMappingService;

    public InsurerMappingController(InsurerMappingService insurerMappingService) {
        this.insurerMappingService = insurerMappingService;
    }

    protected String getTenantId() {
        return RequestContextHolder.get()
                .map(c -> c.tenantId())
                .orElseThrow(() -> new IllegalStateException("TenantId required"));
    }

    @GetMapping
    @Operation(
            summary = "Get all insurer mappings for tenant",
            description = "Retrieves all insurer mappings for the current tenant"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Insurer mappings retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<InsurerMappingResponse>> getAllMappings() {
        String tenantId = getTenantId();
        return ResponseEntity.ok(insurerMappingService.findByTenant(tenantId));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get insurer mapping by ID",
            description = "Retrieves a specific insurer mapping by ID for the current tenant"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Insurer mapping retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Insurer mapping not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<InsurerMappingResponse> getMappingById(
            @Parameter(description = "Mapping ID", required = true)
            @PathVariable String id) {
        String tenantId = getTenantId();
        return ResponseEntity.ok(insurerMappingService.findByTenantAndId(tenantId, id));
    }

    @GetMapping("/adapter-type/{adapterType}")
    @Operation(
            summary = "Get insurer mappings by adapter type",
            description = "Retrieves insurer mappings filtered by adapter type for the current tenant"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Insurer mappings retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<InsurerMappingResponse>> getMappingsByAdapterType(
            @Parameter(description = "Adapter type", required = true)
            @PathVariable String adapterType) {
        String tenantId = getTenantId();
        return ResponseEntity.ok(insurerMappingService.findByTenantAndAdapterType(tenantId, adapterType));
    }

    @GetMapping("/enabled")
    @Operation(
            summary = "Get enabled insurer mappings",
            description = "Retrieves all enabled insurer mappings for the current tenant"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Enabled insurer mappings retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<InsurerMappingResponse>> getEnabledMappings() {
        String tenantId = getTenantId();
        return ResponseEntity.ok(insurerMappingService.findEnabledByTenant(tenantId));
    }

    @GetMapping("/search")
    @Operation(
            summary = "Search insurer mappings",
            description = "Searches insurer mappings by insurer code or name for the current tenant"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search completed successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<InsurerMappingResponse>> searchMappings(
            @Parameter(description = "Search term", required = true)
            @RequestParam String search) {
        String tenantId = getTenantId();
        return ResponseEntity.ok(insurerMappingService.searchByTenant(tenantId, search));
    }

    @GetMapping("/stats/count")
    @Operation(
            summary = "Get insurer mappings count",
            description = "Returns the count of insurer mappings for the current tenant"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Long> getMappingsCount() {
        String tenantId = getTenantId();
        return ResponseEntity.ok(insurerMappingService.countByTenant(tenantId));
    }

    @PostMapping
    @Operation(
            summary = "Create insurer mapping",
            description = "Creates a new insurer mapping for the current tenant"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Insurer mapping created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<InsurerMappingResponse> createMapping(
            @Valid @RequestBody CreateInsurerMappingRequest request) {
        String tenantId = getTenantId();
        InsurerMappingResponse response = insurerMappingService.create(tenantId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update insurer mapping",
            description = "Updates an existing insurer mapping for the current tenant"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Insurer mapping updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Insurer mapping not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<InsurerMappingResponse> updateMapping(
            @Parameter(description = "Mapping ID", required = true)
            @PathVariable String id,
            @Valid @RequestBody UpdateInsurerMappingRequest request) {
        String tenantId = getTenantId();
        return ResponseEntity.ok(insurerMappingService.update(tenantId, id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete insurer mapping",
            description = "Deletes an insurer mapping for the current tenant"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Insurer mapping deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Insurer mapping not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteMapping(
            @Parameter(description = "Mapping ID", required = true)
            @PathVariable String id) {
        String tenantId = getTenantId();
        insurerMappingService.delete(tenantId, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/enable")
    @Operation(
            summary = "Enable insurer mapping",
            description = "Enables an insurer mapping for the current tenant"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Insurer mapping enabled successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Insurer mapping not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<InsurerMappingResponse> enableMapping(
            @Parameter(description = "Mapping ID", required = true)
            @PathVariable String id) {
        String tenantId = getTenantId();
        return ResponseEntity.ok(insurerMappingService.enable(tenantId, id));
    }

    @PostMapping("/{id}/disable")
    @Operation(
            summary = "Disable insurer mapping",
            description = "Disables an insurer mapping for the current tenant"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Insurer mapping disabled successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Insurer mapping not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<InsurerMappingResponse> disableMapping(
            @Parameter(description = "Mapping ID", required = true)
            @PathVariable String id) {
        String tenantId = getTenantId();
        return ResponseEntity.ok(insurerMappingService.disable(tenantId, id));
    }
}
