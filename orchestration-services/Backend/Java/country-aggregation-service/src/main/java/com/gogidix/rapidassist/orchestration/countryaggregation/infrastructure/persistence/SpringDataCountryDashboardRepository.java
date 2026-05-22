package com.gogidix.rapidassist.orchestration.countryaggregation.infrastructure.persistence;

import com.gogidix.rapidassist.orchestration.countryaggregation.domain.model.CountryDashboard;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpringDataCountryDashboardRepository extends MongoRepository<CountryDashboard, String> {
    Optional<CountryDashboard> findByCountryCode(String countryCode);
}
