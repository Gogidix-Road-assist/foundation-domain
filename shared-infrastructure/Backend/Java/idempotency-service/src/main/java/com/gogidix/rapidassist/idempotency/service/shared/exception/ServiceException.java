package com.gogidix.rapidassist.idempotency.service.shared.exception;

public class ServiceException extends RuntimeException {
    public ServiceException(String message) { super(message); }
}