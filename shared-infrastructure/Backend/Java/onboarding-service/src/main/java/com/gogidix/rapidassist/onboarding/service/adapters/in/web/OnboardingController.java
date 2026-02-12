package com.gogidix.rapidassist.onboarding.service.adapters.in.web;

import com.gogidix.rapidassist.onboarding.service.config.OpenApiConfiguration;
import com.gogidix.rapidassist.onboarding.service.domain.model.OnboardingRecord;
import com.gogidix.rapidassist.onboarding.service.domain.port.in.GetOnboardingStatusQuery;
import com.gogidix.rapidassist.onboarding.service.domain.port.in.StartOnboardingCommand;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/onboarding")
@Tag(name = "Onboarding", description = "User onboarding management APIs")
@SecurityRequirement(name = OpenApiConfiguration.BEARER_AUTH)
public class OnboardingController {

    private final StartOnboardingCommand startOnboardingCommand;
    private final GetOnboardingStatusQuery getOnboardingStatusQuery;

    public OnboardingController(StartOnboardingCommand startOnboardingCommand, GetOnboardingStatusQuery getOnboardingStatusQuery) {
        this.startOnboardingCommand = startOnboardingCommand;
        this.getOnboardingStatusQuery = getOnboardingStatusQuery;
    }

    @PostMapping("/start")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Start onboarding", description = "Initiates the onboarding process for an authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Onboarding started successfully",
                    content = @Content(schema = @Schema(implementation = OnboardingStatusResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid authentication"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public OnboardingStatusResponse start(Authentication authentication) {
        String tenantId = RequestContextHolder.get().map(c -> c.tenantId()).orElse(null);
        String subject = authentication != null ? authentication.getName() : null;

        if (tenantId == null || tenantId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing tenantId in JWT context");
        }
        if (subject == null || subject.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing subject in JWT context");
        }

        OnboardingRecord record = startOnboardingCommand.start(tenantId, subject);
        return new OnboardingStatusResponse(record.subject(), record.status(), record.updatedAt());
    }

    @GetMapping("/status")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get onboarding status", description = "Retrieves the current onboarding status for an authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Onboarding status retrieved successfully",
                    content = @Content(schema = @Schema(implementation = OnboardingStatusResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid authentication"),
            @ApiResponse(responseCode = "404", description = "Onboarding record not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public OnboardingStatusResponse status(Authentication authentication) {
        String tenantId = RequestContextHolder.get().map(c -> c.tenantId()).orElse(null);
        String subject = authentication != null ? authentication.getName() : null;

        if (tenantId == null || tenantId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing tenantId in JWT context");
        }
        if (subject == null || subject.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing subject in JWT context");
        }

        OnboardingRecord record = getOnboardingStatusQuery.get(tenantId, subject);
        if (record == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Onboarding record not found");
        }

        return new OnboardingStatusResponse(record.subject(), record.status(), record.updatedAt());
    }
}
