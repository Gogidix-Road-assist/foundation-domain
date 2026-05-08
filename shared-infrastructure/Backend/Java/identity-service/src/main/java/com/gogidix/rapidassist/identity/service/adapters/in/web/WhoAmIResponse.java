package com.gogidix.rapidassist.identity.service.adapters.in.web;

public record WhoAmIResponse(
        String tenantId,
        String country,
        String correlationId
) {
}
