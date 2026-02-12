package com.gogidix.rapidassist.payments.adapter.service.adapters.in.web;

import com.gogidix.rapidassist.payments.adapter.service.application.service.PaymentService;
import com.gogidix.rapidassist.payments.adapter.service.domain.model.Payment;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for Payment operations with CRITICAL tenant isolation.
 * <p>
 * Tenant ID is automatically extracted from the RequestContext to ensure
 * ALL operations are tenant-isolated. Manual tenantId specification in requests
 * is BLOCKED for security reasons.
 *
 * @author Gogidix
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "Payments", description = "Payment management endpoints with tenant isolation")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Extract tenant ID from RequestContext.
     * <p>
     * CRITICAL: This method MUST be called by all endpoint methods to ensure
     * tenant isolation. Returns empty if tenantId is not present, which should
     * trigger an error response.
     *
     * @return Optional tenant ID from RequestContext
     */
    private Optional<String> extractTenantId() {
        return RequestContextHolder.get()
                .map(RequestContext::tenantId);
    }

    /**
     * Get tenant ID or throw exception.
     *
     * @return tenant ID
     * @throws IllegalStateException if tenantId is not present
     */
    private String requireTenantId() {
        return extractTenantId()
                .orElseThrow(() -> {
                    log.error("TenantId not found in RequestContext - CRITICAL SECURITY VIOLATION");
                    return new IllegalStateException("TenantId is required but not present in request context");
                });
    }

    /**
     * Create a new payment.
     * <p>
     * Tenant ID is automatically extracted from the request context.
     *
     * @param request The payment creation request
     * @return The created payment
     */
    @PostMapping
    @Operation(
            summary = "Create payment",
            description = "Create a new payment for the current tenant. Tenant ID is automatically extracted from request context."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Payment created successfully",
                    content = @Content(schema = @Schema(implementation = Payment.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - missing or invalid tenant context"
            )
    })
    public ResponseEntity<Payment> createPayment(@Valid @RequestBody CreatePaymentRequest request) {
        String tenantId = requireTenantId();
        log.info("Creating payment for tenant: {}", tenantId);

        Payment payment = paymentService.createPayment(
                tenantId,
                request.provider(),
                request.providerReference(),
                request.amount(),
                request.currency()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    }

    /**
     * Get a payment by ID.
     * <p>
     * Tenant ID is automatically extracted and validated to ensure
     * the payment belongs to the requesting tenant.
     *
     * @param id The payment ID
     * @return The payment if found and belongs to tenant
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Get payment by ID",
            description = "Get a payment by ID. Tenant ID is automatically validated."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Payment found",
                    content = @Content(schema = @Schema(implementation = Payment.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Payment not found or does not belong to tenant"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - missing or invalid tenant context"
            )
    })
    public ResponseEntity<Payment> getPayment(
            @Parameter(description = "Payment ID", required = true)
            @PathVariable String id
    ) {
        String tenantId = requireTenantId();
        log.info("Fetching payment: {} for tenant: {}", id, tenantId);

        return paymentService.getPaymentById(id, tenantId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all payments for the current tenant.
     *
     * @return List of payments for the tenant
     */
    @GetMapping
    @Operation(
            summary = "Get all payments",
            description = "Get all payments for the current tenant. Results are automatically filtered by tenant ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Payments retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Payment.class, type = "array"))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - missing or invalid tenant context"
            )
    })
    public ResponseEntity<List<Payment>> getPayments(
            @Parameter(description = "Filter by payment status")
            @RequestParam(required = false) Payment.PaymentStatus status,
            @Parameter(description = "Filter by provider")
            @RequestParam(required = false) String provider
    ) {
        String tenantId = requireTenantId();
        log.info("Fetching payments for tenant: {} with filters - status: {}, provider: {}", tenantId, status, provider);

        List<Payment> payments;
        if (status != null && provider != null) {
            // Both filters - need to combine results
            List<Payment> byStatus = paymentService.getPaymentsByTenantAndStatus(tenantId, status);
            List<Payment> byProvider = paymentService.getPaymentsByTenantAndProvider(tenantId, provider);
            payments = byStatus.stream()
                    .filter(p -> byProvider.stream().anyMatch(bp -> bp.getId().equals(p.getId())))
                    .toList();
        } else if (status != null) {
            payments = paymentService.getPaymentsByTenantAndStatus(tenantId, status);
        } else if (provider != null) {
            payments = paymentService.getPaymentsByTenantAndProvider(tenantId, provider);
        } else {
            payments = paymentService.getPaymentsByTenant(tenantId);
        }

        return ResponseEntity.ok(payments);
    }

    /**
     * Get payment statistics for the current tenant.
     *
     * @return Payment statistics
     */
    @GetMapping("/statistics")
    @Operation(
            summary = "Get payment statistics",
            description = "Get payment statistics for the current tenant."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Statistics retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - missing or invalid tenant context"
            )
    })
    public ResponseEntity<PaymentService.PaymentStatistics> getStatistics() {
        String tenantId = requireTenantId();
        log.info("Fetching payment statistics for tenant: {}", tenantId);

        PaymentService.PaymentStatistics stats = paymentService.getPaymentStatistics(tenantId);
        return ResponseEntity.ok(stats);
    }

    /**
     * Get payments by date range for the current tenant.
     *
     * @param startDate Start of date range
     * @param endDate   End of date range
     * @return List of payments in the date range
     */
    @GetMapping("/by-date-range")
    @Operation(
            summary = "Get payments by date range",
            description = "Get payments for the current tenant within the specified date range."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Payments retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - missing or invalid tenant context"
            )
    })
    public ResponseEntity<List<Payment>> getPaymentsByDateRange(
            @Parameter(description = "Start date (ISO 8601)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @Parameter(description = "End date (ISO 8601)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate
    ) {
        String tenantId = requireTenantId();
        log.info("Fetching payments for tenant: {} between {} and {}", tenantId, startDate, endDate);

        List<Payment> payments = paymentService.getPaymentsByTenantAndDateRange(tenantId, startDate, endDate);
        return ResponseEntity.ok(payments);
    }

    /**
     * Mark a payment as completed.
     *
     * @param id      The payment ID
     * @param request The completion request
     * @return The updated payment
     */
    @PostMapping("/{id}/complete")
    @Operation(
            summary = "Mark payment as completed",
            description = "Mark a payment as completed with the external payment ID."
    )
    public ResponseEntity<Payment> markAsCompleted(
            @PathVariable String id,
            @Valid @RequestBody CompletePaymentRequest request
    ) {
        String tenantId = requireTenantId();
        log.info("Marking payment: {} as completed for tenant: {}", id, tenantId);

        return paymentService.markPaymentAsCompleted(id, tenantId, request.externalPaymentId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Mark a payment as failed.
     *
     * @param id The payment ID
     * @return The updated payment
     */
    @PostMapping("/{id}/fail")
    @Operation(
            summary = "Mark payment as failed",
            description = "Mark a payment as failed."
    )
    public ResponseEntity<Payment> markAsFailed(@PathVariable String id) {
        String tenantId = requireTenantId();
        log.warn("Marking payment: {} as failed for tenant: {}", id, tenantId);

        return paymentService.markPaymentAsFailed(id, tenantId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Exception handler for IllegalStateException (missing tenantId).
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ProblemDetail> handleIllegalStateException(IllegalStateException ex) {
        log.error("Tenant context error: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                ex.getMessage()
        );
        problemDetail.setType(java.net.URI.create("https://api.gogidix.com/errors/tenant-context"));
        problemDetail.setTitle("Unauthorized - Missing Tenant Context");
        problemDetail.setProperty("timestamp", Instant.now().toString());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problemDetail);
    }

    // Request DTOs

    public record CreatePaymentRequest(
            @NotBlank(message = "Provider is required")
            String provider,
            @NotBlank(message = "Provider reference is required")
            String providerReference,
            @NotNull(message = "Amount is required")
            @Positive(message = "Amount must be positive")
            BigDecimal amount,
            @NotBlank(message = "Currency is required")
            @Schema(example = "USD")
            String currency
    ) {}

    public record CompletePaymentRequest(
            @NotBlank(message = "External payment ID is required")
            String externalPaymentId
    ) {}
}
