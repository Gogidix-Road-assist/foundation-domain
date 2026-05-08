package com.gogidix.rapidassist.access.control.service.interfaces.rest;

import com.gogidix.rapidassist.access.control.service.application.command.CheckAccessCommandHandler;
import com.gogidix.rapidassist.access.control.service.application.dto.request.CheckAccessRequestDto;
import com.gogidix.rapidassist.access.control.service.application.dto.response.AccessDecisionResponseDto;
import com.gogidix.rapidassist.access.control.service.domain.model.AccessDecision;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller: AccessControlController
 *
 * Handles access control check requests.
 * All operations are tenant-scoped via RequestContext.
 */
@RestController
@RequestMapping("/api/v1/access")
@Tag(name = "Access Control", description = "APIs for checking access control and authorization decisions")
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = "${ALLOWED_ORIGINS:http://localhost:3000}")
public class AccessControlController {

    private static final Logger log = LoggerFactory.getLogger(AccessControlController.class);

    private final CheckAccessCommandHandler checkAccessCommandHandler;

    public AccessControlController(CheckAccessCommandHandler checkAccessCommandHandler) {
        this.checkAccessCommandHandler = checkAccessCommandHandler;
    }

    @PostMapping("/check")
    @Operation(
            summary = "Check access authorization",
            description = "Check if a subject (user/service) is authorized to perform an action on a resource. " +
                    "Supports both Role-Based Access Control (RBAC) and Attribute-Based Access Control (ABAC)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Access check completed successfully",
                    content = @Content(schema = @Schema(implementation = AccessDecisionResponseDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "Bad request - Invalid parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing tenant context")
    })
    public ResponseEntity<AccessDecisionResponseDto> check(
            @Parameter(description = "Access check request containing subject, action, and resource", required = true)
            @Valid @RequestBody CheckAccessRequestDto request) {

        String tenantId = RequestContextHolder.get()
                .orElseThrow(() -> new TenantContextException("Missing tenant context"))
                .tenantId();

        log.info("Access check request: tenantId={}, subjectId={}, resource={}, action={}",
                tenantId, request.subjectId(), request.resource(), request.action());

        AccessDecision decision = checkAccessCommandHandler.checkWithContext(
                tenantId,
                request.subjectId(),
                request.resource(),
                request.action(),
                request.context()
        );

        AccessDecisionResponseDto response = decision.isAllowed()
                ? AccessDecisionResponseDto.allowed(decision.reason(), tenantId,
                        request.subjectId(), request.resource(), request.action())
                : AccessDecisionResponseDto.denied(decision.reason(), tenantId,
                        request.subjectId(), request.resource(), request.action());

        return ResponseEntity.ok(response);
    }

    /**
     * Exception thrown when tenant context is missing.
     */
    public static class TenantContextException extends RuntimeException {
        public TenantContextException(String message) {
            super(message);
        }
    }
}
