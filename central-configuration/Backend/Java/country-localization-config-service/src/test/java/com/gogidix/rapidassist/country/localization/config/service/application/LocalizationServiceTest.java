package com.gogidix.rapidassist.country.localization.config.service.application;

import com.gogidix.rapidassist.country.localization.config.service.domain.model.CountryLocalization;
import com.gogidix.rapidassist.country.localization.config.service.domain.model.LocalizedResource;
import com.gogidix.rapidassist.country.localization.config.service.domain.port.in.LocalizationCommand;
import com.gogidix.rapidassist.country.localization.config.service.domain.port.out.LocalizationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for LocalizationService.
 * Tests the business logic layer with mocked dependencies.
 */
@ExtendWith(MockitoExtension.class)
class LocalizationServiceTest {

    @Mock
    private LocalizationRepository repository;

    private LocalizationService service;

    private static final String COUNTRY_CODE = "IE";
    private static final String USER = "test-user";

    @BeforeEach
    void setUp() {
        service = new LocalizationService();
        // Use reflection to inject mocks
        try {
            var repositoryField = LocalizationService.class.getDeclaredField("repository");
            repositoryField.setAccessible(true);
            repositoryField.set(service, repository);
        } catch (Exception e) {
            fail("Failed to inject mocks: " + e.getMessage());
        }
    }

    private CountryLocalization createTestCountryLocalization() {
        return CountryLocalization.builder()
            .countryCode(COUNTRY_CODE)
            .countryName("Ireland")
            .locale(new CountryLocalization.LocaleConfig("en-IE", "en", "IE"))
            .currency(new CountryLocalization.CurrencyConfig("EUR", "\u20ac", 2, "left"))
            .dateTime(new CountryLocalization.DateTimeConfig("dd/MM/yyyy", "HH:mm", "UTC+0", "GMT", "Europe/Dublin"))
            .addressFormat(new CountryLocalization.AddressFormat("{street}, {city}, {county}", "{street}\n{city}\n{county}", "postal", "IE"))
            .phoneFormat(new CountryLocalization.PhoneFormat("(XXX) XXX-XXXX", "+353", "8", "10", "^(\\+353)?[0-9]{10}$"))
            .emergencyServices(new CountryLocalization.EmergencyServices("112", "999", "112", "112"))
            .legalRequirements(new CountryLocalization.LegalRequirements("GDPR", "English", "21", true))
            .measurementSystem(CountryLocalization.MeasurementSystem.METRIC)
            .active(true)
            .createdBy(USER)
            .build();
    }

    @Nested
    class CreateCountryLocalizationTests {

