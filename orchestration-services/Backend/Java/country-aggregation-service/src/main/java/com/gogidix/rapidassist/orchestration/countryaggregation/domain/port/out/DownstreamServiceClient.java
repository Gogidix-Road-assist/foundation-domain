package com.gogidix.rapidassist.orchestration.countryaggregation.domain.port.out;

import java.util.Map;

public interface DownstreamServiceClient {

    Map<String, Object> fetchFoundationMetrics(String countryCode);

    Map<String, Object> fetchSharedBizMetrics(String countryCode);

    Map<String, Object> fetchClaimMetrics(String countryCode);

    Map<String, Object> fetchIntelligenceMetrics(String countryCode);

    Map<String, Object> checkServiceHealth(String serviceUrl);
}
