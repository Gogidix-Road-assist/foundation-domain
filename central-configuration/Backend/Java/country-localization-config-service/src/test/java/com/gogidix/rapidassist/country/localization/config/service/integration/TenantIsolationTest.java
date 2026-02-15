package com.gogidix.rapidassist.country.localization.config.service.integration;

import com.gogidix.rapidassist.country.localization.config.service.domain.model.CountryLocalization;
import com.gogidix.rapidassist.country.localization.config.service.domain.repository.CountryLocalizationRepositoryInterface;
import com.gogidix.rapidassist.country.localization.config.service.infrastructure.persistence.mongodb.CountryLocalizationRepositoryImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Disabled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * CRITICAL TEST: Validates tenant isolation in the Country Localization service.
 *
 * <p>This test ensures that:
 * <ul>
 *   <li>Tenant A cannot access Tenant B's localizations</li>
 *   <li>All queries properly filter by tenantId</li>
 *   <li>Deletion is scoped to tenant</li>
 *   <li>Updates cannot affect other tenants' data</li>
 * </ul>
 *
 * <p>This test MUST pass before the service can be considered production-ready.
 */
@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Disabled("Requires MongoDB connection - temporarily disabled for CI/CD")
public class TenantIsolationTest {

    private static final Logger logger = LoggerFactory.getLogger(TenantIsolationTest.class);

    private static final String TENANT_A = "tenant-a-isolation-test";
    private static final String TENANT_B = "tenant-b-isolation-test";

    @Autowired
    private CountryLocalizationRepositoryInterface localizationRepository;

    @Autowired
    private CountryLocalizationRepositoryImpl mongoRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private CountryLocalization localizationA1;
    private CountryLocalization localizationA2;
    private CountryLocalization localizationB1;

    @BeforeEach
    void cleanupTestData() {
        // Clean up any existing test data
        mongoTemplate.remove(
            new Query(Criteria.where("tenantId").in(TENANT_A, TENANT_B)),
            com.gogidix.rapidassist.country.localization.config.service.infrastructure.persistence.mongodb.CountryLocalizationRepositoryImpl.CountryLocalizationDocument.class
        );
    }

    @Test
    @Order(1)
    @DisplayName("[CRITICAL] When Tenant A creates localization, only Tenant A can read it")
    void whenTenantACreatesLocalization_thenOnlyTenantACanReadIt() {
        // Given: Tenant A creates a localization
        localizationA1 = CountryLocalization.builder()
            .tenantId(TENANT_A)
            .countryCode("IE")
            .countryName("Ireland")
            .locale(CountryLocalization.LocaleConfig.of("en", "IE"))
            .currency(CountryLocalization.CurrencyConfig.of("EUR", "€"))
            .dateTime(CountryLocalization.DateTimeConfig.of("Europe/Dublin"))
            .addressFormat(CountryLocalization.AddressFormat.of("{street}\\n{city}\\n{postcode}",
                List.of("street", "city", "postcode")))
            .phoneFormat(CountryLocalization.PhoneFormat.of("+353"))
            .emergencyServices(CountryLocalization.EmergencyServices.of("112", "112"))
            .legalRequirements(CountryLocalization.LegalRequirements.standard())
            .measurementSystem(CountryLocalization.MeasurementSystem.METRIC)
            .active(true)
            .createdBy("test-user")
            .build();

        CompletableFuture<CountryLocalization> saveFuture = localizationRepository.save(localizationA1);
        CountryLocalization saved = saveFuture.join();

        assertNotNull(saved);
        assertNotNull(saved.id());
        assertEquals(TENANT_A, saved.tenantId());

        // When: Tenant A queries by their tenant ID
        CompletableFuture<List<CountryLocalization>> tenantAQuery = localizationRepository.findByTenantId(TENANT_A);
        List<CountryLocalization> tenantAResults = tenantAQuery.join();

        // Then: Tenant A should see their localization
        assertThat(tenantAResults).hasSize(1);
        assertThat(tenantAResults.get(0).tenantId()).isEqualTo(TENANT_A);
        assertThat(tenantAResults.get(0).countryCode()).isEqualTo("IE");

        // When: Tenant B queries by their tenant ID
        CompletableFuture<List<CountryLocalization>> tenantBQuery = localizationRepository.findByTenantId(TENANT_B);
        List<CountryLocalization> tenantBResults = tenantBQuery.join();

        // Then: Tenant B should see nothing
        assertThat(tenantBResults).isEmpty();
    }

