package com.gogidix.rapidassist.request.routing.service.domain.port.in;

public interface UpsertRoutingRuleCommand {

    void upsert(String tenantId, String routeKey, String destinationBaseUrl);
}
