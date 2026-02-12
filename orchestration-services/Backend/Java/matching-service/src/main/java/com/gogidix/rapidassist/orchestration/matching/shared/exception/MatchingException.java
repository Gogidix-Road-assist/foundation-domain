package com.gogidix.rapidassist.orchestration.matching.shared.exception;

public class MatchingException extends RuntimeException {

    private final String errorCode;

    public MatchingException(String message) {
        super(message);
        this.errorCode = "MATCHING_ERROR";
    }

    public MatchingException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public MatchingException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "MATCHING_ERROR";
    }

    public String getErrorCode() {
        return errorCode;
    }
}
