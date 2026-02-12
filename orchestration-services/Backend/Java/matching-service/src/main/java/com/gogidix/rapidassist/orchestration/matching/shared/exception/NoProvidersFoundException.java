package com.gogidix.rapidassist.orchestration.matching.shared.exception;

public class NoProvidersFoundException extends MatchingException {

    public NoProvidersFoundException(String message) {
        super("NO_PROVIDERS_FOUND", message);
    }
}
