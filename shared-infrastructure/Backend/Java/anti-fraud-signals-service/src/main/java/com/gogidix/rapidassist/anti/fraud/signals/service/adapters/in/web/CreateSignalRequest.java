package com.gogidix.rapidassist.anti.fraud.signals.service.adapters.in.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gogidix.rapidassist.anti.fraud.signals.service.domain.model.AntiFraudSignal;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Request DTO for creating an anti-fraud signal.
 */
@Schema(description = "Request to create a new anti-fraud signal")
@JsonInclude(Include.NON_NULL)
public record CreateSignalRequest(

        @Schema(
                description = "ID of the transaction that generated this signal",
                example = "txn-12345",
                required = true
        )
        @NotBlank(message = "transactionId cannot be blank")
        String transactionId,

        @Schema(
                description = "Type of fraud signal",
                example = "high_value_transaction",
                required = true
        )
        @NotBlank(message = "signalType cannot be blank")
        String signalType,

        @Schema(
                description = "Severity of the fraud signal",
                example = "HIGH",
                required = true
        )
        @NotNull(message = "severity cannot be null")
        AntiFraudSignal.SignalSeverity severity,

        @Schema(
                description = "Risk score (0-100, higher = more risky)",
                example = "85.5",
                required = true
        )
        @NotNull(message = "riskScore cannot be null")
        @DecimalMin(value = "0.0", message = "riskScore must be >= 0")
        @DecimalMax(value = "100.0", message = "riskScore must be <= 100")
        double riskScore,

        @Schema(
                description = "Description of the fraud signal",
                example = "Unusually high transaction amount detected"
        )
        String description,

        @Schema(
                description = "Additional metadata about the signal",
                example = "{\"amount\": 15000, \"currency\": \"USD\", \"location\": \"US\"}"
        )
        Map<String, Object> metadata
) {
    public CreateSignalRequest {
        if (riskScore < 0 || riskScore > 100) {
            throw new IllegalArgumentException("riskScore must be between 0 and 100");
        }
    }
}
