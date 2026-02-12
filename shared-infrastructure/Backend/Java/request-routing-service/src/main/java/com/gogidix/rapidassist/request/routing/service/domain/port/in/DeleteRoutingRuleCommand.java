package com.gogidix.rapidassist.request.routing.service.domain.port.in;

public interface DeleteRoutingRuleCommand {

    void delete(String tenantId, String routeKey);
}
