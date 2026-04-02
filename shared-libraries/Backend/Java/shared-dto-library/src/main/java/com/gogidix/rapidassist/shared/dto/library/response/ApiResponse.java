package com.gogidix.rapidassist.shared.dto.library.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Standard API Response wrapper for all REST endpoints.
 * Provides consistent response structure across all services.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private int statusCode;
    private T data;
    private LocalDateTime timestamp;
    private String path;
    private ApiError error;

    /**
     * Creates a successful response with data.
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .statusCode(200)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Creates a successful response with data and message.
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .statusCode(200)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Creates a successful response with data, message, and status code.
     */
    public static <T> ApiResponse<T> success(T data, String message, int statusCode) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .statusCode(statusCode)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Creates an error response.
     */
    public static <T> ApiResponse<T> error(int statusCode, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .statusCode(statusCode)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Creates an error response with data.
     */
    public static <T> ApiResponse<T> error(int statusCode, String message, T data) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .statusCode(statusCode)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Creates an error response with ApiError.
     */
    public static <T> ApiResponse<T> error(ApiError apiError) {
        return ApiResponse.<T>builder()
                .success(false)
                .statusCode(apiError.getStatus())
                .error(apiError)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