    @Test
    @Order(2)
    @DisplayName("[CRITICAL] When Tenant A and B both create localizations with same country code, they are isolated")
    void whenTenantsCreateSameCountryCode_thenTheyRemainIsolated() {
        // Given: Both tenants create a localization with the same country code
        localizationA1 = CountryLocalization.builder()
            .tenantId(TENANT_A)
            .countryCode("GB")
            .countryName("United Kingdom")
            .locale(CountryLocalization.LocaleConfig.of("en", "GB"))
            .currency(CountryLocalization.CurrencyConfig.of("GBP", "£"))
            .dateTime(CountryLocalization.DateTimeConfig.of("Europe/London"))
            .addressFormat(CountryLocalization.AddressFormat.of("{street}\\n{city}\\n{postcode}",
                List.of("street", "city", "postcode")))
            .phoneFormat(CountryLocalization.PhoneFormat.of("+44"))
            .emergencyServices(CountryLocalization.EmergencyServices.of("999", "101"))
            .legalRequirements(CountryLocalization.LegalRequirements.standard())
            .measurementSystem(CountryLocalization.MeasurementSystem.IMPERIAL)
            .active(true)
            .createdBy("test-user")
            .build();

        localizationB1 = CountryLocalization.builder()
            .tenantId(TENANT_B)
            .countryCode("GB")
            .countryName("United Kingdom")
            .locale(CountryLocalization.LocaleConfig.of("en", "GB"))
            .currency(CountryLocalization.CurrencyConfig.of("GBP", "£"))
            .dateTime(CountryLocalization.DateTimeConfig.of("Europe/London"))
            .addressFormat(CountryLocalization.AddressFormat.of("{street}\\n{city}\\n{postcode}",
                List.of("street", "city", "postcode")))
            .phoneFormat(CountryLocalization.PhoneFormat.of("+44"))
            .emergencyServices(CountryLocalization.EmergencyServices.of("999", "101"))
            .legalRequirements(CountryLocalization.LegalRequirements.standard())
            .measurementSystem(CountryLocalization.MeasurementSystem.IMPERIAL)
            .active(true)
            .createdBy("test-user")
            .build();

        localizationRepository.save(localizationA1).join();
        localizationRepository.save(localizationB1).join();

        // When: Tenant A queries by natural key
        CompletableFuture<Optional<CountryLocalization>> queryA = localizationRepository.findByNaturalKey(TENANT_A, "GB");
        Optional<CountryLocalization> resultA = queryA.join();

        // Then: Tenant A should get their localization
        assertTrue(resultA.isPresent());
        assertThat(resultA.get().tenantId()).isEqualTo(TENANT_A);
        assertThat(resultA.get().countryCode()).isEqualTo("GB");

        // When: Tenant B queries by the same natural key
        CompletableFuture<Optional<CountryLocalization>> queryB = localizationRepository.findByNaturalKey(TENANT_B, "GB");
        Optional<CountryLocalization> resultB = queryB.join();

        // Then: Tenant B should get their localization
        assertTrue(resultB.isPresent());
        assertThat(resultB.get().tenantId()).isEqualTo(TENANT_B);
        assertThat(resultB.get().countryCode()).isEqualTo("GB");

        // And: They should have different IDs
        assertNotEquals(resultA.get().id(), resultB.get().id());
    }

