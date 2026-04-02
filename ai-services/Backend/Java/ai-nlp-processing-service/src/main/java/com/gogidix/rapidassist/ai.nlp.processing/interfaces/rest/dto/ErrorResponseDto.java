package com.gogidix.rapidassist.ai.nlp.processing.interfaces.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

/**
 * Standard error response DTO for all API errors
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDto {

    private Instant timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private String tenantId;
    private Map<String, String> details;
}