        @Test
        void createCountryLocalization_Success() {
            // Given
            LocalizationCommand.CreateCountryCommand command = new LocalizationCommand.CreateCountryCommand(
                COUNTRY_CODE,
                "Ireland",
                new CountryLocalization.LocaleConfig("en-IE", "en", "IE"),
                new CountryLocalization.CurrencyConfig("EUR", "\u20ac", 2, "left"),
                new CountryLocalization.DateTimeConfig("dd/MM/yyyy", "HH:mm", "UTC+0", "GMT", "Europe/Dublin"),
                new CountryLocalization.AddressFormat("{street}, {city}", "{street}\n{city}", "postal", "IE"),
                new CountryLocalization.PhoneFormat("(XXX) XXX-XXXX", "+353", "8", "10", "^(\\+353)?[0-9]{10}$"),
                new CountryLocalization.EmergencyServices("112", "999", "112", "112"),
                new CountryLocalization.LegalRequirements("GDPR", "English", "21", true),
                CountryLocalization.MeasurementSystem.METRIC,
                USER
            );

            when(repository.findByCountryCode(COUNTRY_CODE))
                .thenReturn(CompletableFuture.completedFuture(Optional.empty()));
            when(repository.saveCountry(any(CountryLocalization.class)))
                .thenAnswer(invocation -> CompletableFuture.completedFuture(invocation.getArgument(0)));
            when(repository.cacheCountry(any(CountryLocalization.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

            // When
            CompletableFuture<CountryLocalization> result = service.createCountryLocalization(command);

            // Then
            assertNotNull(result);
            CountryLocalization saved = result.join();
            assertNotNull(saved);
            assertEquals(COUNTRY_CODE, saved.countryCode());
            assertEquals("Ireland", saved.countryName());

            verify(repository).saveCountry(any(CountryLocalization.class));
            verify(repository).cacheCountry(any(CountryLocalization.class));
        }

        @Test
        void createCountryLocalization_InvalidCountryCode_ThrowsException() {
            // Given
            LocalizationCommand.CreateCountryCommand command = new LocalizationCommand.CreateCountryCommand(
                "XX",
                "Invalid Country",
                new CountryLocalization.LocaleConfig("en-XX", "en", "XX"),
                new CountryLocalization.CurrencyConfig("USD", "$", 2, "left"),
                new CountryLocalization.DateTimeConfig("dd/MM/yyyy", "HH:mm", "UTC+0", "GMT", "UTC"),
                new CountryLocalization.AddressFormat("{street}, {city}", "{street}\n{city}", "postal", "XX"),
                new CountryLocalization.PhoneFormat("(XXX) XXX-XXXX", "+1", "1", "10", "^[0-9]{10}$"),
                new CountryLocalization.EmergencyServices("911", "911", "911", "911"),
                new CountryLocalization.LegalRequirements("None", "English", "18", false),
                CountryLocalization.MeasurementSystem.IMPERIAL,
                USER
            );

            // When & Then
            CompletableFuture<CountryLocalization> result = service.createCountryLocalization(command);
            Exception exception = assertThrows(java.util.concurrent.CompletionException.class, result::join);
            assertTrue(exception.getCause() instanceof IllegalArgumentException);
            assertTrue(exception.getCause().getMessage().contains("Invalid country code"));
        }

        @Test
        void createCountryLocalization_AlreadyExists_ThrowsException() {
            // Given
            LocalizationCommand.CreateCountryCommand command = new LocalizationCommand.CreateCountryCommand(
                COUNTRY_CODE,
                "Ireland",
                new CountryLocalization.LocaleConfig("en-IE", "en", "IE"),
                new CountryLocalization.CurrencyConfig("EUR", "\u20ac", 2, "left"),
                new CountryLocalization.DateTimeConfig("dd/MM/yyyy", "HH:mm", "UTC+0", "GMT", "Europe/Dublin"),
                new CountryLocalization.AddressFormat("{street}, {city}", "{street}\n{city}", "postal", "IE"),
                new CountryLocalization.PhoneFormat("(XXX) XXX-XXXX", "+353", "8", "10", "^(\\+353)?[0-9]{10}$"),
                new CountryLocalization.EmergencyServices("112", "999", "112", "112"),
                new CountryLocalization.LegalRequirements("GDPR", "English", "21", true),
                CountryLocalization.MeasurementSystem.METRIC,
                USER
            );

            when(repository.findByCountryCode(COUNTRY_CODE))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(createTestCountryLocalization())));

            // When & Then
            CompletableFuture<CountryLocalization> result = service.createCountryLocalization(command);
            Exception exception = assertThrows(java.util.concurrent.CompletionException.class, result::join);
            assertTrue(exception.getCause() instanceof IllegalStateException);
            assertTrue(exception.getCause().getMessage().contains("already exists"));
        }
    }

    @Nested
    class UpdateCountryLocalizationTests {

