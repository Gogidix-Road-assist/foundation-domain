package com.gogidix.rapidassist.orchestration.executiveaggregation.domain.port.out;

import java.util.List;
import java.util.Map;

public interface CountryAggregationClient {

    List<Map<String, Object>> fetchAllCountryDashboards();

    Map<String, Object> fetchCountryDashboard(String countryCode);
}
