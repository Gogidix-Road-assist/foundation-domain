package com.gogidix.rapidassist.reporting.read.model.service.adapters.in.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ProblemDetail> handleResponseStatusException(ResponseStatusException ex, HttpServletRequest request) {
        log.warn("Status error on {} {}: {}", request.getMethod(), request.getRequestURI(), ex.getReason());
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(ex.getStatusCode(), ex.getReason() != null ? ex.getReason() : ex.getStatusCode().toString());
        pd.setType(URI.create("https://api.gogidix.com/errors/status"));
        pd.setTitle(ex.getReason());
        pd.setInstance(URI.create(request.getRequestURI()));
        pd.setProperty("timestamp", Instant.now().toString());
        return ResponseEntity.status(ex.getStatusCode()).body(pd);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        log.warn("Validation error: {}", ex.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(", ")));
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Request validation failed");
        pd.setType(URI.create("https://api.gogidix.com/errors/validation"));
        pd.setTitle("Validation Failed");
        pd.setInstance(URI.create(request.getRequestURI()));
        pd.setProperty("timestamp", Instant.now().toString());
        return ResponseEntity.badRequest().body(pd);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
        pd.setType(URI.create("https://api.gogidix.com/errors/internal"));
        pd.setTitle("Internal Server Error");
        pd.setInstance(URI.create(request.getRequestURI()));
        pd.setProperty("timestamp", Instant.now().toString());
        pd.setProperty("errorId", java.util.UUID.randomUUID().toString());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(pd);
    }
}