    @Test
    @Order(3)
    @DisplayName("[CRITICAL] When Tenant A deletes their localization, Tenant B's localization is unaffected")
    void whenTenantADeletes_thenTenantBLocalizationUnaffected() {
        // Given: Both tenants have localizations
        localizationA1 = CountryLocalization.builder()
            .tenantId(TENANT_A)
            .countryCode("FR")
            .countryName("France")
            .locale(CountryLocalization.LocaleConfig.of("fr", "FR"))
            .currency(CountryLocalization.CurrencyConfig.of("EUR", "€"))
            .dateTime(CountryLocalization.DateTimeConfig.of("Europe/Paris"))
            .addressFormat(CountryLocalization.AddressFormat.of("{street}\\n{postcode}\\n{city}",
                List.of("street", "postcode", "city")))
            .phoneFormat(CountryLocalization.PhoneFormat.of("+33"))
            .emergencyServices(CountryLocalization.EmergencyServices.of("112", "112"))
            .legalRequirements(CountryLocalization.LegalRequirements.standard())
            .measurementSystem(CountryLocalization.MeasurementSystem.METRIC)
            .active(true)
            .createdBy("test-user")
            .build();

        localizationB1 = CountryLocalization.builder()
            .tenantId(TENANT_B)
            .countryCode("FR")
            .countryName("France")
            .locale(CountryLocalization.LocaleConfig.of("fr", "FR"))
            .currency(CountryLocalization.CurrencyConfig.of("EUR", "€"))
            .dateTime(CountryLocalization.DateTimeConfig.of("Europe/Paris"))
            .addressFormat(CountryLocalization.AddressFormat.of("{street}\\n{postcode}\\n{city}",
                List.of("street", "postcode", "city")))
            .phoneFormat(CountryLocalization.PhoneFormat.of("+33"))
            .emergencyServices(CountryLocalization.EmergencyServices.of("112", "112"))
            .legalRequirements(CountryLocalization.LegalRequirements.standard())
            .measurementSystem(CountryLocalization.MeasurementSystem.METRIC)
            .active(true)
            .createdBy("test-user")
            .build();

        CountryLocalization savedA = localizationRepository.save(localizationA1).join();
        localizationRepository.save(localizationB1).join();

        // When: Tenant A deletes their localization
        CompletableFuture<Boolean> deleteFuture = localizationRepository.deleteByNaturalKey(TENANT_A, "FR");
        boolean deleted = deleteFuture.join();

        assertTrue(deleted, "Deletion should succeed");

        // Then: Tenant A should not find their localization
        CompletableFuture<Optional<CountryLocalization>> queryA = localizationRepository.findByNaturalKey(TENANT_A, "FR");
        Optional<CountryLocalization> resultA = queryA.join();

        assertFalse(resultA.isPresent(), "Tenant A should not find their deleted localization");

        // And: Tenant B's localization should still exist
        CompletableFuture<Optional<CountryLocalization>> queryB = localizationRepository.findByNaturalKey(TENANT_B, "FR");
        Optional<CountryLocalization> resultB = queryB.join();

        assertTrue(resultB.isPresent(), "Tenant B's localization should still exist");
        assertThat(resultB.get().countryCode()).isEqualTo("FR");
    }

    @Test
    @Order(4)
    @DisplayName("[CRITICAL] When Tenant A updates their localization, Tenant B's localization is unaffected")
    void whenTenantAUpdates_thenTenantBLocalizationUnaffected() {
        // Given: Both tenants have localizations with the same country code
        CountryLocalization originalA = CountryLocalization.builder()
            .tenantId(TENANT_A)
            .countryCode("DE")
            .countryName("Germany")
            .locale(CountryLocalization.LocaleConfig.of("de", "DE"))
            .currency(CountryLocalization.CurrencyConfig.of("EUR", "€"))
            .dateTime(CountryLocalization.DateTimeConfig.of("Europe/Berlin"))
            .addressFormat(CountryLocalization.AddressFormat.of("{street}\\n{postcode}\\n{city}",
                List.of("street", "postcode", "city")))
            .phoneFormat(CountryLocalization.PhoneFormat.of("+49"))
            .emergencyServices(CountryLocalization.EmergencyServices.of("112", "110"))
            .legalRequirements(CountryLocalization.LegalRequirements.standard())
            .measurementSystem(CountryLocalization.MeasurementSystem.METRIC)
            .active(true)
            .createdBy("test-user")
            .build();

        CountryLocalization originalB = CountryLocalization.builder()
            .tenantId(TENANT_B)
            .countryCode("DE")
            .countryName("Germany")
            .locale(CountryLocalization.LocaleConfig.of("de", "DE"))
            .currency(CountryLocalization.CurrencyConfig.of("EUR", "€"))
            .dateTime(CountryLocalization.DateTimeConfig.of("Europe/Berlin"))
            .addressFormat(CountryLocalization.AddressFormat.of("{street}\\n{postcode}\\n{city}",
                List.of("street", "postcode", "city")))
            .phoneFormat(CountryLocalization.PhoneFormat.of("+49"))
            .emergencyServices(CountryLocalization.EmergencyServices.of("112", "110"))
            .legalRequirements(CountryLocalization.LegalRequirements.standard())
            .measurementSystem(CountryLocalization.MeasurementSystem.METRIC)
            .active(true)
            .createdBy("test-user")
            .build();

        CountryLocalization savedA = localizationRepository.save(originalA).join();
        localizationRepository.save(originalB).join();

        // When: Tenant A updates their localization (change country name)
        CountryLocalization updatedA = CountryLocalization.builder()
            .id(savedA.id())
            .tenantId(TENANT_A)
            .countryCode("DE")
            .countryName("Deutschland")  // Changed name
            .locale(savedA.locale())
            .currency(savedA.currency())
            .dateTime(savedA.dateTime())
            .addressFormat(savedA.addressFormat())
            .phoneFormat(savedA.phoneFormat())
            .emergencyServices(savedA.emergencyServices())
            .legalRequirements(savedA.legalRequirements())
            .measurementSystem(savedA.measurementSystem())
            .active(savedA.active())
            .createdBy(savedA.createdBy())
            .createdAt(savedA.createdAt())
            .updatedBy("test-user")
            .updatedAt(java.time.Instant.now())
            .version(savedA.version() + 1)
            .build();

        localizationRepository.save(updatedA).join();

        // Then: Tenant A should see the updated name
        CompletableFuture<Optional<CountryLocalization>> queryA = localizationRepository.findByNaturalKey(TENANT_A, "DE");
        Optional<CountryLocalization> resultA = queryA.join();

        assertTrue(resultA.isPresent());
        assertThat(resultA.get().countryName()).isEqualTo("Deutschland");

        // And: Tenant B should still have their original name
        CompletableFuture<Optional<CountryLocalization>> queryB = localizationRepository.findByNaturalKey(TENANT_B, "DE");
        Optional<CountryLocalization> resultB = queryB.join();

        assertTrue(resultB.isPresent());
        assertThat(resultB.get().countryName()).isEqualTo("Germany");
    }

