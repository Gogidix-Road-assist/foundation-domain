package com.gogidix.rapidassist.orchestration.matching.shared.exception;

public class ProviderNotFoundException extends MatchingException {

    public ProviderNotFoundException(String providerId) {
        super("PROVIDER_NOT_FOUND", String.format("Provider not found: %s", providerId));
    }
}
