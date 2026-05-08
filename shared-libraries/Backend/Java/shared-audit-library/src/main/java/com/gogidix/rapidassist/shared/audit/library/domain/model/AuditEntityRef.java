package com.gogidix.rapidassist.shared.audit.library.domain.model;

import jakarta.validation.constraints.NotBlank;

public record AuditEntityRef(
        @NotBlank String type,
        @NotBlank String id,
        String name
) {
    /**
     * Constructor for backward compatibility - name defaults to null
     */
    public AuditEntityRef(String type, String id) {
        this(type, id, null);
    }
}