    @Test
    @Order(5)
    @DisplayName("[CRITICAL] When Tenant A lists by tenant, only Tenant A localizations are returned")
    void whenTenantAListsByTenant_thenOnlyTenantALocalizationsReturned() {
        // Given: Tenant A has 2 localizations, Tenant B has 1 localization
        localizationA1 = CountryLocalization.builder()
            .tenantId(TENANT_A)
            .countryCode("US")
            .countryName("United States")
            .locale(CountryLocalization.LocaleConfig.of("en", "US"))
            .currency(CountryLocalization.CurrencyConfig.of("USD", "$"))
            .dateTime(CountryLocalization.DateTimeConfig.of("America/New_York"))
            .addressFormat(CountryLocalization.AddressFormat.of("{street}\\n{city}, {state} {postcode}",
                List.of("street", "city", "state", "postcode")))
            .phoneFormat(CountryLocalization.PhoneFormat.of("+1"))
            .emergencyServices(CountryLocalization.EmergencyServices.of("911", "311"))
            .legalRequirements(CountryLocalization.LegalRequirements.standard())
            .measurementSystem(CountryLocalization.MeasurementSystem.IMPERIAL)
            .active(true)
            .createdBy("test-user")
            .build();

        localizationA2 = CountryLocalization.builder()
            .tenantId(TENANT_A)
            .countryCode("CA")
            .countryName("Canada")
            .locale(CountryLocalization.LocaleConfig.of("en", "CA"))
            .currency(CountryLocalization.CurrencyConfig.of("CAD", "C$"))
            .dateTime(CountryLocalization.DateTimeConfig.of("America/Toronto"))
            .addressFormat(CountryLocalization.AddressFormat.of("{street}\\n{city}, {state} {postcode}",
                List.of("street", "city", "state", "postcode")))
            .phoneFormat(CountryLocalization.PhoneFormat.of("+1"))
            .emergencyServices(CountryLocalization.EmergencyServices.of("911", "311"))
            .legalRequirements(CountryLocalization.LegalRequirements.standard())
            .measurementSystem(CountryLocalization.MeasurementSystem.METRIC)
            .active(true)
            .createdBy("test-user")
            .build();

        localizationB1 = CountryLocalization.builder()
            .tenantId(TENANT_B)
            .countryCode("AU")
            .countryName("Australia")
            .locale(CountryLocalization.LocaleConfig.of("en", "AU"))
            .currency(CountryLocalization.CurrencyConfig.of("AUD", "A$"))
            .dateTime(CountryLocalization.DateTimeConfig.of("Australia/Sydney"))
            .addressFormat(CountryLocalization.AddressFormat.of("{street}\\n{city} {state} {postcode}",
                List.of("street", "city", "state", "postcode")))
            .phoneFormat(CountryLocalization.PhoneFormat.of("+61"))
            .emergencyServices(CountryLocalization.EmergencyServices.of("000", "131 444"))
            .legalRequirements(CountryLocalization.LegalRequirements.standard())
            .measurementSystem(CountryLocalization.MeasurementSystem.METRIC)
            .active(true)
            .createdBy("test-user")
            .build();

        localizationRepository.save(localizationA1).join();
        localizationRepository.save(localizationA2).join();
        localizationRepository.save(localizationB1).join();

        // When: Tenant A lists localizations
        CompletableFuture<List<CountryLocalization>> queryA = localizationRepository.findByTenantId(TENANT_A);
        List<CountryLocalization> tenantAResults = queryA.join();

        // Then: Tenant A should see only their 2 localizations
        assertThat(tenantAResults).hasSize(2);
        assertThat(tenantAResults).allMatch(c -> TENANT_A.equals(c.tenantId()));

        // When: Tenant B lists localizations
        CompletableFuture<List<CountryLocalization>> queryB = localizationRepository.findByTenantId(TENANT_B);
        List<CountryLocalization> tenantBResults = queryB.join();

        // Then: Tenant B should see only their 1 localization
        assertThat(tenantBResults).hasSize(1);
        assertThat(tenantBResults.get(0).tenantId()).isEqualTo(TENANT_B);
    }

