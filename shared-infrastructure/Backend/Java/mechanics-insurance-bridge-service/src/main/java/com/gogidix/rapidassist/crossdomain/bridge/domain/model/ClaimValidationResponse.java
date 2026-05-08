package com.gogidix.rapidassist.crossdomain.bridge.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimValidationResponse {
    private String claimId;
    private boolean valid;
    private ValidationResult validationResult;
    private String status; // PENDING, APPROVED, REJECTED, REQUIRES_INFO
    private BigDecimal approvedAmount;
    private String rejectionReason;
    private List<String> requiredDocuments;
    private List<ValidationIssue> issues;
    private LocalDateTime validatedAt;
    private String validatedBy;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationResult {
        private boolean policyActive;
        private boolean coverageValid;
        private boolean withinLimit;
        private boolean workshopApproved;
        private String coverageType; // COMPREHENSIVE, THIRD_PARTY, etc.
        private BigDecimal coverageLimit;
        private BigDecimal remainingLimit;
        private BigDecimal deductible;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationIssue {
        private String code;
        private String severity; // ERROR, WARNING, INFO
        private String message;
        private String field;
    }
}
