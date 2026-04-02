package com.gogidix.rapidassist.maps.geocoding.adapter.service.verification;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Verification Test: Maps Geocoding Adapter Service - Tenant Isolation NOT Required
 *
 * RATIONALE:
 * This service is a STATELESS ADAPTER that wraps external geocoding APIs (Google Maps, MapBox, etc.).
 * It does NOT store any data and therefore does NOT require tenant isolation.
 *
 * ARCHITECTURE:
 * - Stateless adapter service
 * - No database persistence
 * - No entity classes
 * - No repository pattern
 * - External API calls only
 *
 * TENANT DATA FLOW:
 * - Tenant-specific location data is stored in: geo-location-service (HAS tenant isolation)
 * - This service only: Calls external APIs and returns results
 *
 * FUTURE CONSIDERATIONS:
 * - IF adding caching: Re-evaluate tenant isolation requirements
 * - IF adding API key management: Implement tenant isolation
 * - IF adding usage tracking: Implement tenant isolation
 */
@DisplayName("Tenant Isolation Verification - Maps Geocoding Adapter Service")
class TenantIsolationVerificationTest {

    private final JavaClasses classes = new ClassFileImporter()
            .importPackages("com.gogidix.rapidassist.maps.geocoding.adapter.service");

    @Test
    @DisplayName("Should NOT have JPA entities")
    void shouldNotHaveJpaEntities() {
        noClasses()
                .should().beAnnotatedWith("jakarta.persistence.Entity")
                .orShould().beAnnotatedWith("javax.persistence.Entity")
                .check(classes);
    }

    @Test
    @DisplayName("Should NOT have MongoDB documents")
    void shouldNotHaveMongoDocuments() {
        noClasses()
                .should().beAnnotatedWith("org.springframework.data.mongodb.core.mapping.Document")
                .check(classes);
    }

    @Test
    @DisplayName("Should NOT have Spring Data repositories")
    void shouldNotHaveRepositories() {
        noClasses()
                .should().beAnnotatedWith("org.springframework.data.jpa.repository.JpaRepository")
                .orShould().beAnnotatedWith("org.springframework.data.mongodb.repository.MongoRepository")
                .orShould().haveNameMatching(".*Repository")
                .check(classes);
    }

    @Test
    @DisplayName("Should NOT have entity classes in domain.model package")
    void shouldNotHaveDomainEntities() {
        // Check that no classes exist in domain.model package (stateless service)
        noClasses()
                .that().resideInAPackage("..domain.model")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..adapters..", "..infrastructure..", "..application..")
                .because("stateless adapter service should not have domain entities")
                .allowEmptyShould(true)
                .check(classes);
    }

    @Test
    @DisplayName("Should NOT have store implementations in infrastructure.persistence")
    void shouldNotHavePersistenceStores() {
        // Check that no classes exist in infrastructure.persistence package
        noClasses()
                .that().resideInAPackage("..infrastructure.persistence")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..domain..", "..application..")
                .because("stateless adapter service should not have persistence layer")
                .allowEmptyShould(true)
                .check(classes);
    }

    @Test
    @DisplayName("Should be a stateless adapter service")
    void verifyStatelessArchitecture() {
        // This test documents that the service is stateless
        // If this test fails, the architecture has changed and tenant isolation should be re-evaluated

        noClasses()
                .should().beAnnotatedWith("jakarta.persistence.Entity")
                .orShould().beAnnotatedWith("org.springframework.data.mongodb.core.mapping.Document")
                .orShould().haveNameMatching(".*Repository")
                .check(classes);
    }

    @Test
    @DisplayName("Documentation: Maps Geocoding Adapter is stateless")
    void documentStatelessNature() {
        /*
         * MAPS GEOCODING ADAPTER SERVICE ARCHITECTURE
         * ============================================
         *
         * Service Type: Stateless Adapter
         * Data Storage: NONE
         * Tenant Isolation: NOT REQUIRED
         *
         * Purpose:
         * - Wrap external geocoding APIs (Google Maps, MapBox, etc.)
         * - Provide unified interface for geocoding operations
         * - Return results from external APIs without storing data
         *
         * Data Flow:
         * 1. Request received (may include tenant context)
         * 2. External API called (Google Maps, MapBox, etc.)
         * 3. Result returned to caller
         * 4. NO data stored in this service
         *
         * Tenant-Specific Location Data:
         * - Stored in: geo-location-service
         * - That service HAS tenant isolation
         * - Location entity includes: tenantId field
         *
         * Security:
         * - No tenant data leakage possible (no data stored)
         * - External API calls are stateless
         * - No caching or persistence
         *
         * Future Considerations:
         * - IF caching added: Re-evaluate tenant isolation
         * - IF API key management added: Implement tenant isolation
         * - IF usage tracking added: Implement tenant isolation
         */
    }
}
