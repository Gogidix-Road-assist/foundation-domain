package com.gogidix.rapidassist.orchestration.countryaggregation.infrastructure.persistence;

import com.gogidix.rapidassist.orchestration.countryaggregation.domain.model.CountryDashboard;
import com.gogidix.rapidassist.orchestration.countryaggregation.domain.model.DomainHealthSnapshot;
import com.gogidix.rapidassist.orchestration.countryaggregation.domain.port.out.CountryDashboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MongoCountryDashboardAdapter implements CountryDashboardRepository {

    private final SpringDataCountryDashboardRepository dashboardRepo;
    private final SpringDataDomainHealthRepository healthRepo;

    @Override
    public Optional<CountryDashboard> findByCountryCode(String countryCode) {
        return dashboardRepo.findByCountryCode(countryCode);
    }

    @Override
    public List<CountryDashboard> findAll() {
        return dashboardRepo.findAll();
    }

    @Override
    public CountryDashboard save(CountryDashboard dashboard) {
        return dashboardRepo.save(dashboard);
    }

    @Override
    public List<DomainHealthSnapshot> findHealthByCountryCode(String countryCode) {
        return healthRepo.findByCountryCode(countryCode);
    }

    @Override
    public Optional<DomainHealthSnapshot> findHealthByCountryAndDomain(String countryCode, String domainName) {
        return healthRepo.findByCountryCodeAndDomainName(countryCode, domainName);
    }

    @Override
    public DomainHealthSnapshot saveHealth(DomainHealthSnapshot snapshot) {
        return healthRepo.save(snapshot);
    }
}
