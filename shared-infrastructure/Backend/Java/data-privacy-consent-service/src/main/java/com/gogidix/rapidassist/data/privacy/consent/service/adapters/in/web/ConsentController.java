package com.gogidix.rapidassist.data.privacy.consent.service.adapters.in.web;

import com.gogidix.rapidassist.data.privacy.consent.service.domain.model.ConsentPreferences;
import com.gogidix.rapidassist.data.privacy.consent.service.domain.port.in.GetConsentPreferencesQuery;
import com.gogidix.rapidassist.data.privacy.consent.service.domain.port.in.UpsertConsentPreferencesCommand;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/consent")
@Tag(name = "Consent", description = "Data privacy consent management APIs")
@SecurityRequirement(name = "bearerAuth")
public class ConsentController {

    private final GetConsentPreferencesQuery getConsentPreferencesQuery;
    private final UpsertConsentPreferencesCommand upsertConsentPreferencesCommand;

    public ConsentController(GetConsentPreferencesQuery getConsentPreferencesQuery, UpsertConsentPreferencesCommand upsertConsentPreferencesCommand) {
        this.getConsentPreferencesQuery = getConsentPreferencesQuery;
        this.upsertConsentPreferencesCommand = upsertConsentPreferencesCommand;
    }

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get consent preferences", description = "Retrieves the consent preferences for the authenticated user")
    public ConsentResponse me(Authentication authentication) {
        String tenantId = RequestContextHolder.get().map(c -> c.tenantId()).orElse(null);
        String subject = authentication != null ? authentication.getName() : null;

        if (tenantId == null || tenantId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing tenantId in JWT context");
        }
        if (subject == null || subject.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing subject in JWT context");
        }

        ConsentPreferences preferences = getConsentPreferencesQuery.get(tenantId, subject);
        if (preferences == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Consent preferences not found");
        }

        return new ConsentResponse(preferences.subject(), preferences.termsAccepted(), preferences.marketingEmails(), preferences.updatedAt());
    }

    @PostMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update consent preferences", description = "Updates or creates consent preferences for the authenticated user")
    public ConsentResponse upsert(Authentication authentication, @Valid @RequestBody UpsertConsentRequest request) {
        String tenantId = RequestContextHolder.get().map(c -> c.tenantId()).orElse(null);
        String subject = authentication != null ? authentication.getName() : null;

        if (tenantId == null || tenantId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing tenantId in JWT context");
        }
        if (subject == null || subject.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing subject in JWT context");
        }

        ConsentPreferences preferences = upsertConsentPreferencesCommand.upsert(
                tenantId,
                subject,
                request.termsAccepted(),
                request.marketingEmails()
        );

        return new ConsentResponse(preferences.subject(), preferences.termsAccepted(), preferences.marketingEmails(), preferences.updatedAt());
    }
}
