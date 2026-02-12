package com.gogidix.rapidassist.mfa.service.adapters.in.web;

import com.gogidix.rapidassist.mfa.service.domain.model.MfaEnrollment;
import com.gogidix.rapidassist.mfa.service.domain.model.MfaVerificationResult;
import com.gogidix.rapidassist.mfa.service.domain.port.in.EnrollMfaCommand;
import com.gogidix.rapidassist.mfa.service.domain.port.in.VerifyMfaCommand;
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

@RestController
@RequestMapping("/api/v1/mfa")
@Tag(name = "Multi-Factor Authentication", description = "APIs for MFA enrollment and verification")
@SecurityRequirement(name = "bearerAuth")
public class MfaController {

    private final EnrollMfaCommand enrollMfaCommand;
    private final VerifyMfaCommand verifyMfaCommand;

    public MfaController(EnrollMfaCommand enrollMfaCommand, VerifyMfaCommand verifyMfaCommand) {
        this.enrollMfaCommand = enrollMfaCommand;
        this.verifyMfaCommand = verifyMfaCommand;
    }

    @PostMapping("/enroll")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Enroll in MFA", description = "Enrolls a user in multi-factor authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully enrolled in MFA"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
            @ApiResponse(responseCode = "401", description = "Missing or invalid authentication", content = @Content)
    })
    public EnrollMfaResponse enroll(@Valid @RequestBody EnrollMfaRequest request) {
        String tenantId = RequestContextHolder.get().map(c -> c.tenantId()).orElse(null);
        if (tenantId == null || tenantId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing tenantId in JWT context");
        }

        MfaEnrollment enrollment = enrollMfaCommand.enroll(tenantId, request.subject(), request.method());
        return new EnrollMfaResponse(enrollment.enrollmentId(), enrollment.enrolledAt());
    }

    @PostMapping("/verify")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Verify MFA code", description = "Verifies a multi-factor authentication code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "MFA verification result"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
            @ApiResponse(responseCode = "401", description = "Missing or invalid authentication", content = @Content)
    })
    public VerifyMfaResponse verify(@Valid @RequestBody VerifyMfaRequest request) {
        String tenantId = RequestContextHolder.get().map(c -> c.tenantId()).orElse(null);
        if (tenantId == null || tenantId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing tenantId in JWT context");
        }

        MfaVerificationResult result = verifyMfaCommand.verify(tenantId, request.subject(), request.enrollmentId(), request.code());
        return new VerifyMfaResponse(result.verified(), result.reason());
    }
}
