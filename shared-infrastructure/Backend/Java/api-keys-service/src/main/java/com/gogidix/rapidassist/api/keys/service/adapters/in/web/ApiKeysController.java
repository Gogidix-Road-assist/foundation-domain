package com.gogidix.rapidassist.api.keys.service.adapters.in.web;

import com.gogidix.rapidassist.api.keys.service.domain.model.ApiKey;
import com.gogidix.rapidassist.api.keys.service.domain.model.ApiKeyVerificationResult;
import com.gogidix.rapidassist.api.keys.service.domain.port.in.IssueApiKeyCommand;
import com.gogidix.rapidassist.api.keys.service.domain.port.in.RevokeApiKeyCommand;
import com.gogidix.rapidassist.api.keys.service.domain.port.in.VerifyApiKeyQuery;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/api-keys")
public class ApiKeysController {

    private final IssueApiKeyCommand issueApiKeyCommand;
    private final VerifyApiKeyQuery verifyApiKeyQuery;
    private final RevokeApiKeyCommand revokeApiKeyCommand;

    public ApiKeysController(IssueApiKeyCommand issueApiKeyCommand,
                             VerifyApiKeyQuery verifyApiKeyQuery,
                             RevokeApiKeyCommand revokeApiKeyCommand) {
        this.issueApiKeyCommand = issueApiKeyCommand;
        this.verifyApiKeyQuery = verifyApiKeyQuery;
        this.revokeApiKeyCommand = revokeApiKeyCommand;
    }

    @PostMapping("/issue")
    @ResponseStatus(HttpStatus.OK)
    public IssueApiKeyResponse issue(@Valid @RequestBody IssueApiKeyRequest request) {
        String tenantId = RequestContextHolder.get().map(c -> c.tenantId()).orElse(null);
        if (tenantId == null || tenantId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing tenantId in JWT context");
        }

        ApiKey apiKey = issueApiKeyCommand.issue(tenantId, request.subject());
        return new IssueApiKeyResponse(apiKey.keyId(), apiKey.key(), apiKey.issuedAt());
    }

    @PostMapping("/verify")
    @ResponseStatus(HttpStatus.OK)
    public VerifyApiKeyResponse verify(@Valid @RequestBody VerifyApiKeyRequest request) {
        String tenantId = RequestContextHolder.get().map(c -> c.tenantId()).orElse(null);
        if (tenantId == null || tenantId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing tenantId in JWT context");
        }

        ApiKeyVerificationResult result = verifyApiKeyQuery.verify(tenantId, request.apiKey());
        return new VerifyApiKeyResponse(result.valid(), result.keyId(), result.reason());
    }

    @PostMapping("/revoke")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void revoke(@Valid @RequestBody RevokeApiKeyRequest request) {
        String tenantId = RequestContextHolder.get().map(c -> c.tenantId()).orElse(null);
        if (tenantId == null || tenantId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing tenantId in JWT context");
        }

        revokeApiKeyCommand.revoke(tenantId, request.keyId());
    }
}
