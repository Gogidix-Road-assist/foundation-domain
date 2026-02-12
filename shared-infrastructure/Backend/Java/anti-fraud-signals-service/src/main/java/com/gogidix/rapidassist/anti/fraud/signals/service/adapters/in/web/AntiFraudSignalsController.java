package com.gogidix.rapidassist.anti.fraud.signals.service.adapters.in.web;

import com.gogidix.rapidassist.anti.fraud.signals.service.domain.model.AntiFraudSignal;
import com.gogidix.rapidassist.anti.fraud.signals.service.domain.port.in.AntiFraudSignalService;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

/**
 * REST controller for managing anti-fraud signals with CRITICAL tenant isolation.
 *
 * SECURITY: All endpoints extract tenantId from RequestContext to ensure tenant isolation.
 * - No endpoint accepts tenantId as a request parameter (prevents tenant spoofing)
 * - All operations are scoped to the tenant from the JWT token
 * - Cross-tenant access attempts return 401 Unauthorized
 */
@RestController
@RequestMapping("/api/v1/anti-fraud-signals")
@Tag(name = "Anti-Fraud Signals", description = "APIs for managing anti-fraud detection signals")
@SecurityRequirement(name = "Bearer Authentication")
public class AntiFraudSignalsController {

    private static final Logger log = LoggerFactory.getLogger(AntiFraudSignalsController.class);

    private final AntiFraudSignalService signalService;

    public AntiFraudSignalsController(AntiFraudSignalService signalService) {
        this.signalService = signalService;
    }