        @Test
        void updateCountryLocalization_Success() {
            // Given
            LocalizationCommand.UpdateCountryCommand command = new LocalizationCommand.UpdateCountryCommand(
                "Ireland Updated",
                new CountryLocalization.LocaleConfig("en-IE", "en", "IE"),
                new CountryLocalization.CurrencyConfig("EUR", "\u20ac", 2, "left"),
                new CountryLocalization.DateTimeConfig("dd/MM/yyyy", "HH:mm", "UTC+0", "GMT", "Europe/Dublin"),
                new CountryLocalization.AddressFormat("{street}, {city}", "{street}\n{city}", "postal", "IE"),
                new CountryLocalization.PhoneFormat("(XXX) XXX-XXXX", "+353", "8", "10", "^(\\+353)?[0-9]{10}$"),
                new CountryLocalization.EmergencyServices("112", "999", "112", "112"),
                new CountryLocalization.LegalRequirements("GDPR", "English", "21", true),
                CountryLocalization.MeasurementSystem.METRIC,
                USER
            );

            when(repository.findByCountryCode(COUNTRY_CODE))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(createTestCountryLocalization())));
            when(repository.saveCountry(any(CountryLocalization.class)))
                .thenAnswer(invocation -> CompletableFuture.completedFuture(invocation.getArgument(0)));
            when(repository.cacheCountry(any(CountryLocalization.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

            // When
            CompletableFuture<Optional<CountryLocalization>> result = service.updateCountryLocalization(COUNTRY_CODE, command);

            // Then
            assertNotNull(result);
            Optional<CountryLocalization> updated = result.join();
            assertTrue(updated.isPresent());
            assertEquals("Ireland Updated", updated.get().countryName());

            ArgumentCaptor<CountryLocalization> captor = ArgumentCaptor.forClass(CountryLocalization.class);
            verify(repository).saveCountry(captor.capture());
            assertEquals(2, captor.getValue().version());
        }

        @Test
        void updateCountryLocalization_NotFound_ReturnsEmpty() {
            // Given
            LocalizationCommand.UpdateCountryCommand command = new LocalizationCommand.UpdateCountryCommand(
                "Ireland",
                new CountryLocalization.LocaleConfig("en-IE", "en", "IE"),
                new CountryLocalization.CurrencyConfig("EUR", "\u20ac", 2, "left"),
                new CountryLocalization.DateTimeConfig("dd/MM/yyyy", "HH:mm", "UTC+0", "GMT", "Europe/Dublin"),
                new CountryLocalization.AddressFormat("{street}, {city}", "{street}\n{city}", "postal", "IE"),
                new CountryLocalization.PhoneFormat("(XXX) XXX-XXXX", "+353", "8", "10", "^(\\+353)?[0-9]{10}$"),
                new CountryLocalization.EmergencyServices("112", "999", "112", "112"),
                new CountryLocalization.LegalRequirements("GDPR", "English", "21", true),
                CountryLocalization.MeasurementSystem.METRIC,
                USER
            );

            when(repository.findByCountryCode(COUNTRY_CODE))
                .thenReturn(CompletableFuture.completedFuture(Optional.empty()));

            // When
            CompletableFuture<Optional<CountryLocalization>> result = service.updateCountryLocalization(COUNTRY_CODE, command);

            // Then
            assertNotNull(result);
            Optional<CountryLocalization> updated = result.join();
            assertFalse(updated.isPresent());

            verify(repository, never()).saveCountry(any());
        }
    }

    @Nested
    class ActivateCountryTests {

        @Test
        void activateCountry_Success() {
            // Given
            CountryLocalization inactiveCountry = CountryLocalization.builder()
                .countryCode(COUNTRY_CODE)
                .countryName("Ireland")
                .locale(new CountryLocalization.LocaleConfig("en-IE", "en", "IE"))
                .currency(new CountryLocalization.CurrencyConfig("EUR", "\u20ac", 2, "left"))
                .dateTime(new CountryLocalization.DateTimeConfig("dd/MM/yyyy", "HH:mm", "UTC+0", "GMT", "Europe/Dublin"))
                .addressFormat(new CountryLocalization.AddressFormat("{street}, {city}", "{street}\n{city}", "postal", "IE"))
                .phoneFormat(new CountryLocalization.PhoneFormat("(XXX) XXX-XXXX", "+353", "8", "10", "^(\\+353)?[0-9]{10}$"))
                .emergencyServices(new CountryLocalization.EmergencyServices("112", "999", "112", "112"))
                .legalRequirements(new CountryLocalization.LegalRequirements("GDPR", "English", "21", true))
                .measurementSystem(CountryLocalization.MeasurementSystem.METRIC)
                .active(false)
                .createdBy(USER)
                .version(1)
                .build();

            when(repository.findByCountryCode(COUNTRY_CODE))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(inactiveCountry)));
            when(repository.saveCountry(any(CountryLocalization.class)))
                .thenAnswer(invocation -> CompletableFuture.completedFuture(invocation.getArgument(0)));
            when(repository.cacheCountry(any(CountryLocalization.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

            // When
            CompletableFuture<Optional<CountryLocalization>> result = service.activateCountry(COUNTRY_CODE, USER);

            // Then
            assertNotNull(result);
            Optional<CountryLocalization> activated = result.join();
            assertTrue(activated.isPresent());
            assertTrue(activated.get().active());

            ArgumentCaptor<CountryLocalization> captor = ArgumentCaptor.forClass(CountryLocalization.class);
            verify(repository).saveCountry(captor.capture());
            assertTrue(captor.getValue().active());
        }

        @Test
        void activateCountry_NotFound_ReturnsEmpty() {
            // Given
            when(repository.findByCountryCode(COUNTRY_CODE))
                .thenReturn(CompletableFuture.completedFuture(Optional.empty()));

            // When
            CompletableFuture<Optional<CountryLocalization>> result = service.activateCountry(COUNTRY_CODE, USER);

            // Then
            assertNotNull(result);
            Optional<CountryLocalization> activated = result.join();
            assertFalse(activated.isPresent());
        }

        @Test
        void activateCountry_AlreadyActive_Noop() {
            // Given
            when(repository.findByCountryCode(COUNTRY_CODE))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(createTestCountryLocalization())));

            // When
            CompletableFuture<Optional<CountryLocalization>> result = service.activateCountry(COUNTRY_CODE, USER);

            // Then
            assertNotNull(result);
            Optional<CountryLocalization> activated = result.join();
            assertTrue(activated.isPresent());
            assertTrue(activated.get().active());

            verify(repository, never()).saveCountry(any());
        }
    }

