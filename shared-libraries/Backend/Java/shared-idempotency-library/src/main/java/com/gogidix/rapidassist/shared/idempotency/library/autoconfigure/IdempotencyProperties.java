package com.gogidix.rapidassist.shared.idempotency.library.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;

@ConfigurationProperties(prefix = "gogidix.idempotency")
@Validated
public class IdempotencyProperties {

    private String keyHeader = "Idempotency-Key";
    private boolean required = false;
    private boolean enforceRequestHash = false;
    private int conflictStatusCode = 409;
    private int inProgressStatusCode = 409;

    /**
     * Time-to-live for idempotency records.
     * Default: 24 hours.
     */
    @NotNull
    private Duration ttl = Duration.ofHours(24);

    public String getKeyHeader() {
        return keyHeader;
    }

    public void setKeyHeader(String keyHeader) {
        this.keyHeader = keyHeader;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isEnforceRequestHash() {
        return enforceRequestHash;
    }

    public void setEnforceRequestHash(boolean enforceRequestHash) {
        this.enforceRequestHash = enforceRequestHash;
    }

    public int getConflictStatusCode() {
        return conflictStatusCode;
    }

    public void setConflictStatusCode(int conflictStatusCode) {
        this.conflictStatusCode = conflictStatusCode;
    }

    public int getInProgressStatusCode() {
        return inProgressStatusCode;
    }

    public void setInProgressStatusCode(int inProgressStatusCode) {
        this.inProgressStatusCode = inProgressStatusCode;
    }

    public Duration getTtl() {
        return ttl;
    }

    public void setTtl(Duration ttl) {
        this.ttl = ttl;
    }
}
