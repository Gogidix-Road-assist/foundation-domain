package com.gogidix.rapidassist.shared.audit.library.domain.model;

import jakarta.validation.constraints.NotBlank;

public record AuditActor(
        @NotBlank String type,
        @NotBlank String id,
        String displayName,
        String name
) {
    /**
     * Constructor for backward compatibility - name defaults to displayName
     */
    public AuditActor(String type, String id, String displayName) {
        this(type, id, displayName, displayName);
    }
}
