package com.gogidix.rapidassist.request.routing.service.domain.port.in;

import java.util.Optional;

public interface ResolveRouteQuery {

    Optional<String> resolveDestinationBaseUrl(String tenantId, String routeKey);
}
