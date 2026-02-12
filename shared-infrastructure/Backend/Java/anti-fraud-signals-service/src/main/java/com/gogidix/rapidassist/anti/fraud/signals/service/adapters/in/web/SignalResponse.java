package com.gogidix.rapidassist.anti.fraud.signals.service.adapters.in.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gogidix.rapidassist.anti.fraud.signals.service.domain.model.AntiFraudSignal;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Response DTO for an anti-fraud signal.
 */
@Schema(description = "Response containing anti-fraud signal details")
@JsonInclude(Include.NON_NULL)
public record SignalResponse(

        @Schema(description = "Unique identifier of the signal", example = "507f1f77bcf86cd799439011")
        String id,

        @Schema(description = "Tenant identifier", example = "tenant-123")
        String tenantId,

        @Schema(description = "Transaction ID that generated this signal", example = "txn-12345")
        String transactionId,

        @Schema(description = "Type of fraud signal", example = "high_value_transaction")
        String signalType,

        @Schema(description = "Severity of the fraud signal", example = "HIGH")
        AntiFraudSignal.SignalSeverity severity,

        @Schema(description = "Risk score (0-100)", example = "85.5")
        double riskScore,

        @Schema(description = "Description of the fraud signal", example = "Unusually high transaction amount detected")
        String description,

        @Schema(description = "Additional metadata about the signal")
        Map<String, Object> metadata,

        @Schema(description = "Whether the signal is resolved", example = "false")
        boolean resolved,

        @Schema(description = "User who resolved the signal", example = "fraud-analyst@example.com")
        String resolvedBy,

        @Schema(description = "When the signal was resolved", example = "2024-01-01T12:00:00Z")
        Instant resolvedAt,

        @Schema(description = "Notes about the resolution", example = "Verified with customer - legitimate transaction")
        String resolutionNotes,

        @Schema(description = "Timestamp when the signal was created", example = "2024-01-01T00:00:00Z")
        Instant createdAt,

        @Schema(description = "User who created the signal", example = "system")
        String createdBy
) {
    public static SignalResponse fromDomain(AntiFraudSignal signal) {
        return new SignalResponse(
                signal.id(),
                signal.tenantId(),
                signal.transactionId(),
                signal.signalType(),
                signal.severity(),
                signal.riskScore(),
                signal.description(),
                signal.metadata(),
                signal.resolved(),
                signal.resolvedBy(),
                signal.resolvedAt(),
                signal.resolutionNotes(),
                signal.createdAt(),
                signal.createdBy()
        );
    }
}
