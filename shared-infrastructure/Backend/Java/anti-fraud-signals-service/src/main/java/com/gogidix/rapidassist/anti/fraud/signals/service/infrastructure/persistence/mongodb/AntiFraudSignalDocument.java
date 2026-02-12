package com.gogidix.rapidassist.anti.fraud.signals.service.infrastructure.persistence.mongodb;

import com.gogidix.rapidassist.anti.fraud.signals.service.domain.model.AntiFraudSignal;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

/**
 * MongoDB document for AntiFraudSignal.
 * CRITICAL: Compound indexes on (tenantId, resolved) and (tenantId, transactionId) for efficient tenant-isolated queries.
 */
@Document(collection = "anti_fraud_signals")
@CompoundIndex(name = "idx_tenant_resolved", def = "{'tenantId': 1, 'resolved': 1}")
@CompoundIndex(name = "idx_tenant_transaction", def = "{'tenantId': 1, 'transactionId': 1}")
@CompoundIndex(name = "idx_tenant_severity", def = "{'tenantId': 1, 'severity': 1, 'createdAt': -1}")
@CompoundIndex(name = "idx_tenant_created", def = "{'tenantId': 1, 'createdAt': -1}")
public record AntiFraudSignalDocument(

        @Id
        String id,

        @Indexed(name = "idx_tenant_id")
        String tenantId,

        @Indexed(name = "idx_transaction_id")
        String transactionId,

        String signalType,

        AntiFraudSignal.SignalSeverity severity,

        double riskScore,

        String description,

        Map<String, Object> metadata,

        boolean resolved,

        String resolvedBy,

        Instant resolvedAt,

        String resolutionNotes,

        Instant createdAt,

        String createdBy
) {

    public static AntiFraudSignalDocument fromDomain(AntiFraudSignal signal) {
        return new AntiFraudSignalDocument(
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

    public AntiFraudSignal toDomain() {
        return AntiFraudSignal.builder()
                .id(id())
                .tenantId(tenantId())
                .transactionId(transactionId())
                .signalType(signalType())
                .severity(severity())
                .riskScore(riskScore())
                .description(description())
                .metadata(metadata())
                .resolved(resolved())
                .resolvedBy(resolvedBy())
                .resolvedAt(resolvedAt())
                .resolutionNotes(resolutionNotes())
                .createdAt(createdAt())
                .createdBy(createdBy())
                .build();
    }

    public static AntiFraudSignalDocument updateFromDomain(AntiFraudSignalDocument existing, AntiFraudSignal signal) {
        return new AntiFraudSignalDocument(
                existing.id(),
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
                existing.createdAt(),
                existing.createdBy()
        );
    }
}
