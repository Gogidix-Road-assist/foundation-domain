package com.gogidix.rapidassist.shared.exception.library.autoconfigure;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalProblemDetailExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Validation failed");
        problemDetail.setType(URI.create("https://gogidix.com/problems/validation"));
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        enrich(problemDetail, request);

        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errors.put(fe.getField(), fe.getDefaultMessage());
        }
        problemDetail.setProperty("errors", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(ErrorResponseException.class)
    public ResponseEntity<ProblemDetail> handleErrorResponse(ErrorResponseException ex, HttpServletRequest request) {
        ProblemDetail problemDetail = ex.getBody();
        if (problemDetail.getInstance() == null) {
            problemDetail.setInstance(URI.create(request.getRequestURI()));
        }
        // Ensure title is included in the response as a property
        if (problemDetail.getTitle() != null && !problemDetail.getProperties().containsKey("title")) {
            problemDetail.setProperty("title", problemDetail.getTitle());
        }
        enrich(problemDetail, request);
        return ResponseEntity.status(ex.getStatusCode()).body(problemDetail);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnhandled(Exception ex, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setTitle("Internal server error");
        problemDetail.setType(URI.create("https://gogidix.com/problems/internal"));
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        enrich(problemDetail, request);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }

    private static void enrich(ProblemDetail problemDetail, HttpServletRequest request) {
        if (problemDetail.getProperties().containsKey("timestamp")) {
            return;
        }
        problemDetail.setProperty("timestamp", OffsetDateTime.now().toString());
        problemDetail.setProperty("path", request.getRequestURI());

        String correlationId = MDC.get("correlationId");
        if (correlationId != null && !correlationId.isBlank()) {
            problemDetail.setProperty("correlationId", correlationId);
        }

        String tenantId = MDC.get("tenantId");
        if (tenantId != null && !tenantId.isBlank()) {
            problemDetail.setProperty("tenantId", tenantId);
        }

        String country = MDC.get("country");
        if (country != null && !country.isBlank()) {
            problemDetail.setProperty("country", country);
        }
    }
}
