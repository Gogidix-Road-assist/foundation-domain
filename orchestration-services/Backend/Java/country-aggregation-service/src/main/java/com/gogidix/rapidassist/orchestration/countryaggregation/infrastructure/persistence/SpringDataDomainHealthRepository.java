package com.gogidix.rapidassist.orchestration.countryaggregation.infrastructure.persistence;

import com.gogidix.rapidassist.orchestration.countryaggregation.domain.model.DomainHealthSnapshot;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataDomainHealthRepository extends MongoRepository<DomainHealthSnapshot, String> {
    List<DomainHealthSnapshot> findByCountryCode(String countryCode);
    Optional<DomainHealthSnapshot> findByCountryCodeAndDomainName(String countryCode, String domainName);
}
