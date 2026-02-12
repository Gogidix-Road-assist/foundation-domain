package com.gogidix.rapidassist.courier.adapter.service.adapters.in.web;

import com.gogidix.rapidassist.courier.adapter.service.domain.model.CourierAssignment;
import com.gogidix.rapidassist.courier.adapter.service.domain.model.CourierAssignment.AssignmentStatus;
import com.gogidix.rapidassist.courier.adapter.service.domain.port.in.CourierAssignmentService;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Courier Assignment operations.
 * All operations are tenant-scoped - tenantId is extracted from request context.
 */
@RestController
@RequestMapping("/api/courier-assignments")
@Tag(name = "Courier Assignments", description = "APIs for managing courier delivery assignments")
public class CourierAssignmentController {

    private final CourierAssignmentService courierAssignmentService;

    public CourierAssignmentController(CourierAssignmentService courierAssignmentService) {
        this.courierAssignmentService = courierAssignmentService;
    }

    /**
     * Helper method to extract tenantId from request context.
     * All tenant-scoped operations should use this method.
     *
     * @return the tenantId from the current request context
     * @throws IllegalStateException if tenantId is not present in the request context
     */
    private String getTenantId() {
        return RequestContextHolder.get()
            .map(context -> context.tenantId())
            .orElseThrow(() -> new IllegalStateException("TenantId is required but not present in the request context"));
    }

    // CRUD endpoints

    @PostMapping
    @Operation(summary = "Create a new courier assignment", description = "Creates a new assignment for a courier to deliver a package")
    public ResponseEntity<CourierAssignment> createAssignment(
            @Parameter(description = "Order ID", required = true)
            @RequestParam @NotBlank String orderId,

            @Parameter(description = "Courier ID", required = true)
            @RequestParam @NotBlank String courierId,

            @Parameter(description = "Pickup address", required = true)
            @RequestParam @NotBlank String pickupAddress,

            @Parameter(description = "Delivery address", required = true)
            @RequestParam @NotBlank String deliveryAddress) {

        String tenantId = getTenantId();
        CourierAssignment assignment = courierAssignmentService.createAssignment(
            tenantId, orderId, courierId, pickupAddress, deliveryAddress
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(assignment);
    }

    @GetMapping("/{assignmentId}")
    @Operation(summary = "Get assignment by ID", description = "Retrieves a specific courier assignment by its ID")
    public ResponseEntity<CourierAssignment> getAssignment(
            @Parameter(description = "Assignment ID", required = true)
            @PathVariable String assignmentId) {

        String tenantId = getTenantId();
        return courierAssignmentService.getAssignmentById(tenantId, assignmentId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Get all assignments for tenant", description = "Retrieves all courier assignments for the current tenant")
    public ResponseEntity<List<CourierAssignment>> getAllAssignments() {
        String tenantId = getTenantId();
        return ResponseEntity.ok(courierAssignmentService.getAllAssignments(tenantId));
    }

    @GetMapping("/by-order/{orderId}")
    @Operation(summary = "Get assignment by order ID", description = "Retrieves the courier assignment for a specific order")
    public ResponseEntity<CourierAssignment> getAssignmentByOrder(
            @Parameter(description = "Order ID", required = true)
            @PathVariable String orderId) {

        String tenantId = getTenantId();
        return courierAssignmentService.getAssignmentByOrderId(tenantId, orderId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-courier/{courierId}")
    @Operation(summary = "Get assignments by courier", description = "Retrieves all assignments for a specific courier")
    public ResponseEntity<List<CourierAssignment>> getAssignmentsByCourier(
            @Parameter(description = "Courier ID", required = true)
            @PathVariable String courierId) {

        String tenantId = getTenantId();
        return ResponseEntity.ok(courierAssignmentService.getAssignmentsByCourier(tenantId, courierId));
    }

    @GetMapping("/by-status/{status}")
    @Operation(summary = "Get assignments by status", description = "Retrieves all assignments with a specific status")
    public ResponseEntity<List<CourierAssignment>> getAssignmentsByStatus(
            @Parameter(description = "Assignment status", required = true)
            @PathVariable AssignmentStatus status) {

        String tenantId = getTenantId();
        return ResponseEntity.ok(courierAssignmentService.getAssignmentsByStatus(tenantId, status));
    }

    @GetMapping("/active")
    @Operation(summary = "Get active assignments", description = "Retrieves all active (in-progress) assignments")
    public ResponseEntity<List<CourierAssignment>> getActiveAssignments() {
        String tenantId = getTenantId();
        return ResponseEntity.ok(courierAssignmentService.getActiveAssignments(tenantId));
    }

    // Status update endpoints

    @PutMapping("/{assignmentId}/status")
    @Operation(summary = "Update assignment status", description = "Updates the status of a courier assignment")
    public ResponseEntity<CourierAssignment> updateStatus(
            @Parameter(description = "Assignment ID", required = true)
            @PathVariable String assignmentId,

            @Parameter(description = "New status", required = true)
            @RequestParam AssignmentStatus status) {

        String tenantId = getTenantId();
        CourierAssignment assignment = courierAssignmentService.updateAssignmentStatus(tenantId, assignmentId, status);
        return ResponseEntity.ok(assignment);
    }

    @PostMapping("/{assignmentId}/pickup")
    @Operation(summary = "Mark assignment as picked up", description = "Marks a package as picked up by the courier")
    public ResponseEntity<CourierAssignment> markAsPickedUp(
            @Parameter(description = "Assignment ID", required = true)
            @PathVariable String assignmentId) {

        String tenantId = getTenantId();
        CourierAssignment assignment = courierAssignmentService.markAsPickedUp(tenantId, assignmentId);
        return ResponseEntity.ok(assignment);
    }

    @PostMapping("/{assignmentId}/deliver")
    @Operation(summary = "Mark assignment as delivered", description = "Marks a package as delivered")
    public ResponseEntity<CourierAssignment> markAsDelivered(
            @Parameter(description = "Assignment ID", required = true)
            @PathVariable String assignmentId) {

        String tenantId = getTenantId();
        CourierAssignment assignment = courierAssignmentService.markAsDelivered(tenantId, assignmentId);
        return ResponseEntity.ok(assignment);
    }

    @PostMapping("/{assignmentId}/cancel")
    @Operation(summary = "Cancel assignment", description = "Cancels a courier assignment")
    public ResponseEntity<CourierAssignment> cancelAssignment(
            @Parameter(description = "Assignment ID", required = true)
            @PathVariable String assignmentId,

            @Parameter(description = "Cancellation reason", required = true)
            @RequestParam @NotBlank String reason) {

        String tenantId = getTenantId();
        CourierAssignment assignment = courierAssignmentService.cancelAssignment(tenantId, assignmentId, reason);
        return ResponseEntity.ok(assignment);
    }

    @DeleteMapping("/{assignmentId}")
    @Operation(summary = "Delete assignment", description = "Deletes a courier assignment")
    public ResponseEntity<Void> deleteAssignment(
            @Parameter(description = "Assignment ID", required = true)
            @PathVariable String assignmentId) {

        String tenantId = getTenantId();
        courierAssignmentService.deleteAssignment(tenantId, assignmentId);
        return ResponseEntity.noContent().build();
    }

    // Statistics endpoint

    @GetMapping("/statistics")
    @Operation(summary = "Get assignment statistics", description = "Retrieves statistics for courier assignments")
    public ResponseEntity<CourierAssignmentService.AssignmentStatistics> getStatistics() {
        String tenantId = getTenantId();
        return ResponseEntity.ok(courierAssignmentService.getStatistics(tenantId));
    }
}
