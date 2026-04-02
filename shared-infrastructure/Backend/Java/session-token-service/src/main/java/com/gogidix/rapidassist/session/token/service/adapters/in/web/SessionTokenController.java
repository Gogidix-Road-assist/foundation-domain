package com.gogidix.rapidassist.session.token.service.adapters.in.web;

import com.gogidix.rapidassist.session.token.service.config.OpenApiConfiguration;
import com.gogidix.rapidassist.session.token.service.domain.model.SessionIntrospectionResult;
import com.gogidix.rapidassist.session.token.service.domain.model.SessionToken;
import com.gogidix.rapidassist.session.token.service.domain.port.in.IntrospectSessionTokenQuery;
import com.gogidix.rapidassist.session.token.service.domain.port.in.IssueSessionTokenCommand;
import com.gogidix.rapidassist.session.token.service.domain.port.in.RevokeSessionTokenCommand;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;

@RestController
@RequestMapping("/api/v1/session-tokens")
@Tag(name = "Session Token", description = "Session token management APIs")
@SecurityRequirement(name = OpenApiConfiguration.BEARER_AUTH)
public class SessionTokenController {

    private final IssueSessionTokenCommand issueSessionTokenCommand;
    private final IntrospectSessionTokenQuery introspectSessionTokenQuery;
    private final RevokeSessionTokenCommand revokeSessionTokenCommand;

    public SessionTokenController(IssueSessionTokenCommand issueSessionTokenCommand,
                                  IntrospectSessionTokenQuery introspectSessionTokenQuery,
                                  RevokeSessionTokenCommand revokeSessionTokenCommand) {
        this.issueSessionTokenCommand = issueSessionTokenCommand;
        this.introspectSessionTokenQuery = introspectSessionTokenQuery;
        this.revokeSessionTokenCommand = revokeSessionTokenCommand;
    }

    @PostMapping("/issue")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Issue session token", description = "Issues a new session token for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Session token issued successfully",
                    content = @Content(schema = @Schema(implementation = IssueSessionTokenResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid authentication"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public IssueSessionTokenResponse issue(@Valid @RequestBody IssueSessionTokenRequest request) {
        String tenantId = RequestContextHolder.get().map(c -> c.tenantId()).orElse(null);
        if (tenantId == null || tenantId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing tenantId in JWT context");
        }

        SessionToken token = issueSessionTokenCommand.issue(tenantId, request.subject(), Duration.ofSeconds(request.ttlSeconds()));
        return new IssueSessionTokenResponse(token.token(), token.issuedAt(), token.expiresAt());
    }

    @PostMapping("/introspect")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Introspect session token", description = "Introspects a session token to check its validity and retrieve metadata")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token introspected successfully",
                    content = @Content(schema = @Schema(implementation = IntrospectSessionTokenResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid authentication"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public IntrospectSessionTokenResponse introspect(@Valid @RequestBody IntrospectSessionTokenRequest request) {
        String tenantId = RequestContextHolder.get().map(c -> c.tenantId()).orElse(null);
        if (tenantId == null || tenantId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing tenantId in JWT context");
        }

        SessionIntrospectionResult result = introspectSessionTokenQuery.introspect(tenantId, request.token());
        return new IntrospectSessionTokenResponse(result.active(), result.subject(), result.expiresAt(), result.reason());
    }

    @PostMapping("/revoke")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Revoke session token", description = "Revokes a session token, making it invalid for further use")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Session token revoked successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid authentication"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public void revoke(@Valid @RequestBody RevokeSessionTokenRequest request) {
        String tenantId = RequestContextHolder.get().map(c -> c.tenantId()).orElse(null);
        if (tenantId == null || tenantId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing tenantId in JWT context");
        }

        revokeSessionTokenCommand.revoke(tenantId, request.token());
    }
}
