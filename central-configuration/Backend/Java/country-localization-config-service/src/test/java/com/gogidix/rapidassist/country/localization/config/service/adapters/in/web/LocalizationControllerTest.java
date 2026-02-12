package com.gogidix.rapidassist.country.localization.config.service.adapters.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.country.localization.config.service.application.LocalizationService;
import com.gogidix.rapidassist.country.localization.config.service.domain.model.CountryLocalization;
import com.gogidix.rapidassist.country.localization.config.service.domain.model.LocalizedResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Comprehensive tests for LocalizationController using standalone MockMvc setup.
 */
@ExtendWith(MockitoExtension.class)
class LocalizationControllerTest {

    @Mock
    private LocalizationService localizationService;

    @InjectMocks
    private LocalizationController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    private CountryLocalization createTestCountryLocalization() {
        return CountryLocalization.builder()
            .countryCode("IE")
            .countryName("Ireland")
            .locale(new CountryLocalization.LocaleConfig("en-IE", "en", "IE"))
            .currency(new CountryLocalization.CurrencyConfig("EUR", "\u20ac", 2, "left"))
            .dateTime(new CountryLocalization.DateTimeConfig("dd/MM/yyyy", "HH:mm", "UTC+0", "GMT", "Europe/Dublin"))
            .addressFormat(new CountryLocalization.AddressFormat("{street}, {city}", "{street}\n{city}", "postal", "IE"))
            .phoneFormat(new CountryLocalization.PhoneFormat("(XXX) XXX-XXXX", "+353", "8", "10", "^(\\+353)?[0-9]{10}$"))
            .emergencyServices(new CountryLocalization.EmergencyServices("112", "999", "112", "112"))
            .legalRequirements(new CountryLocalization.LegalRequirements("GDPR", "English", "21", true))
            .measurementSystem(CountryLocalization.MeasurementSystem.METRIC)
            .active(true)
            .createdBy("test-user")
            .build();
    }

    @Nested
    class HealthTests {