    @Nested
    class DeactivateCountryTests {

        @Test
        void deactivateCountry_Success() {
            // Given
            when(repository.findByCountryCode(COUNTRY_CODE))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(createTestCountryLocalization())));
            when(repository.saveCountry(any(CountryLocalization.class)))
                .thenAnswer(invocation -> CompletableFuture.completedFuture(invocation.getArgument(0)));
            when(repository.evictCountry(COUNTRY_CODE))
                .thenReturn(CompletableFuture.completedFuture(null));

            // When
            CompletableFuture<Optional<CountryLocalization>> result = service.deactivateCountry(COUNTRY_CODE, USER);

            // Then
            assertNotNull(result);
            Optional<CountryLocalization> deactivated = result.join();
            assertTrue(deactivated.isPresent());
            assertFalse(deactivated.get().active());

            ArgumentCaptor<CountryLocalization> captor = ArgumentCaptor.forClass(CountryLocalization.class);
            verify(repository).saveCountry(captor.capture());
            assertFalse(captor.getValue().active());
            verify(repository).evictCountry(COUNTRY_CODE);
        }

        @Test
        void deactivateCountry_NotFound_ReturnsEmpty() {
            // Given
            when(repository.findByCountryCode(COUNTRY_CODE))
                .thenReturn(CompletableFuture.completedFuture(Optional.empty()));

            // When
            CompletableFuture<Optional<CountryLocalization>> result = service.deactivateCountry(COUNTRY_CODE, USER);

            // Then
            assertNotNull(result);
            Optional<CountryLocalization> deactivated = result.join();
            assertFalse(deactivated.isPresent());
        }
    }

    @Nested
    class DeleteCountryLocalizationTests {

        @Test
        void deleteCountryLocalization_Success() {
            // Given
            when(repository.deleteCountry(COUNTRY_CODE))
                .thenReturn(CompletableFuture.completedFuture(true));
            when(repository.evictCountry(COUNTRY_CODE))
                .thenReturn(CompletableFuture.completedFuture(null));
            when(repository.evictAllCountryResources(COUNTRY_CODE))
                .thenReturn(CompletableFuture.completedFuture(null));

            // When
            CompletableFuture<Boolean> result = service.deleteCountryLocalization(COUNTRY_CODE);

            // Then
            assertNotNull(result);
            Boolean deleted = result.join();
            assertTrue(deleted);

            verify(repository).deleteCountry(COUNTRY_CODE);
            verify(repository).evictCountry(COUNTRY_CODE);
            verify(repository).evictAllCountryResources(COUNTRY_CODE);
        }

        @Test
        void deleteCountryLocalization_NotFound_ReturnsFalse() {
            // Given
            when(repository.deleteCountry(COUNTRY_CODE))
                .thenReturn(CompletableFuture.completedFuture(false));
            when(repository.evictCountry(COUNTRY_CODE))
                .thenReturn(CompletableFuture.completedFuture(null));
            when(repository.evictAllCountryResources(COUNTRY_CODE))
                .thenReturn(CompletableFuture.completedFuture(null));

            // When
            CompletableFuture<Boolean> result = service.deleteCountryLocalization(COUNTRY_CODE);

            // Then
            assertNotNull(result);
            Boolean deleted = result.join();
            assertFalse(deleted);
        }
    }

    @Nested
    class QueryTests {

        @Test
        void getByCountryCode_ReturnsCachedValue() {
            // Given
            CountryLocalization country = createTestCountryLocalization();
            when(repository.getCachedCountry(COUNTRY_CODE))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(country)));

            // When
            CompletableFuture<Optional<CountryLocalization>> result = service.getByCountryCode(COUNTRY_CODE);

            // Then
            assertNotNull(result);
            Optional<CountryLocalization> found = result.join();
            assertTrue(found.isPresent());
            assertEquals(COUNTRY_CODE, found.get().countryCode());

            verify(repository, never()).findByCountryCode(COUNTRY_CODE);
        }

        @Test
        void getByCountryCode_CacheMiss_ReturnsFromStore() {
            // Given
            CountryLocalization country = createTestCountryLocalization();
            when(repository.getCachedCountry(COUNTRY_CODE))
                .thenReturn(CompletableFuture.completedFuture(Optional.empty()));
            when(repository.findByCountryCode(COUNTRY_CODE))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(country)));
            when(repository.cacheCountry(country))
                .thenReturn(CompletableFuture.completedFuture(null));

            // When
            CompletableFuture<Optional<CountryLocalization>> result = service.getByCountryCode(COUNTRY_CODE);

            // Then
            assertNotNull(result);
            Optional<CountryLocalization> found = result.join();
            assertTrue(found.isPresent());
            assertEquals(COUNTRY_CODE, found.get().countryCode());

            verify(repository).findByCountryCode(COUNTRY_CODE);
            verify(repository).cacheCountry(country);
        }

        @Test
        void getAllActiveCountries_Success() {
            // Given
            List<CountryLocalization> countries = List.of(
                createTestCountryLocalization(),
                CountryLocalization.builder()
                    .countryCode("GB")
                    .countryName("United Kingdom")
                    .locale(new CountryLocalization.LocaleConfig("en-GB", "en", "GB"))
                    .currency(new CountryLocalization.CurrencyConfig("GBP", "\u00a3", 2, "left"))
                    .dateTime(new CountryLocalization.DateTimeConfig("dd/MM/yyyy", "HH:mm", "UTC+0", "GMT", "Europe/London"))
                    .addressFormat(new CountryLocalization.AddressFormat("{street}, {city}", "{street}\n{city}", "postal", "GB"))
                    .phoneFormat(new CountryLocalization.PhoneFormat("(XXX) XXX-XXXX", "+44", "7", "10", "^[0-9]{10}$"))
                    .emergencyServices(new CountryLocalization.EmergencyServices("999", "999", "999", "999"))
                    .legalRequirements(new CountryLocalization.LegalRequirements("GDPR", "English", "18", true))
                    .measurementSystem(CountryLocalization.MeasurementSystem.IMPERIAL)
                    .active(true)
                    .createdBy(USER)
                    .build()
            );

            when(repository.findActiveCountries())
                .thenReturn(CompletableFuture.completedFuture(countries));

            // When
            CompletableFuture<List<CountryLocalization>> result = service.getAllActiveCountries();

            // Then
            assertNotNull(result);
            List<CountryLocalization> found = result.join();
            assertEquals(2, found.size());
        }

        @Test
        void getAllCountries_Success() {
            // Given
            List<CountryLocalization> countries = List.of(createTestCountryLocalization());
            when(repository.findAllCountries())
                .thenReturn(CompletableFuture.completedFuture(countries));

            // When
            CompletableFuture<List<CountryLocalization>> result = service.getAllCountries();

            // Then
            assertNotNull(result);
            List<CountryLocalization> found = result.join();
            assertEquals(1, found.size());
        }

        @Test
        void getCountriesByMeasurementSystem_Success() {
            // Given
            List<CountryLocalization> countries = List.of(createTestCountryLocalization());
            when(repository.findByMeasurementSystem(CountryLocalization.MeasurementSystem.METRIC))
                .thenReturn(CompletableFuture.completedFuture(countries));

            // When
            CompletableFuture<List<CountryLocalization>> result = service.getCountriesByMeasurementSystem(CountryLocalization.MeasurementSystem.METRIC);

            // Then
            assertNotNull(result);
            List<CountryLocalization> found = result.join();
            assertEquals(1, found.size());
            assertEquals(CountryLocalization.MeasurementSystem.METRIC, found.get(0).measurementSystem());
        }

        @Test
        void getCurrencySymbol_Success() {
            // Given
            CountryLocalization country = createTestCountryLocalization();
            when(repository.getCachedCountry(COUNTRY_CODE))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(country)));

            // When
            CompletableFuture<String> result = service.getCurrencySymbol(COUNTRY_CODE);

            // Then
            assertNotNull(result);
            String symbol = result.join();
            assertEquals("\u20ac", symbol);
        }

        @Test
        void getCurrencySymbol_NotFound_ReturnsDefault() {
            // Given
            when(repository.getCachedCountry(COUNTRY_CODE))
                .thenReturn(CompletableFuture.completedFuture(Optional.empty()));
            when(repository.findByCountryCode(COUNTRY_CODE))
                .thenReturn(CompletableFuture.completedFuture(Optional.empty()));

            // When
            CompletableFuture<String> result = service.getCurrencySymbol(COUNTRY_CODE);

            // Then
            assertNotNull(result);
            String symbol = result.join();
            assertEquals("\u20ac", symbol); // Default EUR symbol
        }

        @Test
        void getDateFormat_Success() {
            // Given
            CountryLocalization country = createTestCountryLocalization();
            when(repository.getCachedCountry(COUNTRY_CODE))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(country)));

            // When
            CompletableFuture<String> result = service.getDateFormat(COUNTRY_CODE);

            // Then
            assertNotNull(result);
            String format = result.join();
            assertEquals("dd/MM/yyyy", format);
        }

        @Test
        void getDateFormat_NotFound_ReturnsDefault() {
            // Given
            when(repository.getCachedCountry(COUNTRY_CODE))
                .thenReturn(CompletableFuture.completedFuture(Optional.empty()));
            when(repository.findByCountryCode(COUNTRY_CODE))
                .thenReturn(CompletableFuture.completedFuture(Optional.empty()));

            // When
            CompletableFuture<String> result = service.getDateFormat(COUNTRY_CODE);

            // Then
            assertNotNull(result);
            String format = result.join();
            assertEquals("dd/MM/yyyy", format); // Default format
        }

        @Test
        void getEmergencyNumber_Success() {
            // Given
            CountryLocalization country = createTestCountryLocalization();
            when(repository.getCachedCountry(COUNTRY_CODE))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(country)));

            // When
            CompletableFuture<String> result = service.getEmergencyNumber(COUNTRY_CODE);

            // Then
            assertNotNull(result);
            String number = result.join();
            assertEquals("112", number);
        }

        @Test
        void getEmergencyNumber_NotFound_ReturnsDefault() {
            // Given
            when(repository.getCachedCountry(COUNTRY_CODE))
                .thenReturn(CompletableFuture.completedFuture(Optional.empty()));
            when(repository.findByCountryCode(COUNTRY_CODE))
                .thenReturn(CompletableFuture.completedFuture(Optional.empty()));

            // When
            CompletableFuture<String> result = service.getEmergencyNumber(COUNTRY_CODE);

            // Then
            assertNotNull(result);
            String number = result.join();
            assertEquals("112", number); // Default emergency number
        }

        @Test
        void isValidCountryCode_Valid_ReturnsTrue() {
            // When
            CompletableFuture<Boolean> result = service.isValidCountryCode(COUNTRY_CODE);

            // Then
            assertNotNull(result);
            assertTrue(result.join());
        }

        @Test
        void isValidCountryCode_Invalid_ReturnsFalse() {
            // When
            CompletableFuture<Boolean> result = service.isValidCountryCode("XX");

            // Then
            assertNotNull(result);
            assertFalse(result.join());
        }

        @Test
        void isValidCountryCode_Lowercase_ReturnsTrue() {
            // When
            CompletableFuture<Boolean> result = service.isValidCountryCode("ie");

            // Then
            assertNotNull(result);
            assertTrue(result.join());
        }

        @Test
        void isValidLocale_Valid_ReturnsTrue() {
            // When
            CompletableFuture<Boolean> result = service.isValidLocale("en-IE");

            // Then
            assertNotNull(result);
            assertTrue(result.join());
        }

        @Test
        void isValidLocale_WithUnderscore_ReturnsTrue() {
            // When
            CompletableFuture<Boolean> result = service.isValidLocale("en_IE");

            // Then
            assertNotNull(result);
            assertTrue(result.join());
        }

        @Test
        void isValidLocale_Invalid_ReturnsFalse() {
            // When
            CompletableFuture<Boolean> result = service.isValidLocale("invalid");

            // Then
            assertNotNull(result);
            assertFalse(result.join());
        }

        @Test
        void isValidLocale_Null_ReturnsFalse() {
            // When
            CompletableFuture<Boolean> result = service.isValidLocale(null);

            // Then
            assertNotNull(result);
            assertFalse(result.join());
        }
    }

    @Nested
    class ResourceTests {

        @Test
        void createResource_Success() {
            // Given
            LocalizationCommand.CreateResourceCommand command = new LocalizationCommand.CreateResourceCommand(
                "button.submit",
                COUNTRY_CODE,
                "ui",
                Map.of("en", "Submit", "ga", "Cuir isteach"),
                "Submit",
                "Form submission button",
                USER
            );

            when(repository.saveResource(any(LocalizedResource.class)))
                .thenAnswer(invocation -> CompletableFuture.completedFuture(invocation.getArgument(0)));
            when(repository.cacheResource(any(LocalizedResource.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

            // When
            CompletableFuture<LocalizedResource> result = service.createResource(command);

            // Then
            assertNotNull(result);
            LocalizedResource saved = result.join();
            assertNotNull(saved);
            assertEquals("button.submit", saved.resourceKey());
            assertEquals(COUNTRY_CODE, saved.countryCode());

            verify(repository).saveResource(any(LocalizedResource.class));
            verify(repository).cacheResource(any(LocalizedResource.class));
        }

        @Test
        void bulkImportTranslations_Success() {
            // Given
            Map<String, Map<String, String>> translations = Map.of(
                "button.submit", Map.of("en", "Submit", "ga", "Cuir isteach"),
                "button.cancel", Map.of("en", "Cancel", "ga", "Cealaigh")
            );

            when(repository.saveResource(any(LocalizedResource.class)))
                .thenAnswer(invocation -> CompletableFuture.completedFuture(invocation.getArgument(0)));

            // When
            CompletableFuture<List<LocalizedResource>> result = service.bulkImportTranslations(COUNTRY_CODE, translations);

            // Then
            assertNotNull(result);
            List<LocalizedResource> imported = result.join();
            assertEquals(2, imported.size());

            verify(repository, times(2)).saveResource(any(LocalizedResource.class));
        }

        @Test
        void deleteResource_Success() {
            // Given
            String resourceId = "resource-123";
            when(repository.deleteResource(resourceId))
                .thenReturn(CompletableFuture.completedFuture(true));

            // When
            CompletableFuture<Boolean> result = service.deleteResource(resourceId);

            // Then
            assertNotNull(result);
            Boolean deleted = result.join();
            assertTrue(deleted);

            verify(repository).deleteResource(resourceId);
        }

        @Test
        void deleteResource_NotFound_ReturnsFalse() {
            // Given
            String resourceId = "resource-999";
            when(repository.deleteResource(resourceId))
                .thenReturn(CompletableFuture.completedFuture(false));

            // When
            CompletableFuture<Boolean> result = service.deleteResource(resourceId);

            // Then
            assertNotNull(result);
            Boolean deleted = result.join();
            assertFalse(deleted);
        }

        @Test
        void getTranslation_Success() {
            // Given
            LocalizedResource resource = LocalizedResource.builder()
                .resourceKey("button.submit")
                .countryCode(COUNTRY_CODE)
                .resourceType("ui")
                .translations(Map.of("en", "Submit", "ga", "Cuir isteach"))
                .defaultValue("Submit")
                .build();

            when(repository.getCachedResource("button.submit", COUNTRY_CODE))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(resource)));

            // When
            CompletableFuture<String> result = service.getTranslation("button.submit", "ga", COUNTRY_CODE);

            // Then
            assertNotNull(result);
            String translation = result.join();
            assertEquals("Cuir isteach", translation);
        }

        @Test
        void getTranslation_NotFound_ReturnsEmpty() {
            // Given
            when(repository.getCachedResource("button.submit", COUNTRY_CODE))
                .thenReturn(CompletableFuture.completedFuture(Optional.empty()));
            when(repository.findResourceByKey("button.submit", COUNTRY_CODE))
                .thenReturn(CompletableFuture.completedFuture(Optional.empty()));

            // When
            CompletableFuture<String> result = service.getTranslation("button.submit", "en", COUNTRY_CODE);

            // Then
            assertNotNull(result);
            String translation = result.join();
            assertEquals("", translation);
        }
    }
}
