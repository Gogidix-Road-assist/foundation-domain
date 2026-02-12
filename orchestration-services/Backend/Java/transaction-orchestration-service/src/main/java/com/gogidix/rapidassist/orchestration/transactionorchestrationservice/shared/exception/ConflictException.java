package com.gogidix.rapidassist.orchestration.transactionorchestrationservice.shared.exception;

public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String resource, String id, String conflict) {
        super(String.format("Conflict on %s with id %s: %s", resource, id, conflict));
    }
}
