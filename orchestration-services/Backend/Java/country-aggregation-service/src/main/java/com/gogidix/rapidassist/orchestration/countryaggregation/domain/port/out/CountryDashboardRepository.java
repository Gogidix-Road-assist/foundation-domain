package com.gogidix.rapidassist.orchestration.countryaggregation.domain.port.out;

import com.gogidix.rapidassist.orchestration.countryaggregation.domain.model.CountryDashboard;
import com.gogidix.rapidassist.orchestration.countryaggregation.domain.model.DomainHealthSnapshot;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CountryDashboardRepository {

    Optional<CountryDashboard> findByCountryCode(String countryCode);

    List<CountryDashboard> findAll();

    CountryDashboard save(CountryDashboard dashboard);

    List<DomainHealthSnapshot> findHealthByCountryCode(String countryCode);

    Optional<DomainHealthSnapshot> findHealthByCountryAndDomain(String countryCode, String domainName);

    DomainHealthSnapshot saveHealth(DomainHealthSnapshot snapshot);
}
