package com.gogidix.rapidassist.orchestration.matching.shared.exception;

public class InvalidMatchingRequestException extends MatchingException {

    public InvalidMatchingRequestException(String message) {
        super("INVALID_REQUEST", message);
    }
}
