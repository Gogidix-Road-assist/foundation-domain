package com.gogidix.rapidassist.payment.service.adapters.in.web;

import com.gogidix.rapidassist.payment.service.domain.model.PaymentIntent;
import com.gogidix.rapidassist.payment.service.domain.port.in.CreatePaymentIntentCommand;
import com.gogidix.rapidassist.payment.service.domain.port.in.GetPaymentIntentQuery;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * REST controller for Payment Service operations.
 * <p>
 * Provides endpoints for:
 * - Creating payment intents
 * - Retrieving payment intent details
 * <p>
 * All endpoints require OAuth2 authentication and tenant context.
 *
 * @author Gogidix
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "Payment Intents", description = "Payment intent management APIs")
@SecurityRequirement(name = "OAuth2")
public class PaymentController {

    private final CreatePaymentIntentCommand createPaymentIntentCommand;
    private final GetPaymentIntentQuery getPaymentIntentQuery;

    public PaymentController(CreatePaymentIntentCommand createPaymentIntentCommand,
                             GetPaymentIntentQuery getPaymentIntentQuery) {
        this.createPaymentIntentCommand = createPaymentIntentCommand;
        this.getPaymentIntentQuery = getPaymentIntentQuery;
    }

    /**
     * Creates a new payment intent.
     *
     * @param request Payment intent creation request with amount and currency
     * @return Payment intent response with intent details
     */
    @PostMapping("/intents")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create payment intent",
            description = """
                    Creates a new payment intent with the specified amount and currency.
                    The payment intent is processed using Stripe or the configured payment provider.

                    The tenant ID is extracted from the JWT context. Ensure your authentication
                    token includes the tenantId claim.

                    Example amount format:
                    - 10.00 USD = 1000 (amount in cents)
                    - 5.50 EUR = 550 (amount in cents)
                    """,
            operationId = "createPaymentIntent"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Payment intent created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PaymentIntentResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    value = """
                                            {
                                                "intentId": "pi_3Nh1234567890",
                                                "currency": "USD",
                                                "amount": 1000,
                                                "status": "requires_payment_method",
                                                "createdAt": "2025-01-12T10:30:00Z"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request - validation failed",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - missing or invalid authentication",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public PaymentIntentResponse create(
            @Parameter(
                    description = "Payment intent creation request",
                    required = true,
                    example = """
                            {
                                "amount": 1000,
                                "currency": "USD"
                            }
                            """
            )
            @Valid @RequestBody CreatePaymentIntentRequest request
    ) {
        String tenantId = RequestContextHolder.get()
                .map(c -> c.tenantId())
                .orElse(null);
        if (tenantId == null || tenantId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing tenantId in JWT context");
        }

        PaymentIntent intent = createPaymentIntentCommand.create(
                tenantId,
                request.amount(),
                request.currency()
        );
        return new PaymentIntentResponse(
                intent.intentId(),
                intent.currency(),
                intent.amount(),
                intent.status(),
                intent.createdAt()
        );
    }

    /**
     * Retrieves a payment intent by ID.
     *
     * @param intentId The payment intent ID
     * @return Payment intent response with current details
     */
    @GetMapping("/intents/{intentId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get payment intent",
            description = """
                    Retrieves the current details of a payment intent by its ID.
                    Only payment intents belonging to the tenant in the JWT context are accessible.

                    The response includes the current status, amount, and other details of the payment intent.
                    """,
            operationId = "getPaymentIntent"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Payment intent retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PaymentIntentResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    value = """
                                            {
                                                "intentId": "pi_3Nh1234567890",
                                                "currency": "USD",
                                                "amount": 1000,
                                                "status": "succeeded",
                                                "createdAt": "2025-01-12T10:30:00Z"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - missing or invalid authentication",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Payment intent not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public PaymentIntentResponse get(
            @Parameter(
                    description = "Payment intent ID",
                    required = true,
                    example = "pi_3Nh1234567890"
            )
            @PathVariable String intentId
    ) {
        String tenantId = RequestContextHolder.get()
                .map(c -> c.tenantId())
                .orElse(null);
        if (tenantId == null || tenantId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing tenantId in JWT context");
        }

        PaymentIntent intent = getPaymentIntentQuery.get(tenantId, intentId);
        if (intent == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment intent not found");
        }

        return new PaymentIntentResponse(
                intent.intentId(),
                intent.currency(),
                intent.amount(),
                intent.status(),
                intent.createdAt()
        );
    }
}
