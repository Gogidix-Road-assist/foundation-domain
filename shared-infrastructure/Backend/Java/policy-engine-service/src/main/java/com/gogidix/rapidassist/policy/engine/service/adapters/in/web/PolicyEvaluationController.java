package com.gogidix.rapidassist.policy.engine.service.adapters.in.web;

import com.gogidix.rapidassist.policy.engine.service.domain.model.PolicyDecision;
import com.gogidix.rapidassist.policy.engine.service.domain.port.in.EvaluatePolicyCommand;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

/**
 * REST controller for Policy Engine operations.
 * <p>
 * Provides policy evaluation endpoints for business rule enforcement.
 *
 * @author Gogidix
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/policies")
@Tag(name = "Policy Evaluation", description = "Policy evaluation and decision APIs")
@SecurityRequirement(name = "OAuth2")
public class PolicyEvaluationController {

    private final EvaluatePolicyCommand evaluatePolicyCommand;

    public PolicyEvaluationController(EvaluatePolicyCommand evaluatePolicyCommand) {
        this.evaluatePolicyCommand = evaluatePolicyCommand;
    }

    /**
     * Evaluates a policy against the provided input.
     *
     * @param request Policy evaluation request
     * @return Policy evaluation response with decision
     */
    @PostMapping("/evaluate")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Evaluate policy",
            description = """
                    Evaluates a business policy against the provided input parameters.
                    Returns an allow/deny decision with optional reason and obligations.

                    The tenant ID is extracted from the JWT context. Ensure your authentication
                    token includes the tenantId claim.

                    Policy evaluation is deterministic based on:
                    - Policy ID (the specific policy to evaluate)
                    - Input parameters (contextual data for evaluation)
                    - Tenant configuration (tenant-specific policy rules)
                    """,
            operationId = "evaluatePolicy"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Policy evaluated successfully",
                    content = @Content(schema = @Schema(implementation = PolicyEvaluationResponse.class))
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
    public PolicyEvaluationResponse evaluate(
            @Parameter(
                    description = "Policy evaluation request",
                    required = true
            )
            @Valid @RequestBody PolicyEvaluationRequest request
    ) {
        String tenantId = RequestContextHolder.get()
                .map(c -> c.tenantId())
                .orElse(null);
        if (tenantId == null || tenantId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing tenantId in JWT context");
        }

        Map<String, Object> input = request.input() == null ? Map.of() : request.input();
        PolicyDecision decision = evaluatePolicyCommand.evaluate(tenantId, request.policyId(), input);

        return new PolicyEvaluationResponse(
                decision.allowed(),
                decision.policyId(),
                decision.reason(),
                decision.obligations()
        );
    }
}
