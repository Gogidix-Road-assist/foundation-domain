package com.gogidix.rapidassist.request.routing.service.adapters.in.web;

public record ResolvedRouteResponse(
        String routeKey,
        String destinationBaseUrl
) {
}