        @Test
        void health_ReturnsStatus() throws Exception {
            mockMvc.perform(get("/api/localization/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("country-localization-config-service"));
        }
    }

    @Nested
    class GetCountriesTests {

        @Test
        void getAllCountries_Success() throws Exception {
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
                    .createdBy("test-user")
                    .build()
            );

            when(localizationService.getAllCountries())
                .thenReturn(CompletableFuture.completedFuture(countries));

            mockMvc.perform(get("/api/localization/countries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].countryCode").value("IE"))
                .andExpect(jsonPath("$[1].countryCode").value("GB"));
        }

        @Test
        void getAllCountries_WithActiveOnly_ReturnsActive() throws Exception {
            List<CountryLocalization> countries = List.of(createTestCountryLocalization());

            when(localizationService.getAllActiveCountries())
                .thenReturn(CompletableFuture.completedFuture(countries));

            mockMvc.perform(get("/api/localization/countries")
                    .param("activeOnly", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].countryCode").value("IE"))
                .andExpect(jsonPath("$[0].active").value(true));
        }

        @Test
        void getAllCountries_Empty_ReturnsEmptyList() throws Exception {
            when(localizationService.getAllCountries())
                .thenReturn(CompletableFuture.completedFuture(List.of()));

            mockMvc.perform(get("/api/localization/countries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
        }
    }

    @Nested
    class GetCountryTests {

        @Test
        void getCountry_Found_ReturnsCountry() throws Exception {
            when(localizationService.getByCountryCode("IE"))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(createTestCountryLocalization())));

            mockMvc.perform(get("/api/localization/countries/IE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.countryCode").value("IE"))
                .andExpect(jsonPath("$.countryName").value("Ireland"));
        }

        @Test
        void getCountry_NotFound_ReturnsNotFound() throws Exception {
            when(localizationService.getByCountryCode("XX"))
                .thenReturn(CompletableFuture.completedFuture(Optional.empty()));

            mockMvc.perform(get("/api/localization/countries/XX"))
                .andExpect(status().isNotFound());
        }
    }

    @Nested
    class GetCountriesByMeasurementSystemTests {

        @Test
        void getCountriesByMeasurementSystem_Success() throws Exception {
            List<CountryLocalization> countries = List.of(createTestCountryLocalization());

            when(localizationService.getCountriesByMeasurementSystem(CountryLocalization.MeasurementSystem.METRIC))
                .thenReturn(CompletableFuture.completedFuture(countries));

            mockMvc.perform(get("/api/localization/countries/measurement/METRIC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].measurementSystem").value("METRIC"));
        }

        @Test
        void getCountriesByMeasurementSystem_Empty_ReturnsEmptyList() throws Exception {
            when(localizationService.getCountriesByMeasurementSystem(CountryLocalization.MeasurementSystem.IMPERIAL))
                .thenReturn(CompletableFuture.completedFuture(List.of()));

            mockMvc.perform(get("/api/localization/countries/measurement/IMPERIAL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
        }
    }

    @Nested
    class CreateCountryTests {

        @Test
        void createCountry_Success_ReturnsCreated() throws Exception {
            CountryLocalization created = createTestCountryLocalization();

            when(localizationService.createCountryLocalization(any()))
                .thenReturn(CompletableFuture.completedFuture(created));

            String requestJson = """
                {
                    "countryCode": "IE",
                    "countryName": "Ireland",
                    "locale": {
                        "locale": "en-IE",
                        "language": "en",
                        "region": "IE"
                    },
                    "currency": {
                        "code": "EUR",
                        "symbol": "\u20ac",
                        "decimalPlaces": 2,
                        "symbolPosition": "left"
                    },
                    "dateTime": {
                        "dateFormat": "dd/MM/yyyy",
                        "timeFormat": "HH:mm",
                        "timezone": "UTC+0",
                        "timeZoneId": "GMT",
                        "timeZoneName": "Europe/Dublin"
                    },
                    "addressFormat": {
                        "singleLineFormat": "{street}, {city}",
                        "multilineFormat": "{street}\\n{city}",
                        "postalCodeFormat": "postal",
                        "countryCode": "IE"
                    },
                    "phoneFormat": {
                        "formatTemplate": "(XXX) XXX-XXXX",
                        "internationalPrefix": "+353",
                        "trunkPrefix": "8",
                        "maxLength": "10",
                        "validationPattern": "^(\\\\+353)?[0-9]{10}$"
                    },
                    "emergencyServices": {
                        "police": "112",
                        "ambulance": "999",
                        "fire": "112",
                        "emergency": "112"
                    },
                    "legalRequirements": {
                        "dataProtectionLaw": "GDPR",
                        "officialLanguage": "English",
                        "minimumAge": "21",
                        "requiresTermsAcceptance": true
                    },
                    "measurementSystem": "METRIC",
                    "createdBy": "test-user"
                }
                """;

            mockMvc.perform(post("/api/localization/countries")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.countryCode").value("IE"))
                .andExpect(jsonPath("$.countryName").value("Ireland"));
        }

        @Test
        void createCountry_MissingRequiredField_ReturnsBadRequest() throws Exception {
            String invalidRequest = """
                {
                    "countryCode": "IE",
                    "countryName": "Ireland"
                }
                """;

            mockMvc.perform(post("/api/localization/countries")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(invalidRequest))
                .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class UpdateCountryTests {

        @Test
        void updateCountry_Success_ReturnsUpdated() throws Exception {
            CountryLocalization updated = CountryLocalization.builder()
                .countryCode("IE")
                .countryName("Ireland Updated")
                .locale(new CountryLocalization.LocaleConfig("en-IE", "en", "IE"))
                .currency(new CountryLocalization.CurrencyConfig("EUR", "\u20ac", 2, "left"))
                .dateTime(new CountryLocalization.DateTimeConfig("dd/MM/yyyy", "HH:mm", "UTC+0", "GMT", "Europe/Dublin"))
                .addressFormat(new CountryLocalization.AddressFormat("{street}, {city}", "{street}\n{city}", "postal", "IE"))
                .phoneFormat(new CountryLocalization.PhoneFormat("(XXX) XXX-XXXX", "+353", "8", "10", "^(\\+353)?[0-9]{10}$"))
                .emergencyServices(new CountryLocalization.EmergencyServices("112", "999", "112", "112"))
                .legalRequirements(new CountryLocalization.LegalRequirements("GDPR", "English", "21", true))
                .measurementSystem(CountryLocalization.MeasurementSystem.METRIC)
                .active(true)
                .createdBy("test-user")
                .version(2)
                .build();

            when(localizationService.updateCountryLocalization(eq("IE"), any()))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(updated)));

            String requestJson = """
                {
                    "countryName": "Ireland Updated",
                    "locale": {
                        "locale": "en-IE",
                        "language": "en",
                        "region": "IE"
                    },
                    "currency": {
                        "code": "EUR",
                        "symbol": "\u20ac",
                        "decimalPlaces": 2,
                        "symbolPosition": "left"
                    },
                    "dateTime": {
                        "dateFormat": "dd/MM/yyyy",
                        "timeFormat": "HH:mm",
                        "timezone": "UTC+0",
                        "timeZoneId": "GMT",
                        "timeZoneName": "Europe/Dublin"
                    },
                    "addressFormat": {
                        "singleLineFormat": "{street}, {city}",
                        "multilineFormat": "{street}\\n{city}",
                        "postalCodeFormat": "postal",
                        "countryCode": "IE"
                    },
                    "phoneFormat": {
                        "formatTemplate": "(XXX) XXX-XXXX",
                        "internationalPrefix": "+353",
                        "trunkPrefix": "8",
                        "maxLength": "10",
                        "validationPattern": "^(\\\\+353)?[0-9]{10}$"
                    },
                    "emergencyServices": {
                        "police": "112",
                        "ambulance": "999",
                        "fire": "112",
                        "emergency": "112"
                    },
                    "legalRequirements": {
                        "dataProtectionLaw": "GDPR",
                        "officialLanguage": "English",
                        "minimumAge": "21",
                        "requiresTermsAcceptance": true
                    },
                    "measurementSystem": "METRIC",
                    "updatedBy": "test-user"
                }
                """;

            mockMvc.perform(put("/api/localization/countries/IE")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.countryName").value("Ireland Updated"));
        }

        @Test
        void updateCountry_NotFound_ReturnsNotFound() throws Exception {
            when(localizationService.updateCountryLocalization(eq("XX"), any()))
                .thenReturn(CompletableFuture.completedFuture(Optional.empty()));

            String requestJson = """
                {
                    "countryName": "Invalid",
                    "locale": {
                        "locale": "en-XX",
                        "language": "en",
                        "region": "XX"
                    },
                    "currency": {
                        "code": "USD",
                        "symbol": "$",
                        "decimalPlaces": 2,
                        "symbolPosition": "left"
                    },
                    "dateTime": {
                        "dateFormat": "dd/MM/yyyy",
                        "timeFormat": "HH:mm",
                        "timezone": "UTC+0",
                        "timeZoneId": "GMT",
                        "timeZoneName": "UTC"
                    },
                    "addressFormat": {
                        "singleLineFormat": "{street}, {city}",
                        "multilineFormat": "{street}\\n{city}",
                        "postalCodeFormat": "postal",
                        "countryCode": "XX"
                    },
                    "phoneFormat": {
                        "formatTemplate": "(XXX) XXX-XXXX",
                        "internationalPrefix": "+1",
                        "trunkPrefix": "1",
                        "maxLength": "10",
                        "validationPattern": "^[0-9]{10}$"
                    },
                    "emergencyServices": {
                        "police": "911",
                        "ambulance": "911",
                        "fire": "911",
                        "emergency": "911"
                    },
                    "legalRequirements": {
                        "dataProtectionLaw": "None",
                        "officialLanguage": "English",
                        "minimumAge": "18",
                        "requiresTermsAcceptance": false
                    },
                    "measurementSystem": "IMPERIAL",
                    "updatedBy": "test-user"
                }
                """;

            mockMvc.perform(put("/api/localization/countries/XX")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson))
                .andExpect(status().isNotFound());
        }
    }

    @Nested
    class ActivateCountryTests {

        @Test
        void activateCountry_Success_ReturnsActivated() throws Exception {
            CountryLocalization activated = CountryLocalization.builder()
                .countryCode("IE")
                .countryName("Ireland")
                .locale(new CountryLocalization.LocaleConfig("en-IE", "en", "IE"))
                .currency(new CountryLocalization.CurrencyConfig("EUR", "\u20ac", 2, "left"))
                .dateTime(new CountryLocalization.DateTimeConfig("dd/MM/yyyy", "HH:mm", "UTC+0", "GMT", "Europe/Dublin"))
                .addressFormat(new CountryLocalization.AddressFormat("{street}, {city}", "{street}\n{city}", "postal", "IE"))
                .phoneFormat(new CountryLocalization.PhoneFormat("(XXX) XXX-XXXX", "+353", "8", "10", "^(\\+353)?[0-9]{10}$"))
                .emergencyServices(new CountryLocalization.EmergencyServices("112", "999", "112", "112"))
                .legalRequirements(new CountryLocalization.LegalRequirements("GDPR", "English", "21", true))
                .measurementSystem(CountryLocalization.MeasurementSystem.METRIC)
                .active(true)
                .createdBy("test-user")
                .version(2)
                .build();

            when(localizationService.activateCountry("IE", "test-user"))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(activated)));

            mockMvc.perform(post("/api/localization/countries/IE/activate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(Map.of("updatedBy", "test-user"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));
        }

        @Test
        void activateCountry_NotFound_ReturnsNotFound() throws Exception {
            when(localizationService.activateCountry("XX", "test-user"))
                .thenReturn(CompletableFuture.completedFuture(Optional.empty()));

            mockMvc.perform(post("/api/localization/countries/XX/activate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(Map.of("updatedBy", "test-user"))))
                .andExpect(status().isNotFound());
        }
    }

    @Nested
    class DeactivateCountryTests {

        @Test
        void deactivateCountry_Success_ReturnsDeactivated() throws Exception {
            CountryLocalization deactivated = CountryLocalization.builder()
                .countryCode("IE")
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
                .createdBy("test-user")
                .version(2)
                .build();

            when(localizationService.deactivateCountry("IE", "test-user"))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(deactivated)));

            mockMvc.perform(post("/api/localization/countries/IE/deactivate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(Map.of("updatedBy", "test-user"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
        }

        @Test
        void deactivateCountry_NotFound_ReturnsNotFound() throws Exception {
            when(localizationService.deactivateCountry("XX", "test-user"))
                .thenReturn(CompletableFuture.completedFuture(Optional.empty()));

            mockMvc.perform(post("/api/localization/countries/XX/deactivate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(Map.of("updatedBy", "test-user"))))
                .andExpect(status().isNotFound());
        }
    }

    @Nested
    class DeleteCountryTests {

        @Test
        void deleteCountry_Success_ReturnsNoContent() throws Exception {
            when(localizationService.deleteCountryLocalization("IE"))
                .thenReturn(CompletableFuture.completedFuture(true));

            mockMvc.perform(delete("/api/localization/countries/IE"))
                .andExpect(status().isNoContent());
        }

        @Test
        void deleteCountry_NotFound_ReturnsNotFound() throws Exception {
            when(localizationService.deleteCountryLocalization("XX"))
                .thenReturn(CompletableFuture.completedFuture(false));

            mockMvc.perform(delete("/api/localization/countries/XX"))
                .andExpect(status().isNotFound());
        }
    }

    @Nested
    class LocalizationHelperTests {

        @Test
        void getCurrencySymbol_Success() throws Exception {
            when(localizationService.getCurrencySymbol("IE"))
                .thenReturn(CompletableFuture.completedFuture("\u20ac"));

            mockMvc.perform(get("/api/localization/countries/IE/currency-symbol"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.symbol").value("\u20ac"));
        }

        @Test
        void getDateFormat_Success() throws Exception {
            when(localizationService.getDateFormat("IE"))
                .thenReturn(CompletableFuture.completedFuture("dd/MM/yyyy"));

            mockMvc.perform(get("/api/localization/countries/IE/date-format"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dateFormat").value("dd/MM/yyyy"));
        }

        @Test
        void getTimeFormat_Success() throws Exception {
            when(localizationService.getTimeFormat("IE"))
                .thenReturn(CompletableFuture.completedFuture("HH:mm"));

            mockMvc.perform(get("/api/localization/countries/IE/time-format"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.timeFormat").value("HH:mm"));
        }

        @Test
        void getPhoneFormat_Success() throws Exception {
            when(localizationService.getPhoneFormat("IE"))
                .thenReturn(CompletableFuture.completedFuture("(XXX) XXX-XXXX"));

            mockMvc.perform(get("/api/localization/countries/IE/phone-format"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phoneFormat").value("(XXX) XXX-XXXX"));
        }

        @Test
        void getEmergencyNumber_Success() throws Exception {
            when(localizationService.getEmergencyNumber("IE"))
                .thenReturn(CompletableFuture.completedFuture("112"));

            mockMvc.perform(get("/api/localization/countries/IE/emergency-number"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emergencyNumber").value("112"));
        }
    }

    @Nested
    class TranslationTests {

        @Test
        void getTranslation_Success() throws Exception {
            when(localizationService.getTranslation("button.submit", "ga", "IE"))
                .thenReturn(CompletableFuture.completedFuture("Cuir isteach"));

            mockMvc.perform(get("/api/localization/translations")
                    .param("resourceKey", "button.submit")
                    .param("locale", "ga")
                    .param("countryCode", "IE"))
                .andExpect(status().isOk())
                .andExpect(content().string("Cuir isteach"));
        }

        @Test
        void getTranslations_Success() throws Exception {
            Map<String, String> translations = Map.of(
                "en", "Submit",
                "ga", "Cuir isteach"
            );

            when(localizationService.getTranslations("button.submit", "IE"))
                .thenReturn(CompletableFuture.completedFuture(translations));

            mockMvc.perform(get("/api/localization/translations/button.submit")
                    .param("countryCode", "IE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.en").value("Submit"))
                .andExpect(jsonPath("$.ga").value("Cuir isteach"));
        }

        @Test
        void getResources_ByCountry_Success() throws Exception {
            List<LocalizedResource> resources = List.of(
                LocalizedResource.builder()
                    .resourceKey("button.submit")
                    .countryCode("IE")
                    .resourceType("ui")
                    .translations(Map.of("en", "Submit", "ga", "Cuir isteach"))
                    .defaultValue("Submit")
                    .build()
            );

            when(localizationService.getResourcesByCountry("IE"))
                .thenReturn(CompletableFuture.completedFuture(resources));

            mockMvc.perform(get("/api/localization/resources")
                    .param("countryCode", "IE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].resourceKey").value("button.submit"));
        }

        @Test
        void resources_EmptyParams_ReturnsEmpty() throws Exception {
            mockMvc.perform(get("/api/localization/resources"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
        }
    }

    @Nested
    class CreateResourceTests {

        @Test
        void createResource_Success_ReturnsCreated() throws Exception {
            LocalizedResource resource = LocalizedResource.builder()
                .id("resource-123")
                .resourceKey("button.submit")
                .countryCode("IE")
                .resourceType("ui")
                .translations(Map.of("en", "Submit", "ga", "Cuir isteach"))
                .defaultValue("Submit")
                .context("Form button")
                .active(true)
                .createdBy("test-user")
                .build();

            when(localizationService.createResource(any()))
                .thenReturn(CompletableFuture.completedFuture(resource));

            String requestJson = """
                {
                    "resourceKey": "button.submit",
                    "countryCode": "IE",
                    "resourceType": "ui",
                    "translations": {
                        "en": "Submit",
                        "ga": "Cuir isteach"
                    },
                    "defaultValue": "Submit",
                    "context": "Form button",
                    "createdBy": "test-user"
                }
                """;

            mockMvc.perform(post("/api/localization/resources")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.resourceKey").value("button.submit"));
        }
    }

    @Nested
    class BulkImportTests {

        @Test
        void bulkImport_Success_ReturnsImported() throws Exception {
            List<LocalizedResource> resources = List.of(
                LocalizedResource.builder()
                    .resourceKey("button.submit")
                    .countryCode("IE")
                    .translations(Map.of("en", "Submit"))
                    .defaultValue("Submit")
                    .build(),
                LocalizedResource.builder()
                    .resourceKey("button.cancel")
                    .countryCode("IE")
                    .translations(Map.of("en", "Cancel"))
                    .defaultValue("Cancel")
                    .build()
            );

            when(localizationService.bulkImportTranslations(eq("IE"), any()))
                .thenReturn(CompletableFuture.completedFuture(resources));

            String requestJson = """
                {
                    "button.submit": {
                        "en": "Submit"
                    },
                    "button.cancel": {
                        "en": "Cancel"
                    }
                }
                """;

            mockMvc.perform(post("/api/localization/translations/bulk-import")
                    .param("countryCode", "IE")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].resourceKey").value("button.submit"))
                .andExpect(jsonPath("$[1].resourceKey").value("button.cancel"));
        }
    }

    @Nested
    class ValidationTests {

        @Test
        void validateCountryCode_Valid_ReturnsTrue() throws Exception {
            when(localizationService.isValidCountryCode("IE"))
                .thenReturn(CompletableFuture.completedFuture(true));

            mockMvc.perform(get("/api/localization/validate/country-code/IE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true));
        }

        @Test
        void validateCountryCode_Invalid_ReturnsFalse() throws Exception {
            when(localizationService.isValidCountryCode("XX"))
                .thenReturn(CompletableFuture.completedFuture(false));

            mockMvc.perform(get("/api/localization/validate/country-code/XX"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false));
        }

        @Test
        void validateLocale_Valid_ReturnsTrue() throws Exception {
            when(localizationService.isValidLocale("en-IE"))
                .thenReturn(CompletableFuture.completedFuture(true));

            mockMvc.perform(get("/api/localization/validate/locale/en-IE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true));
        }

        @Test
        void validateLocale_Invalid_ReturnsFalse() throws Exception {
            when(localizationService.isValidLocale("invalid"))
                .thenReturn(CompletableFuture.completedFuture(false));

            mockMvc.perform(get("/api/localization/validate/locale/invalid"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false));
        }
    }
}
