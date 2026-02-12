package com.gogidix.rapidassist.orchestration.fleetorganization.shared.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String resource, String id) {
        super(String.format("%s not found with id: %s", resource, id));
    }
}
