package com.gogidix.rapidassist.orchestration.countryaggregation.domain.port.in;

import com.gogidix.rapidassist.orchestration.countryaggregation.domain.model.CountryDashboard;
import com.gogidix.rapidassist.orchestration.countryaggregation.domain.model.DomainHealthSnapshot;

import java.util.List;

public interface CountryAggregationUseCase {

    CountryDashboard getCountryDashboard(String countryCode);

    List<CountryDashboard> getAllCountryDashboards();

    CountryDashboard refreshCountryDashboard(String countryCode);

    List<DomainHealthSnapshot> getCountryDomainHealth(String countryCode);

    DomainHealthSnapshot getDomainHealth(String countryCode, String domainName);

    List<String> getSupportedCountries();
}