    /**
     * Extract tenantId from RequestContext.
     * CRITICAL: This ensures tenant isolation - tenantId comes from JWT, not from client request.
     */
    private String getTenantId() {
        return RequestContextHolder.get()
                .map(c -> c.tenantId())
                .orElseThrow(() -> {
                    log.error("Missing tenantId in RequestContext");
                    return new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                            "Missing tenantId in JWT context");
                });
    }

    /**
     * Get current user from RequestContext (for audit trail).
     */
    private String getCurrentUser() {
        return RequestContextHolder.get()
                .map(c -> c.userId())
                .orElse("system");
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a new fraud signal",
            description = "Creates a new fraud detection signal for the authenticated tenant. " +
                    "The signal is automatically scoped to the tenant from the JWT token."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Signal created successfully",
                    content = @Content(schema = @Schema(implementation = SignalResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing tenant context"),
            @ApiResponse(responseCode = "400", description = "Bad request - Invalid parameters")
    })
    public SignalResponse createSignal(
            @Parameter(description = "Signal creation request", required = true)
            @Valid @RequestBody CreateSignalRequest request
    ) {
        String tenantId = getTenantId();
        log.info("Creating fraud signal for tenant: {}, transaction: {}", tenantId, request.transactionId());

        AntiFraudSignalService.CreateSignalRequest serviceRequest =
                new AntiFraudSignalService.CreateSignalRequest(
                        request.transactionId(),
                        request.signalType(),
                        request.severity(),
                        request.riskScore(),
                        request.description(),
                        request.metadata(),
                        getCurrentUser()
                );

        AntiFraudSignal signal = signalService.createSignal(tenantId, serviceRequest);
        return SignalResponse.fromDomain(signal);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get a signal by ID",
            description = "Retrieves a specific fraud detection signal by ID. " +
                    "Only returns the signal if it belongs to the authenticated tenant."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Signal retrieved successfully",
                    content = @Content(schema = @Schema(implementation = SignalResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing tenant context"),
            @ApiResponse(responseCode = "404", description = "Signal not found or does not belong to tenant")
    })
    public ResponseEntity<SignalResponse> getSignal(
            @Parameter(description = "Signal ID", required = true, example = "507f1f77bcf86cd799439011")
            @PathVariable String id
    ) {
        String tenantId = getTenantId();
        log.debug("Getting signal {} for tenant: {}", id, tenantId);

        return signalService.findById(tenantId, id)
                .map(signal -> ResponseEntity.ok(SignalResponse.fromDomain(signal)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(
            summary = "Get all signals for tenant",
            description = "Retrieves all fraud detection signals for the authenticated tenant. " +
                    "Only returns signals belonging to the tenant from the JWT token."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Signals retrieved successfully",
                    content = @Content(schema = @Schema(implementation = SignalResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing tenant context")
    })
    public Page<SignalResponse> getSignals(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Filter by resolved status", example = "false")
            @RequestParam(required = false) Boolean resolved
    ) {
        String tenantId = getTenantId();
        log.debug("Getting signals for tenant: {}, page: {}, size: {}", tenantId, page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AntiFraudSignal> signals = signalService.findByTenant(tenantId, pageable);

        return signals.map(SignalResponse::fromDomain);
    }

    @GetMapping("/transaction/{transactionId}")
    @Operation(
            summary = "Get signals by transaction",
            description = "Retrieves all fraud detection signals for a specific transaction " +
                    "within the authenticated tenant."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Signals retrieved successfully",
                    content = @Content(schema = @Schema(implementation = SignalResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing tenant context")
    })
    public List<SignalResponse> getSignalsByTransaction(
            @Parameter(description = "Transaction ID", required = true, example = "txn-12345")
            @PathVariable String transactionId
    ) {
        String tenantId = getTenantId();
        log.debug("Getting signals for transaction {} in tenant: {}", transactionId, tenantId);

        List<AntiFraudSignal> signals = signalService.findByTransaction(tenantId, transactionId);
        return signals.stream()
                .map(SignalResponse::fromDomain)
                .toList();
    }

    @GetMapping("/unresolved")
    @Operation(
            summary = "Get unresolved signals",
            description = "Retrieves all unresolved fraud detection signals for the authenticated tenant."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Signals retrieved successfully",
                    content = @Content(schema = @Schema(implementation = SignalResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing tenant context")
    })
    public List<SignalResponse> getUnresolvedSignals() {
        String tenantId = getTenantId();
        log.debug("Getting unresolved signals for tenant: {}", tenantId);

        List<AntiFraudSignal> signals = signalService.findUnresolvedByTenant(tenantId);
        return signals.stream()
                .map(SignalResponse::fromDomain)
                .toList();
    }

    @GetMapping("/severity/{severity}")
    @Operation(
            summary = "Get signals by severity",
            description = "Retrieves all fraud detection signals of a specific severity " +
                    "for the authenticated tenant."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Signals retrieved successfully",
                    content = @Content(schema = @Schema(implementation = SignalResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing tenant context")
    })
    public List<SignalResponse> getSignalsBySeverity(
            @Parameter(description = "Signal severity", required = true, example = "HIGH")
            @PathVariable AntiFraudSignal.SignalSeverity severity
    ) {
        String tenantId = getTenantId();
        log.debug("Getting signals with severity {} for tenant: {}", severity, tenantId);

        List<AntiFraudSignal> signals = signalService.findByTenantAndSeverity(tenantId, severity);
        return signals.stream()
                .map(SignalResponse::fromDomain)
                .toList();
    }

    @GetMapping("/high-risk")
    @Operation(
            summary = "Get high-risk signals",
            description = "Retrieves all high-risk unresolved fraud detection signals " +
                    "for the authenticated tenant."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Signals retrieved successfully",
                    content = @Content(schema = @Schema(implementation = SignalResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing tenant context")
    })
    public List<SignalResponse> getHighRiskSignals(
            @Parameter(description = "Minimum risk score", example = "75.0")
            @RequestParam(defaultValue = "75.0") double minRiskScore
    ) {
        String tenantId = getTenantId();
        log.debug("Getting high-risk signals (score >= {}) for tenant: {}", minRiskScore, tenantId);

        List<AntiFraudSignal> signals = signalService.findHighRiskSignals(tenantId, minRiskScore);
        return signals.stream()
                .map(SignalResponse::fromDomain)
                .toList();
    }

    @GetMapping("/type/{signalType}")
    @Operation(
            summary = "Get signals by type",
            description = "Retrieves all fraud detection signals of a specific type " +
                    "for the authenticated tenant."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Signals retrieved successfully",
                    content = @Content(schema = @Schema(implementation = SignalResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing tenant context")
    })
    public List<SignalResponse> getSignalsByType(
            @Parameter(description = "Signal type", required = true, example = "high_value_transaction")
            @PathVariable String signalType
    ) {
        String tenantId = getTenantId();
        log.debug("Getting signals by type {} for tenant: {}", signalType, tenantId);

        List<AntiFraudSignal> signals = signalService.findBySignalType(tenantId, signalType);
        return signals.stream()
                .map(SignalResponse::fromDomain)
                .toList();
    }

    @GetMapping("/date-range")
    @Operation(
            summary = "Get signals by date range",
            description = "Retrieves all fraud detection signals within a date range " +
                    "for the authenticated tenant."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Signals retrieved successfully",
                    content = @Content(schema = @Schema(implementation = SignalResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing tenant context")
    })
    public List<SignalResponse> getSignalsByDateRange(
            @Parameter(description = "Start date (ISO-8601)", required = true, example = "2024-01-01T00:00:00Z")
            @RequestParam Instant startDate,
            @Parameter(description = "End date (ISO-8601)", required = true, example = "2024-01-31T23:59:59Z")
            @RequestParam Instant endDate
    ) {
        String tenantId = getTenantId();
        log.debug("Getting signals between {} and {} for tenant: {}", startDate, endDate, tenantId);

        List<AntiFraudSignal> signals = signalService.findByDateRange(tenantId, startDate, endDate);
        return signals.stream()
                .map(SignalResponse::fromDomain)
                .toList();
    }

    @PatchMapping("/{id}/resolve")
    @Operation(
            summary = "Resolve a signal",
            description = "Resolves a fraud detection signal. " +
                    "Only updates the signal if it belongs to the authenticated tenant."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Signal resolved successfully",
                    content = @Content(schema = @Schema(implementation = SignalResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing tenant context"),
            @ApiResponse(responseCode = "404", description = "Signal not found or does not belong to tenant")
    })
    public SignalResponse resolveSignal(
            @Parameter(description = "Signal ID", required = true, example = "507f1f77bcf86cd799439011")
            @PathVariable String id,
            @Parameter(description = "Signal resolution request", required = true)
            @Valid @RequestBody ResolveSignalRequest request
    ) {
        String tenantId = getTenantId();
        log.info("Resolving signal {} for tenant: {}", id, tenantId);

        AntiFraudSignalService.ResolveSignalRequest serviceRequest =
                new AntiFraudSignalService.ResolveSignalRequest(
                        request.resolved(),
                        request.resolvedBy(),
                        request.resolutionNotes()
                );

        AntiFraudSignal signal = signalService.resolveSignal(tenantId, id, serviceRequest);
        return SignalResponse.fromDomain(signal);
    }

    @GetMapping("/stats")
    @Operation(
            summary = "Get signal statistics",
            description = "Returns statistics about fraud detection signals for the authenticated tenant."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing tenant context")
    })
    public SignalStatsResponse getStats() {
        String tenantId = getTenantId();
        log.debug("Getting stats for tenant: {}", tenantId);

        long unresolvedCount = signalService.countUnresolvedByTenant(tenantId);
        long highSeverityCount = signalService.countBySeverity(tenantId, AntiFraudSignal.SignalSeverity.HIGH);
        long criticalSeverityCount = signalService.countBySeverity(tenantId, AntiFraudSignal.SignalSeverity.CRITICAL);

        return new SignalStatsResponse(unresolvedCount, highSeverityCount, criticalSeverityCount);
    }

    /**
     * Statistics response for signals.
     */
    public record SignalStatsResponse(
            @Schema(description = "Number of unresolved signals", example = "25")
            long unresolvedSignalCount,
            @Schema(description = "Number of high severity signals", example = "5")
            long highSeverityCount,
            @Schema(description = "Number of critical severity signals", example = "2")
            long criticalSeverityCount
    ) {}
}
