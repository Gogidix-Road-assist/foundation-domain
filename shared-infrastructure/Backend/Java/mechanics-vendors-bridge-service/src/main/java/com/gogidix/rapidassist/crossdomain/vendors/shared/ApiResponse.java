package com.gogidix.rapidassist.crossdomain.vendors.shared;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private Boolean success;
    private Integer statusCode;
    private String message;
    private T data;
    private ApiError error;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApiError {
        private String code;
        private String message;
        private Map<String, String> fieldErrors;
        private LocalDateTime timestamp;
    }
}