    @Test
    @Order(6)
    @DisplayName("[CRITICAL] Cross-tenant data leak prevention - findByTenantId is strictly isolated")
    void crossTenantDataLeakPrevention_findByTenantId() {
        // Given: Multiple localizations across tenants
        for (int i = 0; i < 5; i++) {
            CountryLocalization loc = CountryLocalization.builder()
                .tenantId(TENANT_A)
                .countryCode("T" + i)
                .countryName("Test Country " + i)
                .locale(CountryLocalization.LocaleConfig.of("en", "US"))
                .currency(CountryLocalization.CurrencyConfig.of("USD", "$"))
                .dateTime(CountryLocalization.DateTimeConfig.of("UTC"))
                .addressFormat(CountryLocalization.AddressFormat.of("{street}\\n{city}",
                    List.of("street", "city")))
                .phoneFormat(CountryLocalization.PhoneFormat.of("+1"))
                .emergencyServices(CountryLocalization.EmergencyServices.of("911", "311"))
                .legalRequirements(CountryLocalization.LegalRequirements.standard())
                .measurementSystem(CountryLocalization.MeasurementSystem.METRIC)
                .active(true)
                .createdBy("test-user")
                .build();
            localizationRepository.save(loc).join();
        }

        for (int i = 0; i < 3; i++) {
            CountryLocalization loc = CountryLocalization.builder()
                .tenantId(TENANT_B)
                .countryCode("U" + i)
                .countryName("Test Country " + i)
                .locale(CountryLocalization.LocaleConfig.of("en", "US"))
                .currency(CountryLocalization.CurrencyConfig.of("USD", "$"))
                .dateTime(CountryLocalization.DateTimeConfig.of("UTC"))
                .addressFormat(CountryLocalization.AddressFormat.of("{street}\\n{city}",
                    List.of("street", "city")))
                .phoneFormat(CountryLocalization.PhoneFormat.of("+1"))
                .emergencyServices(CountryLocalization.EmergencyServices.of("911", "311"))
                .legalRequirements(CountryLocalization.LegalRequirements.standard())
                .measurementSystem(CountryLocalization.MeasurementSystem.METRIC)
                .active(true)
                .createdBy("test-user")
                .build();
            localizationRepository.save(loc).join();
        }

        // When: Tenant A queries all their localizations
        CompletableFuture<List<CountryLocalization>> queryA = localizationRepository.findByTenantId(TENANT_A);
        List<CountryLocalization> tenantAResults = queryA.join();

        // Then: Tenant A should see exactly their 5 localizations, no more, no less
        assertThat(tenantAResults).hasSize(5);
        assertThat(tenantAResults).allMatch(c -> TENANT_A.equals(c.tenantId()));

        // And: Verify no data from Tenant B leaked into Tenant A's results
        long tenantBDataInResults = tenantAResults.stream()
            .filter(c -> TENANT_B.equals(c.tenantId()))
            .count();
        assertThat(tenantBDataInResults).isZero();
    }

    @AfterAll
    void cleanupAllTestData() {
        // Final cleanup
        mongoTemplate.remove(
            new Query(Criteria.where("tenantId").in(TENANT_A, TENANT_B)),
            com.gogidix.rapidassist.country.localization.config.service.infrastructure.persistence.mongodb.CountryLocalizationRepositoryImpl.CountryLocalizationDocument.class
        );
        logger.info("Tenant isolation test cleanup completed");
    }
}
